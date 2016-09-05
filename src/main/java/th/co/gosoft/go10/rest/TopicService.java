package th.co.gosoft.go10.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

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
import th.co.gosoft.go10.util.PropertiesUtils;

@Path("topic")
public class TopicService {
    private DateFormat postFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    private DateFormat getFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
    private String domain;
    
    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(TopicModel topicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> topicModel()");
        postFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        System.out.println("topic subject : "+topicModel.getSubject());
        System.out.println("topic content : "+topicModel.getContent());
        
        topicModel.setContent(deleteDomainImagePath(topicModel.getContent()));
        topicModel.setDate(postFormat.format(new Date()));
        
        Database db = CloudantClientUtils.getDBNewInstance();
        com.cloudant.client.api.model.Response response = db.save(topicModel);

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
        concatDomainImagePath(topicModelList);
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
        List<TopicModel> resultList = formatDate(topicModelList);
        
        System.out.println("getHotTopicList list size : "+resultList.size());
        return resultList;
    }
    
    public String deleteDomainImagePath(String content) {
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            domain = System.getenv("domain_image_path");
        } else {
            Properties prop = PropertiesUtils.getProperties();
            domain = prop.getProperty("domain_image_path");
        }
        
        String result = "";
        if(content.contains(domain)){
            String[] parts = content.split(Pattern.quote(domain));
            for (String subString : parts) {
                result += subString;
            }
        } else {
            result = content;
        }
        
        return result;
    }
    
    public void concatDomainImagePath(List<TopicModel> topicModelList) {
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            domain = System.getenv("domain_image_path");
        } else {
            Properties prop = PropertiesUtils.getProperties();
            domain = prop.getProperty("domain_image_path");
        }
        
        for (int i=0; i<topicModelList.size(); i++) {
            topicModelList.get(i).setContent(concatDomainImagePath(topicModelList.get(i).getContent()));
        }
        
    }

    private String concatDomainImagePath(String content) {
        String result = content;
        String regex = "<img src=\"";
        if(result.contains(regex)){
            int fromIndex = 0;
            while(fromIndex<result.length() && fromIndex>=0){
                fromIndex = result.indexOf(regex, fromIndex);
                if(fromIndex != -1){
                    fromIndex = fromIndex + 10;
                    StringBuilder stringBuilder = new StringBuilder(result);
                    stringBuilder.insert(fromIndex, domain);
                    result = stringBuilder.toString();
                }
                
            }
        }
        
        return result;
    }

    private String getTopicByIdJsonString(String topicId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$or\": [{\"_id\":\""+topicId+"\"}, {\"topicId\":\""+topicId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"]}");
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
