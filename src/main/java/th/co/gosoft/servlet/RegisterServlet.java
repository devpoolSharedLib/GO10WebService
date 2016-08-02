package th.co.gosoft.servlet;

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
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import com.cloudant.client.api.Database;

import th.co.gosoft.model.UserAuthenModel;
import th.co.gosoft.model.UserModel;
import th.co.gosoft.util.CloudantClientUtils;
import th.co.gosoft.util.EncryptUtils;
import th.co.gosoft.util.KeyStoreUtils;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SUBJECT = "GO10, activate your email";
	private static final String FROM_EMAIL = "gosoft.sharedlib@gmail.com";
    private static final String PASSWORD = "sharedlib";
	private static final String EMAIL_CONTENT = "Thank you for registration.\n\nPlease click this link to activate your email. \n\n";
	private static final String DOMAIN_LINK = "https://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/activateUserByToken";
	
    public RegisterServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    try{
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
                userModel.setActivate(false);
                userModel.setType("user");
                userModel.setBirthday(birthday);
                db.save(userModel);
                
                UserAuthenModel userAuthenModel = new UserAuthenModel();
                userAuthenModel.setEmpEmail(empEmail);
                userAuthenModel.setPassword(passEncrypt);
                userAuthenModel.setType("authen");
                userAuthenModel.setToken(token);
                db.save(userAuthenModel);
                
                String body = EMAIL_CONTENT + DOMAIN_LINK+tokenVar+token;
                body += "\n\n\nBest Regards,";
                sendFromGMail(FROM_EMAIL, PASSWORD, empEmail, SUBJECT, body);
                
                request.setAttribute("status", "<span style='color:green'>Registration Complete, invitation code will send to your email.</span>");
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
	
	private static void sendFromGMail(String from, String pass, String to, String subject, String body) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, null);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from, "GO10", "utf-8"));;
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to, false));
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport();
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
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
        stingBuilder.append("\"$and\": [{\"type\": \"user\"}, {\"empEmail\":\""+email+"\"} ] ");
        stingBuilder.append("},");
        stingBuilder.append("\"fields\": [\"_id\",\"_rev\",\"accountId\",\"empName\",\"empEmail\",\"avatarName\",\"avatarPic\",\"token\",\"activate\",\"type\"]}");
        
        return stingBuilder.toString();
    }
}
