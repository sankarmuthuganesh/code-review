package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.iv.gravity.entity.AvoidKeywordBugDetails;

public class ReadAvoidKeywords {

   public List<AvoidKeywordBugDetails> getAvoidKeywords() throws IOException, URISyntaxException {
      List<AvoidKeywordBugDetails> avoidKeywordsList = new ArrayList<>();
      Path path = Paths.get(getClass().getClassLoader().getResource("Avoid_Keyword_Bugs.xls").toURI());
      File file = path.toFile();
      FileInputStream inputStream = new FileInputStream(file);
      Workbook excelFile = null;
      String fileExtensionName = file.getName().substring(file.getName().lastIndexOf("."));
      if (fileExtensionName.equals(".xlsx")) {
         excelFile = new XSSFWorkbook(inputStream);
      }
      else if (fileExtensionName.equals(".xls")) {
         excelFile = new HSSFWorkbook(inputStream);
      }
      Sheet sheet = excelFile.getSheet("Avoid_Keyword_Bugs");
      int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
      for (int i = 1; i < rowCount + 1; i++) {
         Row row = sheet.getRow(i);
         // for (int j = 0; j < row.getLastCellNum(); j++) {
         // Construct Avoid Keyword Bug Entity
         AvoidKeywordBugDetails bug = new AvoidKeywordBugDetails();
         bug.setKeyword(row.getCell(0).getStringCellValue().trim());
         bug.setReason(row.getCell(1).getStringCellValue().trim());
         bug.setAddedByUser(row.getCell(2).getStringCellValue().trim());
         bug.setReference(row.getCell(3).getStringCellValue().trim());
         bug.setSeverity(row.getCell(4).getStringCellValue().trim());
         bug.setAddedTime(row.getCell(5).getStringCellValue().trim());
         avoidKeywordsList.add(bug);
         // System.out.print(row.getCell(j).getStringCellValue() + "|| ");
         // }
         // System.out.println();
      }
      inputStream.close();
      return avoidKeywordsList;
   }

}
