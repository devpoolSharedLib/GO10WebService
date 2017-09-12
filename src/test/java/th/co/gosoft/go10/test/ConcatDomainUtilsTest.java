package th.co.gosoft.go10.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import th.co.gosoft.go10.model.TopicModel;
import th.co.gosoft.go10.util.ConcatDomainUtils;

public class ConcatDomainUtilsTest {

    @Test
    public void deleteDomainImagePathNoneImageTest(){
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("None Image Here !!!");
        assertEquals("None Image Here !!!", ConcatDomainUtils.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void deleteDomainImagePathOneImageTest(){
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("<img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/ZLY65XZ7\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">");
        assertEquals("<img src=\"ZLY65XZ7\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">", ConcatDomainUtils.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void deleteDomainImagePathTwoImageTest(){
        TopicModel topicModel = new TopicModel();
        topicModel.setContent("<img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>And Next Image<br><br><img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>");
        assertEquals("<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>And Next Image<br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>", ConcatDomainUtils.deleteDomainImagePath(topicModel.getContent()));
    }
    
    @Test
    public void concatDomainImagePathTest(){
        List<TopicModel> topicModelList = createConcatDomainImageDataList();
        assertEquals("No Image Here", ConcatDomainUtils.concatDomainImagePath(topicModelList.get(0).getContent()));
        assertEquals("<img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>One Image Here", ConcatDomainUtils.concatDomainImagePath(topicModelList.get(1).getContent()));
        assertEquals("<img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>Two Image Here<br><br><img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>",  ConcatDomainUtils.concatDomainImagePath(topicModelList.get(2).getContent()));
        assertEquals("Three Image Here<br><br><img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">",  ConcatDomainUtils.concatDomainImagePath(topicModelList.get(3).getContent()));
    }

    @Test
    public void concatDomainVideoPathTest(){
    	TopicModel topicModel = new TopicModel();
    	topicModel.setContent("<source src=\"JZU50IQPKZXVDY9IITFJ9R8XAE733.mp4\"></a>");
    	assertEquals("<source src=\"https://s3-ap-southeast-1.amazonaws.com/x68br7d28053/GO10/JZU50IQPKZXVDY9IITFJ9R8XAE733.mp4\"></a>",ConcatDomainUtils.concatDomainImagePath(topicModel.getContent()));
    }
    
    private List<TopicModel> createConcatDomainImageDataList() {
        List<TopicModel> resultList = new ArrayList<TopicModel>();
        resultList.add(new TopicModel("0 image", null, "No Image Here"));
        resultList.add(new TopicModel("1 image", null, "<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>One Image Here"));
        resultList.add(new TopicModel("2 image", null, "<img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br>Two Image Here<br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br>"));
        resultList.add(new TopicModel("3 image", null, "Three Image Here<br><br><img src=\"DI2EFC\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\"><br><br><img src=\"2QEJ337YA\" width=\"230\" height=\"408\" alt=\"insertImageUrl\">"));
        resultList.add(new TopicModel("4 image", null, "<a href=\"IJFBU9E.mp4\" style=\"display: none ></a>"));
        return resultList;
    }
}
