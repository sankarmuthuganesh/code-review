package com.iv.gravity.entity;

import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.Map;
import lombok.Data;

@Data
public class GravityCodeReviewStory {

   // Primary Key
   private String storyId;

   private String username;

   private String storyType;

   private Timestamp timeAnalyzed;

   private String repository;

   private String branch;

   private String licenseGroup;

   private String license;

   private String subsystem;

   private String epic;

   private int totalBugs;

   private int criticalBugs;

   private int majorBugs;

   private int minorBugs;

   private Inet4Address analyserIp;

   private Map<String, String> additionalData;

}
