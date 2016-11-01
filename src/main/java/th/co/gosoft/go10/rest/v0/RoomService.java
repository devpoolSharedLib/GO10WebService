package th.co.gosoft.go10.rest.v0;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;

import th.co.gosoft.go10.model.v0.RoomModel;
import th.co.gosoft.go10.util.CloudantClientUtils;

@Path("room")
public class RoomService {

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<RoomModel> getRooms() {
        System.out.println(">>>>>>>>>>>>>>>>>>> getRooms()");
        Database db = CloudantClientUtils.getDBNewInstance();
        List<RoomModel> roomModel = db.findByIndex(getRoomJsonString(), RoomModel.class, new FindByIndexOptions()
         		 .sort(new IndexField("_id", SortOrder.asc)));
        System.out.println("GET Complete");
        return roomModel;
    }
    
    private String getRoomJsonString(){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\":\"room\"}]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"]}");
        return stingBuilder.toString();
    }
}
