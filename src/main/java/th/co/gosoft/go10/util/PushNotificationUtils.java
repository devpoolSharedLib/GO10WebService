package th.co.gosoft.go10.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

public class PushNotificationUtils {

    private static String serviceURL = "https://mobile.au-syd.bluemix.net/imfpush/v1/apps/3c5e9860-be2b-4276-a53b-b12f0d3db6bb/messages";
            
    public static void sendMessagePushNotification(String message) {
        JsonObject parent = new JsonObject();
        JsonObject child = new JsonObject();
        child.addProperty("alert", "Notification from Server");
        parent.add("message", child);
        
        HttpURLConnection connection = null;
        try {
            URL url = new URL(serviceURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("appSecret", "04e8c379-6cd7-4312-8ad5-3fe198897772");
            connection.setRequestProperty("Accept-Language", "en-US");
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(parent.toString());
            osw.close();
            System.out.println(connection.getResponseCode()+" : "+connection.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
