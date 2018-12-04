package com.iv.gravity.service.bugs;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.iv.gravity.entity.AvoidKeywordBugDetails;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;
import com.iv.gravity.enums.BugSeverity;
import com.iv.gravity.service.utilities.ReadWriteSynchronizer;

public class AvoidKeywordBugs {

   @Autowired
   private ReadWriteSynchronizer readWriteSynchronizer;

   public void findBug(List<FileUnit> storyFiles) {
      storyFiles.stream().forEach(storyFile -> {
         List<BugDetails> bugList = new ArrayList<>();
         try {
            List<AvoidKeywordBugDetails> avoidKeywords = readWriteSynchronizer.synchronizedAccess(null);
            List<String> lines = FileUtils.readLines(new File(storyFile.getAbsolutePath()), Charset.defaultCharset());
            avoidKeywords.stream().forEach(keywordBug -> {
               lines.forEach(line -> {
                  if (StringUtils.containsIgnoreCase(line, keywordBug.getKeyword())) {
                     BugDetails invalidField = new BugDetails();
                     invalidField.setLineNumber(String.valueOf(lines.indexOf(line) + 1));
                     invalidField.setSeverityOfBug(BugSeverity.valueOf(keywordBug.getSeverity()).toString());
                     invalidField.setBugCategory(BugCategory.PROHIBITED_USAGE_ISSUE.toString());
                     invalidField.setBug(keywordBug.getReason());
                     invalidField.setFileName(storyFile.getFileName());
                     invalidField.setRemotePathOfFile(storyFile.getRemotePath());
                     bugList.add(invalidField);
                  }
               });
            });
         }
         catch (Exception e) {
            // Error in reading avoid keywords
         }
         if (CollectionUtils.isNotEmpty(bugList)) {
            storyFile.getBugDetailsList().addAll(bugList);
         }
      });

   }

}
