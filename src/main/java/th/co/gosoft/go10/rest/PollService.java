package th.co.gosoft.go10.rest;

import java.util.ArrayList;
import java.util.List;

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
    @Path("/getPollReportByTopicId")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<PollReportModel> getPollReportByTopicId(@QueryParam("topicId") String topicId){
        System.out.println(">>>>>>>>>>>>>>>>>>> getPollReportByPollId() //topicId : "+topicId);
        List<PollReportModel> pollReportModelList = new ArrayList<PollReportModel>();
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
        pollReportModelList.add(pollReportModel); 
       
        return pollReportModelList;
        
        
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
    
    private List<QuestionReportModel> getQuestionReportModel(SearchResult<PollModel> searchResultPollModel){
    	
    	 Search search =   db.search("SearchIndex/ChoiceTransactionModelIndex")
                 .includeDocs(true);
    	 
    	 List<QuestionReportModel> questionReportModelList = new ArrayList<QuestionReportModel>();
    	 List<QuestionModel> questionModelList = searchResultPollModel.getRows().get(0).getDoc().getQuestionMaster();
    	 for(QuestionModel questionModel : questionModelList){
    		 QuestionReportModel questionReportModel = new QuestionReportModel();
    		 
    		 System.out.println("QuestoinId : " + questionModel.getQuestionId());
    		 System.out.println("QuestoinTitle : " + questionModel.getQuestionTitle());
    		 questionReportModel.setQuestionId(questionModel.getQuestionId());
    		 questionReportModel.setQuestionTitle(questionModel.getQuestionTitle());
    		 List<ChoiceMasterModel> choiceTransactionModelList = questionModel.getChoiceMaster();
    		 List<ChoiceReportModel> choiceReportModelList = new ArrayList<ChoiceReportModel>();
    		 for(ChoiceMasterModel choiceTransactionModel : choiceTransactionModelList ){
    			 ChoiceReportModel choiceReportModel = new ChoiceReportModel();
    			 SearchResult<ChoiceTransactionModel> searchResultChoiceTransactionModel = search.querySearchResult("pollId:\""+searchResultPollModel.getRows().get(0).getDoc().get_id()+"\" AND questionId:\""+questionModel.getQuestionId()+"\" AND choiceKey:\""+choiceTransactionModel.getChoiceKey()+"\"", ChoiceTransactionModel.class); 
    			 System.out.println("choiceKey : "+ choiceTransactionModel.getChoiceKey() + "choiceTitle : "+ choiceTransactionModel.getChoiceTitle() + " totalRows : " + searchResultChoiceTransactionModel.getTotalRows());
    			 choiceReportModel.setChoiceKey(choiceTransactionModel.getChoiceKey());
    			 choiceReportModel.setChoiceTitle(choiceTransactionModel.getChoiceTitle());
    			 choiceReportModel.setCountChoice((int) (long)searchResultChoiceTransactionModel.getTotalRows());
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
    
}
