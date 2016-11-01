package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import th.co.gosoft.go10.rest.v2.TopicServiceV2;

public class TopicServiceTestV2 {

    @Test
    @SuppressWarnings("rawtypes")
    public void formatDateTest() {
        TopicServiceV2 topicServiceV2 = new TopicServiceV2();
        List<Map> topicMapList = createTestDataList();
        List<Map> resultList = topicServiceV2.formatDateAndCount(topicMapList);
        assertEquals(2, resultList.size());
        assertEquals("31/10/2016 12:11:04", resultList.get(0).get("date"));
        assertEquals("31/10/2016 12:12:05", resultList.get(1).get("date"));
    }
    
    @Test
    public void isNextDayTest() throws ParseException{
        TopicServiceV2 topicServiceV2 = new TopicServiceV2();
        assertEquals(true, topicServiceV2.isNextDay("2016/10/30 10:11:04", "2016/10/31 12:11:04"));
        assertEquals(false, topicServiceV2.isNextDay("2016/10/31 10:11:04", "2016/10/31 12:11:04"));
        assertEquals(true, topicServiceV2.isNextDay("2016/10/31 10:11:04", "2016/11/01 09:00:01"));
    }
    
    @SuppressWarnings("rawtypes")
    private List<Map> createTestDataList() {
        List<Map> resultList = new ArrayList<Map>();
        resultList.add(generateMap("test1", "2016/10/31 12:11:04", null));
        resultList.add(generateMap("test2", "2016/10/31 12:12:05", null));
        return resultList;
    }

    @SuppressWarnings("rawtypes")
    private Map generateMap(String _id, String date, String content) {
        Map<String, Object> topicMap = new HashMap<>();
        topicMap.put("_id", _id);
        topicMap.put("date", date);
        topicMap.put("content", content);
        return topicMap;
    }
}
