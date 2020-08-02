package com.kmathpal.rest.service;
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
    public boolean compareLinks(String path) throws IOException, InvalidFormatException {
        InputStream inp = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(inp);
        Sheet sheet = workbook.getSheetAt(0);
        int size = sheet.getLastRowNum();

        List<String> tagList = new ArrayList<>(Arrays.asList("title","h1","h2","h3","h4","h5","h6","p","a","href","ul","li"));
        boolean misMatchFlag = false;
        String response="";
        String mismatches="";
        for(int j=1;j<=size;j++){
            response="";
            try{
                //Get content the google home page using Jsoup

                Document docA = Jsoup.connect(sheet.getRow(j).getCell(0).toString()).get();

                Document docB = Jsoup.connect(sheet.getRow(j).getCell(1).toString()).get();
                mismatches="";//reset
                for(String tag:tagList){
                    misMatchFlag=false;
                    List<String> tagA=new ArrayList<>();
                    List<String> tagB=new ArrayList<>();

                    //fetch all the elements for = tag
                    System.out.println("\n\n\n*******Comparing All the <"+ tag+ "> elements******");
                    Elements productionDoc = docA.getElementsByTag(tag);
                    Elements devDoc = docB.getElementsByTag(tag);
                    //---
                    for (Element e : productionDoc) {
                        if(!e.text().isEmpty())
                            tagA.add(e.text());//tagA.add(e.ownText());
                    }
                    for (Element e : devDoc) {
                        if(!e.text().isEmpty())
                            tagB.add(e.text()); //tagB.add(e.ownText());

                    }
                    if(tagA.size()==tagB.size()){
                        for(int i=0;i<tagA.size();i++){
                            if(tagA.get(i).equals(tagB.get(i)))
                                System.out.println("true  || " + tagA.get(i)+"<--->"+tagB.get(i));

                            else {
                                System.out.println("false  || " + tagA.get(i) + "<--->" + tagB.get(i));
                                misMatchFlag=true;
                                mismatches+="false  || " + tagA.get(i) + "<--->" + tagB.get(i)+"\n";
                            }
                        }
                    }else{
                        System.out.println("For tag <"+tag+"> count mismatch "+tagA.size()+"---"+tagB.size());
                        //int size=tagA.size()>tagB.size()?tagB.size():tagA.size();
                        //for(int i=0;i<size;i++)
                        //System.out.println(tagA.get(i)+"<-MM->"+tagB.get(i));
                        //System.out.println(tagA);
                        //System.out.println(tagB);
                        misMatchFlag=true;
                        mismatches+="For tag <"+tag+"> count mismatch "+tagA.size()+"---"+tagB.size()+"\n";
                    }
                }
            }catch(IOException ioe){
                System.out.println("Unable to connect to the URL");
                ioe.printStackTrace();
            }
            response="EnvironmentA :"+sheet.getRow(j).getCell(0)+"\n"+
                     "EnvironmentB :"+sheet.getRow(j).getCell(1)+"\n";
            if(misMatchFlag)
                response+="MisMatch :" + mismatches;
            else
                response+="No mismatch found";

            sheet.getRow(j).getCell(2).setCellValue(misMatchFlag);
            sheet.getRow(j).getCell(3).setCellValue(response);
        }

        //save data to file:
        inp.close();
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        //workbook.close();
        return misMatchFlag;
    }
}
