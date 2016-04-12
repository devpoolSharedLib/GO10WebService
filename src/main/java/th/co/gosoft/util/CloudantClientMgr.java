package th.co.gosoft.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CloudantClientMgr {

    private static CloudantClient cloudant = null;
    private static Database db = null;

    private static String databaseName = "go10_db";

    private static String url = "https://43d5a6b5-c085-4b0f-b609-ff96dd665d57e-bluemix:63f6fe8d0d3736c8a5b90c62ac80a716656eedb93f5be6b86866ab27223c13a3@43d5a6b5-c085-4b0f-b609-f96dd665d57e-bluemix.cloudant.com";
    private static String user = "43d5a6b5-c085-4b0f-b609-f96dd665d57e-bluemix";
    private static String password = "63f6fe8d0d3736c8a5b90c62ac80a716656eedb93f5be6b86866ab27223c13a3";

    private static void initClient() {
        if (cloudant == null) {
            synchronized (CloudantClientMgr.class) {
                if (cloudant != null) {
                    return;
                }
                cloudant = createClient();
                System.out.println("cloudant : " + cloudant.serverVersion());

            } // end synchronized
        }
    }

    private static CloudantClient createClient() {
        // VCAP_SERVICES is a system environment variable
        // Parse it to obtain the NoSQL DB connection info
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        String serviceName = null;

        if (VCAP_SERVICES != null) {
            // parse the VCAP JSON structure
            JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
            Entry<String, JsonElement> dbEntry = null;
            Set<Entry<String, JsonElement>> entries = obj.entrySet();
            // Look for the VCAP key that holds the cloudant no sql db
            // information
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
            serviceName = (String) dbEntry.getKey();
            System.out.println("Service Name - " + serviceName);

            obj = (JsonObject) obj.get("credentials");

            user = obj.get("username").getAsString();
            password = obj.get("password").getAsString();

        }

        try {
            CloudantClient client = ClientBuilder.url(new URL(url)).username(user).password(password).build();
            return client;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to connect to repository", e);
        }
    }

    public static Database getDB() {
        if (cloudant == null) {
            initClient();
        }

        if (db == null) {
            try {
                db = cloudant.database(databaseName, true);
            } catch (Exception e) {
                throw new RuntimeException("DB Not found", e);
            }
        }
        return db;
    }

    private CloudantClientMgr() {
    }
}