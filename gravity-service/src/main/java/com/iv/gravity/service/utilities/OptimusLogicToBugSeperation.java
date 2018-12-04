package com.iv.gravity.service.utilities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.CharMatcher;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.OptimusBugsOfAFile;
import com.iv.gravity.enums.BugCategory;

public class OptimusLogicToBugSeperation {

   public List<BugDetails> makeLineNumberAndCorrespodingBugDetails(OptimusBugsOfAFile optimusBugs, String absolutePath, String httpPath,
      String fileName) {

      List<BugDetails> bugDetailsList = new ArrayList<>();

      // Error Classifier
      // -----------------
      optimusBugs.getErrors().stream().sequential().forEach(error -> {
         String lineNumber;
         String bug = error.substring(0, error.lastIndexOf("|"));
         String category = error.substring(error.lastIndexOf("|") + 1).trim();
         // Assumes the Bug string Line Number is given typically as Line No : 1234
         if (bug.toLowerCase().replace(StringUtils.SPACE, StringUtils.EMPTY).contains("lineno")) {
            // String[] bugExplanationArray = error.split(":");
            // lineNumber = error.substring(error.indexOf(":") + 1,
            // error.lastIndexOf(":") - 1);

            int indexWhereLineNumberStarts = error.toLowerCase().indexOf("line no") + 7;
            lineNumber = CharMatcher.DIGIT.retainFrom(error.substring(indexWhereLineNumberStarts, indexWhereLineNumberStarts + 8));
            bug = bug.replace("Line No", StringUtils.EMPTY).replace(lineNumber, StringUtils.EMPTY).replace("Line no", StringUtils.EMPTY).replace(":",
               StringUtils.EMPTY);
            // bug = bugExplanationArray[2];

         }
         else {
            lineNumber = StringUtils.EMPTY;
         }

         String buggyLines = "";
         // new BuggyLinesGetter().getBuggyLines(lineNumber, absolutePath);

         // Setting it to BugDetails
         BugDetails bugDet = new BugDetails();
         bugDet.setRemotePathOfFile(httpPath);
         bugDet.setSeverityOfBug(BugCategory.valueOf(category).getSeverity().toString());
         bugDet.setBug(bug);
         bugDet.setBugCategory(category);
         bugDet.setLineNumber(lineNumber);
         bugDet.setBuggyLines(buggyLines);
         bugDet.setFileName(fileName);
         bugDetailsList.add(bugDet);
      });

      // Warnings Classifier
      // --------------------
      optimusBugs.getWarnings().stream().sequential().forEach(error -> {
         String lineNumber;
         String bug = error.substring(0, error.lastIndexOf("|"));
         String category = error.substring(error.lastIndexOf("|") + 1).trim();
         // Assumes the Bug string Line Number is given typically as Line No : 1234
         if (bug.toLowerCase().replace(StringUtils.SPACE, StringUtils.EMPTY).contains("lineno")) {
            // String[] bugExplanationArray = error.split(":");
            // lineNumber = error.substring(error.indexOf(":") + 1,
            // error.lastIndexOf(":") - 1);
            int indexWhereLineNumberStarts = error.toLowerCase().indexOf("line no") + 7;
            lineNumber = CharMatcher.DIGIT.retainFrom(error.substring(indexWhereLineNumberStarts, indexWhereLineNumberStarts + 8));
            bug = bug.replace("Line No", StringUtils.EMPTY).replace("Line no", StringUtils.EMPTY).replace(lineNumber, StringUtils.EMPTY).replace(":",
               StringUtils.EMPTY);
            // bug = bugExplanationArray[2];

         }
         else {
            lineNumber = StringUtils.EMPTY;
         }

         String buggyLines = "";
         // new BuggyLinesGetter().getBuggyLines(lineNumber, absolutePath);

         // Setting it to BugDetails
         BugDetails bugDet = new BugDetails();
         bugDet.setSeverityOfBug(BugCategory.valueOf(category).getSeverity().toString());
         bugDet.setRemotePathOfFile(httpPath);
         bugDet.setBug(bug);
         bugDet.setBuggyLines(buggyLines);
         bugDet.setBugCategory(category);
         bugDet.setLineNumber(lineNumber);
         bugDet.setFileName(fileName);
         bugDetailsList.add(bugDet);
      });

      return bugDetailsList;

   }

}
