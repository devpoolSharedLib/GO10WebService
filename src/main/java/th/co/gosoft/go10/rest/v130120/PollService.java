package th.co.gosoft.go10.rest.v130120;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.ChoiceTransactionModel;
import th.co.gosoft.go10.model.PollModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.DateUtils;

@Path("v130120/poll")
public class PollService {

    private static Database db = CloudantClientUtils.getDBNewInstance();
    private String stampDate;
    
    @POST
    @Path("/savePoll")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response savePoll(List<ChoiceTransactionModel> choiceTransactionModelList) {
        System.out.println(">>>>>>>>>>>>>>>>>>> savePoll()");
        stampDate = DateUtils.dbFormat.format(new Date());
        System.out.println("StampDate : " + stampDate);
        List<ChoiceTransactionModel> resultList = insertDate(choiceTransactionModelList, stampDate);
        db.bulk(resultList);
        System.out.println("POST Complete");
        return Response.status(201).build();
    }
    
    @GET
    @Path("/getPoll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<PollModel> getPoll(String topicId, String empEmail) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getPoll() //topcic id : " + topicId);
        List<PollModel> pollModelList = db.findByIndex(getPollByTopicIdJsonString(topicId, empEmail), PollModel.class);
        return pollModelList;
    }

    private List<ChoiceTransactionModel> insertDate(List<ChoiceTransactionModel> choiceTransactionModelList, String stampDate) {
        List<ChoiceTransactionModel> resultList = new ArrayList<>();
        for (ChoiceTransactionModel choiceTransactionModel : choiceTransactionModelList) {
            choiceTransactionModel.setDate(stampDate);
            resultList.add(choiceTransactionModel);
        }
        return resultList;
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
