package th.co.gosoft.test;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import th.co.gosoft.model.TopicModel;

public class TopicServiceTest {
    
//    @Test
    public void postTest() {

        try {

            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            Client client = Client.create(clientConfig);

            WebResource webResource = client.resource("http://localhost:9080/GO10WebService/api/topic/post");

            TopicModel topicModel = new TopicModel();
            topicModel.setSubject("นิว ทอปปิค");
            topicModel.setContent("คอนเทน");
//            topicModel.setSubject("subject");
//            topicModel.setContent("content");
            topicModel.setUser("Host_User 3");
            topicModel.setType("host 2");
            topicModel.setRoomId("room 2");

            ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, topicModel);
            
            System.out.println("Output from Server .... \n");
            String idTest = response.getEntity(String.class);
            System.out.println(">>>>>>>>>>>>>>>>>>.."+idTest);
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    @Test
    public void getTest() {

        try {
            String idTest = "c99831bfd3aa49539fa0fa56e0e381de";
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            Client client = Client.create(clientConfig);
            WebResource webResource = client.resource("http://localhost:9080/GO10WebService/api/topic/gettopicbyid?topicId="+idTest);
            ClientResponse response = webResource.accept("application/json").type("application/json").get(ClientResponse.class);
            
            System.out.println("Output from Server .... \n");
            
            List<TopicModel> topicModelList = response.getEntity(new GenericType<List<TopicModel>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(5, topicModelList.size());
            assertEquals("ab3d80df0fe1403cba4761a569ab2ab2", topicModelList.get(0).get_id());
            assertEquals("host", topicModelList.get(0).getType());
            assertEquals("36a47da9d9144889acc78e858a458004", topicModelList.get(1).get_id());
            assertEquals("comment", topicModelList.get(1).getType());
            assertEquals("642f910b64b3478684ff42dae6ac2fd7", topicModelList.get(2).get_id());
            assertEquals("comment", topicModelList.get(2).getType());
         

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
    
//    @Test
    public void generateDateTest() {
//        DateFormat df = DateFormat.getDateInstance(DateFormat., Locale.US);
//        String formattedDate = df.format(new Date());
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",  Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        Date date = new Date();
         //2014/08/06 15:59:48
        
        System.out.println(dateFormat.format(date));
        
    }
    
}
