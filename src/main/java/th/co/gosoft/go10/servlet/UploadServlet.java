package th.co.gosoft.go10.servlet;

import java.io.IOException;
import java.io.InputStream;
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
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Payloads;

import th.co.gosoft.go10.util.ObjectStorageUtils;

@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DOMAIN_DOWNLOAD_SERVLET = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/DownloadServlet?imageName=";
	private final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private final java.util.Random rand = new java.util.Random();
	private final Set<String> identifiers = new HashSet<String>();
	
	private boolean isMultipart;
	private int maxFileSize = 50000 * 1024;
	private int maxMemSize = 400 * 1024;
	private OSClient os;
       
    public UploadServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check that we have a file upload request
	    os = ObjectStorageUtils.connectObjectStorageService();
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
			upload.setSizeMax( maxFileSize );
	
			try{ 
				// Parse the request to get file items.
				List<FileItem> fileItems = upload.parseRequest(request);
	
				Iterator<FileItem> i = fileItems.iterator();
				while (i.hasNext()){
					FileItem fi = (FileItem)i.next();
					if (!fi.isFormField()){
						
					    InputStream is = fi.getInputStream();
						String randomName =  randomIdentifier();
						
					    String etag = os.objectStorage().objects().put("go10", randomName, Payloads.create(is));
						
					    if(etag != null && !"".equals(etag)){
					        System.out.println("{\"imgUrl\" : \""+DOMAIN_DOWNLOAD_SERVLET+ randomName +"\"}");
	                        out.print("{\"imgUrl\" : \""+DOMAIN_DOWNLOAD_SERVLET+ randomName +"\"}");
					    }
						
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
