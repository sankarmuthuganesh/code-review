package com.iv.gravity.service.hotdeploy;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.io.FileUtils;
import com.iv.cortex.date.DateTimeUtils;
import com.iv.gravity.entity.hotdeploy.BugDetails;
import com.iv.gravity.enums.BugCategory;
import com.iv.gravity.service.bugfixer.FileEditor;

public class SampleBugLogic implements GravityBug {

   public Map<Boolean, BugDetails> bugFinderLogic(File fileToAnalyse) {
      // Write your bug finder logic here. If bug is found set true in map key else false.

      Map<Boolean, BugDetails> bug = new HashMap<>();
      BugDetails bugDetails = new BugDetails();

      // Below is the sample one contains plainly reading a file and finding bug.----------------------
      // There are other options too, javaparser, js parser, to better understand the file and find bugs
      List<String> lines;
      try {
         lines = FileUtils.readLines(fileToAnalyse, Charset.defaultCharset());
         IntStream.rangeClosed(0, lines.size() - 1).boxed().sorted(Collections.reverseOrder()).forEachOrdered(lineNumber -> {
            String eachLineInFile = lines.get(lineNumber);
            if (eachLineInFile.contains("stream")) {
               bugDetails.setReasonWhyItsABug("kkkkkkkkkkkkkkkkkkkkkkkkkkkk");
               bugDetails.getLineNumbers().add(String.valueOf(lineNumber));
               bugDetails.setAlterativeSolutionForTheBug("instead use stream for each");
               bugDetails.setAuthor("Gravity");
               bugDetails.setReference("google.com");
               bugDetails.setSeverity("Critical");
               bugDetails.setCategotyOfBug(BugCategory.TABLE_LAYOUT_ISSUE.toString());
               bugDetails.setCreationTime(DateTimeUtils.now().toString());
               bug.put(true, bugDetails);
            }
         });
      }
      catch (Exception e) {

      }
      if (bug.isEmpty()) {
         bug.put(false, null);
      }
      return bug;
      // ----------------------------------------------
   }

   public Map<Boolean, List<String>> bugFixLogic(File fileToFix) {
      Map<Boolean, List<String>> fix = new HashMap<>();
      List<String> fixedLineNumebrs = new ArrayList<>();

      // Write your bug fixing logic here.
      List<String> lines;
      try {
         lines = FileUtils.readLines(fileToFix, Charset.defaultCharset());
         IntStream.rangeClosed(0, lines.size() - 1).boxed().sorted(Collections.reverseOrder()).forEachOrdered(lineNumber -> {
            String eachLineInFile = lines.get(lineNumber);
            if (eachLineInFile.contains("stream")) {
               // just a typical example of usage
               boolean fixDone = FileEditor.inFile(fileToFix).text("stream").replaceWithText("hai").inLineNumber(lineNumber).fix();
               fixedLineNumebrs.add(String.valueOf(lineNumber));
               fix.put(true, fixedLineNumebrs);
            }
         });
      }
      catch (Exception e) {

      }

      return fix;
   }

}
