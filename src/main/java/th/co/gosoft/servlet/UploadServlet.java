package th.co.gosoft.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private final java.util.Random rand = new java.util.Random();
	private final Set<String> identifiers = new HashSet<String>();
	
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50000 * 1024;
	private int maxMemSize = 400 * 1024;
	private File file ;
       
    public UploadServlet() {
        super();
    }
    
    public void init( ){
        // Get the file location where it would be stored.
        
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String fullPath;
        try {
            fullPath = URLDecoder.decode(path, "UTF-8");
            String pathArr[] = null;
            
            if(fullPath.contains("/WEB-INF/classes/")){
                pathArr = fullPath.split("/WEB-INF/classes/");
                filePath = pathArr[0] + "/images";
            } else if(fullPath.contains("/target/classes/")) {
                pathArr = fullPath.split("/target/classes/");
                filePath = pathArr[0] + "/src/main/webapp/images";
            }   
            
            System.out.println("SubPath : "+pathArr[0]);
            System.out.println("fullPath : "+filePath);
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }

     }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );

		if(!isMultipart){
			System.out.println("No file upload");
			out.print("{reponse:\"No file uploaded\"}");
			
		}else{
		
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// maximum size that will be stored in memory
			factory.setSizeThreshold(maxMemSize);
			
			File theDir = new File(filePath);

			// if the directory does not exist, create it
			if (!theDir.exists()) {
			    System.out.println("creating directory: " + filePath);
			    boolean result = false;

			    try{
			        theDir.mkdir();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("DIR created");  
			    }
			}
			
			System.out.println(theDir);
			
			factory.setRepository(theDir);
	
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax( maxFileSize );
	
			try{ 
				// Parse the request to get file items.
				List<FileItem> fileItems = upload.parseRequest(request);
	
				Iterator<FileItem> i = fileItems.iterator();
				while (i.hasNext()){
					FileItem fi = (FileItem)i.next();
					if (!fi.isFormField()){
						
						String randomName =  randomIdentifier()+".jpg";
						file = new File(filePath + File.separator + randomName) ;
						fi.write(file) ;

						System.out.println("{\"imgUrl\" : \"/GO10WebService/images/"+ randomName +"\"}");
						
						out.print("{\"imgUrl\" : \"/GO10WebService/images/"+ randomName +"\"}");
						
					}
				}
			} catch(Exception ex) {
				System.err.println(ex);
			}
		}
	}
	
	public String randomIdentifier() {
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
