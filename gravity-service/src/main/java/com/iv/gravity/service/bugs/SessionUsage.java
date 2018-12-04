package com.iv.gravity.service.bugs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;

public class SessionUsage {

   public void sessionFind(List<FileUnit> storyFiles) {
      storyFiles.stream().forEach(storyFile -> {
         List<BugDetails> bugList = new ArrayList<>();
         try {
            JavaParser.parse(new File(storyFile.getAbsolutePath())).findAll(FieldDeclaration.class).stream().forEach(field -> {
               String fieldType = field.getElementType().toString().toLowerCase();
               if (fieldType.contains("session") || fieldType.contains("cache")) {
                  BugDetails invalidField = new BugDetails();
                  invalidField.setLineNumber(String.valueOf(field.getBegin().get().line));
                  invalidField.setSeverityOfBug(BugCategory.MEMORY_MANAGEMENT_ISSUE.getSeverity().toString());
                  invalidField.setBugCategory(BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
                  invalidField.setBug("Usage of Session or Cache is Prohibited");
                  invalidField.setFileName(storyFile.getFileName());
                  invalidField.setRemotePathOfFile(storyFile.getRemotePath());
                  bugList.add(invalidField);
               }
            });
         }
         catch (Exception e) {
            // Cannot Parse the File.
         }
         if (CollectionUtils.isNotEmpty(bugList)) {
            storyFile.getBugDetailsList().addAll(bugList);
         }
      });

   }

}
