package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import th.co.gosoft.go10.util.PropertiesUtils;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadVideoServlet")
public class UploadVideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private static final java.util.Random rand = new java.util.Random();
	private static final Set<String> identifiers = new HashSet<String>();
	private static final String BUCKET_NAME = PropertiesUtils.getProperties("s3_bucket_name");
	private static final String ACCESS_KEY = PropertiesUtils.getProperties("s3_access_key");
	private static final String SECRET_ACCESS_KEY = PropertiesUtils.getProperties("s3_secret_access_key");
    private static final String FOLDER_NAME = PropertiesUtils.getProperties("folder_name");
    private static final String DOMAIN_VIDEO_PATH = PropertiesUtils.getProperties("domain_image_path");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadVideoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_ACCESS_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		
		PrintWriter out = response.getWriter();
//		out.print("Upload!!!!");
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
//		System.out.println("Request : "+request.toString()+"\nResponse : "+response.toString());
		try{ 
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);
			System.out.println("File : "+fileItems.toString());
			Iterator<FileItem> i = fileItems.iterator();
			while (i.hasNext()){
				FileItem fi = (FileItem)i.next();
				if (!fi.isFormField()){
					
				    InputStream is = fi.getInputStream();
				    String randomFileName = randomFileName()+".mp4";
					String objectKey = FOLDER_NAME+"/"+randomFileName;
					
					byte[] contentBytes = IOUtils.toByteArray(fi.getInputStream());
                    Long contentLength = Long.valueOf(contentBytes.length);
                    System.out.println("contentLength : "+contentLength);
                    
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(contentLength);
					
                    PutObjectResult etag = s3client.putObject(new PutObjectRequest(BUCKET_NAME, objectKey, is, metadata));
					System.out.println(etag);
				    if(etag != null && !"".equals(etag)){
				        System.out.println("vidUrl : "+DOMAIN_VIDEO_PATH+"/"+FOLDER_NAME+"/"+ randomFileName);
                        out.print(DOMAIN_VIDEO_PATH+"/"+FOLDER_NAME+"/"+ randomFileName);
//                        out.print("https://go10.au-syd.mybluemix.net/GO10WebService/VideoPlayServlet/"+randomFileName);
				    }
				}
			}
		} catch(Exception e) {
			System.err.println(e);
		
		}
	}
	
	
	
	public String randomFileName() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(30)+5;
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
