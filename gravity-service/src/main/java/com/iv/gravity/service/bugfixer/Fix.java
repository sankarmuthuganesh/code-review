package com.iv.gravity.service.bugfixer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Fix {
   private String operation;

   private int lineNumber;

   private String text;

   private String replacementText;

   public static Fix operation(String operation) {
      Fix fix = new Fix();
      fix.operation = operation;
      return fix;
   }

   public Fix lineNumber(int lineNumber) {
      this.lineNumber = lineNumber;
      return this;
   }

   public Fix text(String text) {
      this.text = text;
      return this;
   }

   public Fix replacementText(String replacementText) {
      this.replacementText = replacementText;
      return this;
   }
}
