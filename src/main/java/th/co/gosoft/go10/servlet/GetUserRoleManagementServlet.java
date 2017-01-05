package th.co.gosoft.go10.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
	    String roomId = (String) request.getSession().getAttribute("roomId");
//	    String roomId = "rm01";
        String getURL = GET_URL+"?roomId="+roomId;
        
        try{
            OkHttpClient client = new OkHttpClient();
            Request okHttpRequest = new Request.Builder().url(getURL).build();
            Response okHttpResponse = client.newCall(okHttpRequest).execute();
            String responseString =  okHttpResponse.body().string();
            System.out.println("response string : "+responseString);
            response.setContentType("application/json");
            response.getWriter().print(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
	}

}
