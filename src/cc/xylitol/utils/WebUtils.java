package cc.xylitol.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtils {

    public static String get(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        responseBuilder.append(line);
                        isFirstLine = false;
                    } else {
                        responseBuilder.append("\n").append(line);
                    }
                }
                reader.close();

                return responseBuilder.toString();
            } else {
                throw new IOException("HTTP request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }
}