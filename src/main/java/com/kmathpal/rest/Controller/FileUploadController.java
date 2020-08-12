package com.kmathpal.rest.Controller;

import com.kmathpal.rest.Service.FileUploadService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("/upload")
public class FileUploadController {
    FileUploadService fileUploadService = new FileUploadService();

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws InterruptedException, IOException, InvalidFormatException {

        String uploadedFileLocation = "src/main/java/com/kmathpal/rest/Model/UploadedFile/"
                + fileDetail.getFileName();
            return fileUploadService.uploadFile(uploadedInputStream,uploadedFileLocation);
    }
}