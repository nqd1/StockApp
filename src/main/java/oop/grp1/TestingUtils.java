package oop.grp1;

import oop.grp1.Utils.JsonBeautifier;
import oop.grp1.Utils.JsonToSQLite;
import oop.grp1.Utils.DataFetcher;

public class TestingUtils {
    public static void main(String[] args) {
        String symbol = "MSFT";
        DataFetcher dataFetcher = new DataFetcher();
        String data = dataFetcher.getData(symbol);
        System.out.println(JsonBeautifier.beautify(data));
        JsonToSQLite.saveJsonToDatabase(data);
    }
};