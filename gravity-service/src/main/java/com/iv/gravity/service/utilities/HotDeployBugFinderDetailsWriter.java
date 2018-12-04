package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HotDeployBugFinderDetailsWriter {

   public void writeExcel(String[] dataToWrite) throws IOException, URISyntaxException {
      Path path = Paths
         .get(getClass().getClassLoader().getResource("hotdeploybugfinderjars" + File.separator + "Hot_Deploy_BugFinder_Jars_Details.xls").toURI());
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
      Sheet sheet = excelFile.getSheet("Hot_Deploy_BugFinder_Jars");
      int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
      Row row = sheet.getRow(0);
      Row newRow = sheet.createRow(rowCount + 1);
      for (int j = 0; j < row.getLastCellNum(); j++) {
         Cell cell = newRow.createCell(j);
         cell.setCellValue(dataToWrite[j]);
      }
      inputStream.close();
      FileOutputStream outputStream = new FileOutputStream(file);
      excelFile.write(outputStream);
      outputStream.close();
   }

}
