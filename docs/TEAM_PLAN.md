# 分工與時程

## 一覽

請在「成員」欄填上名子

| 角色 | 成員 | 一句話說明 | 資料結構/演算法核心 |
|---|---|---|---|
| **A 計分模型與評估** | | 算出每個網頁和網站的分數,最後評估排名品質 | Tree、Post-order、Euler Tour |
| **B 爬蟲與部署** | | 抓網頁、找子網頁、讓抓取變快,最後部署上線 | Boyer-Moore、HashSet |
| **C 搜尋流程與整合** |林| 把所有模組串成完整的搜尋流程,並負責排序 | QuickSort、HashMap |
| **D 語意分析與 LLM 比較** || 擴充查詢、從網頁找新關鍵字,最後和 LLM 比較 | TF-IDF |
| **E 介面與 HW8** | Dana | 先完成 HW8(Tavily),再決定並完成整個網站介面 | 樹狀結構的遞迴顯示 |

**介面排版由 E 決定。** 其他人不修改介面檔案,E 也不修改後端檔案。雙方只透過 `SearchResult` 交接。

## 時程

| 階段 | 期間 | 誰在做什麼 | 結束時 |
|---|---|---|---|
| **0** | 11/8 | E 完成 HW8,把 `GoogleQuery` 交給 C | 大家開工 |
| **1** | 11/8～11/15 | A～D 完成核心功能;E 用 mock 開始設計介面 | **11/15(日)中間檢查**、HW8 繳交 |
| **2** | 11/15～11/22 | A～D 用真實資料完成所有後端功能 | **11/22(日)功能驗收** |
| **3** | 11/22～11/29 | E 完成介面;A～D 做評估、LLM 比較、部署 | **11/29(日)介面完成** |
| **4** | 11/29～12/6 | A～D 測試介面並回報問題,E 修正;全組寫報告 | **12/6(日)程式凍結、final report 完成** |
| **5** | 12/6～12/13 | 製作簡報,分工由小組討論決定 | **12/13(日)全組一起確認簡報** |
| 收尾 | 12/14～12/16 | 排練 | **12/17 Demo** |

### 三條重要規則

- **11/22 後,後端只修 bug。** `SearchResult` 的欄位也不再更改,E 才能放心做介面。
- **11/29 後,介面只修測試回報的問題。** 不再大幅改版,讓大家能完整測試。
- **12/6 後,程式凍結。** 除非 Demo 會出問題,否則不再改程式。

## 怎麼看下面的工作表

每項工作的「開始條件」欄:

- **立即可做:** 不用等任何人,用測試資料或 mock 就能開發。
- **等 X:** 必須等 X 完成並併進 `main` 才能開始。被等的那一方請**準時完成**,否則會卡住別人。

每人在階段 1、2 的工作都是立即可做的,除了少數需要真實資料的項目。

## 每人詳細工作

檔案路徑中的 `…/` 代表 `src/main/java/edu/nccu/moviefinder/`。

### A 計分模型與評估

**負責檔案:** `…/model/` 下的所有檔案、`…/cli/StageDemo.java`、`src/main/resources/keywords.csv`

| 階段 | 期限 | 工作 | 開始條件 | 檔案與 method |
|---|---|---|---|---|
| 1 | **11/12** | 單頁計分:Σ 次數 × 權重,記錄每個關鍵字的次數,重複呼叫不累加 | 立即可做 | `WebPage.setScore` |
| 1 | **11/12** | 節點分數:自己 + 0.5 × 子節點分數總和 | 立即可做 | `WebNode.setNodeScore` |
| 1 | **11/12** | 節點深度,根節點為 0 | 立即可做 | `WebNode.getDepth` |
| 1 | **11/12** | 後序走訪計分,抓取失敗的網頁算 0 分 | 立即可做 | `WebTree.setPostOrderScore` |
| 1 | 11/15 | 印出樹:每層縮排 2 格,含網址、分數和關鍵字次數 | 立即可做 | `WebTree.eulerPrintTree` |
| 1 | 11/15 | 啟用 `WebPageTest`(3 個)、`WebTreeTest`(5 個) | 立即可做 | |
| 1 | 11/15 | 確認 `StageDemo page`、`site` 能跑 | 等 C 的 Ranker、B 的子網頁 | `StageDemo` |
| 2 | 11/22 | 用真實搜尋結果調整 `keywords.csv`,定出第一版權重 | 等 C 的一輪真實搜尋 | `keywords.csv` |
| 2 | 11/22 | 實驗是否要依內文長度正規化分數 | 等 C 的一輪真實搜尋 | `WebPage` |
| 3 | **12/2** | 計算 MovieFinder 和 Tavily 原始排名的 Precision@5,報告要用 | 等 11/22 驗收通過 | `eval/` |
| 4 | 12/6 | 在介面上確認分數和 breakdown 顯示正確 | 等 E 的介面完成(11/29) | |
| 4 | 12/3 | 報告段落:2.4 計分公式、2.5 資料結構表、5.4 排名準確度、評估結果中的 Precision@5 | | |

**你的工作會卡住:** C(11/12 要用你的 model)。

### B 爬蟲與部署

**負責檔案:** `…/service/WordCounter.java`、`SubLinkCrawler.java`、`PagePrefetcher.java`、部署設定(例如自行新增的 `Dockerfile`)

| 階段 | 期限 | 工作 | 開始條件 | 檔案與 method |
|---|---|---|---|---|
| 1 | **11/12** | 英文關鍵字改成比對完整單字,中文維持原本比對方式 | 立即可做 | `WordCounter.countKeyword` |
| 1 | 11/15 | 改用 Jsoup 只取內文,設定逾時和 User-Agent | 立即可做 | `WordCounter.fetchContent` |
| 1 | 11/15 | 修 `last()` 重複建表、字串串接 O(n²) 兩個效能問題 | 立即可做 | `WordCounter` |
| 1 | **11/15** | 從 HTML 取出最多 5 個同網域子網頁,去除重複和 `#` 錨點 | 立即可做 | `SubLinkCrawler.extractLinks` |
| 1 | **11/15** | 用 Jsoup 抓 HTML,再交給 `extractLinks` | 立即可做 | `SubLinkCrawler.getSubLinks` |
| 1 | 11/15 | 啟用 `WordCounterTest`(2 個)、`SubLinkCrawlerTest`(4 個) | 立即可做 | |
| 2 | 11/22 | 改成用執行緒池平行抓取,啟用 `PagePrefetcherTest`(1 個) | 立即可做 | `PagePrefetcher.prefetch` |
| 2 | 11/22 | 快取已抓過的網頁,已失敗的網址不重試 | 立即可做 | `PagePrefetcher` |
| 2 | 11/22 | 設定整次搜尋的時間上限,和 C 一起測試 | 等 C 的一輪真實搜尋 | `PagePrefetcher` |
| 2 | 11/22 | 比較雲端平台,確認能跑 Java 17 並設定環境變數 | 立即可做 | |
| 3 | **11/26** | 用目前的介面部署上線,先把部署流程跑通,取得公開網址。金鑰設在雲端平台的環境變數 | 等 11/22 驗收通過 | 部署設定 |
| 3 | **11/29** | E 的介面完成後重新部署,讓大家在公開網址上測試 | 等 E 的介面完成 | |
| 4 | 12/6 | 在公開網址上測搜尋時間,修正後重新部署 | 等 11/29 部署 | |
| 4 | 12/3 | 報告段落:2.3 關鍵字比對、5.2 爬蟲、成果中的公開網址與部署方式 | | |
| 5 | 12/13 | 準備 Demo 備案,例如事先錄影,以防現場網路出問題 | | |

**你的工作會卡住:** A 的分數是否準確(完整單字比對)、`StageDemo site`(子網頁)、全組的 Demo(部署)。

### C 搜尋流程與整合

**負責檔案:** `…/service/SearchService.java`、`Ranker.java`、`GoogleQuery.java`(11/8 由 E 交接)

| 階段 | 期限 | 工作 | 開始條件 | 檔案與 method |
|---|---|---|---|---|
| 1 | **11/10** | 自己實作 QuickSort,依總分由高到低排序,不可用內建排序 | 立即可做 | `Ranker.sort` |
| 1 | 11/10 | 啟用 `RankerTest`(4 個) | 立即可做 | |
| 1 | 11/8 | 接手 `GoogleQuery`,確認錯誤處理方式 | 等 E 交接(11/8) | `GoogleQuery` |
| 1 | 11/15 | 建立網站樹:根節點加上子網頁,網頁一律用 `newPage` 建立 | 等 A 的 model(11/12) | `SearchService.buildTree` |
| 1 | 11/15 | 跑一輪:搜尋、以網址去重、建樹、`prefetcher.prefetch`、計分、排序 | 等 A 的 model(11/12) | `SearchService.runRound` |
| 1 | **11/15** | 用 Tavily 跑通一輪真實搜尋,通知大家 | 等 A 的 model、B 的子網頁 | |
| 2 | 11/22 | 計算 breakdown,加總必須等於總分 | 立即可做 | `SearchService` |
| 2 | 11/22 | 快取相同查詢的 Tavily 結果,節省額度 | 立即可做 | `GoogleQuery` 或 `SearchService` |
| 2 | **11/22** | 完成最多 2 輪迭代,第二輪使用 D 找到的新關鍵字 | 等 D 的 `deriveKeywords`(11/15) | `SearchService.search` |
| 2 | 11/22 | 啟用 `SearchServiceTest`(4 個) | 等上面完成 | |
| 3 | 11/29 | 端對端測試:用 `eval/queries.csv` 的每個查詢跑一次,記錄結果數和時間 | 等 11/22 驗收通過 | |
| 3 | 11/29 | 和 E 確認後刪除 `Hw8SearchService.java` | 等 E 的介面改用完整流程 | |
| 4 | 12/6 | 在介面上測試錯誤情況:無結果、金鑰錯誤、網路中斷 | 等 E 的介面完成(11/29) | |
| 4 | 12/3 | 報告段落:1 Introduction、2.1 搜尋流程、3.1～3.3 系統設計與 Class 結構、4 實際時程 | | |
| 4 | **12/6** | **彙整 final report**:收齊每人的段落,統一格式 | 等每人 12/3 交段落 | |

**你的工作會卡住:** A 的權重調整、B 的時間上限測試、D 的品質調整(都要你 11/15 的一輪真實搜尋),E 的介面(要你 11/22 的完整流程),以及 final report。**C 是時程的關鍵,有困難請盡早提出。**

### D 語意分析與 LLM 比較

**負責檔案:** `…/service/KeywordExpander.java`、`src/main/resources/stopwords.txt`

| 階段 | 期限 | 工作 | 開始條件 | 檔案與 method |
|---|---|---|---|---|
| 1 | 11/12 | 查詢擴充:`cat` → `cat movie`、`cat film`、`cat movie review` | 立即可做 | `KeywordExpander.expand` |
| 1 | 11/12 | 比較 TF-IDF 和共現分析,選定方法,在群組說明理由 | 立即可做 | |
| 1 | **11/15** | 從網頁內文找出相關關鍵字,排除停用字和原始查詢,權重介於 0 和使用者權重之間 | 立即可做(用固定文章測試) | `KeywordExpander.deriveKeywords` |
| 1 | 11/15 | 啟用 `KeywordExpanderTest`(5 個) | 立即可做 | |
| 2 | 11/22 | 用真實網頁檢查找到的關鍵字是否合理,擴充 `stopwords.txt` | 等 C 的一輪真實搜尋 | `stopwords.txt` |
| 2 | 11/22 | 定下防止主題偏移的規則,例如限制每輪新增的關鍵字數量 | 等 C 的一輪真實搜尋 | `deriveKeywords` |
| 3 | **12/2** | 比較加入新關鍵字前後,排名是否改善 | 等 11/22 驗收通過 | `eval/` |
| 3 | 11/29 | 設計 LLM prompt,收集 ChatGPT、Gemini、DeepSeek 的結果,記錄模型版本和日期 | 立即可做 | `eval/` |
| 3 | **12/2** | 確認 LLM 給的網址真實存在,完成比較分析,報告要用 | 等上面完成 | `eval/` |
| 4 | 12/6 | 在介面上確認比較頁的內容正確 | 等 E 的介面完成(11/29) | |
| 4 | 12/3 | 報告段落:5.3 語意分析、評估結果中的新關鍵字效果和 LLM 比較 | | |

**你的工作會卡住:** C 的兩輪迭代(11/15 要用你的 `deriveKeywords`)。

### E 介面與 HW8(Dana)

**負責檔案:** `…/service/Hw8SearchService.java`、`MockSearchService.java`、`…/controller/SearchController.java`、`src/main/resources/templates/`、`src/main/resources/static/`(自行新增)

| 階段 | 期限 | 工作 | 開始條件 | 檔案與 method |
|---|---|---|---|---|
| 0 | **11/8** | 解析 Tavily 的 JSON、呼叫 Tavily API,啟用 `GoogleQueryTest`(4 個) | 立即可做 | `GoogleQuery.parse`、`query` |
| 0 | **11/8** | 把 Tavily 結果包成 `SearchResult` | 立即可做 | `Hw8SearchService.search` |
| 0 | **11/8** | 初步介面:搜尋框、結果清單、「沒有找到結果」提示 | 立即可做 | `index.html`、`result.html` |
| 0 | **11/8** | 合併後把 `GoogleQuery` 交接給 C | | |
| 1 | 11/15 | 打上 `hw8` 標籤,截圖並上傳 Moodle,附上標籤的連結 | | |
| 1、2 | 每次 PR | Review 組員的 PR | | |
| 1、2 | 11/22 | 用 mock 模式設計整體排版和視覺風格,加入 CSS | 立即可做(mock) | `static/`、`templates/` |
| 1、2 | 11/22 | 結果頁:排名、總分、每個關鍵字的分數拆解、子網頁樹的展開與收合 | 立即可做(mock) | `result.html` |
| 1 | 11/15 | 主持中間檢查 | | |
| 2 | 11/22 | 主持功能驗收 | | |
| 3 | 11/29 | 比較頁:Tavily 原始排名在左、MovieFinder 在右 | 立即可做(mock) | `compare.html` |
| 3 | 11/29 | 錯誤畫面、無結果畫面、搜尋中的等待提示、手機版面 | 立即可做(mock) | `SearchController`、`templates/` |
| 3 | **11/29** | 介面改用完整流程,並用真實資料確認 | 等 11/22 驗收通過 | |
| 4 | 12/6 | 修正組員回報的介面問題 | 等組員回報 | |
| 4 | 12/3 | 報告段落:2.2 資料來源(改用 Tavily)、3.4 前端、5.1 搜尋 API、成果中的截圖 | | |
| 5 | 12/10 | Demo 腳本:Demo 時要搜尋哪些關鍵字、依序展示哪些頁面 | | |

**HW8 直接做成最後介面的第一版,不要分開做:**
- 用你想要的最後風格做 `index.html`、`result.html`,之後再往上加功能。
- HW8 模式下沒有分數、breakdown 和子網頁樹。版面先預留位置,資料是空的時候用 `th:if` 隱藏,不要刪掉。
- 繳交前打上標籤,讓助教看到的固定是 HW8 當時的版本:
  ```bash
  git tag hw8
  git push origin hw8
  ```
  繳交連結:`https://github.com/Auroralin623/2026DS_Group7/tree/hw8`

`SearchControllerTest` 會檢查頁面能正常顯示,改版後要維持通過。測試若依賴頁面文字,改版時一起更新。

## 檢查清單

### 11/15(日)中間檢查

- [ ] HW8 已繳交
- [ ] A 的 8 個測試、C 的 Ranker 4 個測試、D 的 5 個測試、B 的 6 個測試都已啟用並通過
- [ ] `StageDemo page` 能對 3 個網址輸出排名、分數和每個關鍵字的次數
- [ ] `StageDemo site` 能印出網站樹,每個節點都有關鍵字次數
- [ ] 能用 Tavily 跑完一輪真實搜尋並排序
- [ ] `cat` 不會再算到 `category` 裡

### 11/22(日)功能驗收

- [ ] `./mvnw test` 全部通過,所有測試都已啟用
- [ ] 真實搜尋能跑完兩輪,並依分數排序
- [ ] 每筆結果的 breakdown 加總等於總分
- [ ] 一次搜尋在大家同意的時間內完成
- [ ] 權重第一版定案

### 11/29(日)介面完成

- [ ] 三個頁面都已完成,並使用真實資料
- [ ] 已重新部署到公開網址
- [ ] 端對端測試已跑完

### 12/6(日)程式凍結、final report 完成

- [ ] 測試回報的問題都已修正或決定不修
- [ ] 公開網址上的版本就是 Demo 版本
- [ ] Precision@5 和 LLM 比較的結果已寫進報告
- [ ] final report 已彙整完成

### 12/13(日)簡報定稿

- [ ] 全組一起看過整份簡報,確認內容和風格一致
- [ ] Demo 腳本和備案(例如錄影)已準備好
- [ ] 排練時間已排定

沒通過的項目,負責人要在群組說明原因和新的完成日期。

## 階段 4:怎麼回報介面問題

用 GitHub 的 Issues 回報,不要只在群組說,以免遺漏:

1. 到 repo 的 **Issues** 分頁,按 **New issue**。
2. 標題寫清楚問題,例如「搜尋 cooking 時子網頁樹無法展開」。
3. 內容寫:輸入了什麼、預期看到什麼、實際看到什麼,附上截圖。
4. 右側 **Assignees** 選 E。

E 修好後,在 PR 說明裡寫 `Fixes #編號`,合併時 Issue 會自動關閉。

## Final report 與簡報

Final report 以 midterm proposal 為基礎:**修正做的時候有改的地方,再補上新加的內容。** Proposal 的每個章節,交給最了解那部分的人修改。`docs/ARCHITECTURE.md` 的「和 Proposal 不同的地方」可以直接參考。

| Proposal 章節 | 要修改或補充的內容 | 負責 |
|---|---|---|
| 1 Introduction | 大致不變,檢查一下 | C |
| 2.1 Search Process | 整體流程的實際做法 | C |
| 2.2 Data Source | SerpApi 改成 Tavily 的原因 | E |
| 2.3 Keyword Matching | 改成比對完整單字、抓取改用 Jsoup | B |
| 2.4 Score Formulation、2.5 資料結構表 | 最後的公式和權重 | A |
| 3.1～3.3 系統設計、Class 結構 | 新增的 class 和改過的 method | C |
| 3.4 Frontend | 最後的介面 | E |
| 4 Schedule | 實際時程和原計畫的比較 | C |
| 5.1 搜尋 API | 實際遇到的問題和解法 | E |
| 5.2 爬蟲 | 實際遇到的問題和解法、部署 | B |
| 5.3 語意分析 | 實際遇到的問題和解法 | D |
| 5.4 排名準確度 | 實際遇到的問題和解法 | A |
| **新增:評估結果** | Precision@5、新關鍵字的效果、LLM 比較 | A、D |
| **新增:成果** | 公開網址、部署方式、介面截圖 | B、E |

| 工作 | 誰 | 期限 |
|---|---|---|
| 自己負責的段落 | 每個人 | 12/3 |
| 彙整 final report | C | 12/6 |
| Demo 腳本 | E | 12/10 |
| 一起確認整份簡報 | 全組 | 12/13 |

**簡報的分工之後由小組討論決定。**

## 檔案歸屬總表

修改不是自己負責的檔案前,先問負責人。

| 檔案 | 負責人 |
|---|---|
| `model/Keyword.java`、`KeywordList.java`、`WebPage.java`、`WebNode.java`、`WebTree.java` | A |
| `model/SearchResult.java` | A,**11/22 後不再更改** |
| `cli/StageDemo.java`、`keywords.csv` | A |
| `service/WordCounter.java`、`SubLinkCrawler.java`、`PagePrefetcher.java`、部署設定 | B |
| `service/SearchService.java`、`Ranker.java`、`GoogleQuery.java` | C(`GoogleQuery` 在 11/8 由 E 交接) |
| `service/KeywordExpander.java`、`stopwords.txt` | D |
| `service/Hw8SearchService.java`、`MockSearchService.java`、`controller/SearchController.java` | E |
| `templates/`、`static/` | E |
| `pom.xml`、`application.properties` | 共用,修改前在群組說一聲 |
| `docs/`、`README.md` | 共用 |

## 交接點

| 交接內容 | 從誰 | 到誰 | 時間 | 交接時要說明 |
|---|---|---|---|---|
| `GoogleQuery` | E | C | 11/8 | 錯誤和額度用盡時的行為 |
| `WebPage`、`WebNode`、`WebTree` | A | C | 11/12 | 失敗網頁的處理方式 |
| `Ranker`、子網頁 | C、B | A | 11/15 | 讓 `StageDemo` 能跑 |
| `deriveKeywords` 第一版 | D | C | 11/15 | 每次回傳多少關鍵字、權重範圍 |
| 一輪真實搜尋 | C | A、B、D | 11/15 | 怎麼啟動、一次搜尋大約花多久 |
| 完整後端 | A～D | E | 11/22 | `SearchResult` 每個欄位的意思 |
| 公開網址 | B | E | 11/26 | 網址和重新部署的方式 |
| 完成的介面 | E | A～D | 11/29 | 要測哪些頁面和功能 |
| 報告段落 | 每個人 | C | 12/3 | 依 proposal 章節 |

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

- 一項工作一條分支,最好 2～3 天內合併。工作表上標粗體的期限,別人在等,請務必準時。
- `./mvnw test` 沒全過不可以合併。
- 不要 commit API key。repo 是公開的,金鑰一旦 commit 就要立刻到 Tavily 換新的。設定方式見 [README](../README.md#tavily-金鑰)。
- 不小心在 `main` 上改了,還沒 commit 的話,直接執行 `git checkout -b feature/xxx`,改動會帶到新分支。

## 怎麼測試自己的部分

- **mock 模式只有假資料。** 它不會執行後端的程式,後端組員改了程式,畫面也不會變。
- **後端的測試方式是跑測試,** 不需要打開網頁。
- **測試上寫著 `@Disabled` 時會被跳過,** 要先移除才算真的有跑。

| 角色 | 平常怎麼測 | 接上別人的部分後 |
|---|---|---|
| A | `./mvnw test -Dtest=WebPageTest,WebTreeTest` | `StageDemo page`、`site` |
| B | `./mvnw test -Dtest=WordCounterTest,SubLinkCrawlerTest,PagePrefetcherTest` | `StageDemo site` |
| C | `./mvnw test -Dtest=RankerTest,SearchServiceTest,GoogleQueryTest` | 設定 `TAVILY_API_KEY` 後啟動完整網站 |
| D | `./mvnw test -Dtest=KeywordExpanderTest` | C 的一輪真實搜尋完成後啟動完整網站 |
| E | mock 模式和 hw8 模式 | 11/22 後啟動完整網站 |

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
| E | `GoogleQueryTest`(4 個),HW8 時啟用 | 4 |

## 工作量

點數是相對工作量的**估算**,不是實測值。11/22 驗收時依實際進度重新檢視。

| 工作項目 | A | B | C | D | E |
|---|--:|--:|--:|--:|--:|
| 核心實作 1 | WebPage 計分 3 | WordCounter 升級 4 | Ranker QuickSort 3 | expand 1 | Tavily 串接(HW8)4 |
| 核心實作 2 | WebNode、WebTree 5 | SubLinkCrawler 4 | SearchService 流程 6 | 研究與資料準備 3 | Controller、錯誤畫面 3 |
| 核心實作 3 | StageDemo 1 | 平行抓取、快取 5 | breakdown 計算 2 | deriveKeywords 6 | 三個頁面 7 |
| 強化 | 權重調校 4 | 部署上線 5 | 額度與快取 3 | 主題偏移規則 3 | 比較頁、手機版 5 |
| 評估 | Precision@5 5 | | 端對端測試 3 | LLM 比較 5 | |
| 報告與 Demo | 2 | 2 | 彙整報告 3 | 2 | Demo 腳本 2 |
| **合計** | **20** | **20** | **20** | **20** | **21** |

各階段的忙碌程度不同:A～D 在 11/22 前最忙,E 在 11/22～11/29 最忙。階段 4 的介面測試和人工評分由全組分攤,不計入上表。

## 維持公平的做法

- 工作量以完成的項目為準,不看 commit 數或程式行數。
- 卡關超過兩天就在群組提出,尤其是會卡住別人的工作。
- 檢查日如果發現有人明顯過重,就把「強化」那一列的工作移給較輕鬆的人。
