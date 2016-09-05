package th.co.gosoft.go10.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    }
    
    @Test
    public void deleteDomainImagePathNoneImageTest(){
        TopicService topicService = new TopicService();
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("None Image Here !!!");
        assertEquals("None Image Here !!!", topicService.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void deleteDomainImagePathOneImageTest(){
        TopicService topicService = new TopicService();
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("<img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=ZLY65XZ7\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">");
        assertEquals("<img src=\"ZLY65XZ7\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">", topicService.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void deleteDomainImagePathTwoImageTest(){
        TopicService topicService = new TopicService();
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("<img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>And Next Image<br><br><img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>");
        assertEquals("<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>And Next Image<br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>", topicService.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void concatDomainImagePathTest(){
        TopicService topicService = new TopicService();
        List<TopicModel> topicModelList = createConcatDomainImageDataList();
        topicService.concatDomainImagePath(topicModelList);
        assertEquals("No Image Here", topicModelList.get(0).getContent());
        assertEquals("<img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>One Image Here", topicModelList.get(1).getContent());
        assertEquals("<img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>Two Image Here<br><br><img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>", topicModelList.get(2).getContent());
        assertEquals("Three Image Here<br><br><img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">", topicModelList.get(3).getContent());
    }

    private List<TopicModel> createTestDataList() {
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        resultList.add(new TopicModel("test1", postFormat.format(new Date()), null));
        resultList.add(new TopicModel("test2", postFormat.format(new Date()), null));
        return resultList;
    }
    
    private List<TopicModel> createConcatDomainImageDataList() {
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        resultList.add(new TopicModel("0 image", null, "No Image Here"));
        resultList.add(new TopicModel("1 image", null, "<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>One Image Here"));
        resultList.add(new TopicModel("2 image", null, "<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>Two Image Here<br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>"));
        resultList.add(new TopicModel("3 image", null, "Three Image Here<br><br><img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">"));
        return resultList;
    }
}
