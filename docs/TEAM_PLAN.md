# 分工與時程

## 一覽

請在「成員」欄填上姓名。

| 角色 | 成員 | 一句話說明 | 資料結構/演算法核心 |
|---|---|---|---|
| **A 計分模型與評估** | | 算出每個網頁和網站的分數,最後評估排名品質 | Tree、Post-order、Euler Tour |
| **B 爬蟲與部署** | | 抓網頁、找子網頁、讓抓取變快,最後部署上線 | Boyer-Moore、HashSet |
| **C 搜尋流程與整合** | | 把所有模組串成完整的搜尋流程,並負責排序 | QuickSort、HashMap |
| **D 語意分析與 LLM 比較** | | 擴充查詢、從網頁找新關鍵字,最後和 LLM 比較 | TF-IDF |
| **E 介面與 HW8** | Dana | 先完成 HW8(Tavily),之後決定並完成整個網站介面 | 樹狀結構的遞迴顯示 |

**介面排版由 E 決定。** 其他人不修改介面檔案,E 也不修改後端檔案。雙方只透過 `SearchResult` 交接。

## 時程

| 期間 | 重點 | 結束時要做到 |
|---|---|---|
| **第一輪** 11/8～11/22 | 各自完成核心模組,E 完成 HW8 | **11/22(日)驗收**,標準見下方 |
| **第二輪** 11/22～12/6 | 接上真實資料、兩輪迭代、完整介面 | **12/6(日)檢查**:Stage 3、4 能跑,介面完成初版 |
| **第三輪** 12/6～12/13 | 部署、評估、LLM 比較 | **12/13(日)程式凍結**:公開網址可用,評估資料齊全 |
| **收尾** 12/14～12/16 | 不加新功能,只修 bug 和排練 | **12/17 Demo** |

另一個硬期限:**HW8 在 11/15(日)23:59 繳交**,由 E 負責。

### 11/22 驗收清單

由 E 驗收,全部打勾才算通過:

- [ ] 所有人第一輪的 PR 都已併進 `main`
- [ ] `./mvnw test` 在 `main` 上全部通過,第一輪的測試都已啟用
- [ ] `StageDemo page` 能對 3 個網址輸出排名、分數和每個關鍵字的次數
- [ ] `StageDemo site` 能印出網站樹,每個節點都有關鍵字次數
- [ ] 輸入關鍵字後,能用 Tavily 跑完一輪真實搜尋,並依分數排序
- [ ] `cat` 不會再算到 `category` 裡

沒通過的項目,負責人要在群組說明原因和新的完成日期。

## 每人詳細工作

檔案路徑中的 `…/` 代表 `src/main/java/edu/nccu/moviefinder/`。

### A 計分模型與評估

**負責檔案**
- `…/model/WebPage.java`、`WebNode.java`、`WebTree.java`
- `…/model/Keyword.java`、`KeywordList.java`(已完成,有需要再改)
- `…/cli/StageDemo.java`
- `src/main/resources/keywords.csv`
- `eval/` 中和 Tavily 原始排名的比較

**第一輪**

| 期限 | 工作 | 檔案與 method |
|---|---|---|
| 11/12 | 單頁計分:Σ 次數 × 權重,記錄每個關鍵字的次數,重複呼叫不累加 | `WebPage.setScore` |
| 11/12 | 節點分數:自己 + 0.5 × 子節點分數總和 | `WebNode.setNodeScore` |
| 11/12 | 節點深度,根節點為 0 | `WebNode.getDepth` |
| 11/12 | 後序走訪計分,抓取失敗的網頁算 0 分 | `WebTree.setPostOrderScore` |
| 11/15 | 印出樹:每層縮排 2 格,含網址、分數和關鍵字次數 | `WebTree.eulerPrintTree` |
| 11/15 | 啟用 `WebPageTest`(3 個)、`WebTreeTest`(5 個) | |
| 11/22 | 確認 `StageDemo page`、`site` 能跑 | 需要 B、C 的部分 |

**第二輪(12/6 前)**
- [ ] 調整 `keywords.csv`:增加或刪除關鍵字、調整權重,使用者關鍵字的權重必須最高
- [ ] 實驗是否要依內文長度正規化分數(proposal §5.4),把比較結果記在 `eval/`

**第三輪(12/13 前)**
- [ ] 用 `eval/queries.csv` 計算 MovieFinder 和 Tavily 原始排名的 Precision@5
- [ ] 依結果定案權重,整理成報告用的表格

**誰在等你:** C 的搜尋流程需要你的 model,**11/12 前一定要合併**。

### B 爬蟲與部署

**負責檔案**
- `…/service/WordCounter.java`
- `…/service/SubLinkCrawler.java`
- `…/service/PagePrefetcher.java`
- 部署設定,例如 `Dockerfile`(自行新增)

**第一輪**

| 期限 | 工作 | 檔案與 method |
|---|---|---|
| 11/12 | **優先:** 英文關鍵字改成比對完整單字,中文維持原本比對方式 | `WordCounter.countKeyword` |
| 11/15 | 改用 Jsoup 只取內文,設定逾時和 User-Agent | `WordCounter.fetchContent` |
| 11/15 | 修 `last()` 重複建表、字串串接 O(n²) 兩個效能問題 | `WordCounter` |
| 11/19 | 從 HTML 取出最多 5 個同網域子網頁,去除重複和 `#` 錨點 | `SubLinkCrawler.extractLinks` |
| 11/19 | 用 Jsoup 抓 HTML,再交給 `extractLinks` | `SubLinkCrawler.getSubLinks` |
| 11/19 | 啟用 `WordCounterTest`(2 個)、`SubLinkCrawlerTest`(4 個) | |
| 11/22 | 確認 `StageDemo site` 能跑 | |

**第二輪(12/6 前)**
- [ ] `PagePrefetcher` 改成用執行緒池平行抓取,啟用 `PagePrefetcherTest`(1 個)
- [ ] 快取已抓過的網頁,第二輪搜尋不重抓;已失敗的網址不重試
- [ ] 設定整次搜尋的時間上限
- [ ] 比較雲端平台,確認能跑 Java 17 並設定環境變數,在群組報告選擇

**第三輪(12/13 前)**
- [ ] **12/10 前**部署上線,取得公開網址,讓 E 在線上確認介面
- [ ] 在公開網址上測一次搜尋要花多久
- [ ] 準備 Demo 備案,例如事先錄影,以防現場網路出問題

**誰在等你:** A 的分數準不準,取決於你的完整單字比對;C 的完整流程速度,取決於你的平行抓取。

### C 搜尋流程與整合

**負責檔案**
- `…/service/SearchService.java`
- `…/service/Ranker.java`
- `…/service/GoogleQuery.java`(**11/15 由 E 交接**後歸你)

**第一輪**

| 期限 | 工作 | 檔案與 method |
|---|---|---|
| 11/12 | 自己實作 QuickSort,依總分由高到低排序,不可用內建排序 | `Ranker.sort` |
| 11/12 | 啟用 `RankerTest`(4 個) | |
| 11/19 | 建立網站樹:根節點加上 `SubLinkCrawler` 找到的子網頁,網頁一律用 `newPage` 建立 | `SearchService.buildTree` |
| 11/19 | 跑一輪:搜尋每個查詢、以網址去重、建樹、`prefetcher.prefetch`、計分、排序 | `SearchService.runRound` |
| 11/22 | 用 Tavily 跑通一輪真實搜尋 | 需要 A、E 的部分 |

**第二輪(12/6 前)**
- [ ] `search` 完成最多 2 輪迭代,第二輪使用 D 找到的新關鍵字
- [ ] 計算 `breakdown`,加總必須等於總分(子網頁的貢獻要乘上折扣)
- [ ] 快取相同查詢的 Tavily 結果,節省額度
- [ ] 啟用 `SearchServiceTest`(4 個)
- [ ] 完整流程可用後,和 E 確認並刪除 `Hw8SearchService.java`

**第三輪(12/13 前)**
- [ ] 端對端測試:用 `eval/queries.csv` 的每個查詢實際跑一次,記錄結果數和花費時間
- [ ] 每個檢查日前確認 `main` 能跑,發現問題就交給對應負責人

**誰在等你:** E 的介面需要真實資料;D 需要真實網頁來調整關鍵字品質。

### D 語意分析與 LLM 比較

**負責檔案**
- `…/service/KeywordExpander.java`
- `src/main/resources/stopwords.txt`
- `eval/` 中的 LLM 比較

**第一輪**

| 期限 | 工作 | 檔案與 method |
|---|---|---|
| 11/12 | 查詢擴充:`cat` → `cat movie`、`cat film`、`cat movie review` | `KeywordExpander.expand` |
| 11/15 | 比較 TF-IDF 和共現分析,選定方法,在群組說明理由 | |
| 11/22 | 從網頁內文找出相關關鍵字,排除停用字和原始查詢,權重介於 0 和使用者權重之間 | `KeywordExpander.deriveKeywords` |
| 11/22 | 啟用 `KeywordExpanderTest`(5 個) | |

**第二輪(12/6 前)**
- [ ] 用真實網頁檢查找到的關鍵字是否合理,擴充 `stopwords.txt`
- [ ] 定下防止主題偏移的規則,例如限制每輪新增的關鍵字數量
- [ ] 比較加入新關鍵字前後,排名是否改善

**第三輪(12/13 前)**
- [ ] 設計 LLM prompt,每個 LLM 使用相同的 prompt
- [ ] 收集 ChatGPT、Gemini、DeepSeek 的結果,記錄模型版本和日期
- [ ] 確認 LLM 給的網址真實存在
- [ ] 完成比較分析

**誰在等你:** C 的第二輪迭代需要 `deriveKeywords`。

### E 介面與 HW8(Dana)

**負責檔案**
- `…/service/GoogleQuery.java`(**到 11/15 為止**,之後交接給 C)
- `…/service/Hw8SearchService.java`
- `…/service/MockSearchService.java`
- `…/controller/SearchController.java`
- `src/main/resources/templates/` 下的所有頁面
- `src/main/resources/static/`(自行新增 CSS、圖片)

**HW8(11/15 繳交)**

| 期限 | 工作 | 檔案與 method |
|---|---|---|
| 11/12 | 申請 Tavily 金鑰,解析 Tavily 的 JSON | `GoogleQuery.parse` |
| 11/12 | 呼叫 Tavily API,金鑰缺少時提示設定 `TAVILY_API_KEY` | `GoogleQuery.query` |
| 11/12 | 啟用 `GoogleQueryTest`(4 個) | |
| 11/14 | 把 Tavily 結果包成 `SearchResult` | `Hw8SearchService.search` |
| 11/14 | 初步介面:搜尋框、結果清單、「沒有找到結果」提示 | `index.html`、`result.html` |
| 11/15 | 截圖並上傳 Moodle,附上 GitHub 連結 | |
| 11/15 | 把 `GoogleQuery` 交接給 C,說明錯誤處理方式 | |

**第一輪剩下的時間**
- [ ] Review 組員的 PR
- [ ] 11/22 驗收

**第二輪(12/6 前)**
- [ ] 決定整體排版和視覺風格,加入 CSS
- [ ] 結果頁:排名、總分、每個關鍵字的分數拆解
- [ ] 子網頁樹的展開與收合
- [ ] 比較頁:Tavily 原始排名在左、MovieFinder 在右
- [ ] 錯誤畫面、無結果畫面、搜尋中的等待提示
- [ ] 手機版面

**第三輪(12/13 前)**
- [ ] 在 B 部署的公開網址上確認介面
- [ ] 準備 Demo 腳本,彙整簡報

**注意:** `SearchControllerTest` 會檢查頁面能正常顯示,改版後要維持通過。測試若依賴頁面文字,改版時一起更新。

## 檔案歸屬總表

修改不是自己負責的檔案前,先問負責人。

| 檔案 | 負責人 |
|---|---|
| `model/Keyword.java`、`KeywordList.java`、`WebPage.java`、`WebNode.java`、`WebTree.java` | A |
| `model/SearchResult.java` | A,**欄位要改須先和 C、E 討論** |
| `cli/StageDemo.java`、`keywords.csv` | A |
| `service/WordCounter.java`、`SubLinkCrawler.java`、`PagePrefetcher.java`、部署設定 | B |
| `service/SearchService.java`、`Ranker.java` | C |
| `service/GoogleQuery.java` | E → 11/15 後歸 C |
| `service/KeywordExpander.java`、`stopwords.txt` | D |
| `service/Hw8SearchService.java`、`MockSearchService.java`、`controller/SearchController.java` | E |
| `templates/`、`static/` | E |
| `pom.xml`、`application.properties` | 共用,修改前在群組說一聲 |
| `docs/`、`README.md` | 共用 |

## 交接點

| 交接內容 | 從誰 | 到誰 | 時間 | 交接時要說明 |
|---|---|---|---|---|
| `WebPage`、`WebNode`、`WebTree` | A | C | 11/12 | 失敗網頁的處理方式 |
| `GoogleQuery` | E | C | 11/15 | 錯誤和額度用盡時的行為 |
| `deriveKeywords` | D | C | 11/22 | 每次回傳多少關鍵字、權重範圍 |
| 完整搜尋流程 | C | E | 第二輪初 | 一次搜尋大約花多久 |
| 公開網址 | B | E | 12/10 | 網址和重新部署的方式 |

`SearchResult` 是後端和介面之間唯一的介面:後端負責把資料放進去,介面只負責顯示。

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

5. 到 GitHub 的 repo 頁面,按 **Compare & pull request** 開 PR 到 `main`,請一位組員 review。
6. Review 通過就 merge,然後刪除分支。
7. 下一項工作回到第 1 步。

規則:

- 一項工作一條分支,最好 2～3 天、最晚一週內合併。
- `./mvnw test` 沒全過不可以合併。
- 不要 commit API key。repo 是公開的,金鑰一旦 commit 就要立刻到 Tavily 換新的。
- 不小心在 `main` 上改了,還沒 commit 的話,直接執行 `git checkout -b feature/xxx`,改動會帶到新分支。

## 怎麼測試自己的部分

- **mock 模式只有假資料。** 它不會執行後端的程式,後端組員改了程式,畫面也不會變。
- **後端的測試方式是跑測試,** 不需要打開網頁。
- **測試上寫著 `@Disabled` 時會被跳過,** 要先移除才算真的有跑。

| 角色 | 平常怎麼測 | 接上別人的部分後 |
|---|---|---|
| A | `./mvnw test -Dtest=WebPageTest,WebTreeTest` | `StageDemo page`、`site` |
| B | `./mvnw test -Dtest=WordCounterTest,SubLinkCrawlerTest,PagePrefetcherTest` | `StageDemo site` |
| C | `./mvnw test -Dtest=RankerTest,SearchServiceTest` | 設定 `TAVILY_API_KEY` 後啟動完整網站 |
| D | `./mvnw test -Dtest=KeywordExpanderTest` | C 的流程完成後啟動完整網站 |
| E | `./mvnw test -Dtest=GoogleQueryTest`,以及 mock 模式和 hw8 模式 | C 的流程完成後啟動完整網站 |

啟動網站和 `StageDemo` 的指令在 [README](../README.md)。Windows 請把 `./mvnw` 換成 `mvnw.cmd`。

## 怎樣算完成

1. 移除自己測試上的 `@Disabled`。
2. 實作到 `./mvnw test` 全部通過。
3. 開 PR,review 後合併。

目前有 43 個測試,其中 32 個待各負責人啟用:

| 負責人 | 測試類別 | 待啟用數 |
|---|---|--:|
| A | `WebPageTest`(3 個)、`WebTreeTest`(5 個) | 8 |
| B | `WordCounterTest`(2 個)、`SubLinkCrawlerTest`(4 個)、`PagePrefetcherTest`(1 個) | 7 |
| C | `RankerTest`(4 個)、`SearchServiceTest`(4 個) | 8 |
| D | `KeywordExpanderTest`(5 個) | 5 |
| E | `GoogleQueryTest`(4 個) | 4 |

## 工作量

點數是相對工作量的**估算**,不是實測值。11/22 驗收時依實際進度重新檢視。

| 工作項目 | A | B | C | D | E |
|---|--:|--:|--:|--:|--:|
| 核心實作 1 | WebPage 計分 3 | WordCounter 升級 4 | Ranker QuickSort 3 | expand 1 | Tavily 串接(HW8)4 |
| 核心實作 2 | WebNode、WebTree 5 | SubLinkCrawler 4 | SearchService 流程 6 | 研究與資料準備 3 | Controller、錯誤畫面 3 |
| 核心實作 3 | StageDemo 1 | 平行抓取、快取 5 | breakdown 計算 2 | deriveKeywords 6 | 三個頁面 7 |
| 強化 | 權重調校 4 | 部署上線 5 | 額度與快取 3 | 主題偏移規則 3 | 比較頁、手機版 5 |
| 評估 | Precision@5 5 | | 端對端測試 3 | LLM 比較 5 | |
| 報告與 Demo | 2 | 2 | 2 | 2 | 2 |
| **合計** | **20** | **20** | **19** | **20** | **21** |

全組一起做、不計入上表的工作:

- 人工評分,方式見 `eval/README.md`。
- 互相 review PR。
- 期末報告,每人講自己的模組和資料結構。

## 維持公平的做法

- 工作量以完成的項目為準,不看 commit 數或程式行數。
- 卡關超過兩天就在群組提出。
- 檢查日如果發現有人明顯過重,就把「強化」那一列的工作移給較輕鬆的人。
