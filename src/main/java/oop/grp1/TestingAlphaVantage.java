package oop.grp1;

import oop.grp1.Utils.DataFetcher.AlphaVantage;
import oop.grp1.Utils.JsonBeautifier;
import oop.grp1.Utils.JsonToSQLite.AlphaVantageJsonToSQLite;

public class TestingAlphaVantage {
    public static void main(String[] args) {
        String symbol = "QWE"; 
        AlphaVantage dataFetcher = new AlphaVantage();

        String data = JsonBeautifier.beautify(dataFetcher.getData(symbol));

        System.out.println(data);

        AlphaVantageJsonToSQLite.saveJsonToDatabase(data);
    }
}
