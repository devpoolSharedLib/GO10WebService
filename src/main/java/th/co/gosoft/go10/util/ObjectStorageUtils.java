package th.co.gosoft.go10.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

public class ObjectStorageUtils {
	
    private static final String USER_ID = PropertiesUtils.getProperties("obs_user_id");
    private static final String PASSWORD = PropertiesUtils.getProperties("obs_password");
    private static final String AUTH_URL = PropertiesUtils.getProperties("obs_auth_url");
    private static final String DOMAIN_NAME = PropertiesUtils.getProperties("obs_domain_name");
    private static final String PROJECT = PropertiesUtils.getProperties("obs_project");
	
    private static Identifier domainIdent;
    private static Identifier projectIdent;
    
    public static OSClient<OSClientV3> connectObjectStorageService(){
    	
        domainIdent = Identifier.byName(DOMAIN_NAME);
        projectIdent = Identifier.byName(PROJECT);  
        
        return OSFactory.builderV3()
                .endpoint(AUTH_URL+"/v3")
                .credentials(USER_ID, PASSWORD)
                .scopeToProject(projectIdent, domainIdent)
                .authenticate();
    }
    
}
