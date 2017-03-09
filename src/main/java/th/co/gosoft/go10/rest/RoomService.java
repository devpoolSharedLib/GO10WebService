package th.co.gosoft.go10.rest;

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

import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.model.UserRoleManagementModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.StringUtils;

@Path("server/room")
public class RoomService {

    private static Database db = CloudantClientUtils.getDBNewInstance();
    
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<RoomModel> getRooms(@QueryParam("empEmail") String empEmail) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getRooms()");
        List<RoomModel> roomModelList = db.findByIndex(getRoomJsonString(empEmail), RoomModel.class, new FindByIndexOptions()
         		 .sort(new IndexField("_id", SortOrder.asc)).fields("_id").fields("_rev")
         		 .fields("name").fields("desc").fields("type").fields("postUser").fields("commentUser").fields("readUser"));
        System.out.println("GET Complete");
        return roomModelList;
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
    
    private String getRoomJsonString(String empEmail) {
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
