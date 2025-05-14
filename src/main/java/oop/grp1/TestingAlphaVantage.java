package oop.grp1;

import java.util.Arrays;
import java.util.List;

import oop.grp1.Control.DBManager.StockManager;

public class TestingAlphaVantage {
    public static void main(String[] args) {
        List<String> topStocks = Arrays.asList(
        "AAPL",   // Apple Inc.
            "MSFT",   // Microsoft Corp.
            "AMZN",   // Amazon.com Inc.
            "GOOGL",  // Alphabet Inc. (Class A)
            "GOOG",   // Alphabet Inc. (Class C)
            "META",   // Meta Platforms Inc.
            "TSLA",   // Tesla Inc.
            "NVDA",   // NVIDIA Corp.
            "BRK.B",  // Berkshire Hathaway Inc.
            "UNH",    // UnitedHealth Group
            "JNJ",    // Johnson & Johnson
            "V",      // Visa Inc.
            "PG",     // Procter & Gamble
            "JPM",    // JPMorgan Chase
            "HD",     // Home Depot
            "MA",     // Mastercard Inc.
            "XOM",    // Exxon Mobil Corp.
            "PFE",    // Pfizer Inc.
            "ABBV",   // AbbVie Inc.
            "KO",     // Coca-Cola Co.
            "PEP",    // PepsiCo Inc.
            "MRK",    // Merck & Co.
            "CVX",    // Chevron Corp.
            "WMT",    // Walmart Inc.
            "BAC",    // Bank of America
            "DIS",    // Walt Disney Co.
            "INTC",   // Intel Corp.
            "CSCO",   // Cisco Systems
            "MCD",    // McDonald's Corp.
            "ADBE"    // Adobe Inc.
        );

        for (String symbol : topStocks) {
            StockManager.SymbolToSQLite(symbol);
        }
    }
}
