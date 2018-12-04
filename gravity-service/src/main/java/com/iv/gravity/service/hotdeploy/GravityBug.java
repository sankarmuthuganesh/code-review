package com.iv.gravity.service.hotdeploy;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.iv.gravity.entity.hotdeploy.BugDetails;

/**
 * @author sankraja
 */
public interface GravityBug {
   /**
    * findBug will find the bug in the specified file and will fix it if possible.
    * 
    * @param fileToAnalyse - the file to check the presence of bug.
    * @return Map<Boolean, BugFixerLogic> - If bug is found the key is set to true else false. The value contains the
    * details of the bug.
    */
   public Map<Boolean, BugDetails> bugFinderLogic(File fileToAnalyse);

   /**
    * bugFixLogic will find the bug and make a fix accordingly in the file.
    * 
    * @param fileToFix - the file to fix the places of bug occurrence.
    * @return If bug fix happened successfully the key is set to true else false. , The value contains list of
    * linenumbers that were changed in file.
    */
   public Map<Boolean, List<String>> bugFixLogic(File fileToFix);

}
