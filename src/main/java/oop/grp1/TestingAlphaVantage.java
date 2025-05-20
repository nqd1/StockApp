package oop.grp1;

import oop.grp1.Control.DBManager.StockManager;

public class TestingAlphaVantage {
    public static void main(String[] args) {
        String symbol = "AVL";
        StockManager manager = new StockManager();
        manager.fetchAndStore(symbol);
    }
}
