package th.co.gosoft.rest;

import java.util.ArrayList;
import java.util.List;

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

import th.co.gosoft.model.UserAuthenModel;
import th.co.gosoft.model.UserModel;
import th.co.gosoft.util.CloudantClientUtils;
import th.co.gosoft.util.KeyStoreUtils;

@Path("user")
public class UserService {

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
            List<UserModel> userModelList = db.findByIndex(getUserByUserPasswordJsonString(email), UserModel.class);
            return userModelList;
        }
    }
    
    @GET
    @Path("/activateUserByToken")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String activateUser(@QueryParam("token") String token) {
        System.out.println(">>>>>>>>>>>>>>>>>>> activateUserByToken() // token : " + token);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserAuthenModel> userAuthenModelList = db.findByIndex(activateUserByTokenJsonString(token), UserAuthenModel.class);
        if(userAuthenModelList != null && !userAuthenModelList.isEmpty()){
        	List<UserModel> userModelList =  db.findByIndex(getUserByUserPasswordJsonString(userAuthenModelList.get(0).getEmpEmail()), UserModel.class);
       	 		if(userModelList.get(0).isActivate()){
       	 			return "Your account has been activated";
       	 		}else{
       	 			UserModel userModel = userModelList.get(0);
		       		userModel.setActivate(true);
		       		db.update(userModel);
		       		System.out.println("You have update the user activate true");
		       		return "Complete Registration"; 
       	 		}
        }else{
        	System.out.println("Invalid Authentication");
            return "Invalid Authentication";
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
        System.out.println("You have update the user");

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
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"empEmail\",\"password\",\"type\"]}");
        
        return stingBuilder.toString();
    }
    
    private String getUserByUserPasswordJsonString(String email){
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
    
    
}
