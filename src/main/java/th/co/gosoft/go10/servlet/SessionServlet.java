package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/SessionServlet")
public class SessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SessionServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession();
	    String roomId = request.getParameter("roomId");
	    String roomName = request.getParameter("roomName");
	    byte[] bytesubject = roomName.getBytes(StandardCharsets.ISO_8859_1);
	    roomName = new String(bytesubject, StandardCharsets.UTF_8);
        String currentPage = request.getParameter("currentPage");
        System.out.println("roomId : " + roomId + " roomName : " + roomName + " currentPage : " + currentPage);
        
        session.setAttribute("roomId", roomId);
        session.setAttribute("roomName", roomName);
		session.setAttribute("currentPage", "currentPage");
		response.sendRedirect(currentPage);
    }
}
