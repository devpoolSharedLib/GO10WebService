package th.co.gosoft.test;

import org.junit.Test;
import org.openstack4j.api.OSClient;

import th.co.gosoft.util.ObjectStorageUtils;

public class ObjectStorageServiceTest {

    @Test
    public void connectionObjectStorageClient(){
        OSClient osClient = ObjectStorageUtils.connectObjectStorageService();
    }
    
}   
