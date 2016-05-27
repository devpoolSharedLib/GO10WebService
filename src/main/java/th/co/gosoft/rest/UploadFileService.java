package th.co.gosoft.rest;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file")
public class UploadFileService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormParam("fileid") int theFileid,
            @FormParam("description") String theDescription,
            @Encoded @FormParam("thefile") File file) {
        System.out.println("UPLOAD");

//        String uploadedFileLocation = "d://uploaded/" + fileDetail.getFileName();
//
//        // save it
//        writeToFile(uploadedInputStream, uploadedFileLocation);
//
//        String output = "File uploaded to : " + uploadedFileLocation;
        
        System.out.println("imageFile : "+ file.getName());
//
        return Response.status(200).entity("good").build();

    }

    // save uploaded file to new location

}