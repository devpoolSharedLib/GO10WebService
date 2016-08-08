package th.co.gosoft.go10.test;

import org.junit.Test;
import org.openstack4j.api.OSClient;

import th.co.gosoft.go10.util.ObjectStorageUtils;

public class ObjectStorageServiceTest {

    @Test
    public void connectionObjectStorageClient(){
        OSClient osClient = ObjectStorageUtils.connectObjectStorageService();
    }
    
}   
