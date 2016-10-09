package th.co.gosoft.go10.util;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

public class ObjectStorageUtils {
	
    private static String USER_ID;
    private static String PASSWORD;
    private static String AUTH_URL;
    private static String DOMAIN_NAME;
    private static String PROJECT;
	
    private static Identifier domainIdent;
    private static Identifier projectIdent;
    
    public static OSClient<OSClientV3> connectObjectStorageService(){
    	
        initialVariable();
        
        domainIdent = Identifier.byName(DOMAIN_NAME);
        projectIdent = Identifier.byName(PROJECT);  
        
        return OSFactory.builderV3()
                .endpoint(AUTH_URL+"/v3")
                .credentials(USER_ID, PASSWORD)
                .scopeToProject(projectIdent, domainIdent)
                .authenticate();
    }
    
    
   
    
    private static void initialVariable(){
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            USER_ID = System.getenv("obs_user_id");
            PASSWORD = System.getenv("obs_password");
            AUTH_URL = System.getenv("obs_auth_url");
            DOMAIN_NAME = System.getenv("obs_domain_name");
            PROJECT = System.getenv("obs_project");
        } 
        else {
            Properties prop = PropertiesUtils.getProperties();
            USER_ID = prop.getProperty("obs_user_id");
            PASSWORD = prop.getProperty("obs_password");
            AUTH_URL = prop.getProperty("obs_auth_url");
            DOMAIN_NAME = prop.getProperty("obs_domain_name");
            PROJECT = prop.getProperty("obs_project");
        }
    }
    
}
