package com.kmathpal.rest.Service;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/excel")
public class ExcelService {

    public static String path = "";

    public static void setPath(String path) {
        ExcelService.path = path;
    }

    @GET
    @Path("/get")
    @Produces("application/vnd.ms-excel")
    public Response getFile() {

        File file = new File(path);

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=new-excel-file.xlsx");
        return response.build();

    }
    @GET
    @Path("/template")
    @Produces("application/vnd.ms-excel")
    public Response getTemplate() {

        File file = new File("/Users/kartikmathpal/Documents/JavaProjects/ContentChecker/src/main/java/com/kmathpal/rest/Model/Template/template.xlsx");

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=template.xlsx");
        return response.build();

    }
}