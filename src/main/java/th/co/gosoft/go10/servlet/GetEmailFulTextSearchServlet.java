package th.co.gosoft.go10.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import th.co.gosoft.go10.rest.client.AdminService;

@WebServlet("/GetEmailFulTextSearchServlet")
public class GetEmailFulTextSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetEmailFulTextSearchServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("empEmail");
		AdminService adminService = new AdminService();
		String responseString = adminService.getEmailFulTextSerch(email);
		response.setContentType("application/json");
        response.getWriter().print(responseString);
	}

}
