package oop.grp1.Control.DataFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

import io.github.cdimascio.dotenv.Dotenv;

public class NewsDF extends DFAbstract {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("ALPHA_VANTAGE_API_KEY3");

    @Override
    public String fetch(String ticker) {
        return getNews(ticker);
    }

    public String getNews(String ticker) {
        try {
            URI uri = buildURI(ticker);
            return sendRequest(uri);
        } catch (URISyntaxException | IOException e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String getNews(String ticker, String date) {
        try {
            URI uri = buildURI(ticker, date);
            return sendRequest(uri);
        } catch (URISyntaxException | IOException e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String getNews(String ticker, String date, String time) {
        try {
            URI uri = buildURI(ticker, date, time);
            return sendRequest(uri);
        } catch (URISyntaxException | IOException e) {
            return "Exception: " + e.getMessage();
        }
    }

    private String sendRequest(URI uri) throws IOException {
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
            conn.disconnect();
            return "Error HTTP " + responseCode;
        }
    }

    private URI buildURI(String ticker) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s",
            ticker,
            API_KEY
        );

        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null
        );
    }

    private URI buildURI(String ticker, String date) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s&time_from=%s&limit=1",
            ticker,
            API_KEY,
            timeReformat(date)
        );

        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null
        );
    }

    private URI buildURI(String ticker, String date, String time) throws URISyntaxException {
        String query = String.format(
            "function=NEWS_SENTIMENT&tickers=%s&apikey=%s&time_from=%s",
            ticker,
            API_KEY,
            timeReformat(date, time)
        );

        return new URI(
            "https",
            "www.alphavantage.co",
            "/query",
            query,
            null
        );
    }

    private String timeReformat(String date, String time) {
        return date.replace("-", "") + "T" + time.replace(":", "");
    }

    private String timeReformat(String date) {
        return timeReformat(date, "00:00:00");
    }
}
