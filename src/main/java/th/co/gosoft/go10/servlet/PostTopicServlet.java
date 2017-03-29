package th.co.gosoft.go10.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cloudant.client.api.Database;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.gosoft.go10.model.ChoiceMasterModel;
import th.co.gosoft.go10.model.NewTopicModel;
import th.co.gosoft.go10.model.PollModel;
import th.co.gosoft.go10.model.QuestionModel;
import th.co.gosoft.go10.model.UserAdminModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/PostTopicServlet")
public class PostTopicServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final String URL_POST = PropertiesUtils.getProperties("url_post_topic");
    private static final String URL_POST_POLL = PropertiesUtils.getProperties("url_post_poll");
	private static String responseTopicId;
    public PostTopicServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		if(postTopic(request)){
			System.out.println("Post Topic Complete");
			 String poll = request.getParameter("checkboxPoll");
	            System.out.println("checkPoll : " + poll);
	            if(poll.equals("on")){
	            	if(postPoll(request,responseTopicId)){
	    				System.out.println("Post Poll Complete");
	    				session.setAttribute("statusPost", "Post Topic Complete");
	    				response.sendRedirect("posttopic.jsp");
	    			}else{
	    				System.out.println("Error Post Poll");
	    			}
	            }else{
	            	System.out.println("Pin not checked");
	            }
		}else{
			System.out.println("Error Post Topic");  
		}       
        
        
	}
        
        private boolean postPoll(HttpServletRequest request,String topicId) throws JsonProcessingException{
        	List<QuestionModel> questionModelList = new ArrayList<QuestionModel>();
            List<ChoiceMasterModel> choiceMasterModelList = new ArrayList<ChoiceMasterModel>();
            QuestionModel questionmodel;
            ChoiceMasterModel choicemastermodel;
           
            String dropdownCountQuestion = request.getParameter("dropdownCountQuestion");
            System.out.println("dropdownCountQuestion : " + dropdownCountQuestion);
            
            int countChoice;
            String questionTitle;
            String choiceTitle;
            for(int i=1;i<=Integer.parseInt(dropdownCountQuestion);i++){
            	System.out.println("questionNumberInput " + i + " : " + request.getParameter("questionNumberInput" + i));
            	questionmodel = new QuestionModel();
            	choiceMasterModelList = new ArrayList<ChoiceMasterModel>();
            	countChoice = 0;
            	questionmodel.setQuestionId("q"+i);
            	questionTitle = request.getParameter("questionNumberInput" + i);
        		byte[] bytequestiontitle = questionTitle.getBytes(StandardCharsets.ISO_8859_1);
            	questionTitle = new String(bytequestiontitle, StandardCharsets.UTF_8);
            	questionmodel.setQuestionTitle(questionTitle);
            	countChoice = Integer.parseInt(request.getParameter("dropdownCountChoiceQuestionNumber"+i));
            	for(int j=1;j<=countChoice;j++){
            		choicemastermodel = new ChoiceMasterModel();
            		choicemastermodel.setChoiceKey("q"+i+"c"+j);
            		System.out.println("setChoiceKey " +  "q"+i+"c"+j );
            		choiceTitle = request.getParameter("questionNumber"+i+"choiceNumberInput" + j);
            		byte[] bytechoicetitle= choiceTitle.getBytes(StandardCharsets.ISO_8859_1);
            		choiceTitle = new String(bytechoicetitle, StandardCharsets.UTF_8);
            		choicemastermodel.setChoiceTitle(choiceTitle);
            		System.out.println("setChoiceTitle " +  request.getParameter("questionNumber"+i+"choiceNumberInput" + j));
            		
            		choiceMasterModelList.add(choicemastermodel);
            	}
            	questionmodel.setChoiceMaster(choiceMasterModelList);
            	questionModelList.add(questionmodel);
            }
            
            
            String checkUserPoll = request.getParameter("defineUserPoll");
            System.out.println("checkUserPoll : " + checkUserPoll);
            String txtPoll = request.getParameter("txtPoll");
            System.out.println("txtPoll : " + txtPoll);
            
            List<String> empEmailPollList = new ArrayList<String>();
            if(checkUserPoll.equals("all")){
            	empEmailPollList.add("all");
            }else if(checkUserPoll.equals("specific")){
            	empEmailPollList.addAll(Arrays.asList(txtPoll.split("\\s*,\\s*")));
            }
            
            PollModel pollmodel = new PollModel();
            pollmodel.setDate("");
            pollmodel.setUpdateDate("");
            pollmodel.setQuestionMaster(questionModelList);
            pollmodel.setTopicId(topicId);
            pollmodel.setType("poll");
            pollmodel.setEmpEmailPoll(empEmailPollList);
            
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(pollmodel);
            
    	    HttpURLConnection con = null;
            try{
            	URL object=new URL(URL_POST_POLL);
            	con = (HttpURLConnection) object.openConnection();
            	con.setDoOutput(true);
            	con.setDoInput(true);
            	con.setRequestProperty("Content-Type", "application/json");
            	con.setRequestProperty("Accept", "application/json");
            	con.setRequestMethod("POST");
            	con.connect();
            	OutputStream os = con.getOutputStream();
            	OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            	osw.write(jsonInString);
            	osw.flush();
            	osw.close();
            	
            	int HttpResult = con.getResponseCode(); 
            	if (HttpResult == 201) {
            		return true;
            	} else {
            		System.out.println(con.getResponseMessage());  
            	    return false;
            	}  
            	
            } catch(Exception e) {
            	System.out.println("Error : " + e.getMessage());  
            	return false;
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                
            }
            
            //check questionModelList
//            int temp = 2;
//            System.out.println("questionModelList " + questionModelList );
//            for(int i=0;i<Integer.parseInt(dropdownCountQuestion);i++){
//            	System.out.println("getQuestionId " + questionModelList.get(i).getQuestionId());
//            	System.out.println("getQuestionTitle " + questionModelList.get(i).getQuestionTitle());
//            
//            	for(int j=0;j<temp;j++){
//            		System.out.println("getChoiceKey " + questionModelList.get(i).getChoiceMasterModelList().get(j).getChoiceKey());
//                	System.out.println("getChoiceTitle " +  questionModelList.get(i).getChoiceMasterModelList().get(j).getChoiceTitle());
//            
//            	}
//            	temp=3;
//            }
            
        }


	private boolean postTopic(HttpServletRequest request) throws JsonProcessingException{
		HttpSession session = request.getSession();
		String subject = request.getParameter("title");
		
		byte[] bytesubject = subject.getBytes(StandardCharsets.ISO_8859_1);
		subject = new String(bytesubject, StandardCharsets.UTF_8);
		
        String content = request.getParameter("articleContent");
        
        byte[] bytescontent = content.getBytes(StandardCharsets.ISO_8859_1);
        content = new String(bytescontent, StandardCharsets.UTF_8);
		
        System.out.println("title : " + subject);
        System.out.println("content : " + content);
        
        
        String roomId = (String) session.getAttribute("roomId");
        System.out.println("ROOM ID : " + roomId);
        
		UserAdminModel useradminmodel = (UserAdminModel) session.getAttribute("userAdminModel");
		System.out.println("RoomAdmin : " + useradminmodel.getRoomAdmin());
		System.out.println("empEmail : " + useradminmodel.getEmpEmail());
		
		
		String empEmail = (String) session.getAttribute("empEmail");
		System.out.println("empEmail : " + empEmail);
		
		List<UserModel> userModelList = getUserModel(empEmail);
    	
    	NewTopicModel newtopicmodel = new NewTopicModel();
		newtopicmodel.setAvatarName(userModelList.get(0).getAvatarName());
		newtopicmodel.setAvatarPic(userModelList.get(0).getAvatarPic());
        newtopicmodel.setContent(content);
        newtopicmodel.setCountLike(0);
        newtopicmodel.setEmpEmail(userModelList.get(0).getEmpEmail());
        newtopicmodel.setRoomId(roomId);	
        newtopicmodel.setSubject(subject);
        newtopicmodel.setType("host");
        newtopicmodel.setDate("");
        newtopicmodel.setUpdateDate("");
        
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(newtopicmodel);
        
	    HttpURLConnection con = null;
        try{
        	URL object=new URL(URL_POST);
        	con = (HttpURLConnection) object.openConnection();
        	con.setDoOutput(true);
        	con.setDoInput(true);
        	con.setRequestProperty("Content-Type", "application/json");
        	con.setRequestProperty("Accept", "application/json");
        	con.setRequestMethod("POST");
        	con.connect();
        	OutputStream os = con.getOutputStream();
        	OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        	osw.write(jsonInString);
        	osw.flush();
        	osw.close();
        	
        	int HttpResult = con.getResponseCode(); 
        	if (HttpResult == 201) {
        		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
        		responseTopicId = sb.toString();
        		return true;
        	} else {
        		System.out.println(con.getResponseMessage());  
        	    return false;
        	}  
        } catch(Exception e) {
        	System.out.println("Error : " + e.getMessage());  
        	return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
            
        }
	}
	
	private  List<UserModel> getUserModel(String empEmail) {
		 Database db = CloudantClientUtils.getDBNewInstance();
		 List<UserModel> userModelList = db.findByIndex(getUserByEmailJsonString(empEmail), UserModel.class);
		 System.out.println("usermodelList " + userModelList.get(0).getEmpEmail());
		 if (userModelList != null && !userModelList.isEmpty()) {
			    System.out.println("get User Model Complete");
				return userModelList;
		 } else {
				System.out.println("No User Model");
				return new ArrayList<UserModel>();
		 }
	}
	
	private String getUserByEmailJsonString(String email){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"empEmail\":\""+email+"\"}] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"activate\",\"type\",\"birthday\"]}");
        
        return stingBuilder.toString();
    }

}
