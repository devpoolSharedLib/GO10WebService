package th.co.gosoft.go10.rest.v130120;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.RoomNotificationModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.model.UserRoleManagementModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.DateUtils;
import th.co.gosoft.go10.util.StringUtils;

@Path("v120110/room")
public class RoomService {

    private static Database db = CloudantClientUtils.getDBNewInstance();
    
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<RoomModel> getRooms(@QueryParam("empEmail") String empEmail) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getRooms()");
        List<RoomModel> roomModelList = db.findByIndex(getRoomJsonString(empEmail), RoomModel.class, new FindByIndexOptions()
         		 .sort(new IndexField("sort", SortOrder.asc)));
        System.out.println("room list size : "+roomModelList.size());
        List<RoomModel> resultList = calculateBadgeNumber(roomModelList, empEmail);
        System.out.println("GET Complete");
        return resultList;
    }

    @POST
    @Path("/postUserRoleManagement")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postUserRoleManagement(UserRoleManagementModel userRoleManagementModel) {
        UserRoleManagementModel localUserRoleManagentModel = userRoleManagementModel;
        System.out.println(">>>>>>>>>>>>>>>>>>> postUserRoleManagement() //room id : "+localUserRoleManagentModel.getRoomId());
        RoomModel roomModel = db.find(RoomModel.class, localUserRoleManagentModel.getRoomId());
        roomModel.setPostUser(localUserRoleManagentModel.getPostUser());
        roomModel.setCommentUser(localUserRoleManagentModel.getCommentUser());
        roomModel.setReadUser(localUserRoleManagentModel.getReadUser());
        db.update(roomModel);
        System.out.println("POST Complete");
        return Response.status(201).entity("complete").build();
    }
    
    @GET
    @Path("/getUserRoleManagement")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public UserRoleManagementModel getUserRole(@QueryParam("roomId") String roomId) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserRoleManagement() //room id : "+roomId);
        UserRoleManagementModel userRoleManagementModel = new UserRoleManagementModel();
        RoomModel roomModel = db.find(RoomModel.class, roomId);
        userRoleManagementModel.setPostUser(roomModel.getPostUser());
        userRoleManagementModel.setCommentUser(roomModel.getCommentUser());
        userRoleManagementModel.setReadUser(roomModel.getReadUser());
        userRoleManagementModel.setPostUserModelList(db.findByIndex(getUserModelFromEmails(roomModel.getPostUser()), UserModel.class, new FindByIndexOptions()
                 .fields("_id").fields("_rev").fields("empName").fields("empEmail").fields("type")));
        userRoleManagementModel.setCommentUserModelList(db.findByIndex(getUserModelFromEmails(roomModel.getCommentUser()), UserModel.class, new FindByIndexOptions()
                .fields("_id").fields("_rev").fields("empName").fields("empEmail").fields("type")));
        userRoleManagementModel.setReadUserModelList(db.findByIndex(getUserModelFromEmails(roomModel.getReadUser()), UserModel.class, new FindByIndexOptions()
                .fields("_id").fields("_rev").fields("empName").fields("empEmail").fields("type")));
        System.out.println("GET Complete");
        return userRoleManagementModel;
    }
    
    private List<RoomModel> calculateBadgeNumber(List<RoomModel> roomModelList, String empEmail) {
        List<RoomModel> resultList = new ArrayList<>();
        List<RoomNotificationModel> roomNotificationModelList = db.findByIndex(getRoomNotificationModelByEmpEmailString(empEmail), RoomNotificationModel.class);
        Map<String, Integer> roomIdCountTopicMap = parseListToMap(roomNotificationModelList);
        for (RoomModel roomModel : roomModelList) {
            int countTopic;
            if(roomIdCountTopicMap.containsKey(roomModel.get_id())) {
                countTopic = roomIdCountTopicMap.get(roomModel.get_id());
            } else {
                countTopic = createRoomNotificationModel(roomModel, empEmail);
            }
            roomModel.setBadgeNumber(roomModel.getTotalTopic() - countTopic);
            resultList.add(roomModel);
        }
        return resultList;
    }

    private int createRoomNotificationModel(RoomModel roomModel, String empEmail) {
        String stampDate = DateUtils.dbFormat.format(new Date());
        RoomNotificationModel roomNotificationModel = new RoomNotificationModel();
        roomNotificationModel.setRoomId(roomModel.get_id());
        roomNotificationModel.setEmpEmail(empEmail);
        roomNotificationModel.setCountTopic(roomModel.getTotalTopic());
        roomNotificationModel.setDate(stampDate);
        roomNotificationModel.setUpdateDate(stampDate);
        roomNotificationModel.setType("roomNotification");
        db.save(roomNotificationModel);
        return roomNotificationModel.getCountTopic();
    }

    private Map<String, Integer> parseListToMap(List<RoomNotificationModel> roomNotificationModelList) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (RoomNotificationModel roomNotificationModel : roomNotificationModelList) {
            resultMap.put(roomNotificationModel.getRoomId(), roomNotificationModel.getCountTopic());
        }
        return resultMap;
    }
    
    private String getRoomNotificationModelByEmpEmailString(String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"roomNotification\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("}}");
        return sb.toString();    
    }
    
    private String getRoomJsonString(String empEmail) {
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"sort\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [");
        stingBuilder.append("{\"type\":\"room\"},");
        stingBuilder.append("{\"readUser\":{\"$elemMatch\": {");
        stingBuilder.append("\"$or\": [\"all\", \""+empEmail+"\"]");
        stingBuilder.append("}}},");
        stingBuilder.append("{\"show\": true}]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"]}");
        System.out.println("query string : "+stingBuilder.toString());
        return stingBuilder.toString();
    }
    
    private String getUserModelFromEmails(List<String> empEmailList) {
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [");
        stingBuilder.append("{\"type\":\"user\"},");
        stingBuilder.append("{\"empEmail\":{");
        stingBuilder.append("\"$or\": "+StringUtils.parseListToString(empEmailList));
        stingBuilder.append("}}]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"]}");
        return stingBuilder.toString();
    }
}
