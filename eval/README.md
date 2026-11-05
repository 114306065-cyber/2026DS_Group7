# 成效評估

Proposal §5.4 和 Stage 6 用的資料放在這裡。Member A 負責 Google 比較,Member D 負責 LLM 比較,人工評分由全組分攤。

## 檔案

| 檔案 | 內容 |
|---|---|
| `queries.csv` | 固定的測試查詢,評估期間不要更動 |
| `judgments.csv` | 人工評分結果 |

## 評分方式

1. 每個查詢分別取 Google、MovieFinder、各 LLM 的前 5 筆結果。
2. **隱藏來源後再評分**,避免評分者偏向自己的系統。
3. 每筆結果由兩人獨立評分,意見不同時由第三人決定。
4. 判斷標準:這筆結果是否能幫使用者找到符合主題的電影。相關填 `1`,不相關填 `0`。

`judgments.csv` 欄位:

```
query,source,rank,url,rater,relevant
```

`source` 填 `google`、`moviefinder`、`chatgpt`、`gemini` 或 `deepseek`。

## 指標

```
Precision@5 = 前 5 筆中相關的筆數 / 5
```

報告時列出每個來源在所有查詢上的平均值。

## LLM 比較注意事項

- 每個 LLM 使用相同的 prompt,並記錄模型版本和日期。
- LLM 可能編造不存在的網址,所以每個網址都要實際開啟確認。打不開的算不相關。
