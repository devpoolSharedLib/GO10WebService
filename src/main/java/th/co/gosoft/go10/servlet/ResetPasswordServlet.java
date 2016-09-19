package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.UserAuthenModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.KeyStoreUtils;

/**
 * Servlet implementation class ResetPasswordServlet
 */
@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ResetPasswordServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String password = request.getParameter("password");
		System.out.println("password : " + password);
		request.getQueryString();
		String token = request.getParameter("token");
		System.out.println("token : " + token);
		List<UserAuthenModel> userAuthenModelList = getUserByToken(token);
		
		try{
		if(isInvalidInput(password, token)){
            throw new Exception("Invalid input");
        } else if(userAuthenModelList.isEmpty()){
            request.setAttribute("status", "<span style='color:red'>User does not exist on the system.</span>");
            request.getRequestDispatcher("/resetpassword.jsp").forward(request, response);
        }else{
        	byte[] passEncrypt = encryptPassword(password);
        	UserAuthenModel userAuthenModel = userAuthenModelList.get(0);
        	userAuthenModel.setPassword(passEncrypt);
        	Database db = CloudantClientUtils.getDBNewInstance();
        	db.update(userAuthenModel);
       		System.out.println("Update Password Complete");
        	request.setAttribute("status", "<span style='color:green'>Reset Password Complete.</span>");
            request.getRequestDispatcher("/resetpassword.jsp").forward(request, response);
        }

		}catch (Exception e){
	        request.setAttribute("status", "Registration Error");
	        throw new RuntimeException(e.getMessage(), e);
	    }
	}
	
	private boolean isInvalidInput(String password, String token) {
        return password == null || password.isEmpty() || token == null || token.isEmpty();
    }
	
	private List<UserAuthenModel> getUserByToken(@QueryParam("token") String token) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserByEmail() // email : "+token);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserAuthenModel> userAuthenModelList = db.findByIndex(getUserByTokenJsonString(token), UserAuthenModel.class);
        System.out.println("GET Complete");
        return userAuthenModelList;
    }
	
	private String getUserByTokenJsonString(String token){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"authen\"}, {\"token\":\""+token+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",,\"empEmail\",\"token\",\"type\"]}");

        return stingBuilder.toString();
    }
	
	private byte[] encryptPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return desCipher.doFinal(password.getBytes());
    }

}
