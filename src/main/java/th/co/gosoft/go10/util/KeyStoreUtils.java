package th.co.gosoft.go10.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

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
    
    public static boolean authenPassword(byte[] queryPassword, String inputPassword) {
        SecretKey secretKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        Cipher desCipher;
        try {
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] textDecrypted = desCipher.doFinal(queryPassword);
            String passDecryptedString = new String(textDecrypted);
            return passDecryptedString.equals(inputPassword);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String parseSecretKeyToString(SecretKey secretKey){
        return new String(Base64.encodeBase64(secretKey.getEncoded()));
    }
    
    public static SecretKey parseStringToSecretKey(String keyString){
        byte[] decodedKey = Base64.decodeBase64(keyString.getBytes());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
    }
    
}
