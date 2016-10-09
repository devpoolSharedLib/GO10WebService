package th.co.gosoft.go10.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


import org.apache.xmlbeans.impl.jam.visitor.PropertyInitializer;


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
