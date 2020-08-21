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
        //##

        //clear prev entries
        while (sheet.getLastRowNum() > 0) {
            sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
        }

        for (int j = 0,r=1; j < 20; j++,r++) {

            Row row = sheet.createRow(r);
            //remove later
//            if(j==5)
//                continue;
            StringBuilder resp = new StringBuilder();
            StringBuilder resp1 = new StringBuilder();
            try {

                //Get content the google home page using Jsoup
                String urlA = listA.get(j);
                String urlB = listB.get(j);
                resp.append("{\nURLA :" + urlA);
                resp.append("\nURLB :" + urlB);
                Document docA = Jsoup.connect(listA.get(j)).get();//store in sheet
                Document docB = Jsoup.connect(listB.get(j)).get();//store in sheet
                row.createCell(0).setCellValue(listA.get(j).toString());
                row.createCell(1).setCellValue(listB.get(j).toString());
                Elements elementsOfDocA = docA.body().select("*");
                Elements elementsOfDocB = docB.body().select("*");
                boolean mismatch = false;

                if (elementsOfDocA.size() == elementsOfDocB.size()) {
                    for (int i = 0; i < elementsOfDocA.size(); i++) {
                        if (!elementsOfDocA.get(i).ownText().equals(elementsOfDocB.get(i).ownText()) ? true : false) {
                            mismatch = true;
                            resp1.append("\n" + "<" + elementsOfDocA.get(i).tagName() + ">" + elementsOfDocA.get(i).ownText() + "||"
                                    + "<" + elementsOfDocB.get(i).tagName() + ">" + elementsOfDocB.get(i).ownText() + "||"
                                    + (elementsOfDocA.get(i).ownText().equals(elementsOfDocB.get(i).ownText()) ? true : false));
                        }
                    }

                } else {
                    //when tags are missing
                    mismatch = true;
                    List<String> elementTextDocA = new ArrayList<>();
                    List<String> elementTextDocB = new ArrayList<>();

                    for (Element e : elementsOfDocA)
                        elementTextDocA.add(e.ownText());

                    for (Element e : elementsOfDocB)
                        elementTextDocB.add(e.ownText());

                    List<String> presentInANotInB = new ArrayList<>();
                    List<String> presentInBNotInA = new ArrayList<>();

                    for (String e : elementTextDocA)
                        if (!elementTextDocB.contains(e))
                            presentInANotInB.add(e);

                    for (String e : elementTextDocB)
                        if (!elementTextDocA.contains(e))
                            presentInBNotInA.add(e);

                    if (presentInANotInB.size() > 0) {
                        resp1.append("<< Content present in " + urlA + " and absent in " + urlB + " >>");
                        for (String s : presentInANotInB)
                            resp1.append("\n\t" + s);
                    }

                    if (presentInBNotInA.size() > 0) {
                        resp1.append("\n<< Content present in " + urlB + " and absent in " + urlA + " >>");
                        for (String s : presentInBNotInA)
                            resp1.append("\n\t" + s);
                    }
                }

                if (!mismatch)
                    resp.append("\nMismatch :False");
                else {
                    resp.append("\nMismatch :True");
                    resp.append("\n" + resp1);
                }
                resp.append("\n}");
                //System.out.println(resp.toString());

                //###############
                row.createCell(2).setCellValue(mismatch == true ? "Fail" : "Pass");
                row.createCell(3).setCellValue(resp.toString());
            } catch (Exception e) {

                System.out.println("Exception Ocurred For--->" + listA.get(j) + "  ||  " + listB.get(j));
                e.printStackTrace();
                j++;
            }
        }


        //save data to file:
        inp.close();
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }
}
