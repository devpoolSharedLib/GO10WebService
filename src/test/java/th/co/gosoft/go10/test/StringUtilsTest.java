package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import th.co.gosoft.go10.util.StringUtils;

public class StringUtilsTest {

    @Test
    public void userAllTest() {
        List<String> resultList = StringUtils.parseStringToList("all");
        assertEquals(1, resultList.size());
        assertEquals("all", resultList.get(0));
    }
    
    @Test
    public void splitOneUserTest() {
        List<String> resultList = StringUtils.parseStringToList("manitkan@gosoft.co.th");
        assertEquals(1, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
    }
    
    @Test
    public void splitTwoUserTest() {
        List<String> resultList = StringUtils.parseStringToList("manitkan@gosoft.co.th,jirapaschi@gosoft.co.th");
        assertEquals(2, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
        assertEquals("jirapaschi@gosoft.co.th", resultList.get(1));
    }
    
    @Test
    public void splitFiveUserTest() {
        List<String> resultList = StringUtils.parseStringToList("manitkan@gosoft.co.th,jirapaschi@gosoft.co.th,chalijar@gosoft.co.th,pongsakorntri@gosoft.co.th,phanthatana@gosoft.co.th");
        assertEquals(5, resultList.size());
        assertEquals("manitkan@gosoft.co.th", resultList.get(0));
        assertEquals("jirapaschi@gosoft.co.th", resultList.get(1));
        assertEquals("chalijar@gosoft.co.th", resultList.get(2));
        assertEquals("pongsakorntri@gosoft.co.th", resultList.get(3));
        assertEquals("phanthatana@gosoft.co.th", resultList.get(4));
    }
    
    @Test
    public void parseListToStringTest() {
        List<String> stringList = new ArrayList<>();
        stringList.add("manitkan@gosoft.co.th");
        stringList.add("jirapaschi@gosoft.co.th");
        stringList.add("chalitjar@gosoft.co.th");
        String result = StringUtils.parseListToString(stringList);
        assertEquals("[\"manitkan@gosoft.co.th\", \"jirapaschi@gosoft.co.th\", \"chalitjar@gosoft.co.th\"]", result);
    }


}
