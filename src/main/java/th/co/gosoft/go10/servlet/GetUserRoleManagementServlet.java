package th.co.gosoft.go10.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/GetUserRoleManagementServlet")
public class GetUserRoleManagementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String GET_URL = PropertiesUtils.getProperties("domain_get_user_role");
       
    public GetUserRoleManagementServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpURLConnection con = null;
	    String roomId = (String) request.getSession().getAttribute("roomId");
        String getURL = GET_URL+"?roomId="+roomId;
        
        try{
            URL obj = new URL(getURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if(status == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                response.setContentType("application/json");
                System.out.println("get user role : "+sb.toString());
                response.getWriter().print(sb.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
            
        }
	}

}
