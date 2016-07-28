package th.co.gosoft.util;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

public class CloudantClientUtils {

    private static String DB_NAME = "go10_db";
    
    private static String USER = "2297d22e-ca8c-42a3-b3bd-024c9020ee8e-bluemix";
    private static String PASSWORD = "4a9ebcbedbb237e902167f14ec1c88208810ab3dd4d4d31ce5258411ff1ade61";
    
    public static Database getDBNewInstance() {
        CloudantClient cloudantClient = ClientBuilder.account(USER)
                .username(USER)
                .password(PASSWORD)
                .build();
        
        return cloudantClient.database(DB_NAME, false);
    }

}