package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import th.co.gosoft.go10.model.LastTopicModel;
import th.co.gosoft.go10.util.DateUtils;

public class DateUtilsTest {

    @Test
    public void formatDBDateToClientDateTest() {
        List<LastTopicModel> lastTopicModelList = createTestDataList();
        List<LastTopicModel> resultList = DateUtils.formatDBDateToClientDate(lastTopicModelList);
        assertEquals(3, resultList.size());
        assertEquals("31/10/2016 09:05:05", resultList.get(0).getDate());
        assertEquals("31/10/2016 12:59:59", resultList.get(1).getDate());
        assertEquals("01/01/2017 12:00:00", resultList.get(2).getDate());
    }
    
    private List<LastTopicModel> createTestDataList() {
        List<LastTopicModel> resultList = new ArrayList<>();
        resultList.add(new LastTopicModel("test1", "2016/10/31 09:05:05", null));
        resultList.add(new LastTopicModel("test2", "2016/10/31 12:59:59", null));
        resultList.add(new LastTopicModel("test2", "2017/01/01 12:00:00", null));
        return resultList;
    }

}
