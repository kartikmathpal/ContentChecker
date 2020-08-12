package com.kmathpal.rest.Service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;

public class FileUploadService {
    ComparisonService comparisonService = new ComparisonService();

    public Response uploadFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException, InvalidFormatException {
        writeToFile(uploadedInputStream, uploadedFileLocation);
        //Call service to read from this file
        comparisonService.compareLinks(uploadedFileLocation);

        ExcelService.setPath(uploadedFileLocation);
        return Response.temporaryRedirect(URI.create("http://localhost:8080/download.html")).build();
    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
