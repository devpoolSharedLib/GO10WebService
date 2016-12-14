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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;

import th.co.gosoft.go10.model.NewLikeModel;
import th.co.gosoft.go10.model.NewRoomRuleTopicModel;
import th.co.gosoft.go10.model.NewTopicModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.ConcatDomainUtils;

@Path("newtopic")
public class TopicServiceV1 {
    
    private static DateFormat postFormat = createSimpleDateFormat("yyyy/MM/dd HH:mm:ss", "GMT+7");
    private static DateFormat getFormat = createSimpleDateFormat("dd/MM/yyyy HH:mm:ss", "GMT+7");
    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
    
    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(NewTopicModel newTopicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> topicModel()");
        postFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        
        System.out.println("topic subject : "+newTopicModel.getSubject());
        System.out.println("topic content : "+newTopicModel.getContent());
        System.out.println("topic type : "+newTopicModel.getType());
        
        newTopicModel.setContent(ConcatDomainUtils.deleteDomainImagePath(newTopicModel.getContent()));
        
        stampDate = postFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        
        com.cloudant.client.api.model.Response response = null;
        
        if(newTopicModel.getType().equals("host")){
        	newTopicModel.setDate(stampDate);
        	newTopicModel.setUpdateDate(stampDate);
            response = db.save(newTopicModel);
        }else if(newTopicModel.getType().equals("comment")){
        	newTopicModel.setDate(stampDate);
        	response = db.save(newTopicModel);
        	NewTopicModel hostTopic = db.find(NewTopicModel.class, newTopicModel.getTopicId());
        	hostTopic.setUpdateDate(stampDate);
        	if("Admin".equals(hostTopic.getAvatarName())){
                NewRoomRuleTopicModel newRoomRuleTopicModel = parseToRoomRuleTopicModel(hostTopic);
                newRoomRuleTopicModel.setPin(0);
                response = db.update(newRoomRuleTopicModel);
            } else {
                response = db.update(hostTopic);
            }
        }
        
        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : "+result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }
    
    @POST
    @Path("/newLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response newLike(NewLikeModel newLikeModel){
        System.out.println("newLike() topic id : "+newLikeModel.getTopicId());
        stampDate = postFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        NewTopicModel newHostTopic = db.find(NewTopicModel.class, newLikeModel.getTopicId());
        newHostTopic.setCountLike(newHostTopic.getCountLike()+1);
        if("Admin".equals(newHostTopic.getAvatarName())){
            NewRoomRuleTopicModel newRoomRuleTopicModel = parseToRoomRuleTopicModel(newHostTopic);
            newRoomRuleTopicModel.setPin(0);
            db.update(newRoomRuleTopicModel);
        } else {
            db.update(newHostTopic);
        }

        newLikeModel.setDate(stampDate);
        db.save(newLikeModel);
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }

    @PUT
    @Path("/updateLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateLike(NewLikeModel newLikeModel){
        System.out.println("updateLike() topic id : " +newLikeModel.getTopicId());
        stampDate = postFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        NewTopicModel newHostTopic = db.find(NewTopicModel.class, newLikeModel.getTopicId());
        newHostTopic.setCountLike(newHostTopic.getCountLike()+1);
        if("Admin".equals(newHostTopic.getAvatarName())){
            NewRoomRuleTopicModel newRoomRuleTopicModel = parseToRoomRuleTopicModel(newHostTopic);
            newRoomRuleTopicModel.setPin(0);
            db.update(newRoomRuleTopicModel);
        } else {
            db.update(newHostTopic);
        }
        
        newLikeModel.setDate(stampDate);
        db.update(newLikeModel);
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @PUT
    @Path("/updateDisLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateDisLike(NewLikeModel newLikeModel){
        System.out.println("updateDisLike() topic id : "+newLikeModel.getTopicId());
        stampDate = postFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        NewTopicModel newHostTopic = db.find(NewTopicModel.class, newLikeModel.getTopicId());
        newHostTopic.setCountLike(newHostTopic.getCountLike()-1);
        if("Admin".equals(newHostTopic.getAvatarName())){
            NewRoomRuleTopicModel newRoomRuleTopicModel = parseToRoomRuleTopicModel(newHostTopic);
            newRoomRuleTopicModel.setPin(0);
            db.update(newRoomRuleTopicModel);
        } else {
            db.update(newHostTopic);
        }
        
        newLikeModel.setDate(stampDate);
        db.update(newLikeModel);
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @GET
    @Path("/checkLikeTopic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<NewLikeModel> checkLikeTopic(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail){
        List<NewLikeModel> likeModelList = db.findByIndex(getLikeModelByTopicIdAndEmpEmailJsonString(topicId, empEmail), NewLikeModel.class);
        System.out.println("GET Complete");
        return likeModelList;
    }
    
    @GET
    @Path("/gettopicbyid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<NewTopicModel> getTopicById(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicById() //topcic id : "+topicId);
        List<NewTopicModel> newTopicModelList = db.findByIndex(getTopicByIdJsonString(topicId), NewTopicModel.class, new FindByIndexOptions()
          		 .sort(new IndexField("date", SortOrder.asc)));
        concatDomainImagePath(newTopicModelList);
        List<NewTopicModel> resultList = formatDate(newTopicModelList);
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gettopiclistbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<NewTopicModel> getTopicListByRoomId(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicListByRoomId() //room id : "+roomId);
        
        List<NewTopicModel> newTopicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), NewTopicModel.class, new FindByIndexOptions()
       		 .sort(new IndexField("date", SortOrder.desc)));
        List<NewTopicModel> formatDateList = formatDate(newTopicModelList);
        List<NewTopicModel> roomRuleList = getRoomRuleToppic(roomId);
        List<NewTopicModel> resultList = insertRoomRuleTopicAtZero(formatDateList, roomRuleList.get(0));
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/getroomruletoppic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<NewTopicModel> getRoomRuleToppic(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getRoomRuleToppic()");
        List<NewTopicModel> newTopicModelList = db.findByIndex(getRoomRuleToppicJsonString(roomId), NewTopicModel.class, new FindByIndexOptions()
             .fields("_id").fields("_rev").fields("avatarName").fields("avatarPic").fields("subject")
             .fields("content").fields("date").fields("type").fields("roomId").fields("countLike"));
        
        List<NewTopicModel> resultList = formatDate(newTopicModelList);
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gethottopiclist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<NewTopicModel> getNewUpdateTopicList(){
        System.out.println(">>>>>>>>>>>>>>>>>>> getnewHotTopicList()");
        List<NewTopicModel> newTopicModelList = db.findByIndex(getHotTopicListJsonString(), NewTopicModel.class, new FindByIndexOptions()
                 .sort(new IndexField("updateDate", SortOrder.desc)).limit(20));
        List<NewTopicModel> resultList = formatDate(newTopicModelList);
        System.out.println("getNewUpdateTopicList list size : "+resultList.size());
        return resultList;
    }
  
    private List<NewTopicModel> insertRoomRuleTopicAtZero(List<NewTopicModel> formatDateList, NewTopicModel roomRuleTopic) {
        List<NewTopicModel> resultList = new ArrayList<>();
        for (int i=0; i<=formatDateList.size(); i++) {
            if(i == 0) {
                resultList.add(roomRuleTopic);
            } else {
                resultList.add(formatDateList.get(i-1));
            }
        }
        return resultList;
    }
    
    private static DateFormat createSimpleDateFormat(String formatString, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat;
    }
    
    public void concatDomainImagePath(List<NewTopicModel> newTopicModelList) {
        for (int i=0; i<newTopicModelList.size(); i++) {
        	newTopicModelList.get(i).setContent(ConcatDomainUtils.concatDomainImagePath(newTopicModelList.get(i).getContent()));
        }
    }

    private String getTopicByIdJsonString(String topicId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$nor\": [{ \"type\": \"like\" }],");
        sb.append("\"$or\": [{\"_id\":\""+topicId+"\"}, {\"topicId\":\""+topicId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\",\"updateDate\"]}");
        return sb.toString();
    }
    
    private String getTopicListByRoomIdJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$exists\": false},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\"]}");
        return sb.toString();
    }
    
    private String getRoomRuleToppicJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$eq\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();
    }
    
    private String getHotTopicListJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"updateDate\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$exists\": false},");
        sb.append("\"$and\": [{\"type\":\"host\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\",\"updateDate\"]}");
        return sb.toString();
    }
    
    private String getLikeModelByTopicIdAndEmpEmailJsonString(String topicId, String empEmail){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"like\"}, {\"topicId\":\""+topicId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"isLike\",\"type\"]}");
        return sb.toString();
    }
    
    public List<NewTopicModel> formatDate(List<NewTopicModel> newTopicModelList) {
        List<NewTopicModel> resultList = new ArrayList<NewTopicModel>();
        for (NewTopicModel newTopicModel : newTopicModelList) {
        	NewTopicModel resultModel = newTopicModel;
            resultModel.setDate(getFormat.format(parseStringToDate(newTopicModel.getDate())));
            resultList.add(resultModel);
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
    
    private NewRoomRuleTopicModel parseToRoomRuleTopicModel(NewTopicModel newHostTopic) {
    	NewRoomRuleTopicModel newRoomRuleTopicModel = new NewRoomRuleTopicModel();
    	newRoomRuleTopicModel.set_id(newHostTopic.get_id());
        newRoomRuleTopicModel.set_rev(newHostTopic.get_rev());
        newRoomRuleTopicModel.setAvatarName(newHostTopic.getAvatarName());
        newRoomRuleTopicModel.setAvatarPic(newHostTopic.getAvatarPic());
        newRoomRuleTopicModel.setContent(newHostTopic.getContent());
        newRoomRuleTopicModel.setCountLike(newHostTopic.getCountLike());
        newRoomRuleTopicModel.setDate(newHostTopic.getDate());
        newRoomRuleTopicModel.setEmpEmail(newHostTopic.getEmpEmail());
        newRoomRuleTopicModel.setRoomId(newHostTopic.getRoomId());
        newRoomRuleTopicModel.setSubject(newHostTopic.getSubject());
        newRoomRuleTopicModel.setTopicId(newHostTopic.getTopicId());
        newRoomRuleTopicModel.setType(newHostTopic.getType());
        newRoomRuleTopicModel.setUpdateDate(newHostTopic.getUpdateDate());
        return newRoomRuleTopicModel;
    }
    

    
}
