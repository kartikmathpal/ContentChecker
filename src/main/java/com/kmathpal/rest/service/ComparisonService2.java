package com.kmathpal.rest.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
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

public class ComparisonService2 {
//    File file = new File(
//            getClass().getClassLoader().getResource("resturlCompare.xlsx").getFile()
//    );

    //ClassLoader classLoader = getClass().getClassLoader();
    //File file = new File(classLoader.getResource("com/kmathpal/resturlCompare.xlsx").getFile());
    //InputStream inputStream = new FileInputStream(file);

    public ComparisonService2() throws FileNotFoundException {
    }

    public boolean compareLinks(List<String> listA, List<String> listB) throws IOException, InvalidFormatException {
        InputStream inp = new FileInputStream("src/main/java/com/kmathpal/rest/Model/resturlCompare.xlsx");
        Workbook workbook = WorkbookFactory.create(inp);
        Sheet sheet = workbook.getSheetAt(0);

        List<String> tagList = new ArrayList<>(Arrays.asList("title", "h1", "h2", "h3", "h4", "h5", "h6", "p", "a", "href", "ul", "li"));
        boolean misMatchFlag = false;
        String response = "";
        String mismatches = "";
        for (int j = 0, r = 1; j < listA.size(); j++, r++) {
            Row row = sheet.createRow(r);
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
                        mismatches += "For tag <" + tag + "> count mismatch " + tagA.size() + "---" + tagB.size() + "\n";
                    }
                }
//                response="Prod URL :"+sheet.getRow(j).getCell(0)+"\n"+
//                        "Dev URL :"+sheet.getRow(j).getCell(1)+"\n";
//                if(misMatchFlag)
//                    response+="MisMatch :" + mismatches;
//                row.createCell(2).setCellValue(misMatchFlag);
//                row.createCell(3).setCellValue(response);
            } catch (Exception e) {

                System.out.println("Exception Ocurred For--->" + listA.get(j) + "  ||  " + listB.get(j));
                e.printStackTrace();
                j++;
                //r++;
            }
            response = "{\nEnvironmentA   :" + listA.get(j).toString() + "\n" +
                    "EnvironmentB   :" + listB.get(j).toString() + "\n";
            if (misMatchFlag)
                response += "MisMatch :\n" + mismatches;
            else
                response += "No MisMatch\n}";
            row.createCell(2).setCellValue(misMatchFlag == true ? "Fail" : "Pass");
            row.createCell(3).setCellValue(response);
            System.out.println("Row No "+ j+" processed");
        }

        //save data to file:
        inp.close();
        FileOutputStream fileOut = new FileOutputStream("src/main/java/com/kmathpal/rest/Model/resturlCompare.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        return misMatchFlag;
    }
}
