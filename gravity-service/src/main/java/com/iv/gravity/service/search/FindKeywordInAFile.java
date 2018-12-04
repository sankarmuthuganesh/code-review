package com.iv.gravity.service.search;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.common.io.Files;
import com.iv.gravity.entity.FileUnit;

public class FindKeywordInAFile {

   public List<FileUnit> findTheKeyword(String keyword, List<FileUnit> fileUnits) {
      List<FileUnit> foundFiles = new ArrayList<>();
      fileUnits.forEach(file -> {
         File fileToSearch = new File(file.getAbsolutePath());
         try {
            int lineCount = 1; // UTF-8
            boolean foundReadingLines = false;
            List<String> listOfLinesInFile = Files.readLines(fileToSearch, Charset.defaultCharset());
            for (String eachLineInFile : listOfLinesInFile) {
               // Pattern Matcher is not Used. For Loose Search Comfortability.
               if (StringUtils.containsIgnoreCase(eachLineInFile, keyword)) {
                  Map<String, List<String>> searchStore = file.getSearchStringLineNos();
                  if (searchStore.containsKey(keyword)) {
                     searchStore.get(keyword).add(String.valueOf(lineCount));
                  }
                  else {
                     List<String> lineNumbers = new ArrayList<>();
                     lineNumbers.add(String.valueOf(lineCount));
                     searchStore.put(keyword, lineNumbers);
                  }
                  foundReadingLines = true;
               }
               lineCount++;
            }
            if (foundReadingLines) {
               foundFiles.add(file);
            }
            // if(!foundReadingLines){
            // String stringContentOfFile=FileUtils.readFileToString(fileToSearch);
            // stringContentOfFile=stringContentOfFile.replaceAll("\\r\\n|\\r|\\n",
            // StringUtils.EMPTY);
            // if(StringUtils.containsIgnoreCase(stringContentOfFile, keyword)){
            // foundFiles.add(file);
            // }
            // }
         }
         catch (Exception e) {
            // Keyword Search. Problem in Reading File
            // @branch.getValue().get(countOfFiles)@ By Lines
         }
      });
      return foundFiles;
   }

}
