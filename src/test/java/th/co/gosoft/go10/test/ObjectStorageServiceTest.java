package th.co.gosoft.go10.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;

import th.co.gosoft.go10.util.ObjectStorageUtils;

public class ObjectStorageServiceTest {

    @Test
    public void connectionObjectStorageClient(){
        OSClient<OSClientV3> osClient = ObjectStorageUtils.connectObjectStorageService();
        assertEquals("https://identity.open.softlayer.com/v3", osClient.getEndpoint());
    }
    
}   
