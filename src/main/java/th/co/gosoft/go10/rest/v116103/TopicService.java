package th.co.gosoft.go10.rest.v116103;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import th.co.gosoft.go10.model.LastLikeModel;
import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.model.ReadModel;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.RoomNotificationModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.ConcatDomainUtils;
import th.co.gosoft.go10.util.DateUtils;

@Path("v116103/topic")
public class TopicService {

    private static final String NOTIFICATION_MESSAGE = "You have new topic.";
    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
    
    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(LastTopicModel lastTopicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> topicModel()");
        System.out.println("topic subject : "+lastTopicModel.getSubject());
        System.out.println("topic content : "+lastTopicModel.getContent());
        System.out.println("topic type : "+lastTopicModel.getType());
        lastTopicModel.setContent(ConcatDomainUtils.deleteDomainImagePath(lastTopicModel.getContent()));
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        com.cloudant.client.api.model.Response response = null;
        if(lastTopicModel.getType().equals("host")) { 
            lastTopicModel.setDate(stampDate);
            lastTopicModel.setUpdateDate(stampDate);
            response = db.save(lastTopicModel);
            lastTopicModel.set_id(response.getId());
            lastTopicModel.set_rev(response.getRev());
            increaseReadCount(lastTopicModel, lastTopicModel.getEmpEmail());
            updateCountTopicInNotificationModel(lastTopicModel.getRoomId(), lastTopicModel.getEmpEmail());
        } else if(lastTopicModel.getType().equals("comment")) {
            lastTopicModel.setDate(stampDate);
            response = db.save(lastTopicModel);
            LastTopicModel hostTopicModel = db.find(LastTopicModel.class, lastTopicModel.getTopicId());
            hostTopicModel.setUpdateDate(stampDate);
            response = db.update(hostTopicModel);
        }
        updateTotalTopicInRoomModel(lastTopicModel.getRoomId());
//        PushNotificationUtils.sendMessagePushNotification(NOTIFICATION_MESSAGE);
        
        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : "+result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }
    
    @POST
    @Path("/newLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response newLike(LastLikeModel lastLikeModel){
        System.out.println("newLike() topic id : "+lastLikeModel.getTopicId());
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        LastTopicModel lastTopicModel = db.find(LastTopicModel.class, lastLikeModel.getTopicId());
        lastTopicModel.setCountLike(lastTopicModel.getCountLike() == null ? 1 : lastTopicModel.getCountLike()+1);
        System.out.println("count like : "+lastTopicModel.getCountLike());
        db.update(lastTopicModel);
        lastLikeModel.setDate(stampDate);
        db.save(lastLikeModel);
        System.out.println("POST Complete");
        return Response.status(201).build();
    }

    @PUT
    @Path("/updateLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateLike(LastLikeModel lastLikeModel){
        System.out.println("updateLike() topic id : " +lastLikeModel.getTopicId());
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        LastTopicModel lastTopicModel = db.find(LastTopicModel.class, lastLikeModel.getTopicId());
        lastTopicModel.setCountLike(lastTopicModel.getCountLike()+1);
        db.update(lastTopicModel);
        lastLikeModel.setDate(stampDate);
        db.update(lastLikeModel);
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @PUT
    @Path("/updateDisLike")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateDisLike(LastLikeModel lastLikeModel){
        System.out.println("updateDisLike() topic id : "+lastLikeModel.getTopicId());
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : "+stampDate);
        LastTopicModel lastTopicModel = db.find(LastTopicModel.class, lastLikeModel.getTopicId());
        lastTopicModel.setCountLike(lastTopicModel.getCountLike()-1);
        db.update(lastTopicModel);
        lastLikeModel.setDate(stampDate);
        db.update(lastLikeModel);
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @GET
    @Path("/checkLikeTopic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastLikeModel> checkLikeTopic(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail){
        List<LastLikeModel> lastLikeModelList = db.findByIndex(getLikeModelByTopicIdAndEmpEmailJsonString(topicId, empEmail), LastLikeModel.class);
        System.out.println("GET Complete");
        return lastLikeModelList;
    }
    
    @GET
    @Path("/gettopicbyid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getTopicById(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicById() //topcic id : "+topicId);
        List<LastTopicModel> topicModelList = db.findByIndex(getTopicByIdJsonString(topicId), LastTopicModel.class, new FindByIndexOptions()
                 .sort(new IndexField("date", SortOrder.asc)));
        increaseReadCount(topicModelList.get(0), empEmail);
        concatDomainImagePath(topicModelList);
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(topicModelList);
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gethottopiclist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getHotTopicList(@QueryParam("empEmail") String empEmail, @QueryParam("startDate") String startDate){
        System.out.println(">>>>>>>>>>>>>>>>>>> getHotTopicList() empEmail : "+empEmail+" startDate : "+startDate);
        List<RoomModel> roomModelList = db.findByIndex(getRoomJsonStringReadUser(empEmail), RoomModel.class, new FindByIndexOptions()
                .sort(new IndexField("_id", SortOrder.asc)).fields("_id").fields("_rev")
                .fields("name").fields("desc").fields("type"));
        
        List<LastTopicModel> lastTopicModelList = db.findByIndex(getHotTopicListJsonString(roomModelList), LastTopicModel.class, new FindByIndexOptions()
                 .sort(new IndexField("updateDate", SortOrder.desc)).limit(20));
        List<LastTopicModel> completeList = checkStatusRead(lastTopicModelList, empEmail, startDate);
        System.out.println("status read : "+completeList.get(0).getStatusRead());
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(completeList);
        System.out.println("getHotTopicList list size : "+resultList.size());
        return lastTopicModelList;
    }
    
    @GET
    @Path("/gettopiclistbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getTopicListByRoomId(@QueryParam("roomId") String roomId, @QueryParam("empEmail") String empEmail, @QueryParam("startDate") String startDate) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicListByRoomId() //room id : "+roomId+" empEmail : "+empEmail+" startDate : "+startDate);
        List<LastTopicModel> LastTopicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), LastTopicModel.class, new FindByIndexOptions()
             .sort(new IndexField("date", SortOrder.desc)));
        List<LastTopicModel> formatDateList = DateUtils.formatDBDateToClientDate(LastTopicModelList);
        List<LastTopicModel> roomRuleList = getRoomRuleToppic(roomId);
        List<LastTopicModel> completeList = checkStatusRead(formatDateList, empEmail, startDate);
        List<LastTopicModel> resultList = insertRoomRuleTopicAtZero(completeList, roomRuleList.get(0));
        System.out.println("size : "+resultList.size());
        updateCountTopicInNotificationModel(roomId, empEmail);
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/getroomruletoppic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getRoomRuleToppic(@QueryParam("roomId") String roomId) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getRoomRuleToppic()");
        List<LastTopicModel> topicModelList = db.findByIndex(getRoomRuleToppicJsonString(roomId), LastTopicModel.class, new FindByIndexOptions()
             .fields("_id").fields("_rev").fields("avatarName").fields("avatarPic").fields("subject")
             .fields("content").fields("date").fields("type").fields("roomId").fields("countLike"));
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(topicModelList);
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    private void updateCountTopicInNotificationModel(String roomId, String empEmail) {
        String stampDate = DateUtils.dbFormat.format(new Date());
        List<RoomNotificationModel> roomNotificationModelList = db.findByIndex(getRoomNotificationModelByRoomIdAndEmpEmail(roomId, empEmail), RoomNotificationModel.class);
        RoomNotificationModel roomNotificationModel = roomNotificationModelList.get(0);
        List<RoomModel> roomModelList = db.findByIndex(getRoomModelByRoomId(roomId), RoomModel.class);
        RoomModel roomModel = roomModelList.get(0);
        roomNotificationModel.setCountTopic(roomModel.getTotalTopic());
        roomNotificationModel.setUpdateDate(stampDate);
        db.update(roomNotificationModel);
    }
    
    private void updateTotalTopicInRoomModel(String roomId) {
        RoomModel roomModel = db.find(RoomModel.class, roomId);
        roomModel.setTotalTopic(roomModel.getTotalTopic() == null ? 1 : roomModel.getTotalTopic()+1);
        db.update(roomModel);
    }
    
    private List<LastTopicModel> checkStatusRead(List<LastTopicModel> lastTopicModelList, String empEmail, String startDate) {
        List<LastTopicModel> resultList = new ArrayList<>();
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
            if(DateUtils.isAfterDate(startDate, lastTopicModel.getDate())) {
                List<ReadModel> readModelList = db.findByIndex(getReadModelByEmpEmailString(lastTopicModel.get_id(), empEmail), ReadModel.class);
                if(readModelList == null || readModelList.isEmpty()) {
                    lastTopicModel.setStatusRead(false);
                } else {
                    lastTopicModel.setStatusRead(true);
                }
            } else {
                lastTopicModel.setStatusRead(true); 
            }
            resultList.add(lastTopicModel);
        }
        return resultList;
    }

    private void increaseReadCount(LastTopicModel lastTopicModel, String empEmail) {
        try {
            System.out.println(">>>>>>>>>>>>>>>>>> increaseReadCount() topicModelMap : "+lastTopicModel.get_id()+", empEmail : "+empEmail);
            stampDate = DateUtils.dbFormat.format(new Date());
            System.out.println("StampDate : "+stampDate);
            LastTopicModel localLastTopicModel = lastTopicModel;
            List<ReadModel> readModelList = db.findByIndex(getReadModelByEmpEmailString(localLastTopicModel.get_id(), empEmail), ReadModel.class);
            if (readModelList == null || readModelList.isEmpty()) {
                System.out.println("read model is null");
                ReadModel readModel = createReadModelMap(localLastTopicModel.get_id(), empEmail);
                db.save(readModel);
                localLastTopicModel.setCountRead(getCountRead(localLastTopicModel)+1);
                db.update(localLastTopicModel);
            } else {
                System.out.println("read model is not null");
                ReadModel readModel = readModelList.get(0);
                if(DateUtils.isNextDay(readModel.getDate(), stampDate)) {
                    readModel.setDate(stampDate);
                    db.update(readModel);
                    localLastTopicModel.setCountRead(getCountRead(localLastTopicModel)+1);
                    db.update(localLastTopicModel);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getRoomNotificationModelByRoomIdAndEmpEmail(String roomId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"roomNotification\"}, {\"roomId\":\""+roomId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("}}");
        return sb.toString();    
    }
    
    private String getRoomModelByRoomId(String roomId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"room\"}, {\"_id\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();    
    }
    
    private String getReadModelByEmpEmailString(String topicId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"read\"}, {\"topicId\":\""+topicId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"type\",\"date\"]}");
        return sb.toString();
    }
    
    private String getHotTopicListJsonString(List<RoomModel> roomModelList) {
        String roomIdString = generateRoomIdString(roomModelList);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"updateDate\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$exists\": false},");
        sb.append("\"$and\": [{\"type\":\"host\"},");
        sb.append("{\"roomId\":{\"$or\": ["+roomIdString+"]}}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\",\"updateDate\"]}");
        return sb.toString();
    }
    
    private String getTopicByIdJsonString(String topicId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$nor\": [{ \"type\": \"like\" }, { \"type\": \"read\" }],");
        sb.append("\"$or\": [{\"_id\":\""+topicId+"\"}, {\"topicId\":\""+topicId+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\",\"updateDate\"]}");
        return sb.toString();
    }
    
    private String getRoomJsonStringReadUser(String empEmail) {
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [");
        stingBuilder.append("{\"type\":\"room\"},");
        stingBuilder.append("{\"readUser\":{\"$elemMatch\": {");
        stingBuilder.append("\"$or\": [\"all\", \""+empEmail+"\"]");
        stingBuilder.append("}}}]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"]}");
        return stingBuilder.toString();
    }
    
    private String getTopicListByRoomIdJsonString(String roomId) {
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
    
    private String getLikeModelByTopicIdAndEmpEmailJsonString(String topicId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"like\"}, {\"topicId\":\""+topicId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"isLike\",\"type\"]}");
        return sb.toString();
    }
    
    private String getRoomRuleToppicJsonString(String roomId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$eq\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();
    }
    
    private int getCountRead(LastTopicModel lastTopicModel) {
        return lastTopicModel.getCountRead() == null ? 0 : lastTopicModel.getCountRead();
    }
    
    private ReadModel createReadModelMap(String topicId, String empEmail) {
        System.out.println("topicId : "+topicId+", empEmail : "+empEmail);
        ReadModel readModel = new ReadModel();
        readModel.setTopicId(topicId);
        readModel.setEmpEmail(empEmail);
        readModel.setType("read");
        readModel.setDate(stampDate);
        return readModel;
    }

    public void concatDomainImagePath(List<LastTopicModel> lastTopicModelList) {
        for (int i=0; i<lastTopicModelList.size(); i++) {
            String content = (String) lastTopicModelList.get(i).getContent();
            if(content != null){
                lastTopicModelList.get(i).setContent(ConcatDomainUtils.concatDomainImagePath(content));
            }
        }
    }
    
    private List<LastTopicModel> insertRoomRuleTopicAtZero(List<LastTopicModel> formatDateList, LastTopicModel roomRuleTopic) {
        List<LastTopicModel> resultList = new ArrayList<>();
        for (int i=0; i<=formatDateList.size(); i++) {
            if(i == 0) {
                resultList.add(roomRuleTopic);
            } else {
                resultList.add(formatDateList.get(i-1));
            }
        }
        return resultList;
    }
    
    public String generateRoomIdString(List<RoomModel> roomModelList) {
        StringBuilder stingBuilder = new StringBuilder();
        String prefix = "";
        for (RoomModel roomModel : roomModelList) {
            stingBuilder.append(prefix);
            prefix = ",";
            stingBuilder.append("\""+roomModel.get_id()+"\"");
        }
        return stingBuilder.toString();
    }
    
}
