package com.kmathpal.rest.Service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessEnvironmentService {
    public void processEnvironments(String envA, String envB, String path) throws IOException, InvalidFormatException {
        List<String> listA = new ArrayList<>();
        List<String> listB = new ArrayList<>();
        Document doc = Jsoup.connect("https://asuords.edpl.us/preview/").get();
        Elements links = doc.select("li[class=\"list-group-item\"]");
        for (Element link : links) {
            System.out.print("https://" + envA + link.text() + "\t");
            System.out.println("https://" + envB + link.text());
            listA.add("https://" + envA + link.text());
            listB.add("https://" + envB + link.text());
        }
        //access sheet to write data
        InputStream inp = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(inp);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> tagList = new ArrayList<>(Arrays.asList("title","div","h1", "h2", "h3", "h4", "h5", "h6", "p", "a", "href", "ul", "li"));
        boolean misMatchFlag = false;
        String response = "";
        String mismatches = "";
        //clear prev entries
        while (sheet.getLastRowNum() > 0) {
            sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
        }

        for (int j = 0, r = 1; j < 50; j++, r++) {

            Row row = sheet.createRow(r);
            //remove later
            if(j==5)
                continue;

            response = "";
            try {

                //Get content the google home page using Jsoup
                Document docA = Jsoup.connect(listA.get(j)).get();//store in sheet
                Document docB = Jsoup.connect(listB.get(j)).get();//store in sheet
                row.createCell(0).setCellValue(listA.get(j).toString());
                row.createCell(1).setCellValue(listB.get(j).toString());
                mismatches = "";//reset
                for (String tag : tagList) {
                    misMatchFlag = false;
                    List<String> tagA = new ArrayList<>();
                    List<String> tagB = new ArrayList<>();

                    //fetch all the elements for = tag
                    System.out.println("\n\n\n*******Comparing All the <" + tag + "> elements******");
                    Elements productionDoc = docA.getElementsByTag(tag);
                    Elements devDoc = docB.getElementsByTag(tag);
                    //---
                    for (Element e : productionDoc) {
                        if (!e.text().isEmpty())
                            tagA.add(e.text());//tagA.add(e.ownText());
                    }
                    for (Element e : devDoc) {
                        if (!e.text().isEmpty())
                            tagB.add(e.text()); //tagB.add(e.ownText());

                    }
                    if (tagA.size() == tagB.size()) {
                        for (int i = 0; i < tagA.size(); i++) {
                            if (tagA.get(i).equals(tagB.get(i)))
                                System.out.println("true  || " + tagA.get(i) + "<--->" + tagB.get(i));

                            else {
                                System.out.println("false  || " + tagA.get(i) + "<--->" + tagB.get(i));
                                misMatchFlag = true;
                                mismatches += "false  || " + tagA.get(i) + "<--->" + tagB.get(i) + "\n";
                            }
                        }
                    } else {
                        System.out.println("For tag <" + tag + "> count mismatch " + tagA.size() + "---" + tagB.size());
                        System.out.println(tagA);
                        System.out.println(tagB);
                        misMatchFlag = true;
                        mismatches += "For tag <" + tag + "> count mismatch " + tagA.size() + "---" + tagB.size() + "\n" +
                                "tag list:\n" +
                                "envA : " + tagA + "\n" +
                                "envB : " + tagB + "\n" +
                                "}";
                    }
                }

                response = "{\nEnvironmentA   :" + listA.get(j).toString() + "\n" +
                        "EnvironmentB   :" + listB.get(j).toString() + "\n";
                if (misMatchFlag)
                    response += "MisMatch :\n" + mismatches;
                else
                    response += "No MisMatch\n}";
                row.createCell(2).setCellValue(misMatchFlag == true ? "Fail" : "Pass");
                row.createCell(3).setCellValue(response);
            } catch (Exception e) {

                System.out.println("Exception Ocurred For--->" + listA.get(j) + "  ||  " + listB.get(j));
                e.printStackTrace();
                j++;
                //r++;
            }
//            response = "{\nEnvironmentA   :" + listA.get(j).toString() + "\n" +
//                    "EnvironmentB   :" + listB.get(j).toString() + "\n";
//            if (misMatchFlag)
//                response += "MisMatch :\n" + mismatches;
//            else
//                response += "No MisMatch\n}";
//            row.createCell(2).setCellValue(misMatchFlag == true ? "Fail" : "Pass");
//            row.createCell(3).setCellValue(response);
        }

        //save data to file:
        inp.close();
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }
}
