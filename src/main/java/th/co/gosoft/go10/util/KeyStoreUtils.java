package th.co.gosoft.go10.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KeyStoreUtils {

    public static void setKeyToCloudant(SecretKey secretKey, String id){
        Database db = CloudantClientUtils.getDBNewInstance();
        String keyString = parseSecretKeyToString(secretKey);
        JsonObject json = new JsonObject();
        json.addProperty("_id", id);
        json.addProperty("key-string", keyString);
        json.addProperty("type", "key");
        db.save(json);
    }
    
    public static SecretKey getKeyFromCloudant(String id){
        try{
            Database db = CloudantClientUtils.getDBNewInstance();
            InputStream inputStream = db.find(id);
            BufferedReader streamReader  = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null){
                responseStrBuilder.append(inputStr);
            }
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseStrBuilder.toString()).getAsJsonObject();
            String keyString = jsonObject.get("key-string").getAsString();
            return parseStringToSecretKey(keyString);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
       
    }
    
    public static String parseSecretKeyToString(SecretKey secretKey){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    public static SecretKey parseStringToSecretKey(String keyString){
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
    }
    
}
