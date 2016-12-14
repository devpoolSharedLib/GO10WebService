package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.rest.TopicService;

public class TopicServiceTest {

    DateFormat postFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    
    @Test
    public void formatDateTest() {
        TopicService topicService = new TopicService();
        List<TopicModel> topicModelList = createTestDataList();
        List<TopicModel> resultList = topicService.formatDate(topicModelList);
        assertEquals(2, resultList.size());
        assertEquals("31/10/2016 09:05:05", resultList.get(0).getDate());
        assertEquals("31/10/2016 12:59:59", resultList.get(1).getDate());
    }
    
    private List<TopicModel> createTestDataList() {
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        resultList.add(new TopicModel("test1", "2016/10/31 09:05:05", null));
        resultList.add(new TopicModel("test2", "2016/10/31 12:59:59", null));
        return resultList;
    }
}
