package th.co.gosoft.go10.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.gosoft.go10.model.TopicManagementModel;
import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/UpdatePinServlet")
public class UpdatePinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_PIN_TOPIC_URL = PropertiesUtils.getProperties("domain_save_pin_topic");
	private static final String DELETE_PIN_TOPIC_URL = PropertiesUtils.getProperties("domain_delete_pin_topic");

	public UpdatePinServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    ObjectMapper mapper = new ObjectMapper();
	    TopicManagementModel topicManagementModel = parseJSONrequestToModel(request, mapper);
	    
	    int responseStatus = callPostWebSrvice(mapper, topicManagementModel.getUnsavePinList(), SAVE_PIN_TOPIC_URL);
	    if(topicManagementModel.getDeletePinList() != null && topicManagementModel.getDeletePinList().size() != 0) {
	        responseStatus = callPostWebSrvice(mapper, topicManagementModel.getDeletePinList(), DELETE_PIN_TOPIC_URL);
	    }
	    response.setStatus(responseStatus);
        response.getWriter().print(responseStatus);
	}

	private int callPostWebSrvice(ObjectMapper mapper, Object dataObject, String url) {
	    
	    HttpURLConnection con = null;
	    try{
            URL object = new URL(url);
            con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(mapper.writeValueAsString(dataObject));
            osw.flush();
            osw.close();
            return con.getResponseCode();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private TopicManagementModel parseJSONrequestToModel(HttpServletRequest request, ObjectMapper mapper) throws ServletException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
        String jsonInputSearchCriteria = "";
        if (bufferedReader != null) {
            jsonInputSearchCriteria = bufferedReader.readLine();
        }
        return mapper.readValue(jsonInputSearchCriteria, TopicManagementModel.class);
    }
}
