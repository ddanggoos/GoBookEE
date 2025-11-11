package com.gobookee.book.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class AladinApiClient {

    private static final String API_URL = "https://www.aladin.co.kr/ttb/api/ItemList.aspx";
    private static final String API_SEARCH_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String API_LOOKUP_URL = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
    private static final String CONFIG_PATH = "config/aladin-api.properties";
    private static final String PROPERTY_KEY = "aladin.ttb.key";
    private static final String ENV_KEY = "ALADIN_TTB_KEY";
    private static final String TTB_KEY = loadTtbKey();

    private static final AladinApiClient CLIENT = new AladinApiClient();

    private AladinApiClient() {
    }

    public static AladinApiClient aladinApiClient() {
        return CLIENT;
    }

    private static String loadTtbKey() {
        String envValue = System.getenv(ENV_KEY);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        try (InputStream inputStream = AladinApiClient.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing configuration file: " + CONFIG_PATH);
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            String propertyValue = properties.getProperty(PROPERTY_KEY);
            if (propertyValue != null && !propertyValue.isBlank()) {
                return propertyValue;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read configuration: " + CONFIG_PATH, e);
        }

        throw new IllegalStateException(
                "Aladin TTB key is not configured. Set the " + ENV_KEY + " environment variable or provide "
                        + PROPERTY_KEY + " in " + CONFIG_PATH + ".");
    }

    public String getBookListJson(String queryType, int maxResults) {
        try {
            String urlStr = API_URL +
                    "?ttbkey=" + TTB_KEY +
                    "&QueryType=" + queryType +
                    "&MaxResults=" + maxResults +
                    "&start=1&SearchTarget=Book&output=js&Version=20131101";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                return sb.toString();
            } else {
                System.out.println("알라딘 API 요청 실패: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBookByIdJson(String bookId) {
        try {
            String urlStr = API_LOOKUP_URL +
                    "?ttbkey=" + TTB_KEY +
                    "&itemIdType=ItemId" +
                    "&ItemId=" + bookId +
                    "&output=js&Version=20131101";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                return sb.toString();
            } else {
                System.out.println("알라딘 API 요청 실패: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBookSearchJson(String queryType, String query, int categoryId, int start) {
        try {
            String urlStr = API_SEARCH_URL +
                    "?ttbkey=" + TTB_KEY +
                    "&QueryType=" + queryType +
                    "&Query=" + query +
                    "&MaxResults=5" +
                    "&start=" + start +
                    "&SearchTarget=Book&output=js&Version=20131101";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                return sb.toString();
            } else {
                System.out.println("알라딘 API 요청 실패: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBookDummyListJson(int categoryId, int Start) {
        try {
            String urlStr = API_URL +
                    "?ttbkey=" + TTB_KEY +
                    "&QueryType=Bestseller" +
                    "&CategoryId=" + categoryId +
                    "&MaxResults=50" +
                    "&Start=" + Start +
                    "&SearchTarget=Book" +
                    "&output=JS" +
                    "&Version=20131101";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                return sb.toString();
            } else {
                System.out.println("알라딘 API 요청 실패: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
