package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.IOUtils;

import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private final java.util.Random rand = new java.util.Random();
	private final Set<String> identifiers = new HashSet<String>();
//	private final String folderName = "GO10";
	
	private static String bucketName;
	private String domainImagePath;
	private String folderName;
	private String accessKeyId;
	private String secretAccessKey;
	
	private boolean isMultipart;
	private int maxFileSize = 50000 * 1024;
	private int maxMemSize = 400 * 1024;
//	private OSClient<OSClientV3> os;
       
    public UploadServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            domainImagePath = System.getenv("domain_image_path");
            accessKeyId = System.getenv("s3_access_key");
            secretAccessKey = System.getenv("s3_secret_access_key");
            bucketName = System.getenv("s3_bucket_name");
            folderName = System.getenv("folder_name");
        } else {
            Properties prop = PropertiesUtils.getProperties();
            domainImagePath = prop.getProperty("domain_image_path");
            accessKeyId = prop.getProperty("s3_access_key");
            secretAccessKey = prop.getProperty("s3_secret_access_key");
            bucketName = prop.getProperty("s3_bucket_name");
            folderName = prop.getProperty("folder_name");
        }
	    
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        AmazonS3 s3client = new AmazonS3Client(credentials);
       
        
//	    os = ObjectStorageUtils.connectObjectStorageService();
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );

		if(!isMultipart){
			System.out.println("No file upload");
			out.print("{reponse:\"No file uploaded\"}");
			
		}else{
		
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(maxMemSize);
			
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxFileSize);
	
			try{ 
				// Parse the request to get file items.
				List<FileItem> fileItems = upload.parseRequest(request);
	
				Iterator<FileItem> i = fileItems.iterator();
				while (i.hasNext()){
					FileItem fi = (FileItem)i.next();
					if (!fi.isFormField()){
						
					    InputStream is = fi.getInputStream();
					    String randomFileName = randomFileName()+".jpg";
						String objectKey = folderName+"/"+randomFileName;
						
						byte[] contentBytes = IOUtils.toByteArray(fi.getInputStream());
                        Long contentLength = Long.valueOf(contentBytes.length);
                        System.out.println("contentLength : "+contentLength);
                        
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(contentLength);
						
                        PutObjectResult etag = s3client.putObject(new PutObjectRequest(bucketName, objectKey, is, metadata));
						
					    if(etag != null && !"".equals(etag)){
					        System.out.println("{\"imgUrl\" : \""+domainImagePath+"/"+folderName+"/"+ randomFileName +"\"}");
	                        out.print("{\"imgUrl\" : \""+domainImagePath+"/"+folderName+"/"+ randomFileName +"\"}");
					    }
						
					}
				}
			} catch(Exception e) {
				System.err.println(e);
			}
		}
	}
	
	public String randomFileName() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(12)+5;
	        for(int i = 0; i < length; i++){
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	            
	    }
	    
	    System.out.println("file random name : "+builder.toString());
	    identifiers.add(builder.toString());
	   
	    return builder.toString();
	}

}
