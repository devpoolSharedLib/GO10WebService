package th.co.gosoft.go10.util;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.UserAdminModel;

public class SecurityUtils {
    
    private static SecurityUtils instance;
    private static Database db = CloudantClientUtils.getDBNewInstance();
    
    private SecurityUtils() {

    }

    public static SecurityUtils getInstance() {
        if (instance == null) {
            instance = new SecurityUtils();
        }
        return instance;
    }
    
    public List<RoomModel> getRoom(HttpSession session) {
        UserAdminModel userAdminModel = (UserAdminModel) session.getAttribute("userAdminModel");
        List<RoomModel> roomModels = db.findByIndex(getRoomsByRoomIdJsonString(userAdminModel.getRoomAdmin()), RoomModel.class);
        
        System.out.println("Session roomName : " + session.getAttribute("roomName"));
        
    	if (session == null || (session.getAttribute("roomName") == null)) {
    		session.setAttribute("roomName", roomModels.get(0).getName());
    	}
    	
    	System.out.println("Session roomName : " + session.getAttribute("roomName"));
    	
        return roomModels;
    }
    
    private String getRoomsByRoomIdJsonString(List<String> roomIdList) {
        String roomIdString = generateRoomIdString(roomIdList);
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$or\": ["+roomIdString+"]},");
        stingBuilder.append("\"$and\": [");
        stingBuilder.append("{\"type\":\"room\"}");
        stingBuilder.append("]");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"name\",\"desc\", \"type\"]}");
        return stingBuilder.toString();
    }

    private String generateRoomIdString(List<String> roomIdList) {
        StringBuilder stingBuilder = new StringBuilder();
        String prefix = "";
        for (String roomId : roomIdList) {
            stingBuilder.append(prefix);
            prefix = ",";
            stingBuilder.append("\""+roomId+"\"");
        }
        System.out.println(stingBuilder.toString());
        return stingBuilder.toString();
    }
}
