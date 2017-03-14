package th.co.gosoft.go10.rest;

import java.util.ArrayList;
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
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.model.ReadModel;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.RoomNotificationModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.ConcatDomainUtils;
import th.co.gosoft.go10.util.DateUtils;
import th.co.gosoft.go10.util.PushNotificationUtils;
import th.co.gosoft.go10.util.StringUtils;

@Path("server/topic")
public class TopicService {
    
	private static final String NOTIFICATION_MESSAGE = "You have new topic.";
    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
    
    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createTopic(LastTopicModel lastTopicModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> createTopic()");
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
            updateTotalTopicInRoomModel(lastTopicModel.getRoomId());
            increaseReadCount(lastTopicModel, lastTopicModel.getEmpEmail());
            PushNotificationUtils.sendMessagePushNotification(NOTIFICATION_MESSAGE);
        } else if(lastTopicModel.getType().equals("comment")) {
            lastTopicModel.setDate(stampDate);
            response = db.save(lastTopicModel);
            LastTopicModel hostTopicModel = db.find(LastTopicModel.class, lastTopicModel.getTopicId());
            hostTopicModel.setUpdateDate(stampDate);
            db.update(hostTopicModel);
        }
        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : "+result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }
    
    @GET
    @Path("/getreadtopic")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<ReadModel> getReadTopic(){
        System.out.println(">>>>>>>>>>>>>>>>>>> getReadTopic()");
        List<ReadModel> readModelList = db.findByIndex(getReadModelAll(), ReadModel.class);
        System.out.println("size Read Model : "+readModelList.size());
        System.out.println("GET Complete");
        return readModelList;
    }
    
    @GET
    @Path("/getreadtopicbytopicid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<ReadModel> getReadTopicbyTopicId(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> countReadTopic()");
        List<ReadModel> readModelList = db.findByIndex(getReadModelByTopicId(topicId), ReadModel.class);
        System.out.println("size Read Model By TopicId : "+readModelList.size());
        System.out.println("GET Complete");
        return readModelList;
    }
    
    @GET
    @Path("/getreadtopicbyroomid")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public int getReadTopicbyRoomId(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getReadTopicbyRoomId()");
        List<LastTopicModel> lastTopicModelList = db.findByIndex(getAllTopicListByRoomIdJsonString(roomId), LastTopicModel.class);
        System.out.println("size Read Model By TopicId : "+lastTopicModelList.size());
        List<ReadModel> readModelList;
        int countReadTopic = 0;
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
        	 readModelList = db.findByIndex(getReadModelByTopicId(lastTopicModel.get_id()), ReadModel.class);
        	 countReadTopic += readModelList.size();
		}
        System.out.println("size Read Model By RoomId : "+countReadTopic);
        System.out.println("GET Complete");
        return countReadTopic;
    }
    
    @GET
    @Path("/getnopintoppiclistbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getNoPinToppicListbyRoomId(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicListByRoomId() //room id : "+roomId);
        List<LastTopicModel> lastTopicModelList = db.findByIndex(getTopicListByRoomIdJsonString(roomId), LastTopicModel.class, new FindByIndexOptions()
             .sort(new IndexField("date", SortOrder.desc)));
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(lastTopicModelList);
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/getroomruletoppicbyroom")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getRoomRuleToppic(@QueryParam("roomId") String roomId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getRoomRuleToppic()  //room id : "+roomId);
        List<LastTopicModel> lastTopicModelList = db.findByIndex(getRoomRuleToppicJsonString(roomId), LastTopicModel.class, new FindByIndexOptions()
             .sort(new IndexField("pin", SortOrder.asc)));
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(lastTopicModelList);
        System.out.println("size : "+resultList.size());
        System.out.println("GET Complete");
        return resultList;
    }

<<<<<<< HEAD
    
    @POST
    @Path("/savePinTopic")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response savePinTopic(List<LastTopicModel> lastTopicModelList) {
        System.out.println(">>>>>>>>>>>>>>>>>>> savePinTopic()");
        String topicIdString = StringUtils.generateTopicIdString(lastTopicModelList);
        return null;
    }
=======
>>>>>>> fb114dc5003557c181bab0b8b62922ddb6989ae2
    
    private String increaseReadCount(LastTopicModel lastTopicModel, String empEmail) {
        try {
            String rev = null;
            System.out.println(">>>>>>>>>>>>>>>>>> increaseReadCount() topicModelMap : "+lastTopicModel.get_id()+", empEmail : "+empEmail);
            stampDate = DateUtils.dbFormat.format(new Date());
            System.out.println("StampDate : "+stampDate);
            LastTopicModel localLastTopicModel = lastTopicModel;
            List<ReadModel> readModelList = db.findByIndex(getReadModelByTopicIdAndEmpEmailString(localLastTopicModel.get_id(), empEmail), ReadModel.class);
            if (readModelList == null || readModelList.isEmpty()) {
                System.out.println("read model is null");
                ReadModel readModel = createReadModelMap(localLastTopicModel.get_id(), empEmail);
                db.save(readModel);
                localLastTopicModel.setCountRead(getCountRead(localLastTopicModel)+1);
                rev = db.update(localLastTopicModel).getRev();
                plusCountTopicInNotificationModel(lastTopicModel.getRoomId(), empEmail);
            } else {
                System.out.println("read model is not null");
                ReadModel readModel = readModelList.get(0);
                if(DateUtils.isNextDay(readModel.getDate(), stampDate)) {
                    readModel.setDate(stampDate);
                    db.update(readModel);
                    localLastTopicModel.setCountRead(getCountRead(localLastTopicModel)+1);
                    rev = db.update(localLastTopicModel).getRev();
                }
            }
            return rev;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
    
    private void updateTotalTopicInRoomModel(String roomId) {
        RoomModel roomModel = db.find(RoomModel.class, roomId);
        roomModel.setTotalTopic(roomModel.getTotalTopic() == null ? 1 : roomModel.getTotalTopic()+1);
        db.update(roomModel);
    }
    
    private int getCountRead(LastTopicModel lastTopicModel) {
        return lastTopicModel.getCountRead() == null ? 0 : lastTopicModel.getCountRead();
    }
    
    public void concatDomainImagePath(List<LastTopicModel> lastTopicModelList) {
        for (int i=0; i<lastTopicModelList.size(); i++) {
            String content = (String) lastTopicModelList.get(i).getContent();
            if(content != null){
                lastTopicModelList.get(i).setContent(ConcatDomainUtils.concatDomainImagePath(content));
            }
        }
    }

    private void plusCountTopicInNotificationModel(String roomId, String empEmail) {
        String stampDate = DateUtils.dbFormat.format(new Date());
        List<RoomNotificationModel> roomNotificationModelList = db.findByIndex(getRoomNotificationModelByRoomIdAndEmpEmail(roomId, empEmail), RoomNotificationModel.class);
        RoomNotificationModel roomNotificationModel = roomNotificationModelList.get(0);
        roomNotificationModel.setCountTopic(roomNotificationModel.getCountTopic() + 1);
        roomNotificationModel.setUpdateDate(stampDate);
        System.out.println("room noti countTopic : "+roomNotificationModel.getCountTopic());
        db.update(roomNotificationModel);
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
    
    private String getAllTopicListByRoomIdJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"date\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();
    }
    
    private String getRoomRuleToppicJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$gte\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();
    }
    
    private String getReadModelByTopicIdAndEmpEmailString(String topicId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"read\"}, {\"topicId\":\""+topicId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"type\",\"date\"]}");
        return sb.toString();
    }
    
    private String getRoomNotificationModelByRoomIdAndEmpEmail(String roomId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"roomNotification\"}, {\"roomId\":\""+roomId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("}}");
        System.out.println("query string : "+sb.toString());
        return sb.toString();    
    }

    private String getReadModelByTopicId(String topicId) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"selector\": {");
		sb.append("\"_id\": {\"$gt\": 0},");
		sb.append("\"$and\": [{\"type\":\"read\"}, {\"topicId\":\"" + topicId + "\"}]");
		sb.append("},");
		sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"type\",\"date\"]}");
		return sb.toString();
	}
    
    private String getReadModelAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"selector\": {");
		sb.append("\"_id\": {\"$gt\": 0},");
		sb.append("\"$and\": [{\"type\":\"read\"}]");
		sb.append("},");
		sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"type\",\"date\"]}");
		return sb.toString();
	}
    
    
}
