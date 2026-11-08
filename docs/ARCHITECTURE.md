# 架構與介面約定

類別設計依照 proposal §3。本文件記錄實作時必須遵守的約定,以及和 proposal 不同的地方。

## 搜尋流程

```
index.html → SearchController → SearchService.search(q)
                                   │
                                   ├─ KeywordList.defaultsFor(q)   初始關鍵字
                                   ├─ KeywordExpander.expand(q)    "cat" → "cat movie"…
                                   │
                                   │  ┌── 最多 MAX_ROUND = 2 輪 ──────────────┐
                                   │  │ GoogleQuery.query()     取得候選網站  │
                                   │  │ SubLinkCrawler          最多 5 個子頁 │
                                   │  │ PagePrefetcher          先抓好網頁    │
                                   │  │ WebTree.setPostOrderScore 後序計分    │
                                   │  │ Ranker.sort             QuickSort     │
                                   │  │ KeywordExpander.deriveKeywords 新字詞 │
                                   │  └───────────────────────────────────────┘
                                   ▼
                        ArrayList<SearchResult> → result.html / compare.html
```

## 計分公式

```
WebPage.score    = Σ count(k_i) × weight(k_i)
WebNode.nodeScore = webPage.score + 0.5 × Σ child.nodeScore
WebTree.totalScore = root.nodeScore
```

## 必須遵守的約定

這些約定會影響其他人的模組。要修改時請先在群組討論。

1. **關鍵字清單是每次搜尋獨立的。** `SearchService` 是 Spring singleton,`KeywordList` 必須是區域變數,否則同時搜尋的使用者會互相污染。
2. **單一失敗不可擴大。** 子網頁失敗時該頁算 0 分,網站失敗時略過該網站,都不可讓整次搜尋失敗。
3. **`breakdown` 加總必須等於 `score`。** 子網頁的貢獻要乘上折扣,深度為 d 的節點乘上 0.5^d。
4. **`GoogleQuery` 回傳的順序就是搜尋引擎的排名。** 比較頁依賴這個順序,所以要用 `LinkedHashMap`。
5. **使用者關鍵字的權重最高。** `keywords.csv` 和 `deriveKeywords` 的權重都必須低於 `KeywordList.USER_WEIGHT`。
6. **`Ranker` 必須自己實作 QuickSort。** 不可使用內建排序。
7. **`WebPage` 一律透過 `SearchService.newPage` 建立**,這樣測試才能不連網。

## 和 Proposal 不同的地方

| 項目 | Proposal | 實作 | 原因 |
|---|---|---|---|
| `SearchService.keywordList` | 欄位 | 區域變數 | singleton 的欄位會被同時搜尋的使用者共用 |
| `GoogleQuery` | 欄位 `query`,`query()` 無參數 | `query(String q)` | 成為 Spring bean 後不能保存單次查詢的狀態 |
| `GoogleQuery.query()` 回傳 | 標題 → 網址 | 網址 → 標題 | 不同網站可能同標題,以標題當 key 會遺失結果 |
| `GoogleQuery` 資料來源 | §2 寫 SerpApi,§3.3 圖寫 Jsoup | Tavily | Google 不再開放新用戶申請搜尋 API,課程 HW8 改用 Tavily。類別名稱沿用 proposal |
| `deriveKeywords` | `(topPages)` | `(topPages, originalQuery)` | 排除原始查詢並防止主題偏移時需要 |
| `buildTree` | `(url)` | `(url, title)` | 標題來自搜尋引擎的結果 |
| `runRound` | `(keywords)` | `(queries, kws)` | 查詢字串和計分關鍵字是兩回事 |
| `SearchService` | 無 | 增加 `googleResults(q)`、`newPage(...)` | 比較頁需要搜尋引擎的原始結果,測試需要替換網頁來源 |
| `WebPage`、`WebNode` | 無 | 增加 `fixture(...)` | 讓前端和 Ranker 不必等計分完成 |
| `WebPage` | 無 | 增加 `getContent()` | `deriveKeywords` 需要內文 |
| `MockSearchService`、`StageDemo` | 無 | 新增 | 支援平行開發,以及 Stage 1、2 的命令列展示 |
| `Hw8SearchService` | 無 | 新增(暫時) | HW8 要顯示真實結果,但完整流程還沒完成。C 的流程可用後刪除 |
| `PagePrefetcher` | 無 | 新增 | 平行抓取需要獨立的位置,否則 B 要改 A 和 C 的檔案 |

## 已知風險

### 關鍵字比對(Member B,W9 優先處理)

從 Stage 0 移植的 `countKeyword` 比對的是子字串,而且連 HTML 標籤都會計入。實測結果:

| 內容 | 關鍵字 | 目前結果 | 正確結果 |
|---|---|--:|--:|
| `category education cat` | `cat` | 3 | 1 |
| `email said AI main` | `AI` | 4 | 1 |

`cat` 和 `AI` 都是 proposal 的範例查詢,而且使用者關鍵字的權重最高,所以這個問題會直接影響排名。修正時要保留中文比對,因為中文沒有空格可以判斷單字邊界。

### 單次搜尋的抓取量(Member B、C,W12 前處理)

依 proposal 的上限估算,一次搜尋最多要抓:

```
10 個網站 × (1 根頁面 + 5 子頁面) × 2 輪 = 120 個網頁
```

依 `tavily.max-results=10` 計算。如果依序抓取,每頁 5 秒逾時,最差情況要 10 分鐘,使用者不可能等這麼久。處理方式:

- 在 `PagePrefetcher` 用執行緒池平行抓取。
- 快取已抓過的網頁,第二輪不重抓。
- 為整次搜尋設定時間上限,時間到就用已有的結果排名。
- 若仍太慢,第二輪只重抓新出現的網站,或減少每輪的網站數。

### Tavily 額度(Member E 在 HW8 時確認,之後由 Member C 管理)

依 HW8 說明,Tavily 免費方案每月 1,000 次查詢。每個擴充查詢都是一次呼叫,`expand` 產生 3 個查詢、跑 2 輪時,一次完整搜尋約 6 次,全組一個月大約只能做 160 次完整搜尋。開發時請:

- 快取相同查詢的結果。
- 平時用 mock profile 或 `tavily-sample.json` 開發。
- 每個人用自己的金鑰,不要共用,也不要 commit。

### 內文長度偏差(Member A,W12)

長網頁的關鍵字次數天生較多,proposal §5.4 也提到這點。調校權重時可比較是否要依內文長度正規化。

### 動態載入的網頁(Member B)

Jsoup 不會執行 JavaScript,完全靠前端渲染的網站抓不到內文。這類網頁會以低分呈現,報告時可列為系統限制。
