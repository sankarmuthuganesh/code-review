package com.iv.gravity.service.bugs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.collections.CollectionUtils;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.hotdeploy.BugDetails;

public class AddHotDeployBugs {
   public Map<Boolean, List<String>> executeAndFindBugs(FileUnit fileUnit, Function<File, Map<Boolean, BugDetails>> findBug,
      Function<File, Map<Boolean, List<String>>> bugFixLogic) {
      File file = new File(fileUnit.getAbsolutePath());
      Map<Boolean, BugDetails> bugResult = findBug.apply(file);
      boolean bugFound = bugResult.keySet().iterator().next();
      Map<Boolean, List<String>> changesMade = null;
      if (bugFound) {
         BugDetails bugDetails = bugResult.values().iterator().next();
         List<com.iv.gravity.entity.BugDetails> bugList = new ArrayList<>();
         bugDetails.getLineNumbers().forEach(bugLine -> {
            com.iv.gravity.entity.BugDetails fileUnitBug = new com.iv.gravity.entity.BugDetails();
            fileUnitBug.setBug(bugDetails.getReasonWhyItsABug());
            fileUnitBug.setBugCategory(bugDetails.getCategotyOfBug());
            fileUnitBug.setFileName(fileUnit.getFileName());
            fileUnitBug.setLineNumber(bugLine);
            fileUnitBug.setRemotePathOfFile(fileUnit.getRemotePath());
            fileUnitBug.setSeverityOfBug(bugDetails.getSeverity());
            bugList.add(fileUnitBug);
         });
         if (CollectionUtils.isNotEmpty(bugList)) {
            fileUnit.getBugDetailsList().addAll(bugList);
         }
         // Fixing Bug
         changesMade = bugFixLogic.apply(file);
      }
      return changesMade;
   }
}
