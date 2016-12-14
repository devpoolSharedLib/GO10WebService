package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import th.co.gosoft.go10.rest.RoomServiceV1;

public class RoomServiceV1Test {

    @Test
    public void userAllTest() {
        RoomServiceV1 roomServiceV1 = new RoomServiceV1();
        List<String> resultList = roomServiceV1.splitStringToArray("all");
        assertEquals(1, resultList.size());
        assertEquals("all", resultList.get(0));
    }
    
    @Test
    public void splitOneUserTest() {
        RoomServiceV1 roomServiceV1 = new RoomServiceV1();
        List<String> resultList = roomServiceV1.splitStringToArray("manitkan@gosoft.co.th");
        assertEquals(1, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
    }
    
    @Test
    public void splitTwoUserTest() {
        RoomServiceV1 roomServiceV1 = new RoomServiceV1();
        List<String> resultList = roomServiceV1.splitStringToArray("manitkan@gosoft.co.th,jirapaschi@gosoft.co.th");
        assertEquals(2, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
        assertEquals("jirapaschi@gosoft.co.th", resultList.get(1));
    }
    
    @Test
    public void splitFiveUserTest() {
        RoomServiceV1 roomServiceV1 = new RoomServiceV1();
        List<String> resultList = roomServiceV1.splitStringToArray("manitkan@gosoft.co.th,jirapaschi@gosoft.co.th,chalijar@gosoft.co.th,pongsakorntri@gosoft.co.th,phanthatana@gosoft.co.th");
        assertEquals(5, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
        assertEquals("jirapaschi@gosoft.co.th", resultList.get(1));
        assertEquals("chalijar@gosoft.co.th", resultList.get(2));
        assertEquals("pongsakorntri@gosoft.co.th", resultList.get(3));
        assertEquals("phanthatana@gosoft.co.th", resultList.get(4));
    }

}
