# 分工與時程

## 工作方式

- **開發同步進行:** 五個人一起開工,用已定好的介面、mock 和測試資料,不必等別人。
- **合併隨時進行:** 工作做完、測試通過、有人 review,就併進 `main`。**不用開會,也不要留到同步日。**
- **每兩週同步一次:** 只確認整體能不能跑、下一輪要做什麼。

## 每次開工的流程

**不要直接在 `main` 上寫程式。** 每項工作都要開新分支。

```bash
# 1. 開工前:切到 main 並更新
git checkout main
git pull

# 2. 開新分支,名稱格式是 feature/模組-內容
git checkout -b feature/ranker-quicksort

# 3. 寫程式,移除自己測試上的 @Disabled,然後跑全部測試
./mvnw test

# 4. 測試全過才 push
git add 你改的檔案
git commit -m "Implement QuickSort in Ranker"
git push -u origin feature/ranker-quicksort
```

5. 到 GitHub 開 PR 到 `main`,請一位其他模組的組員 review。
6. Review 通過就 merge,然後刪除分支。
7. 下一項工作回到第 1 步。

規則:

- 一項工作一條分支,最好 2～3 天、最晚一週內合併。
- `./mvnw test` 沒全過不可以合併。
- 修改別人負責的檔案前,先告知負責人。
- 修改 [ARCHITECTURE.md](ARCHITECTURE.md) 的介面約定前,先在群組討論。
- 不要 commit API key。
- 如果不小心在 `main` 上改了,還沒 commit 的話,直接執行 `git checkout -b feature/xxx`,改動會帶到新分支。

## 五個角色

請在「成員」欄填上姓名。

| 角色 | 成員 | 負責檔案 | 資料結構/演算法核心 |
|---|---|---|---|
| **A 計分模型與評估** | | `model/*`、`cli/StageDemo`、`keywords.csv` | Tree、Post-order、Euler Tour |
| **B 爬蟲與部署** | | `WordCounter`、`SubLinkCrawler`、`PagePrefetcher`、部署設定 | Boyer-Moore、HashSet |
| **C 搜尋流程與整合** | | `GoogleQuery`、`SearchService` | HashMap、LinkedHashMap |
| **D 語意分析與 LLM 比較** | | `KeywordExpander`、`stopwords.txt` | TF-IDF |
| **E 前端與排序** | | `Ranker`、`SearchController`、`MockSearchService`、`templates/`、`static/` | QuickSort、樹狀結構的遞迴渲染 |

分工依據:

- Proposal 的 critical path 是一條直線,按 Stage 分工會讓後面的人一直等,所以按模組分工。
- 每個檔案只有一位負責人,幾乎不會有 merge conflict。
- 前期較重的 A、B,後期接手評估和部署。

## 時程

| 期間 | 目標 | 同步日要確認 |
|---|---|---|
| **第一輪** 11/5～11/19 | 各自完成核心模組 | 11/19(四):Stage 1、2 能跑 |
| **第二輪** 11/19～12/3 | 接上真實資料 | 12/3(四):Stage 3、4 能跑 |
| **第三輪** 12/3～12/13 | 部署與評估 | 12/10(四):公開網址可用,評估資料齊全 |
| **收尾** 12/14～12/16 | 不加新功能,只修 bug 和排練 | 12/17 Demo |

Proposal 把 Stage 1 排在 W9。如果老師會按週檢查進度,A 和 E 要優先完成 Stage 1。

## 每人詳細工作

### A 計分模型與評估

**第一輪(11/12 前完成,因為 C 要接)**
- [ ] `WebPage.setScore`:計算 Σ 次數 × 權重,並記錄每個關鍵字的次數
- [ ] `WebNode.setNodeScore`、`getDepth`
- [ ] `WebTree.setPostOrderScore`:後序走訪,失敗的網頁算 0 分
- [ ] `WebTree.eulerPrintTree`:印出縮排、分數和關鍵字次數
- [ ] 啟用 `WebPageTest`、`WebTreeTest`
- [ ] 和 E 一起確認 `StageDemo page` 能跑

**第二輪**
- [ ] 調整 `keywords.csv` 的關鍵字和權重
- [ ] 比較是否要依內文長度正規化(proposal §5.4)

**第三輪**
- [ ] 完成 MovieFinder 和 Google 的 Precision@5 比較
- [ ] 整理評估結果,用來定案權重

### B 爬蟲與部署

**第一輪**
- [ ] **優先:** `WordCounter` 改成比對完整單字,同時保留中文比對
- [ ] `WordCounter` 改用 Jsoup 只取內文,並設定逾時和 User-Agent
- [ ] 修掉 `WordCounter` 註解列出的兩個效能問題
- [ ] `SubLinkCrawler.extractLinks`、`getSubLinks`
- [ ] 啟用 `WordCounterTest`、`SubLinkCrawlerTest`
- [ ] 確認 `StageDemo site` 能跑

**第二輪**
- [ ] `PagePrefetcher` 改成平行抓取,啟用 `PagePrefetcherTest`
- [ ] 加上網頁快取和整次搜尋的時間上限
- [ ] 比較雲端平台,確認能跑 Java 並設定環境變數

**第三輪**
- [ ] 撰寫部署設定(例如 Dockerfile)並上線,取得公開網址
- [ ] 在公開網址上確認效能,準備 Demo 備案

### C 搜尋流程與整合

**第一輪**
- [ ] 申請 SerpApi 金鑰,確認額度
- [ ] `GoogleQuery.parse`、`query`,處理失敗和額度用盡
- [ ] 啟用 `GoogleQueryTest`
- [ ] A 合併後:`SearchService.buildTree`、`runRound`,單輪流程能跑

**第二輪**
- [ ] `SearchService.search` 完成兩輪迭代
- [ ] 計算 `breakdown`,加總必須等於總分
- [ ] 快取相同查詢,節省額度
- [ ] 啟用 `SearchServiceTest`,用真實資料跑通

**第三輪**
- [ ] 端對端測試:用 `eval/queries.csv` 的查詢全部跑過
- [ ] 每個同步日前確認 `main` 能跑,問題交給對應負責人

### D 語意分析與 LLM 比較

**第一輪**
- [ ] `KeywordExpander.expand`
- [ ] 研究 TF-IDF 和共現分析,選定方法
- [ ] `deriveKeywords` 第一版,用固定文章測試
- [ ] 啟用 `KeywordExpanderTest`

**第二輪**
- [ ] 用真實網頁調整關鍵字品質,擴充 `stopwords.txt`
- [ ] 定下防止主題偏移的規則
- [ ] 比較加入新關鍵字前後的結果是否改善

**第三輪**
- [ ] 設計 LLM prompt,收集 ChatGPT、Gemini、DeepSeek 的結果
- [ ] 確認 LLM 給的網址真實存在
- [ ] 完成 LLM 比較分析

### E 前端與排序

**第一輪**
- [ ] `Ranker.sort`:自己實作 QuickSort,啟用 `RankerTest`
- [ ] 用 mock 模式設計 `index.html`、`result.html`
- [ ] 建立 `static/`,加入 CSS
- [ ] 子網頁樹的展開與收合

**第二輪**
- [ ] `compare.html`:Google 在左、MovieFinder 在右
- [ ] `SearchController` 的錯誤畫面和無結果畫面
- [ ] 手機版面
- [ ] 接上真實後端,處理搜尋等待時間的提示

**第三輪**
- [ ] 調整 UI
- [ ] Demo 腳本和簡報彙整

## 怎麼測試自己的部分

先分清楚兩件事:

- **mock 模式只有假資料。** 它只會執行前端、`SearchController` 和 `MockSearchService`,**不會執行其他人的程式**。後端組員的改動不會反映在 mock 畫面上。
- **後端的測試方式是跑測試。** 不需要打開網頁。

| 角色 | 平常怎麼測 | 接上別人的部分後 |
|---|---|---|
| A | `./mvnw test -Dtest=WebPageTest,WebTreeTest` | E 的 Ranker 合併後跑 `StageDemo page` |
| B | `./mvnw test -Dtest=WordCounterTest,SubLinkCrawlerTest,PagePrefetcherTest` | A、E 合併後跑 `StageDemo site` |
| C | `./mvnw test -Dtest=GoogleQueryTest,SearchServiceTest` | 設定 `SERPAPI_KEY` 後啟動真實網站 |
| D | `./mvnw test -Dtest=KeywordExpanderTest` | C 合併後啟動真實網站,看第二輪的結果 |
| E | `./mvnw spring-boot:run -Dspring-boot.run.profiles=mock`,開 http://localhost:8080 | C 合併後啟動真實網站 |

`StageDemo` 和真實網站的指令在 [README](../README.md)。Windows 請把 `./mvnw` 換成 `mvnw.cmd`。

## 怎樣算完成

完成標準寫在測試裡:

1. 移除自己測試上的 `@Disabled`。
2. 實作到 `./mvnw test` 全部通過。
3. 開 PR,review 後合併。

目前的測試狀態:42 個測試中有 31 個待各負責人啟用。

| 負責人 | 測試類別 | 待啟用數 |
|---|---|--:|
| A | `WebPageTest`、`WebTreeTest` | 8 |
| B | `WordCounterTest`(2 個)、`SubLinkCrawlerTest`(4 個)、`PagePrefetcherTest`(1 個) | 7 |
| C | `GoogleQueryTest`、`SearchServiceTest` | 7 |
| D | `KeywordExpanderTest` | 5 |
| E | `RankerTest` | 4 |

E 的頁面由 `SearchControllerTest` 驗證,這組測試已啟用,改版後要維持通過。測試若依賴頁面文字,改版時可以一起更新。

## 工作量

點數是相對工作量的**估算**,不是實測值。11/19 同步時請依實際進度重新檢視。

| 工作項目 | A | B | C | D | E |
|---|--:|--:|--:|--:|--:|
| 核心實作 1 | WebPage 計分 3 | WordCounter 升級 4 | GoogleQuery 4 | expand 1 | Ranker QuickSort 3 |
| 核心實作 2 | WebNode、WebTree 5 | SubLinkCrawler 4 | SearchService 流程 6 | 研究與資料準備 3 | Controller、錯誤畫面 3 |
| 核心實作 3 | StageDemo 1 | 平行抓取、快取 5 | breakdown 計算 2 | deriveKeywords 6 | 三個頁面 7 |
| 強化 | 權重調校 4 | 部署上線 5 | 額度與錯誤處理 3 | 主題偏移規則 3 | 比較頁、UI 整合 5 |
| 評估 | Precision@5 5 | | 端對端測試 3 | LLM 比較 5 | |
| 報告與 Demo | 2 | 2 | 2 | 2 | 2 |
| **合計** | **20** | **20** | **20** | **20** | **20** |

全組一起做、不計入上表的工作:

- 人工評分,方式見 `eval/README.md`。
- 互相 review PR。
- 期末報告,每人講自己的模組和資料結構。

## 真正需要等的地方

| 需要等 | 被等的工作 | 期限 | 等待期間的做法 |
|---|---|---|---|
| `StageDemo page` | A 的計分、E 的 Ranker | 第一輪 | 先用測試驗證 |
| C 的 `SearchService` | A 的 model | 11/12 | 先做 GoogleQuery |
| D 用真實資料調整 | C 的單輪流程 | 第二輪初 | 先用固定文章實驗 |
| E 接真實後端 | C 的完整流程 | 第二輪 | 繼續用 mock 模式 |
| A、D 的評估 | 完整系統 | 12/3 | 先準備查詢和評分表 |

## 維持公平的做法

- 工作量以完成的項目為準,不看 commit 數或程式行數。
- 卡關超過兩天就在群組提出。
- 同步日如果發現有人明顯過重,就把「強化」那一列的工作移給較輕鬆的人。
