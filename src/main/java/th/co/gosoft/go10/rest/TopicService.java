package th.co.gosoft.go10.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;

import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.CloudantClientUtils;

@Path("topic")
public class TopicService {
    DateFormat postFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    DateFormat getFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(TopicModel topicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> topicModel()");
        postFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        System.out.println("topic subject : "+topicModel.getSubject());
        System.out.println("topic content : "+topicModel.getContent());
        
        topicModel.setDate(postFormat.format(new Date()));
        Database db = CloudantClientUtils.getDBNewInstance();

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
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicById() //topcic id : "+topicId);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<TopicModel> topicModelList = db.findByIndex(getTopicByIdJsonString(topicId), TopicModel.class, new FindByIndexOptions()
          		 .sort(new IndexField("date", SortOrder.asc)));
        List<TopicModel> resultList = formatDate(topicModelList);
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gettopiclistbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getTopicListByRoomId(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicListByRoomId() //room id : "+roomId);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<TopicModel> topicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), TopicModel.class, new FindByIndexOptions()
       		 .sort(new IndexField("date", SortOrder.desc)));
        List<TopicModel> resultList = formatDate(topicModelList);
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gethottopiclist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getHotTopicList(){
        System.out.println(">>>>>>>>>>>>>>>>>>> getHotTopicList()");
        Database db = CloudantClientUtils.getDBNewInstance();
        
        List<TopicModel> topicModelList = db.findByIndex(getHotTopicListJsonString(), TopicModel.class, new FindByIndexOptions()
        		 .sort(new IndexField("date", SortOrder.desc)));

        System.out.println("topicModelList");
        for(int i=0;i<topicModelList.size();i++){
        	System.out.println("datetime : " + topicModelList.get(i).getDate());
        }
        List<TopicModel> resultList = formatDate(topicModelList);
        System.out.println("resultList");
        for(int i=0;i<resultList.size();i++){
        	System.out.println("datetime : " + resultList.get(i).getDate());
        }
        System.out.println("getHotTopicList list size : "+resultList.size());
        return resultList;
    }
    
    private String getTopicByIdJsonString(String topicId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$or\": [{\"_id\":\""+topicId+"\"}, {\"topicId\":\""+topicId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"]}");
//        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"],"); 
//        sb.append("\"sort\": [ {\"date\": \"asc\"}]}");
        
        return sb.toString();
    }
    
    private String getTopicListByRoomIdJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"]}");
        
        //sb.append("\"sort\": [ {\"date\": \"desc\"}]}");
        
        return sb.toString();
    }
    
    private String getHotTopicListJsonString(){
    	
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"]}");

//        sb.append("\"sort\": [ {\"date\": \"desc\"}]}");
        
        System.out.println("string query " + sb.toString());
        return sb.toString();
    }
    
    public List<TopicModel> formatDate(List<TopicModel> topicModelList) {
       
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        
        for (TopicModel topicModel : topicModelList) {
            TopicModel resultModel = topicModel;
            resultModel.setDate(getFormat.format(parseStringToDate(topicModel.getDate())));
            resultList.add(topicModel);
        }
        return resultList;
    }
    
    private Date parseStringToDate(String dateString){
        try {
            return postFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
}
