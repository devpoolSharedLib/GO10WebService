package th.co.gosoft.go10.util;

import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtils {

    
    private static final Properties CONFIG_PROPERTIES = initialInstance();

    private static Properties initialInstance(){
        try{
            Properties prop = new Properties();
            InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");
            if(input != null){
                prop.load(input);
            }
            return prop;
        } catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String getProperties(String keyName){
        String value = System.getenv(keyName);
        if(value == null || "".equals(value)){
            value = CONFIG_PROPERTIES.getProperty(keyName);
        } 
        return value;
    }
    
}
