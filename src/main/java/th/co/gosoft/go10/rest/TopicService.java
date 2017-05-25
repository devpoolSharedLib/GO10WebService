package th.co.gosoft.go10.rest;

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
import com.cloudant.client.api.Search;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.SearchResult;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.model.LikeModel;
import th.co.gosoft.go10.model.PollModel;
import th.co.gosoft.go10.model.ReadModel;
import th.co.gosoft.go10.model.RoomModel;
import th.co.gosoft.go10.model.TopicManagementModel;
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
    
    @POST
    @Path("/postPoll")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postPoll(PollModel pollModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> postPoll()");
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : " + stampDate);
        pollModel.setDate(stampDate);
        pollModel.setUpdateDate(stampDate);
        com.cloudant.client.api.model.Response response = db.save(pollModel);
        String result = response.getId();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result id : " + result);
        System.out.println("POST Complete");
        return Response.status(201).entity(result).build();
    }
    
    @GET
    @Path("/getToppicListbyRoomId")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public TopicManagementModel getToppicListbyRoomId(@QueryParam("roomId") String roomId, @QueryParam("bookmark") String bookmark){
        System.out.println(">>>>>>>>>>>>>>>>>>> getTopicListByRoomId() //room id : "+roomId+", bookmark : "+bookmark);
        Search search =   db.search("SearchIndex/TopicRoomIndex")
                .includeDocs(true)
                .sort("[\"pin<number>\",\"-date<string>\"]")
                .limit(30);
        if(bookmark != null && !("".equals(bookmark))){
            search.bookmark(bookmark);
        }
        SearchResult<LastTopicModel> searchResult = search.querySearchResult("roomId:\""+roomId+"\" AND type:\"host\"", LastTopicModel.class); 
        
        List<SearchResult<LastTopicModel>.SearchResultRow> searchResultRowList = searchResult.getRows();
        TopicManagementModel topicManagementModel = getTopicModelFromSearchResult(searchResultRowList);
        topicManagementModel.setTotalRows((int) searchResult.getTotalRows());
        topicManagementModel.setBookmark(searchResult.getBookmark());
        
        System.out.println("GET Complete");
        return topicManagementModel;
    }
    
    private TopicManagementModel getTopicModelFromSearchResult(List<SearchResult<LastTopicModel>.SearchResultRow> searchResultRowList) {
        TopicManagementModel topicManagementModel = new TopicManagementModel();
        
        List<LastTopicModel> pinTopicList = new ArrayList<>();
        List<LastTopicModel> noPinTopicList = new ArrayList<>();
        List<LastTopicModel> allTopicModelList = new ArrayList<>();
        
        for (SearchResult<LastTopicModel>.SearchResultRow searchResultRow : searchResultRowList) {
            LastTopicModel lastTopicModel = searchResultRow.getDoc();
            allTopicModelList.add(lastTopicModel);
        }
        
        String topicIdListString = StringUtils.generateTopicIdString(allTopicModelList);
        List<PollModel> pollModelList = getAllPollByTopicIdList(topicIdListString);
        
        String pollIdListString = getPollIdListStringFromList(pollModelList);
        System.out.println("pollId string : "+pollIdListString);
        if(pollIdListString != null && !pollIdListString.isEmpty()) {
            Map<String, Integer> answerUserMap =  getAnswerUserByPollIdList(pollIdListString);
            Map<String, String> topicIdAndPollIdMap = createTopicIdAndPollMap(pollModelList);
            getCountAccepPoll(pinTopicList, noPinTopicList, allTopicModelList, answerUserMap, topicIdAndPollIdMap);
        } else {
            getCountAccepPoll(pinTopicList, noPinTopicList, allTopicModelList, null, null);
        }
        
        topicManagementModel.setPinTopicList(DateUtils.formatDBDateToClientDate(pinTopicList));
        topicManagementModel.setNoPinTopicList(DateUtils.formatDBDateToClientDate(noPinTopicList));
        return topicManagementModel;
    }

    private Map<String, String> createTopicIdAndPollMap(List<PollModel> pollModelList) {
        Map<String, String> resultMap = new HashMap<>();
        for (PollModel pollModel : pollModelList) {
            resultMap.put(pollModel.getTopicId(), pollModel.get_id());
        }
        return resultMap;
    }

    private void getCountAccepPoll(List<LastTopicModel> pinTopicList, List<LastTopicModel> noPinTopicList, List<LastTopicModel> allTopicModelList
            , Map<String, Integer> answerUserMap, Map<String, String> topicIdAndPollIdMap) {
        
        for (LastTopicModel lastTopicModel : allTopicModelList) {
            if(answerUserMap != null && allTopicModelList != null) {
              String topicPoll = topicIdAndPollIdMap.get(lastTopicModel.get_id());
              if((answerUserMap.get(topicPoll) != null)) {
                  lastTopicModel.setCountAcceptPoll(answerUserMap.get(topicPoll));
              }
            }
            
            if(lastTopicModel.getPin() != null) {
              pinTopicList.add(lastTopicModel);
            } else {
              noPinTopicList.add(lastTopicModel);
            }
        }
    }

    private Map<String, Integer> getAnswerUserByPollIdList(String pollIdListString) {
        PollService pollService = new PollService();
        return pollService.getAllCountAcceptPollByPollIdList(pollIdListString);
    }

    private List<PollModel> getAllPollByTopicIdList(String topicIdListString) {
        PollService pollService = new PollService();
        return pollService.getAllPollByTopicIdList(topicIdListString);
    }
    
    private String getPollIdListStringFromList(List<PollModel> pollModelList){
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (PollModel pollModel : pollModelList) {
            sb.append(prefix);
            prefix = " or ";
            sb.append("\""+pollModel.get_id()+"\"");
        }
        return sb.toString();
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
    
    @POST
    @Path("/savePinTopic")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response savePinTopic(List<LastTopicModel> lastTopicModelList) {
        System.out.println(">>>>>>>>>>>>>>>>>>> savePinTopic()");
        stampDate = DateUtils.dbFormat.format(new Date());
        List<LastTopicModel> dbModelList = db.findByIndex(getTopicByIdList(lastTopicModelList), LastTopicModel.class
                ,new FindByIndexOptions().sort(new IndexField("date", SortOrder.desc)));
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
            LastTopicModel dbModel = findModelById(dbModelList, lastTopicModel.get_id());
            dbModel.setPin(lastTopicModel.getPin());
            db.update(dbModel);
        }
        return Response.status(201).build();
    }
    
    @POST
    @Path("/deletePinTopic")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deletePinTopic(List<LastTopicModel> lastTopicModelList) {
        System.out.println(">>>>>>>>>>>>>>>>>>> savePinTopic()");
        stampDate = DateUtils.dbFormat.format(new Date());
        List<LastTopicModel> dbModelList = db.findByIndex(getTopicByIdList(lastTopicModelList), LastTopicModel.class
                ,new FindByIndexOptions().sort(new IndexField("date", SortOrder.desc)));
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
            LastTopicModel dbModel = findModelById(dbModelList, lastTopicModel.get_id());
            dbModel.setPin(null);
            db.update(dbModel);
        }
        return Response.status(201).build();
    }
    
    @GET
    @Path("/getReadUserList")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<ReadModel> getReadUserList(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getReadUserList()// topicId : "+topicId);
        SearchResult<ReadModel> searchResult =   db.search("SearchIndex/ReadModelIndex")
                .groupField("empEmail", false)
                .groupSort("[\"-date<string>\"]")
                .sort("[\"-date<string>\"]")
                .querySearchResult("topicId:\""+topicId+"\"", ReadModel.class);
        
        List<ReadModel> readModelList = new ArrayList<>();
        List<SearchResult<ReadModel>.SearchResultGroup> searchResultGroupList = searchResult.getGroups();
        for (SearchResult<ReadModel>.SearchResultGroup searchResultGroup : searchResultGroupList) {
            List<SearchResult<ReadModel>.SearchResultRow> searchResultRowList = searchResultGroup.getRows();
            SearchResult<ReadModel>.SearchResultRow searchResultRow = searchResultRowList.get(0);
            ReadModel readModel = searchResultRow.getFields();
            readModelList.add(readModel);
        }
        List<ReadModel> resultList = DateUtils.formatDBDateToClientDateForReadModel(readModelList);
        return resultList;
    }
    
    @GET
    @Path("/getLikeUserList")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<LikeModel> getLikeUserList(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getLikeUserList()");
        SearchResult<LikeModel> searchResult =   db.search("SearchIndex/LikeModelIndex")
                .sort("[\"-date<string>\"]")
                .querySearchResult("topicId:\""+topicId+"\"", LikeModel.class);
        
        List<LikeModel> likeModelList = new ArrayList<>();
        List<SearchResult<LikeModel>.SearchResultRow> searchResultRowList = searchResult.getRows();
        for (SearchResult<LikeModel>.SearchResultRow searchResultRow : searchResultRowList) {
            LikeModel likeModel = searchResultRow.getFields();
            likeModelList.add(likeModel);
        }
        
        List<LikeModel> resultList = DateUtils.formatDBDateToClientDateForLikeModel(likeModelList);
        return resultList;
    }
    
    private LastTopicModel findModelById(List<LastTopicModel> dbModelList, String _id) {
        LastTopicModel resultModel = null;
        for (LastTopicModel lastTopicModel : dbModelList) {
            if(_id.equals(lastTopicModel.get_id())) {
                resultModel = lastTopicModel;
                break;
            }
        }
        return resultModel;
    }

    private void updateTotalTopicInRoomModel(String roomId) {
        RoomModel roomModel = db.find(RoomModel.class, roomId);
        roomModel.setTotalTopic(roomModel.getTotalTopic() == null ? 1 : roomModel.getTotalTopic()+1);
        db.update(roomModel);
    }
    
    public void concatDomainImagePath(List<LastTopicModel> lastTopicModelList) {
        for (int i=0; i<lastTopicModelList.size(); i++) {
            String content = (String) lastTopicModelList.get(i).getContent();
            if(content != null){
                lastTopicModelList.get(i).setContent(ConcatDomainUtils.concatDomainImagePath(content));
            }
        }
    }

    private String getTopicByIdList(List<LastTopicModel> lastTopicModelList) {
        String topicIdString = StringUtils.generateTopicIdString(lastTopicModelList);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$or\": ["+topicIdString+"]}");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"avatarName\",\"avatarPic\",\"subject\",\"content\",\"date\",\"type\",\"roomId\",\"countLike\",\"updateDate\",\"pin\"]}");
        return sb.toString();
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
    
    private String getRoomRuleToppicJsonString(String roomId){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"pin\": {\"$gte\": 0},");
        sb.append("\"$and\": [{\"type\":\"host\"}, {\"roomId\":\""+roomId+"\"}]");
        sb.append("}}");
        return sb.toString();
    }
    
}
