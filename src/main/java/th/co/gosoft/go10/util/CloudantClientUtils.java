package th.co.gosoft.go10.util;

import java.util.Map.Entry;
import java.util.Set;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CloudantClientUtils {

    private static String DB_NAME = "go10_db";
    
    private static String user = null;
    private static String password = null;

    private static CloudantClient createClient() {
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

        if (VCAP_SERVICES != null) {
            JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
            Entry<String, JsonElement> dbEntry = null;
            Set<Entry<String, JsonElement>> entries = obj.entrySet();
            
            for (Entry<String, JsonElement> eachEntry : entries) {
                if (eachEntry.getKey().toLowerCase().contains("cloudant")) {
                    dbEntry = eachEntry;
                    break;
                }
            }
            
            if (dbEntry == null) {
                throw new RuntimeException("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable");
            }

            obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
            obj = (JsonObject) obj.get("credentials");

            user = obj.get("username").getAsString();
            password = obj.get("password").getAsString();

        } else {
            try {
//                Properties prop = PropertiesUtils.getProperties();
                user = PropertiesUtils.getProperties("cloudant_user");
                password = PropertiesUtils.getProperties("cloudant_password");
            } catch (Exception e){
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        try {
            CloudantClient client = ClientBuilder.account(user)
                    .username(user)
                    .password(password)
                    .build();
            return client;
        } catch (CouchDbException e) {
            throw new RuntimeException("Unable to connect to repository", e);
        }
    }
    
    public static Database getDBNewInstance() {
        CloudantClient cloudantClient = createClient();
        
        return cloudantClient.database(DB_NAME, false);
    }

}