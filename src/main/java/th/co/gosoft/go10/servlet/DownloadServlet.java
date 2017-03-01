package th.co.gosoft.go10.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import th.co.gosoft.go10.util.PropertiesUtils;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String S3_IMAGE_URL = PropertiesUtils.getProperties("domain_image_path")+"/"+PropertiesUtils.getProperties("folder_name");
	
    public DownloadServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    InputStream inputStream = null;
	    OutputStream outPutStream = null;
	    try {
	        String fileName = request.getParameter("imageName");
	        URL url = new URL(S3_IMAGE_URL+"/"+fileName);
	        URLConnection urlConnection = url.openConnection();
	        String contentType = urlConnection.getHeaderField("Content-Type");
	        int length = Integer.parseInt(urlConnection.getHeaderField("Content-Length"));
	        System.out.println("content type : "+contentType+", content length : "+length);
	        response.setContentType(contentType);
	        response.setContentLength(length);
	        response.setHeader("Content-disposition","attachment; filename="+fileName);
	        
	        inputStream = new BufferedInputStream(urlConnection.getInputStream());
	        outPutStream = response.getOutputStream();
	        
	        byte[] buf = new byte[1024];
	        int count = 0;
	        while ((count = inputStream.read(buf)) >= 0) {
	           outPutStream.write(buf, 0, count);
	        }
	    } catch (Exception e) {
	        throw new RuntimeException(e.getMessage(), e);
	    } finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outPutStream != null) {
                outPutStream.close();
            }
        }

	}
    
}
