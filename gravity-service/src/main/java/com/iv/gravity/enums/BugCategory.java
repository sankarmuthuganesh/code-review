package com.iv.gravity.enums;

public enum BugCategory {

   // Common Categories
   SECURITY_ISSUE(BugSeverity.Critical), DOCS_ISSUE(BugSeverity.Major), READABILITY_MAINTAINABILITY_ISSUE(BugSeverity.Minor), FILE_NAMING_ISSUE(
      BugSeverity.Major), FOLDER_STRUCTURE_ISSUE(BugSeverity.Major), VARIABLE_METHOD_NAMING_ISSUE(BugSeverity.Minor), EXCEPTION_HANDLING_ISSUE(
         BugSeverity.Critical), PERFORMANCE_ISSUE(BugSeverity.Critical), UNUTILIZED_RESOURCES_ISSUE(BugSeverity.Major), CASTING_ISSUE(
            BugSeverity.Critical), MEMORY_MANAGEMENT_ISSUE(BugSeverity.Critical), HARDCODE_ISSUE(BugSeverity.Critical), MISUSED_LAYER_LOGIC_ISSUE(
               BugSeverity.Major), CONVENTION_VIOLATIONS_ISSUE(
                  BugSeverity.Minor), DUPLICATION_ISSUE(BugSeverity.Major), UNNECESSARY_CODE_ISSUE(BugSeverity.Major),

   // Table Layout Issue
   TABLE_LAYOUT_ISSUE(BugSeverity.Critical), TABLE_CONVENTION_VIOLATION_ISSUE(BugSeverity.Major), OBJECT_OUT_OF_MEMORY_ISSUE(
      BugSeverity.Critical), DATABASE_PERFORMANCE_ISSUE(BugSeverity.Critical), TABLE_NAMING_ISSUE(BugSeverity.Critical),

   // Organisation Specific Issue
   MISUSE_OF_AUTOINDEX_ISSUE(BugSeverity.Critical), INVALID_CLASS_FIELD(BugSeverity.Major), PROHIBITED_USAGE_ISSUE(BugSeverity.Dynamic);
   private final BugSeverity severity;

   BugCategory(BugSeverity severity) {
      this.severity = severity;
   }

   public BugSeverity getSeverity() {
      return this.severity;
   }

}
