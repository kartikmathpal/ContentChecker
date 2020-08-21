package com.kmathpal.rest.Service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComparisonService {
    public void compareLinks(String path) throws IOException, InvalidFormatException {
        InputStream inp = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(inp);
        Sheet sheet = workbook.getSheetAt(0);

        int size = sheet.getLastRowNum();
        for (int j = 1; j <= size; ) {
            try {
                boolean mismatch = false;
                StringBuilder resp = new StringBuilder();
                StringBuilder resp1 = new StringBuilder();
                String urlA = sheet.getRow(j).getCell(0).toString();
                String urlB = sheet.getRow(j).getCell(1).toString();
                Document docA = Jsoup.connect(urlA).get();
                Document docB = Jsoup.connect(urlB).get();
                resp.append("{\nURLA :" + urlA);
                resp.append("\nURLB :" + urlB);

                Elements elementsOfDocA = docA.body().select("*");
                Elements elementsOfDocB = docB.body().select("*");

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
                sheet.getRow(j).createCell(2).setCellValue(mismatch == true ? "Fail" : "Pass");
                sheet.getRow(j).createCell(3).setCellValue(resp.toString());
                j++;

            } catch (Exception ioe) {
                System.out.println("Unable to connect to the URL");
                ioe.printStackTrace();
                sheet.getRow(j).createCell(2).setCellValue("Fail");
                sheet.getRow(j).createCell(3).setCellValue("404: Unable to connect to the URL");
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
