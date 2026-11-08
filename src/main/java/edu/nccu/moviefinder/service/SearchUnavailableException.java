package edu.nccu.moviefinder.service;

/**
 * 搜尋引擎整個無法使用時丟出,例如沒有設定金鑰、金鑰錯誤、額度用盡、連不上 Tavily。
 * 介面收到後會顯示「搜尋服務暫時無法使用」,而不是「沒有找到結果」。
 * 單一網站抓取失敗不算,那種情況應該略過該網站。
 */
public class SearchUnavailableException extends RuntimeException {

    public SearchUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
