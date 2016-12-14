package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.UserAdminModel;
import th.co.gosoft.go10.model.UserAuthenModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.KeyStoreUtils;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Database db = CloudantClientUtils.getDBNewInstance();

	public AdminLoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    String email = request.getParameter("email").toLowerCase();
        String password = request.getParameter("password");
        
        List<UserAdminModel> userAdminModelList = db.findByIndex(getUserAdminByEmailJsonString(email), UserAdminModel.class);
        if(checkUserAdmin(userAdminModelList)){
        	try {
				if(checkUserByUserPassword(email,password)){
					System.out.println("Login Complete");
					session.setAttribute("userAdminModel", userAdminModelList.get(0));
					session.setAttribute("empEmail", email);
					session.setAttribute("status", "complete");
					response.sendRedirect("main.jsp");
				}else{
				    session.setAttribute("status", "<span style='color:red'>Invalid Authentication.</span>");
				    response.sendRedirect("login.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }else{
            session.setAttribute("status", "<span style='color:red'>No User Admin.</span>");
        	response.sendRedirect("login.jsp");
        }
	}

	private boolean checkUserAdmin(List<UserAdminModel> userAdminModelList){
        try {
        	if (userAdminModelList == null || userAdminModelList.isEmpty()){
        		System.out.println("No User Admin");
        		return false;
        	}else{
        		System.out.println("emailAdmin : " + userAdminModelList.get(0).getEmpEmail());
        		return true;
        	}
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
	
	public boolean checkUserByUserPassword(String email, String password) throws IOException, Exception {
	    List<UserAuthenModel> userAuthenModelList = db.findByIndex(getUserAuthenByEmailJsonString(email), UserAuthenModel.class);
        if(userAuthenModelList != null && !userAuthenModelList.isEmpty() && !KeyStoreUtils.authenPassword(userAuthenModelList.get(0).getPassword(), password)){
        	System.out.println("Invalid Authentication");
	        return false;
	    } else {
	        System.out.println("GET Complete");
	        return true;
	    }
    }
	
	 private String getUserAdminByEmailJsonString(String empEmail){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"selector\": {");
        sb.append("\"_id\": {\"$gt\": 0},");
        sb.append("\"$and\": [{ \"type\": \"admin\" }, { \"empEmail\": \""+empEmail+"\" }]");
        sb.append("},");
        sb.append("\"fields\": [\"_id\",\"_rev\",\"empEmail\",\"type\",\"roomAdmin\"]}");
        return sb.toString();
	 }
	 
	 private String getUserAuthenByEmailJsonString(String email){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"authen\"}, {\"empEmail\":\""+email+"\"}] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empEmail\",\"password\",\"type\",\"token\"]}");
        
        return stingBuilder.toString();
    }
}
