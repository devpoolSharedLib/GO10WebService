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

import th.co.gosoft.go10.model.LikeModel;
import th.co.gosoft.go10.model.RoomRuleTopicModel;
import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.ConcatDomainUtils;

@Path("topic")
public class TopicService {
    
    private static DateFormat postFormat = createSimpleDateFormat("yyyy/MM/dd HH:mm:ss", "GMT+7");
    private static DateFormat getFormat = createSimpleDateFormat("dd/MM/yyyy HH:mm:ss", "GMT+7");
    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
       
    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(TopicModel topicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> topicModel()");
        postFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        
        System.out.println("topic subject : "+topicModel.getSubject());
        System.out.println("topic content : "+topicModel.getContent());
        topicModel.setContent(ConcatDomainUtils.deleteDomainImagePath(topicModel.getContent()));
        
        stampDate = postFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        com.cloudant.client.api.model.Response response = null;
        topicModel.setDate(stampDate);
        response = db.save(topicModel);
        
        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : "+result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }
    
    @POST
    @Path("/newLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response newLike(LikeModel likeModel){
        System.out.println("newLike() topic id : "+likeModel.getTopicId());
        TopicModel hostTopic = db.find(TopicModel.class, likeModel.getTopicId());
        hostTopic.setCountLike(hostTopic.getCountLike()+1);
        if("Admin".equals(hostTopic.getAvatarName())){
            RoomRuleTopicModel roomRuleTopicModel = parseToRoomRuleTopicModel(hostTopic);
            roomRuleTopicModel.setPin(0);
            db.update(roomRuleTopicModel);
        } else {
            db.update(hostTopic);
        }

        db.save(likeModel);
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @PUT
    @Path("/updateLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateLike(LikeModel likeModel){
        System.out.println("updateLike() topic id : " +likeModel.getTopicId());

        TopicModel hostTopic = db.find(TopicModel.class, likeModel.getTopicId());
        hostTopic.setCountLike(hostTopic.getCountLike()+1);
        if("Admin".equals(hostTopic.getAvatarName())){
            RoomRuleTopicModel roomRuleTopicModel = parseToRoomRuleTopicModel(hostTopic);
            roomRuleTopicModel.setPin(0);
            db.update(roomRuleTopicModel);
        } else {
            db.update(hostTopic);
        }

        db.update(likeModel);        
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @PUT
    @Path("/updateDisLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateDisLike(LikeModel likeModel){
        System.out.println("updateDisLike() topic id : "+likeModel.getTopicId());

        TopicModel hostTopic = db.find(TopicModel.class, likeModel.getTopicId());
        hostTopic.setCountLike(hostTopic.getCountLike()-1);
        if("Admin".equals(hostTopic.getAvatarName())){
            RoomRuleTopicModel roomRuleTopicModel = parseToRoomRuleTopicModel(hostTopic);
            roomRuleTopicModel.setPin(0);
            db.update(roomRuleTopicModel);
        } else {
            db.update(hostTopic);
        }
        
        db.update(likeModel);
        
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @GET
    @Path("/checkLikeTopic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LikeModel> checkLikeTopic(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail){
        List<LikeModel> likeModelList = db.findByIndex(getLikeModelByTopicIdAndEmpEmailJsonString(topicId, empEmail), LikeModel.class);
        System.out.println("GET Complete");
        return likeModelList;
    }
    
    @GET
    @Path("/gettopicbyid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getTopicById(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicById() //topcic id : "+topicId);
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
        
        List<TopicModel> topicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), TopicModel.class, new FindByIndexOptions()
       		 .sort(new IndexField("date", SortOrder.desc)));
        List<TopicModel> formatDateList = formatDate(topicModelList);
        List<TopicModel> roomRuleList = getRoomRuleToppic(roomId);
        List<TopicModel> resultList = insertRoomRuleTopicAtZero(formatDateList, roomRuleList.get(0));
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    private List<TopicModel> insertRoomRuleTopicAtZero(List<TopicModel> formatDateList, TopicModel roomRuleTopic) {
        List<TopicModel> resultList = new ArrayList<>();
        for (int i=0; i<=formatDateList.size(); i++) {
            if(i == 0) {
                resultList.add(roomRuleTopic);
            } else {
                resultList.add(formatDateList.get(i-1));
            }
        }
        return resultList;
    }

    @GET
    @Path("/getroomruletoppic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<TopicModel> getRoomRuleToppic(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getRoomRuleToppic()");
        List<TopicModel> topicModelList = db.findByIndex(getRoomRuleToppicJsonString(roomId), TopicModel.class, new FindByIndexOptions()
             .fields("_id").fields("_rev").fields("avatarName").fields("avatarPic").fields("subject")
             .fields("content").fields("date").fields("type").fields("roomId").fields("countLike"));
        
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
        List<TopicModel> topicModelList = db.findByIndex(getHotTopicListJsonString(), TopicModel.class, new FindByIndexOptions()
              .sort(new IndexField("date", SortOrder.desc)).limit(20));
        List<TopicModel> resultList = formatDate(topicModelList);
        System.out.println("getHotTopicList list size : "+resultList.size());
        return resultList;
    }
    
    private static DateFormat createSimpleDateFormat(String formatString, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat;
    }
    
    public void concatDomainImagePath(List<TopicModel> topicModelList) {
        for (int i=0; i<topicModelList.size(); i++) {
            topicModelList.get(i).setContent(ConcatDomainUtils.concatDomainImagePath(topicModelList.get(i).getContent()));
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
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\"]}");
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
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$exists\": false},");
        sb.append("\"$and\": [{\"type\":\"host\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\"]}");
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
    
    public List<TopicModel> formatDate(List<TopicModel> topicModelList) {
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        for (TopicModel topicModel : topicModelList) {
            TopicModel resultModel = topicModel;
            resultModel.setDate(getFormat.format(parseStringToDate(topicModel.getDate())));
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
    
    private RoomRuleTopicModel parseToRoomRuleTopicModel(TopicModel hostTopic) {
        RoomRuleTopicModel roomRuleTopicModel = new RoomRuleTopicModel();
        roomRuleTopicModel.set_id(hostTopic.get_id());
        roomRuleTopicModel.set_rev(hostTopic.get_rev());
        roomRuleTopicModel.setAvatarName(hostTopic.getAvatarName());
        roomRuleTopicModel.setAvatarPic(hostTopic.getAvatarPic());
        roomRuleTopicModel.setContent(hostTopic.getContent());
        roomRuleTopicModel.setCountLike(hostTopic.getCountLike());
        roomRuleTopicModel.setDate(hostTopic.getDate());
        roomRuleTopicModel.setEmpEmail(hostTopic.getEmpEmail());
        roomRuleTopicModel.setRoomId(hostTopic.getRoomId());
        roomRuleTopicModel.setSubject(hostTopic.getSubject());
        roomRuleTopicModel.setTopicId(hostTopic.getTopicId());
        roomRuleTopicModel.setType(hostTopic.getType());
        return roomRuleTopicModel;
    }
    
}
