package com.kmathpal.rest.service;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/excel")
public class ExcelService {

    private static final String FILE_PATH = "src/main/java/com/kmathpal/rest/Model/resturlCompare.xlsx";

    @GET
    @Path("/get")
    @Produces("application/vnd.ms-excel")
    public Response getFile() {

        File file = new File(FILE_PATH);

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=new-excel-file.xlsx");
        return response.build();

    }

}