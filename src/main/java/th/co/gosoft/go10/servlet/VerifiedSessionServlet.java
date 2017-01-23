package th.co.gosoft.go10.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/VerifiedSessionServlet")
public class VerifiedSessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public VerifiedSessionServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("VerifiedSessionServlet doPost");
	    	Boolean sessionTimeout = false;
        	HttpSession session = request.getSession(false);
        	
        	if (session == null || (session.getAttribute("userAdminModel") == null)) {
	    		sessionTimeout = true;
        	}
	    	
	    	ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), sessionTimeout);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
}
