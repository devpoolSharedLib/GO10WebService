package th.co.gosoft.go10.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.util.TopicUtils;

public class TopicUtilsTest {

    @Test
    public void concatList(){
        List<LastTopicModel> firstList = createTopic(0, 3);
        List<LastTopicModel> secondList = createTopic(4, 5);
        List<LastTopicModel> resultList = TopicUtils.concatList(firstList, secondList);
        assertEquals(6, resultList.size());
        assertEquals("_id : 0", resultList.get(0).get_id());
        assertEquals("subject : 1", resultList.get(1).getSubject());
        assertEquals("avatar : 3", resultList.get(3).getAvatarName());
        assertEquals("_id : 4", resultList.get(4).get_id());
        assertEquals("subject : 5", resultList.get(5).getSubject());
    }

    private List<LastTopicModel> createTopic(int startRows, int endRows) {
        List<LastTopicModel> lastTopicModelList = new ArrayList<>();
        for (int i = startRows; i <= endRows; i++) {
            LastTopicModel lastTopicModel = new LastTopicModel();
            lastTopicModel.set_id("_id : "+i);
            lastTopicModel.setSubject("subject : "+i);
            lastTopicModel.setAvatarName("avatar : "+i);
            lastTopicModelList.add(lastTopicModel);
        }
        return lastTopicModelList;
    }
    
}
