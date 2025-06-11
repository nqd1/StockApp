package oop.grp1;

import oop.grp1.Control.DBManager.StockManager;

import java.util.List;
import java.util.Arrays;


public class Testing{
    public static void main(String args[]){
       List<String> otherStocks = Arrays.asList(
    "ADBE", "NFLX", "COST", "CRM", "TSM", "AVGO", "AMD", "QCOM", "TXN", "AMAT",
    "LIN", "HON", "SBUX", "MCD", "LLY", "BMY", "GE", "UPS", "BA", "CAT",
    "DE", "NEE", "SO", "DUK", "MDT"
);

        StockManager manager = new StockManager();
        for(String ticker : otherStocks){
            manager.fetchAndStore(ticker);
        }
    }
}