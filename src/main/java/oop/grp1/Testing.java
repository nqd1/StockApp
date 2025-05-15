package oop.grp1;

import oop.grp1.Control.DBManager.NewsManager;
import java.util.List;
import java.util.Arrays;


public class Testing{
    public static void main(String args[]){
        List<String> top30Stocks = Arrays.asList("AAPL","MSFT","GOOGL","AMZN","TSLA","NVDA","META","BRK.B","UNH","JNJ","V","PG","JPM","HD","MA","XOM","PFE","KO","PEP","BAC","MRK","ABBV","WMT","CVX","DIS","CSCO","ORCL","INTC","T","VZ");
        NewsManager manager = new NewsManager();
        for(String ticker : top30Stocks){
            manager.fetchAndStore(ticker);
        }
    }
}