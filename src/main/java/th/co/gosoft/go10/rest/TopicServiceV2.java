package th.co.gosoft.go10.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.ConcatDomainUtils;
import th.co.gosoft.go10.util.DateUtils;

@Path("topicv2")
public class TopicServiceV2 {

    private static DateFormat postFormat = createSimpleDateFormat("yyyy/MM/dd HH:mm:ss", "GMT+7");
    private static DateFormat getFormat = createSimpleDateFormat("dd/MM/yyyy HH:mm:ss", "GMT+7");
    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
    
    @GET
    @Path("/gettopicbyid")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Map> getTopicById(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicById() //topcic id : "+topicId);
        List<Map> newTopicMapList = db.findByIndex(getTopicByIdJsonString(topicId), Map.class, new FindByIndexOptions()
                 .sort(new IndexField("date", SortOrder.asc)));
        increaseReadCount(newTopicMapList.get(0), empEmail);
        concatDomainImagePath(newTopicMapList);
        List<Map> resultList = formatDateAndCount(newTopicMapList);
        System.out.println("GET Complete");
        return resultList;
    }
    
    @GET
    @Path("/gethottopiclist")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LastTopicModel> getHotTopicList(@QueryParam("empEmail") String empEmail){
        System.out.println(">>>>>>>>>>>>>>>>>>> getHotTopicList()");
        List<RoomModel> roomModelList = db.findByIndex(getRoomJsonStringReadUser(empEmail), RoomModel.class, new FindByIndexOptions()
                .sort(new IndexField("_id", SortOrder.asc)).fields("_id").fields("_rev")
                .fields("name").fields("desc").fields("type"));
        
        List<LastTopicModel> lastTopicModelList = db.findByIndex(getHotTopicListJsonString(roomModelList), LastTopicModel.class, new FindByIndexOptions()
                 .sort(new IndexField("updateDate", SortOrder.desc)).limit(20));
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(lastTopicModelList);
        System.out.println("getHotTopicList list size : "+resultList.size());
        return lastTopicModelList;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void increaseReadCount(Map<String, Object> topicModelMap, String empEmail){
        try {
            System.out.println(">>>>>>>>>>>>>>>>>> increaseReadCount() topicModelMap : "+topicModelMap.get("_id")+", empEmail : "+empEmail);

            stampDate = postFormat.format(new Date());
            System.out.println("StampDate : "+stampDate);
            
            Map localTopicModelMap = topicModelMap;
            List<Map> readModelMapList = db.findByIndex(getReadModelByEmpEmail((String) localTopicModelMap.get("_id"), empEmail), Map.class);
            if (readModelMapList == null || readModelMapList.isEmpty()){
                System.out.println("read model is null");
                Map readModelMap = createReadModelMap((String) localTopicModelMap.get("_id"), empEmail);
                db.save(readModelMap);
                localTopicModelMap.put("countRead", getCountRead(localTopicModelMap)+1);
                db.update(localTopicModelMap);
            } else {
                System.out.println("read model is not null");
                Map readModelMap = readModelMapList.get(0);
                if(DateUtils.isNextDay((String) readModelMap.get("date"), stampDate)){
                    readModelMap.put("date", stampDate);
                    db.update(readModelMap);
                    localTopicModelMap.put("countRead", getCountRead(localTopicModelMap)+1);
                    db.update(localTopicModelMap);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private int getCountRead(Map<String, Object> localTopicModelMap) {
        return ((Double) localTopicModelMap.get("countRead")) == null? 0:((Double) localTopicModelMap.get("countRead")).intValue();
    }
    
    private String getReadModelByEmpEmail(String topicId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"read\"}, {\"topicId\":\""+topicId+"\"}, {\"empEmail\":\""+empEmail+"\"}]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"topicId\",\"empEmail\",\"type\",\"date\"]}");
        return sb.toString();
    }
    
    private String getHotTopicListJsonString(List<RoomModel> roomModelList){
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

    @SuppressWarnings("rawtypes")
    private Map createReadModelMap(String topicId, String empEmail) {
        System.out.println("topicId : "+topicId+", empEmail : "+empEmail);
        Map<String, Object> readModelMap = new HashMap<>();
        readModelMap.put("empEmail", empEmail);
        readModelMap.put("topicId", topicId);
        readModelMap.put("type", "read");
        readModelMap.put("date", stampDate);
        return readModelMap;
    }

    private static DateFormat createSimpleDateFormat(String formatString, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void concatDomainImagePath(List<Map> newTopicModelList) {
        for (int i=0; i<newTopicModelList.size(); i++) {
            String content = (String) newTopicModelList.get(i).get("content");
            if(content != null){
                newTopicModelList.get(i).put("content", ConcatDomainUtils.concatDomainImagePath(content));
            }
        }
    }
    
    private String getTopicByIdJsonString(String topicId){
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> formatDateAndCount(List<Map> newTopicModelList) {
        List<Map> resultList = new ArrayList<Map>();
        for (Map newTopicMap : newTopicModelList) {
            Map resultMap = newTopicMap;
            resultMap.put("date", getFormat.format(parseStringToDate((String) resultMap.get("date"))));
            if(resultMap.get("countLike") != null) {
                resultMap.put("countLike", ((Double) resultMap.get("countLike")).intValue());
            }
            resultList.add(resultMap);
        }
        return resultList;
    }
    
    private Date parseStringToDate(String dateString){
        try {
            return postFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
