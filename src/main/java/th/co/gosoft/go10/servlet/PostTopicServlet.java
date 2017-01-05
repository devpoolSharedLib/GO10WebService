package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cloudant.client.api.Database;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.gosoft.go10.model.NewTopicModel;
import th.co.gosoft.go10.model.UserAdminModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/PostTopicServlet")
public class PostTopicServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final String URL_POST = PropertiesUtils.getProperties("url_post_topic");
	
    public PostTopicServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
		
//        String empEmail = (String) session.getAttribute("empEmail");
//        List<UserModel> userModelList = getUserModel(empEmail);
//		
//		List<String> myString = new ArrayList<String>();
//		myString.add("rm01");
//		useradminmodel = new UserAdminModel();
//		useradminmodel.setRoomAdmin(myString);
		

    	
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
    	
		if(postTopic(jsonInString)){
			System.out.println("Post Topic Complete");
			session.setAttribute("statusPost", "Post Topic Complete");
			response.sendRedirect("posttopic.jsp");
		}else{
			System.out.println("Error Post Topic");  
		}
	}

	private boolean postTopic(String jsonInString){
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
