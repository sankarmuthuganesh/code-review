package com.iv.gravity.entity.hotdeploy;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BugDetails {
   /**
    * The reason why the identified code is a bug.
    */
   private String reasonWhyItsABug;

   /**
    * The alternative solution to be followed to avoid the bug.
    */
   private String alterativeSolutionForTheBug;

   /**
    * Your name.
    */
   private String author;

   /**
    * The time now at which you are creating this bug.
    */
   private String creationTime;

   /**
    * The severity of the bug and can be Minor, Major or Critical
    */
   private String severity;

   /**
    * The categoty to which the bug belongs. Docs, Harcodes,...
    */
   private String categotyOfBug;

   /**
    * Any reference that states the detailed view of bug and its impact.
    */
   private String reference;

   /**
    * The lineNumbers in the file where this bug is present.
    */
   private List<String> lineNumbers = new ArrayList<>();

}
