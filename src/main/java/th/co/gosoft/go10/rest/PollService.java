package th.co.gosoft.go10.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;
import com.cloudant.client.api.model.SearchResult;

import th.co.gosoft.go10.model.ChoiceMasterModel;
import th.co.gosoft.go10.model.ChoiceReportModel;
import th.co.gosoft.go10.model.ChoiceTransactionModel;
import th.co.gosoft.go10.model.PollModel;
import th.co.gosoft.go10.model.PollReportModel;
import th.co.gosoft.go10.model.QuestionModel;
import th.co.gosoft.go10.model.QuestionReportModel;
import th.co.gosoft.go10.util.CloudantClientUtils;

@Path("server/poll")
public class PollService {

    private static Database db = CloudantClientUtils.getDBNewInstance();
    
    @GET
    @Path("/getPoll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<PollModel> getPoll(@QueryParam("topicId") String topicId, @QueryParam("empEmail") String empEmail) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getPoll() //topcic id : " + topicId);
        List<PollModel> pollModelList = db.findByIndex(getPollByTopicIdJsonString(topicId, empEmail), PollModel.class);
        return pollModelList;
    }
    
    @GET
    @Path("/getAllPollByTopicIdList")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<PollModel> getAllPollByTopicIdList(@QueryParam("topicId") String topicIdList) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getAllPollByTopicIdList() //topcic id : " + topicIdList);
        List<PollModel> pollModelList = db.findByIndex(getAllPollByTopicIdListJsonString(topicIdList), PollModel.class);
        return pollModelList;
    }
    
    @GET
    @Path("/getPollReportByTopicId")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public PollReportModel getPollReportByTopicId(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getPollReportByPollId() //topicId : "+topicId);
        PollReportModel pollReportModel = new PollReportModel();
       
        Search search =   db.search("SearchIndex/PollModelIndex")
                .includeDocs(true);
    
        SearchResult<PollModel> searchResultPollModel = search.querySearchResult("topicId:\""+topicId+"\"", PollModel.class); 
        System.out.println("searchResult : " + searchResultPollModel.getTotalRows());
        String pollId =  searchResultPollModel.getRows().get(0).getDoc().get_id();
        System.out.println("Poll Id : " + pollId);
        
        Integer countAcceptPoll = getCountAcceptPoll(pollId);
        System.out.println("countAcceptPoll : "+countAcceptPoll);
        pollReportModel.setCountAcceptPoll(countAcceptPoll);
        
        pollReportModel.setQuestionReport(getQuestionReportModel(searchResultPollModel));
        pollReportModel.setEmpEmailAcceptPoll(getEmpEmailAcceptPoll(pollId));
       
        return pollReportModel;
    }
    
    private List<String> getEmpEmailAcceptPoll(String pollId){
        Search search =   db.search("SearchIndex/ChoiceTransactionModelIndex")
                .includeDocs(true)
                .sort("[\"questionId<string>\"]")
                .groupField("empEmail", false);
        SearchResult<ChoiceTransactionModel> searchResult = search.querySearchResult("pollId:\""+pollId+"\"", ChoiceTransactionModel.class); 
        List<String> resultList = new ArrayList<>();
        List<SearchResult<ChoiceTransactionModel>.SearchResultGroup> searchResultGroupList = searchResult.getGroups();
        for (SearchResult<ChoiceTransactionModel>.SearchResultGroup searchResultGroup : searchResultGroupList) {
            List<SearchResult<ChoiceTransactionModel>.SearchResultRow> searchResultRowList = searchResultGroup.getRows();
            resultList.add(searchResultRowList.get(0).getDoc().getEmpEmail());
        }
        return resultList;
   }
    
    public Integer getCountAcceptPoll(String pollId){
    	 
         Search search =   db.search("SearchIndex/ChoiceTransactionModelIndex")
                 .includeDocs(true)
                 .sort("[\"questionId<string>\"]")
                 .groupField("empEmail", false);
         SearchResult<ChoiceTransactionModel> searchResult = search.querySearchResult("pollId:\""+pollId+"\"", ChoiceTransactionModel.class); 
         System.out.println("getCountAcceptPoll poll id : " + pollId + "size : " + searchResult.getGroups().size());
         return searchResult.getGroups().size();
    }
    
    public Map<String, Integer> getAllCountAcceptPollByPollIdList(String pollId){
        Search search =   db.search("SearchIndex/ChoiceTransactionModelIndex")
                .includeDocs(true)
                .sort("[\"empEmail<string>\"]")
                .groupField("pollId", false);
        SearchResult<ChoiceTransactionModel> searchResult = search.querySearchResult("pollId:("+pollId+")", ChoiceTransactionModel.class); 
      
        Map<String, Integer> countAcceptPollMap = new HashMap<String, Integer>();
        List<SearchResult<ChoiceTransactionModel>.SearchResultGroup> searchResultGroupList = searchResult.getGroups();
        for (SearchResult<ChoiceTransactionModel>.SearchResultGroup searchResultGroup : searchResultGroupList) {
            List<SearchResult<ChoiceTransactionModel>.SearchResultRow> searchResultRowList = searchResultGroup.getRows();
            int countAcceptPoll = 0;
            String tempEmpEmail = "";
            System.out.println("poll id : " + searchResultRowList.get(0).getDoc().getPollId());
            for(SearchResult<ChoiceTransactionModel>.SearchResultRow searchResultRow : searchResultRowList){
                 if(!(tempEmpEmail.equals(searchResultRow.getDoc().getEmpEmail().toString()))){
                     tempEmpEmail = searchResultRow.getDoc().getEmpEmail();
                     countAcceptPoll++;
                 }
            }
            countAcceptPollMap.put(searchResultRowList.get(0).getDoc().getPollId(), countAcceptPoll);
            System.out.println("countAccept Poll " + countAcceptPoll);
        }
        return countAcceptPollMap;
    }
    
    private List<QuestionReportModel> getQuestionReportModel(SearchResult<PollModel> searchResultPollModel){
        
        List<QuestionModel> questionModelList = searchResultPollModel.getRows().get(0).getDoc().getQuestionMaster();
        List<ChoiceMasterModel> choiceTransactionModelList;
        String queryCountChoiceAllQuestion = "pollId:\""+searchResultPollModel.getRows().get(0).getDoc().get_id()+"\" AND (";
        for(QuestionModel questionModel : questionModelList){
            choiceTransactionModelList = questionModel.getChoiceMaster();
             for(ChoiceMasterModel choiceTransactionModel : choiceTransactionModelList ){
                 if(!(choiceTransactionModel.getChoiceKey().equals("q1c1"))){
                     queryCountChoiceAllQuestion +=  " OR ";
                 }
                 queryCountChoiceAllQuestion += "(questionId:\""+questionModel.getQuestionId()+"\" AND choiceKey:\""+choiceTransactionModel.getChoiceKey()+"\")";
             }
        }
        queryCountChoiceAllQuestion += ")";
        
        System.out.println("getQuestionReportModel >>>>>>>>> queryCountChoiceAllQuestion : " + queryCountChoiceAllQuestion);
        SearchResult<ChoiceTransactionModel> searchResult =   db.search("SearchIndex/ChoiceTransactionModelIndex")
                .includeDocs(true)
                .counts(new String[]{"choiceKey"})
                .querySearchResult(queryCountChoiceAllQuestion,ChoiceTransactionModel.class);
        Map<String, Map<String, Long>> countsChoiceKey = searchResult.getCounts();
        Map<String, Long> countsChoiceKeyMap = countsChoiceKey.get("choiceKey");
        List<QuestionReportModel> questionReportModelList = new ArrayList<QuestionReportModel>();
        for (QuestionModel questionModel : questionModelList) {
            QuestionReportModel questionReportModel = new QuestionReportModel();
            questionReportModel.setQuestionId(questionModel.getQuestionId());
            questionReportModel.setQuestionTitle(questionModel.getQuestionTitle());
            choiceTransactionModelList = questionModel.getChoiceMaster();
            List<ChoiceReportModel> choiceReportModelList = new ArrayList<ChoiceReportModel>();
            for (ChoiceMasterModel choiceTransactionModel : choiceTransactionModelList) {
                ChoiceReportModel choiceReportModel = new ChoiceReportModel();
                choiceReportModel.setChoiceKey(choiceTransactionModel.getChoiceKey());
                choiceReportModel.setChoiceTitle(choiceTransactionModel.getChoiceTitle());
                if (countsChoiceKeyMap.get(choiceTransactionModel.getChoiceKey()) == null) {
                    choiceReportModel.setCountChoice(0);
                } else {
                    choiceReportModel.setCountChoice((int) (long) countsChoiceKeyMap.get(choiceTransactionModel.getChoiceKey()));
                }
                choiceReportModelList.add(choiceReportModel);
            }
            questionReportModel.setChoiceReportModel(choiceReportModelList);
            questionReportModelList.add(questionReportModel);
        }
        return questionReportModelList;
    }
    
    private String getPollByTopicIdJsonString(String topicId, String empEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"poll\"}, ");
        sb.append("{\"topicId\":\"" + topicId + "\"}, ");
        sb.append("{\"empEmailPoll\":{\"$elemMatch\": {");
        sb.append("\"$or\": [\"all\", \"" + empEmail + "\"]");
        sb.append("}}}]");
        sb.append("}}");
        return sb.toString();
    }
    
    private String getAllPollByTopicIdListJsonString(String topicIdList) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{\"type\":\"poll\"},");
        sb.append("{\"topicId\": {");
        sb.append("\"$or\": ["+topicIdList+"]");
        sb.append("}}]");
        sb.append("}}");
        return sb.toString();
    }
    
}
