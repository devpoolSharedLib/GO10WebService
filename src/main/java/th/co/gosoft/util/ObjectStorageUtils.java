package th.co.gosoft.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

public class ObjectStorageUtils {
    
    private static final String USER_ID = "bd077d18a3624ddba1f1817edf898448";
    private static final String PASSWORD = "k{S{W59JjWimdxII";
    private static final String AUTH_URL = "https://identity.open.softlayer.com"+"/v3";
    private static final String DOMAIN_NAME = "1053809";
    private static final String PROJECT = "object_storage_477f36e6_f772_48e4_9899_fd5c8c207029";
    private static Identifier domainIdent;
    private static Identifier projectIdent;
    private static OSClient osClient;
    
    static {
        domainIdent = Identifier.byName(DOMAIN_NAME);
        projectIdent = Identifier.byName(PROJECT);  
    }
    
    public static OSClient connectObjectStorageService(){
        if(osClient == null){
            osClient = OSFactory.builderV3()
                    .endpoint(AUTH_URL)
                    .credentials(USER_ID, PASSWORD)
                    .scopeToProject(projectIdent, domainIdent)
                    .authenticate();
        } 
        return osClient;
        
    }
    
    
    
}
