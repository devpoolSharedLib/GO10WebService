package th.co.gosoft.go10.rest.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import th.co.gosoft.go10.util.PropertiesUtils;

public class AdminService {
    
    private static final String GET_URL = PropertiesUtils.getProperties("domain_email_ful_text");

    public String getEmailFulTextSerch(String empEmail){
        String getURL = GET_URL+"?empEmail="+empEmail;
        System.out.println("url : "+getURL);
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(getURL).build();
            Response response = client.newCall(request).execute();
            String responseString =  response.body().string();
            System.out.println("response String : "+responseString);
            return responseString;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
    }
    
}
