package th.co.gosoft.go10.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.storage.object.SwiftObject;

import th.co.gosoft.go10.util.ObjectStorageUtils;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public DownloadServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    String fileName = request.getParameter("imageName");
	    
	    OSClient<OSClientV3> os = ObjectStorageUtils.connectObjectStorageService();
	    SwiftObject swiftObject = os.objectStorage().objects().get("go10", fileName);
	    DLPayload dp = swiftObject.download();
	    InputStream is = dp.getInputStream();
	    
	    response.setContentType("image/jpeg");  
	    ServletOutputStream out = response.getOutputStream();  
	      
	    BufferedInputStream bin = new BufferedInputStream(is);  
	    BufferedOutputStream bout = new BufferedOutputStream(out);  
	    int ch =0;  
	    while((ch=bin.read())!=-1) {  
	        bout.write(ch);  
	    }  
	      
	    bin.close();  
	    is.close();  
	    bout.close();  
	    out.close();  
	}
    
}
