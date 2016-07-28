package th.co.gosoft.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import th.co.gosoft.util.EncryptUtils;

public class EncryptUtilsTest {

    @Test
    public void testencodeAnddecode(){
        String testEmail = "manitkan@gosoft.co.th";
        String encode = EncryptUtils.encode(testEmail);
        String decode = EncryptUtils.decode(encode);
        assertEquals(testEmail, decode);
    }
    
    @Test
    public void testencodeAnddecode_2(){
        String testEmail = "jintanasog@gosoft.co.th";
        String encode = EncryptUtils.encode(testEmail);
        String decode = EncryptUtils.decode(encode);
        assertEquals(testEmail, decode);
    }
}
