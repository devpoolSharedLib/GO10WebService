package th.co.gosoft.go10.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cloudant.client.api.Database;

import th.co.gosoft.go10.model.UserAuthenModel;
import th.co.gosoft.go10.model.UserModel;
import th.co.gosoft.go10.util.CloudantClientUtils;
import th.co.gosoft.go10.util.EncryptUtils;
import th.co.gosoft.go10.util.KeyStoreUtils;



public class RegistrationBatch {
	private static UserModel userModel ;
	private static UserAuthenModel userAuthenModel;
	
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("TEST");
		String excelFilePath = "Registration.xlsx";
		readExcelFile(excelFilePath);
    }
	
	public static void readExcelFile(String excelFilePath) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		userModel = new UserModel();
		userAuthenModel = new UserAuthenModel();
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    Sheet firstSheet = workbook.getSheetAt(0);
	    Iterator<Row> iterator = firstSheet.iterator();
	    int i = 1;
	    while (iterator.hasNext()) {
	    	
	        Row nextRow = iterator.next();
	        Iterator<Cell> cellIterator = nextRow.cellIterator();
	        
	 
	        while (cellIterator.hasNext()) {
	            Cell nextCell = cellIterator.next();
	            int columnIndex = nextCell.getColumnIndex();
		 
	            switch (columnIndex) {
	            case 0:
	            	userAuthenModel.setPassword(encryptPassword((String) getCellValue(nextCell)));
	                break;
	            case 1:
	            	userModel.setEmpName((String) getCellValue(nextCell));
	                break;
	            case 2:
	            	userModel.setEmpEmail((String) getCellValue(nextCell));
	            	userAuthenModel.setEmpEmail((String) getCellValue(nextCell));
	                break;
	            case 3:
	            	userModel.setBirthday((String) getCellValue(nextCell));
	                break;
	            }
	 
	 
	        }
	        userModel.setAvatarName("Avatar Name");
	        userModel.setAvatarPic("default_avatar");
	        userModel.setActivate(true);
	        userModel.setType("user");
	       
	       userAuthenModel.setType("authen");
	       String token = EncryptUtils.encode(userAuthenModel.getEmpEmail());
	       userAuthenModel.setToken(token);
	       
//	       System.out.println("Name : " + userModel.getEmpName());
//		    System.out.println("Email : " + userModel.getEmpEmail());
//		    System.out.println("Avartar Pic : " + userModel.getAvatarPic());
//		    System.out.println("Avatar Name : " + userModel.getAvatarName());
//		    System.out.println("type : " + userModel.getType());
//		    System.out.println("activate : " + userModel.isActivate());
//		    System.out.println("Birthday : " + userModel.getBirthday());	
//		    
	       saveToDatabase(userModel);
		   saveToDatabase(userAuthenModel);
		   System.out.println("Count : " + i);
		   i++;
	    }
	 
	    workbook.close();
	    inputStream.close();
	}
	
	private static Object getCellValue(Cell cell) {
	    switch (cell.getCellType()) {
	    case Cell.CELL_TYPE_STRING:
	        return cell.getStringCellValue();
	 
	    case Cell.CELL_TYPE_BOOLEAN:
	        return cell.getBooleanCellValue();
	 
	    case Cell.CELL_TYPE_NUMERIC:
	        return cell.getNumericCellValue();
	    }
	 
	    return null;
	}
	
    private static byte[] encryptPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = KeyStoreUtils.getKeyFromCloudant("password-key");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return desCipher.doFinal(password.getBytes());
    }
	
    private static void saveToDatabase(Object model){
      Database db = CloudantClientUtils.getDBNewInstance();
      db.save(model);
      System.out.println("Insert " + model + " Complete");
    }

}
