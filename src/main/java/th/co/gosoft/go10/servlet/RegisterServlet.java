package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

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
import th.co.gosoft.go10.util.EmailUtils;
import th.co.gosoft.go10.util.EncryptUtils;
import th.co.gosoft.go10.util.KeyStoreUtils;
import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SUBJECT = "GO10 - Please activate your account";
	private static final String EMAIL_CONTENT = "ขอบคุณที่ลงทะเบียน \n\nกรุณา copy และ  paste ใน  Google Chrome Browser \n\n";
	private static final String EMAIL_CONTACT = "\n\nขอบคุณครับ\nGO10";
	private static final String EMAIL_FOOTER = " \n\n\n หากท่านพบปัญหา หรือต้องการสอบถามข้อมูลเพิ่มเติมสามารถติดต่อได้ที่  thanomcho@gosoft.co.th, manitkan@gosoft.co.th, jirapaschi@gosoft.co.th";
	private static String FROM_EMAIL;
    private static String PASSWORD;
    private static String DOMAIN_LINK;
    
    public RegisterServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    try{
	        initialVariable();
	        String empSurName = request.getParameter("surname");
	        String empLastName = request.getParameter("lastname");
	        String empEmail = request.getParameter("email");
	        String password = request.getParameter("password");
	        String birthday = request.getParameter("birthday");
	        if(isInvalidInput(empSurName, empLastName, empEmail, password, birthday)){
	            throw new Exception("Invalid input");
	        } else if(!getUserByEmail(empEmail).isEmpty()){
	            request.setAttribute("status", "<span style='color:red'>this email is already registered.</span>");
                request.getRequestDispatcher("/registration.jsp").forward(request, response);
	        } else {
	            byte[] passEncrypt = encryptPassword(password);
	            String token = EncryptUtils.encode(empEmail);
	            String tokenVar = "?token=";
	            
	            Database db = CloudantClientUtils.getDBNewInstance();
                UserModel userModel = new UserModel();
                userModel.setEmpName(empSurName + " " + empLastName);
                userModel.setEmpEmail(empEmail);
                userModel.setAvatarName("Avatar Name");
                userModel.setAvatarPic("default_avatar");
                userModel.setActivate(true);
                userModel.setType("user");
                userModel.setBirthday(birthday);
                db.save(userModel);
                
                UserAuthenModel userAuthenModel = new UserAuthenModel();
                userAuthenModel.setEmpEmail(empEmail);
                userAuthenModel.setPassword(passEncrypt);
                userAuthenModel.setType("authen");
                userAuthenModel.setToken(token);
                db.save(userAuthenModel);
                
//                String body = EMAIL_CONTENT + DOMAIN_LINK + tokenVar+token;
//                body += EMAIL_FOOTER;
//                body += EMAIL_CONTACT;
//                EmailUtils.sendFromGMail(FROM_EMAIL, PASSWORD, empEmail, SUBJECT, body);
                
//                request.setAttribute("status", "<span style='color:green'>Registration Complete<br>Please check your inbox and activate your account</span>");
                request.setAttribute("status", "<span style='color:green'>Register Complete.</span>");
                request.getRequestDispatcher("/registration.jsp").forward(request, response);
	        }
	    } catch (Exception e){
	        request.setAttribute("status", "Registration Error");
	        throw new RuntimeException(e.getMessage(), e);
	    }
	    
	}

    private byte[] encryptPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return desCipher.doFinal(password.getBytes());
    }

    private boolean isInvalidInput(String empSurName, String empLastName, String empEmail, String password, String birthday) {
        return empSurName == null || empSurName.isEmpty() || empLastName == null || empLastName.isEmpty() || empEmail == null || empEmail.isEmpty() ||  password == null || password.isEmpty() || birthday == null || birthday.isEmpty();
    }
	
	
	
	private List<UserModel> getUserByEmail(@QueryParam("token") String email) {
        System.out.println(">>>>>>>>>>>>>>>>>>> getUserByEmail() // email : "+email);
        Database db = CloudantClientUtils.getDBNewInstance();
        List<UserModel> userModelList = db.findByIndex(getUserByEmailJsonString(email), UserModel.class);
        System.out.println("GET Complete");
        return userModelList;
    }
	
	private String getUserByEmailJsonString(String email){
        StringBuilder stingBuilder = new StringBuilder();
        stingBuilder.append("{\"selector\": {");
        stingBuilder.append("\"_id\": {\"$gt\": 0},");
        stingBuilder.append("\"$and\": [{\"type\": \"authen\"}, {\"empEmail\":\""+email+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"accountId\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"token\",\"activate\",\"type\"]}");
        
        return stingBuilder.toString();
    }
	 
	private static void initialVariable(){
        String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            FROM_EMAIL = System.getenv("send_email");
            PASSWORD = System.getenv("send_email_password");
            DOMAIN_LINK = System.getenv("domain_acctivate");
        } else {
            Properties prop = PropertiesUtils.getProperties();
            FROM_EMAIL = prop.getProperty("send_email");
            PASSWORD = prop.getProperty("send_email_password");
            DOMAIN_LINK = prop.getProperty("domain_acctivate");
        }
	 }
}
