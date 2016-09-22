package th.co.gosoft.go10.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.UserAuthenModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.EmailUtils;
import th.co.gosoft.go10.util.KeyStoreUtils;
import th.co.gosoft.go10.util.PropertiesUtils;

@Path("user")
public class UserService {
	private static final String SUBJECT = "GO10, reset your password";
	private static final String EMAIL_CONTENT = "\nPlease copy and paste the following link in Google Chrome Browser. \n\n";
	private static final String DOMAIN_LINK_RESET_PASSWORD = "https://go10webservice.au-syd.mybluemix.net/GO10WebService/resetpassword.jsp";
	
	private static String FROM_EMAIL;
    private static String PASSWORD;
    
    @GET
    @Path("/getUserByAccountId")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<UserModel> getUserByAccountId(@QueryParam("accountId") String accountId) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserByAccountId() // accountId : "+accountId);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserModel> userModelList = db.findByIndex(getUserByAccountIdJsonString(accountId), UserModel.class);
        System.out.println("GET Complete");
        return userModelList;
    }
    
    @GET
    @Path("/getUserByToken")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<UserModel> getUserByToken(@QueryParam("token") String token) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserByToken() // token : "+token);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserModel> userModelList = db.findByIndex(getUserByTokenJsonString(token), UserModel.class);
        System.out.println("GET Complete");
        return userModelList;
    }
    
    @GET
    @Path("/getUserByUserPassword")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<UserModel> getUserByUserPassword(@QueryParam("email") String email, @QueryParam("password") String password) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserByUserPassword() // user : "+email+", password : "+password);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserAuthenModel> userAuthenModelList = db.findByIndex(getUserAuthenByEmailJsonString(email), UserAuthenModel.class);
        
        if(userAuthenModelList != null && !userAuthenModelList.isEmpty() && !authenPassword(userAuthenModelList.get(0).getPassword(), password)){
            System.out.println("Invalid Authentication");
            return new ArrayList<UserModel>();
        } else {
            System.out.println("GET Complete");
            List<UserModel> userModelList = db.findByIndex(getUserByEmailJsonString(email), UserModel.class);
            return userModelList;
        }
    }
    
    @GET
    @Path("/activateUserByToken")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String activateUserByToken(@QueryParam("token") String token) {
        System.out.println(">>>>>>>>>>>>>>>>>>> activateUserByToken() // token : " + token);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserAuthenModel> userAuthenModelList = db.findByIndex(activateUserByTokenJsonString(token), UserAuthenModel.class);
        if(userAuthenModelList != null && !userAuthenModelList.isEmpty()){
        	List<UserModel> userModelList =  db.findByIndex(getUserByEmailJsonString(userAuthenModelList.get(0).getEmpEmail()), UserModel.class);
       	 		if(userModelList.get(0).isActivate()){
       	 			return "This account has been activated.";
       	 		}else{
       	 			UserModel userModel = userModelList.get(0);
		       		userModel.setActivate(true);
		       		db.update(userModel);
		       		System.out.println("You have update the user activate true");
		       		return "Your account is activated. You can search \"GO10\" to download the application on App Store and Play Store."; 
       	 		}
        }else{
        	System.out.println("Invalid Authentication");
            return "Invalid Authentication, Please contact development team : thanomcho@gosoft.co.th, manitkan@gosoft.co.th, jirapaschi@gosoft.co.th";
        }
        
    }
    
    @GET
    @Path("/resetPasswordByEmail")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String resetPasswordByEmail(@QueryParam("email") String email) {
        System.out.println(">>>>>>>>>>>>>>>>>>> resetPasswordByEmail() // email : " + email);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserModel> UserModelList = db.findByIndex(getUserByEmailJsonString(email), UserModel.class);
        if(UserModelList != null && !UserModelList.isEmpty()){
        	List<UserAuthenModel> userAuthenModelList =  db.findByIndex(getUserAuthenByEmailJsonString(UserModelList.get(0).getEmpEmail()), UserAuthenModel.class);
   	 		if(UserModelList.get(0).isActivate()){
   	 		    initialVariable();
   	 			System.out.println("Send Email");
       	 		String tokenVar = "?token=";
       	 		String token = userAuthenModelList.get(0).getToken();
       	 		System.out.println(">>>>>>>>>"+userAuthenModelList.get(0).getToken() + " " + token);
       	 		String body = EMAIL_CONTENT + DOMAIN_LINK_RESET_PASSWORD+tokenVar+token;
                body += "\n\n\nBest Regards,";
                EmailUtils.sendFromGMail(FROM_EMAIL, PASSWORD, email, SUBJECT, body);
       	 		return "You can check e-mail for reset password.";
   	 		}else{
   	 		    System.out.println("User has not been activated.");
	       		return "User does not exist on the system."; 
   	 		}
        }else{
        	System.out.println("User does not exist on the system.");
       		return "User does not exist on the system."; 
        }
    }
    
    @PUT
    @Path("/updateUser")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateUser(UserModel userModel) {
        System.out.println(">>>>>>>>>>>>>>>>>>> updateUser()");
        userModel.setActivate(true);
        Database db = CloudantClientUtils.getDBNewInstance();
        com.cloudant.client.api.model.Response response = db.update(userModel);
        String result = response.getRev();
        System.out.println(">>>>>>>>>>>>>>>>>>> post result Rev : "+result);
        System.out.println("PUT Complete");
        return Response.status(201).entity(result).build();
    }
    
    private boolean authenPassword(byte[] queryPassword, String inputPassword) {
        SecretKey secretKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        Cipher desCipher;
        try {
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] textDecrypted = desCipher.doFinal(queryPassword);
            String passDecryptedString = new String(textDecrypted);
            return passDecryptedString.equals(inputPassword);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private String getUserByAvatarNameJsonString(String avatarName){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"avatarName\":\""+avatarName+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"accountId\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"token\",\"activate\",\"type\"]}");
        
        return stingBuilder.toString();
    }
    
    private String getUserByAccountIdJsonString(String accountId){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"accountId\":\""+accountId+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"accountId\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"token\",\"activate\",\"type\"]}");
        
        return stingBuilder.toString();
    }
    
    private String getUserByTokenJsonString(String token){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"token\":\""+token+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"accountId\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"token\",\"activate\",\"type\"]}");
        
        return stingBuilder.toString();
    }
    
    private String getUserAuthenByEmailJsonString(String email){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"authen\"}, {\"empEmail\":\""+email+"\"}] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empEmail\",\"password\",\"type\",\"token\"]}");
        
        return stingBuilder.toString();
    }
    
    private String getUserByEmailJsonString(String email){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"empEmail\":\""+email+"\"}] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"activate\",\"type\",\"birthday\"]}");
        
        return stingBuilder.toString();
    }
    
    private String activateUserByTokenJsonString(String token){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"authen\"}, {\"token\":\""+token+"\"}] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empEmail\",\"password\",\"type\",\"token\"]}");
        
        return stingBuilder.toString();
    }
    
    private static void initialVariable(){
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            FROM_EMAIL = System.getenv("send_email");
            PASSWORD = System.getenv("send_email_password");
        } else {
            Properties prop = PropertiesUtils.getProperties();
            FROM_EMAIL = prop.getProperty("send_email");
            PASSWORD = prop.getProperty("send_email_password");
        }
     }
    
    
}
