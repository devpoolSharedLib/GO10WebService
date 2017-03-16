package th.co.gosoft.go10.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.model.RoomModel;

public class StringUtils {

    public static List<String> parseStringToList(String string) {
        List<String> result;
        if ("all".equals(string)) {
            result = new ArrayList<>(); 
            result.add("all");
        } else {
            result = Arrays.asList(string.split("\\s*,\\s*"));
        }
        return result;
    }
    
    public static String parseListToString(List<String> list) {
        String result = "[";
        String delimiter = "";
        for (String string : list) {
            result += delimiter;
            result += "\""+string+"\"";
            delimiter = ", ";
        }
        result += "]";
        return result;
    }
    
    public static String generateRoomIdString(List<RoomModel> roomModelList) {
        StringBuilder stingBuilder = new StringBuilder();
        String prefix = "";
        for (RoomModel roomModel : roomModelList) {
            stingBuilder.append(prefix);
            prefix = ",";
            stingBuilder.append("\""+roomModel.get_id()+"\"");
        }
        return stingBuilder.toString();
    }
    
    public static String generateTopicIdString(List<LastTopicModel> lastTopicModelList) {
        StringBuilder stingBuilder = new StringBuilder();
        String prefix = "";
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
            stingBuilder.append(prefix);
            prefix = ",";
            stingBuilder.append("\""+lastTopicModel.get_id()+"\"");
        }
        return stingBuilder.toString();
    }

}
