package th.co.gosoft.go10.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotificationUtils {

    private static String serviceURL = "https://onesignal.com/api/v1/notifications";
            
    public static void sendMessagePushNotification(String message) {
        
        String strJsonBody = "{"
                +   "\"app_id\": \""+PropertiesUtils.getProperties("one_signal_app_id")+"\","
                +   "\"included_segments\": [\"All\"],"
                +   "\"contents\": {\"en\": \""+message+"\"}"
                + "}";
        
        HttpURLConnection connection = null;
        try {
            URL url = new URL(serviceURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Basic "+PropertiesUtils.getProperties("one_signal_api_key"));
            
            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            connection.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = connection.getResponseCode();
            System.out.println("http response : "+httpResponse+", "+connection.getResponseMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
