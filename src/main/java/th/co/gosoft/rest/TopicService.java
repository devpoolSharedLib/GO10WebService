package th.co.gosoft.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;

import th.co.gosoft.model.TopicModel;
import th.co.gosoft.util.CloudantClientMgr;

@Path("topic")
public class TopicService {

    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTopic(TopicModel topicModel) {
        System.out.println("You are in post method");
        topicModel.setDate(new Date());
        Database db = CloudantClientMgr.getDB();

        com.cloudant.client.api.model.Response response = db.save(topicModel);
        System.out.println("You have inserted the document");

        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : "+result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }

    @GET
    @Path("/gettopicbyid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getTopicById(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> GET topcic id : "+topicId);
        Database db = CloudantClientMgr.getDB();
        List<TopicModel> topicModelList = db.findByIndex(getTopicByIdJsonString(topicId), TopicModel.class);
        System.out.println("GET Complete");
        return topicModelList;
    }
    
    @GET
    @Path("/gettopiclistbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getTopicListByRoomId(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> GET room id : "+roomId);
        Database db = CloudantClientMgr.getDB();
        List<TopicModel> topicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), TopicModel.class);
        System.out.println("size : "+topicModelList.size());
        System.out.println("GET Complete");
        return topicModelList;
    }
    
    @GET
    @Path("/gethottopiclist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getHotTopicList(){
        System.out.println(">>>>>>>>>>>>>>>>>>> GET");
        Database db = CloudantClientMgr.getDB();
        List<TopicModel> topicModelList = db.findByIndex(getHotTopicListJsonString(), TopicModel.class);
        System.out.println("GET Complete");
        return topicModelList;
    }
    
    private String getTopicByIdJsonString(String topicId){
        StringBuilder sb = new StringBuilder();
        sb.append("\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$or\": [{\"_id\":\""+topicId+"\"}, {\"topicId\":\""+topicId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"user\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"],");
        sb.append("\"sort\": [ {\"date\": \"asc\"}]");
        
        return sb.toString();
    }
    
    private String getTopicListByRoomIdJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"user\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"],");
        sb.append("\"sort\": [ {\"date\": \"desc\"}]");
        
        return sb.toString();
    }
    
    private String getHotTopicListJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"user\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"],");
        sb.append("\"sort\": [ {\"date\": \"desc\"}]");
        
        return sb.toString();
    }
    
    
}
