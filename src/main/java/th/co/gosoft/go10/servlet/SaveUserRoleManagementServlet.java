package th.co.gosoft.go10.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import th.co.gosoft.go10.model.UserRoleManagementModel;
import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/SaveUserRoleManagementServlet")
public class SaveUserRoleManagementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String POST_URL = PropertiesUtils.getProperties("domain_post_user_role");
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
       
    public SaveUserRoleManagementServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String roomId = (String) request.getSession().getAttribute("roomId");
	    ObjectMapper mapper = new ObjectMapper();
	    UserRoleManagementModel userRoleManagementModel = parseJSONrequestToModel(request, mapper);
	    userRoleManagementModel.setRoomId(roomId);
        try{
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, mapper.writeValueAsString(userRoleManagementModel));
            Request okHttpRequest = new Request.Builder().url(POST_URL).post(body).build();
            Response okHttpResponse = client.newCall(okHttpRequest).execute();
            int responseStatus =  okHttpResponse.code();
            response.setStatus(responseStatus);
            response.getWriter().print(responseStatus);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
	}

	private UserRoleManagementModel parseJSONrequestToModel(HttpServletRequest request, ObjectMapper mapper) throws ServletException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
        String jsonInputSearchCriteria = "";
        if (bufferedReader != null) {
            jsonInputSearchCriteria = bufferedReader.readLine();
        }
        return mapper.readValue(jsonInputSearchCriteria, UserRoleManagementModel.class);
    }

}
