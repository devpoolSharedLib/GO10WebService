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

@WebServlet("/GetEmailFullTextSearchServlet")
public class GetEmailFullTextSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String GET_URL = PropertiesUtils.getProperties("domain_email_full_text_search");
       
    public GetEmailFullTextSearchServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String empEmail = request.getParameter("empEmail");
		String getURL = GET_URL+"?empEmail="+empEmail;
        System.out.println("url : "+getURL);
        try{
            OkHttpClient client = new OkHttpClient();
            Request okHttpRequest = new Request.Builder().url(getURL).build();
            Response okHttpResponse = client.newCall(okHttpRequest).execute();
            String responseString =  okHttpResponse.body().string();
            response.setContentType("application/json");
            response.getWriter().print(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
	}

}
