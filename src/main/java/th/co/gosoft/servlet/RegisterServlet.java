package th.co.gosoft.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;

import th.co.gosoft.model.UserModel;
import th.co.gosoft.util.CloudantClientMgr;
import th.co.gosoft.util.EncryptUtils;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RegisterServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        String empName = request.getParameter("name");
	    String empEmail = request.getParameter("email");
	    System.out.println("name : "+empName+" & email : "+empEmail);
	    
	    String token = EncryptUtils.encode(empEmail);
	    
	    UserModel userModel = new UserModel();
	    userModel.setEmpName(empName);
	    userModel.setEmpEmail(empEmail);
	    userModel.setToken(token);
	    userModel.setActivate(false);
	    userModel.setType("user");
	    Database db = CloudantClientMgr.getDBNewInstance();
	    db.save(userModel);
	    
	    System.out.println("token : "+token);
	    request.setAttribute("token", token);
        request.getRequestDispatcher("/registration.jsp").forward(request, response);
	    
	}

}
