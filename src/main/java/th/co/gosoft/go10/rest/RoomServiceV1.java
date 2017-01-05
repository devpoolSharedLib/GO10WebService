package th.co.gosoft.go10.rest;

import java.util.ArrayList;
import java.util.Arrays;
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
import th.co.gosoft.go10.model.UserRoleManagementModel;
import th.co.gosoft.go10.util.CloudantClientUtils;

@Path("roomv1")
public class RoomServiceV1 {

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
    @Path("/newUserRole")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response newUserRole(UserRoleManagementModel userRoleManagementModel) {
        UserRoleManagementModel localUserRoleManagentModel = userRoleManagementModel;
        System.out.println(">>>>>>>>>>>>>>>>>>> newUserRole() //room id : "+localUserRoleManagentModel.getRoomId());
        RoomModel roomModel = db.find(RoomModel.class, localUserRoleManagentModel.getRoomId());
        roomModel.setPostUser(splitStringToArray(localUserRoleManagentModel.getUserPost()));
        roomModel.setCommentUser(splitStringToArray(localUserRoleManagentModel.getUserComment()));
        roomModel.setReadUser(splitStringToArray(localUserRoleManagentModel.getUserRead()));
        db.update(roomModel);
        System.out.println("POST Complete");
        return Response.status(201).entity("complete").build();
    }
    
    public List<String> splitStringToArray(String userString) {
        List<String> result;
        if ("all".equals(userString)) {
            result = new ArrayList<>(); 
            result.add("all");
        } else {
            result = Arrays.asList(userString.split("\\s*,\\s*"));
        }
        return result;
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
}
