package th.co.gosoft.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sun.jersey.core.util.Base64;

public class EncryptUtils {

    private static final String DEFAULT_ENCODING = "UTF-8"; 
    private static String key = "devpool.gosoft";
    
    public static String encode(String gosoftEmail){
        String split[] = gosoftEmail.split("@gosoft.co.th");
        return base64encode(xorMessage(split[0], key));
    }
    
    public static String decode(String encodeString){
        return xorMessage(base64decode(encodeString), key)+"@gosoft.co.th";
    }

    private static String base64encode(String text) {
        try {
            return new String(Base64.encode(text.getBytes(DEFAULT_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String base64decode(String text) {
        try {
            return new String(Base64.decode(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }
    
    private static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) return null;

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }

            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }
}
