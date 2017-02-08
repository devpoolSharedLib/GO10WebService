package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
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
    
    @Test
    public void isNextDayTest() throws ParseException {
        assertEquals(true, DateUtils.isNextDay("2016/10/30 10:11:04", "2016/10/31 12:11:04"));
        assertEquals(false, DateUtils.isNextDay("2016/10/31 10:11:04", "2016/10/31 12:11:04"));
        assertEquals(true, DateUtils.isNextDay("2016/10/31 10:11:04", "2016/11/01 09:00:01"));
    }
    
    @Test
    public void isAfterDateTest() {
        assertEquals(true, DateUtils.isAfterDate("2016/02/29 10:00:05", "2016/02/29 10:00:07"));
        assertEquals(true, DateUtils.isAfterDate("2016/02/29 10:00:07", "2016/03/01 10:00:00"));
        assertEquals(true, DateUtils.isAfterDate("2015/02/28 10:00:00", "2016/02/29 10:00:00"));
        assertEquals(false, DateUtils.isAfterDate("2016/02/29 10:00:00", "2016/02/29 09:00:00"));
        assertEquals(false, DateUtils.isAfterDate("2016/02/29 10:00:07", "2016/02/28 10:00:05"));
        assertEquals(false, DateUtils.isAfterDate("2016/02/29 10:00:07", "2016/01/28 10:00:07"));
    }

}
