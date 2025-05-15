package oop.grp1.Control.DataFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import io.github.cdimascio.dotenv.Dotenv;
import java.net.URISyntaxException;

public class StockDF extends DFAbstract {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("ALPHA_VANTAGE_API_KEY1");

    @Override
    public String fetch(String symbol) {
        try {
            URI uri = buildURI(symbol);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    return content.toString();
                } finally {
                    conn.disconnect();
                }
            } else {
                return "Error HTTP " + responseCode;
            }
        } catch (URISyntaxException | IOException e) {
            return "Exception: " + e.getMessage();
        }
    }
    
    private URI buildURI(String symbol) throws URISyntaxException {
        String query = String.format(
            "function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&outputsize=compact&apikey=%s",
            symbol, API_KEY
            );
        return new URI(
            "https", 
            "www.alphavantage.co", 
            "/query", 
            query, 
            null
            );
    }
}
