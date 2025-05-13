package oop.grp1.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DataFetcher {

    private static final String API_KEY = "KV8ZNpLPoq8tIN5oU1mKbFoG_YizoO7s"; 

    public String getData(String symbol) {
        try {
            URI uri = new URI(
                "https",
                "api.polygon.io",
                "/v2/aggs/ticker/" + symbol + "/prev",
                "adjusted=true&apiKey=" + API_KEY,
                null
            );

            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();

                return content.toString();
            } else {
                return "Error HTTP " + responseCode;
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
