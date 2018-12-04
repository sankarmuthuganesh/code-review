package com.iv.gravity.entity;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CortexStoryDetails {

   private String storyId;

   private String storyName;

   private String status;

   private Map<String, List<String>> repositories;

   private String repositoryGITURL;

   private String workingBranch;

   private String licenseGroup;

   private String license;

   private String subsystem;

   private String epic;

   private String lauchVersion;

}
