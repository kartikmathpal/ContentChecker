package com.kmathpal.rest.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kmathpal.rest.service.ComparisonService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

@Path("/file")
public class UploadFileService {
	ComparisonService comparisonService = new ComparisonService();
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) throws InterruptedException, IOException, InvalidFormatException {

		String uploadedFileLocation = "src/main/java/com/kmathpal/resturlCompare.xlsx"
				+ fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "<html><body>" +
				"<h2>Sheet Processed!!</h2>" +
				"<p>Click the link to Download</p>"+
				"<p>"+
				"<a href=\"http://localhost:8080/RESTfulExample/rest/excel/get\">Result sheet</a>"+
				"</p>"+
				"</body></html>";

		//Call service to read from this file
		comparisonService.compareLinks(uploadedFileLocation);


		return Response.status(200).entity(output).build();

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