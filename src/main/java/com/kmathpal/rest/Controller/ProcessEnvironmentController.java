package com.kmathpal.rest.Controller;

import com.kmathpal.rest.Service.ExcelService;
import com.kmathpal.rest.Service.ProcessEnvironmentService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

@Path("/getresults")
public class ProcessEnvironmentController {


    public ProcessEnvironmentController() throws FileNotFoundException {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response compareURLDomains(@FormParam("envA") String envA,
                                      @FormParam("envB") String envB) throws IOException, InvalidFormatException {
        ProcessEnvironmentService processURLInputService= new ProcessEnvironmentService();
        processURLInputService.processEnvironments(envA,envB);
        ExcelService.setPath("C:\\Users\\Edplus\\Desktop\\kartikM\\ContentChecker\\src\\main\\java\\com\\kmathpal\\rest\\Model\\resturlCompare.xlsx");
        return Response.temporaryRedirect(URI.create("http://localhost:8080/download.html")).build();

    }
}
