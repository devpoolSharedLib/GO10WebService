package th.co.gosoft.go10.util;

import org.apache.commons.codec.binary.Base64;



public class EncryptUtils {
    
    private static String key = "devpool.gosoft";
    
    public static String encode(String gosoftEmail){
        String split[] = gosoftEmail.split("@gosoft.co.th");
        return base64encode(xorMessage(split[0], key));
    }
    
    public static String decode(String encodeString){
        return xorMessage(base64decode(encodeString), key)+"@gosoft.co.th";
    }

    private static String base64encode(String text) {
        return new String(Base64.encodeBase64(text.getBytes()));
    }

    private static String base64decode(String text) {
        return new String(Base64.decodeBase64(text));
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
