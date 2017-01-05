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
	    HttpURLConnection con = null;
		String empEmail = request.getParameter("empEmail");
		String getURL = GET_URL+"?empEmail="+empEmail;
        System.out.println("url : "+getURL);
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
