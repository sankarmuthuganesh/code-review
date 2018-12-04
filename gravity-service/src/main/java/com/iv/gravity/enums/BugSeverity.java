package com.iv.gravity.enums;

public enum BugSeverity {

   Critical(1), Major(2), Minor(3), Dynamic(4);
   private final int priority;

   BugSeverity(int priority) {
      this.priority = priority;
   }

   public int getPriority() {
      return this.priority;
   }

}
