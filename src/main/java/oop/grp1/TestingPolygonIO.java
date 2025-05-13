package oop.grp1;

import oop.grp1.Utils.JsonBeautifier;
import oop.grp1.Utils.JsonToSQLite.PolygonIOJsonToSQLite;
import oop.grp1.Utils.DataFetcher.PolygonIO;

public class TestingPolygonIO {
    public static void main(String[] args) {
        String symbol = "AAPL";
        PolygonIO dataFetcher = new PolygonIO();
        String data = dataFetcher.getData(symbol);
        System.out.println(JsonBeautifier.beautify(data));
        PolygonIOJsonToSQLite.saveJsonToDatabase(data);
    }
};