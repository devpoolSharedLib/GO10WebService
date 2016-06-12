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
        System.out.println(">>>>>>>>>>>>>>>>>>> getRooms()");
        Database db = CloudantClientMgr.getDBNewInstance();
        List<RoomModel> roomModel = db.findByIndex(getRoomJsonString(), RoomModel.class);
        System.out.println("GET Complete");
        return roomModel;
    }
    
    private String getRoomJsonString(){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\":\"room\"}]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"],");
        stingBuilder.append("\"sort\": [{\"_id\": \"asc\"}]");
        
        return stingBuilder.toString();
    }
}
