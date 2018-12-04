package com.iv.gravity.service.hotdeploy;

public class Test {

   public static void main(String[] args) {
      // Below is the way of testing your logic
      GravityBugConstruct
         // Give any test file path to test your logic below
         .filePath("C:\\Users\\sankraja\\Downloads\\iv-gravity-front-1.3.2-20181101.133455-5\\js\\gravity\\iv-gravity-git-search.js")
         // Give the Method Reference of the logic like below
         .findBugLogic(new SampleBugLogic()::bugFinderLogic).fixBugLogic(new SampleBugLogic()::bugFixLogic).analyze();
      // And run the file
   }

}
