package oop.grp1.Control.DataFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import io.github.cdimascio.dotenv.Dotenv;
import java.net.URISyntaxException;

public class NewsDF {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("ALPHA_VANTAGE_API_KEY2");

    public String getNews(String ticker) {
        try {
            URI uri = buildURI(ticker);
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

    public String getNews(String ticker, String date) {
        try {
            URI uri = buildURI(ticker, date);
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

    public String getNews(String ticker, String date, String time) {
        try {
            URI uri = buildURI(ticker, date, time);
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

    private URI buildURI(String ticker) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s", 
            ticker, API_KEY);
        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null);
    }

    private URI buildURI(String ticker, String date, String time) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s&time_from=%s", 
            ticker, API_KEY, timeReformat(date, time));

        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null);
    }

    private URI buildURI(String ticker, String date) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s&time_from=%s", 
            ticker, API_KEY, timeReformat(date));

        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null);
    }

    private String timeReformat(String date, String time) {
        // ie 2005-04-14 17:00:00 to 20050414T170000
        return date.replace("-", "") + "T" + time.replace(":", "");
    }

    private String timeReformat(String date) {
        // default time is 00:00:00
        return timeReformat(date, "00:00:00");
    }
}
