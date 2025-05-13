package oop.grp1.Utils.DataFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import io.github.cdimascio.dotenv.Dotenv;

public class PolygonIO {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("PolygonIO_API_KEY");    
    public String getData(String symbol) {
        try {
            URI uri = buildURI(symbol);
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

    public URI buildURI(String symbol) throws Exception {
        return new URI(
            "https",
            "api.polygon.io",
            "/v2/aggs/ticker/" + symbol + "/prev",
            "adjusted=true&apiKey=" + API_KEY,
            null
        );
    }
}
