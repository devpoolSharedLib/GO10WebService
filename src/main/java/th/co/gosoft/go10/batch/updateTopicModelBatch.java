package th.co.gosoft.go10.batch;

import java.util.List;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.util.CloudantClientUtils;

public class updateTopicModelBatch {

	private static Database db = CloudantClientUtils.getDBNewInstance();
	
	public static void main(String [ ] args){
		updateTopicModel();	
	}


	private static void updateTopicModel() {
		List<LastTopicModel> lastTopicModelList = db.findByIndex(getAllTopicModel(),LastTopicModel.class);
//		int count = 0;
		for (LastTopicModel topicModel : lastTopicModelList) {
			topicModel.setCountLike(0);
			topicModel.setCountRead(0);
			db.update(topicModel);
//			count++;
//			System.out.println("Update Topic " + count);
		}
		System.out.println("Upadte Topic Complete : " + lastTopicModelList.size());
	}
	
	
	private static String getAllTopicModel() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"selector\": {");
		sb.append("\"_id\": {\"$gt\": 0},");
		sb.append("\"$and\": [{\"type\":\"host\"}]");
		sb.append("}}");
		return sb.toString();
	}
}
