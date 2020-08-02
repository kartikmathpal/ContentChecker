package com.kmathpal.rest.service;

import com.kmathpal.rest.service.ComparisonService2;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Path("/input")
public class UserInputService {
    ComparisonService2 comparisonService2 = new ComparisonService2();

    public UserInputService() throws FileNotFoundException {
    }

    @POST
    @Path("/push")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response userInput(@FormParam("envA") String envA,
                              @FormParam("envB") String envB) throws IOException, InvalidFormatException, ParseException {
        List<String> envA_Url_List = new ArrayList<>();
        List<String> envB_Url_List = new ArrayList<>();
        /* */
        long startTime = System.currentTimeMillis();
        /**/
        System.out.println(envA+"---"+envB);
        Document doc = Jsoup.connect("https://asuords.edpl.us/preview/").get();
        Elements links = doc.select("li[class=\"list-group-item\"]");
        for (Element link : links) {
            System.out.print("https://"+envA+link.text()+"\t");
            System.out.println("https://"+envB+link.text());
            envA_Url_List.add("https://"+envA+link.text());
            envB_Url_List.add("https://"+envB+link.text());
        }

        comparisonService2.compareLinks(envA_Url_List,envB_Url_List);
        /* */
        long endTime = System.currentTimeMillis();
        long timetaken = (endTime-startTime)/60000;
        System.out.println(timetaken);
        String output = "<html><body>" +
                "<h2>Sheet Processed!!</h2>" +
                "<p>Click the link to Download</p>"+
                "<p>"+
                "<a href=\"http://localhost:8080/RESTfulExample/rest/excel/get\">Result sheet</a>"+
                "</p>"+
                "<p> Time taken to complete the request : "+ timetaken+" minutes.</p>"+
                "</body></html>";
        /**/
        return Response.status(200).entity(output).build();
    }
}