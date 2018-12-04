package com.iv.gravity.entity;

import java.util.List;
import lombok.Data;

@Data
public class StoryReport {

   private String storyId;

   private String username;

   private String storyType;

   private String timeAnalyzed;

   private String repository;

   private String branch;

   private String licenseGroup;

   private String license;

   private String subsystem;

   private String epic;

   private String totalBugs;

   private String criticalBugs;

   private String majorBugs;

   private String minorBugs;

   private String analyserIp;

   private String additionalData;

   private List<FileUnit> filesWithReport;

}
