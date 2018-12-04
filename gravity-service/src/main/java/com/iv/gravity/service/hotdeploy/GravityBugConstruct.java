package com.iv.gravity.service.hotdeploy;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import com.iv.gravity.entity.hotdeploy.BugDetails;

public class GravityBugConstruct {

   private File file;

   private Function<File, Map<Boolean, BugDetails>> findBugLogic;

   private boolean bugFound;

   private Consumer<File> bugFixLogic;

   public static GravityBugConstruct filePath(String absolutePath) {
      GravityBugConstruct gravityBug = new GravityBugConstruct();
      gravityBug.file = new File(absolutePath);
      return gravityBug;
   }

   public GravityBugConstruct findBugLogic(Function<File, Map<Boolean, BugDetails>> logic) {
      Objects.requireNonNull(logic);
      this.findBugLogic = logic;
      return this;
   }

   public GravityBugConstruct fixBugLogic(Consumer<File> bugFixLogic) {
      Objects.requireNonNull(bugFixLogic);
      this.bugFixLogic = bugFixLogic;
      return this;
   }

   public boolean analyze() {
      try {
         System.out.println("-------------------------------");
         System.out.println("File:	" + file.getName());
         System.out.println("-------------------------------");
         System.out.println("Executing Logic ...");
         Map<Boolean, BugDetails> bugResult = findBugLogic.apply(file);
         this.bugFound = bugResult.keySet().iterator().next();
         if (this.bugFound) {
            BugDetails bugDetails = bugResult.values().iterator().next();
            System.out.println("Bug Identified:	" + this.bugFound);
            System.out.println("-------------------------------");
            System.out.println("Bug Found in Line Numbers:	" + bugDetails.getLineNumbers());
            System.out.println("Resaon:	" + bugDetails.getReasonWhyItsABug());
            System.out.println("Solution:	" + bugDetails.getAlterativeSolutionForTheBug());
            System.out.println("Author:	" + bugDetails.getAuthor());
            System.out.println("Reference:	" + bugDetails.getReference());
            System.out.println("Creation Date:	" + bugDetails.getCreationTime());
            System.out.println("Bug Severity:	" + bugDetails.getSeverity());
            System.out.println("-------------------------------");

            if (Objects.isNull(bugFixLogic)) {
               System.out.println("Since Bug Fixing Logic is not provided, skipping fix...");
               System.out.println("-------------Done!------------------");
            }
            else {
               System.out.println("Fixing Bug ...");
               this.bugFixLogic.accept(file);
               System.out.println("Bug fix completed. Check the file to verify! ...");
               System.out.println("-------------Done!------------------");
            }
         }
         else {
            System.out.println("-------------No Bug Found in the File------------------");
            System.out.println("-------------Done!------------------");
         }
         return true;
      }
      catch (final Exception ignore) {
         return false;
      }

   }

}
