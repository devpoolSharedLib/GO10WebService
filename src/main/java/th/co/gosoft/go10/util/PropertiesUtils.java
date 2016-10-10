package th.co.gosoft.go10.util;

import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtils {

    
    private static Properties prop;

    public static Properties getProperties(){
        if(prop == null){
            try{
                prop = new Properties();
                InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");
                prop.load(input);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        return prop;
    }
    
}
