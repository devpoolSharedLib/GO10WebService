package th.co.gosoft.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cloudant.client.api.Database;

import th.co.gosoft.model.RoomModel;
import th.co.gosoft.util.CloudantClientMgr;

@Path("room")
public class RoomService {

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<RoomModel> getRooms() {
        System.out.println(">>>>>>>>>>>>>>>>>>> in GET getRooms()");
        Database db = CloudantClientMgr.getDB();
        List<RoomModel> roomModel = db.findByIndex(getCommentJsonString(), RoomModel.class);
        System.out.println("GET Complete");
        return roomModel;
    }
    
    private String getCommentJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"room\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\"],");
        sb.append("\"sort\": [{\"_id\": \"asc\"}]");
        
        return sb.toString();
    }
}
