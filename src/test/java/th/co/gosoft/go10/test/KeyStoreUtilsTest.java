package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import th.co.gosoft.go10.util.KeyStoreUtils;

public class KeyStoreUtilsTest {

    static KeyGenerator keygenerator;
    static SecretKey secretKey;
    
    @BeforeClass
    public static void initialize() {
        try {
            keygenerator = KeyGenerator.getInstance("DES");
            secretKey = keygenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void parseSecretTransformationStringTest() {
        SecretKey key = keygenerator.generateKey();
        String keyString = KeyStoreUtils.parseSecretKeyToString(key);
        SecretKey actualKey = KeyStoreUtils.parseStringToSecretKey(keyString);
        assertEquals(key, actualKey);
    }
    
    @Test
    @Ignore
    public void setKeyToCloudantTest(){
        KeyStoreUtils.setKeyToCloudant(secretKey, "password-key");
        SecretKey actualKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        assertEquals(secretKey, actualKey);
    }
    
}
