package com.iv.gravity.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * DetailsPojoEntity is pojo class for Gravity screens.
 *
 * @author Mohammed Zaid A.P.
 */
@Getter
@Setter
public class DetailsPojoEntity {

   private String issueId;

   private String repository;

   private String branch;

   private String licenseGroup;

   private String license;

   private String subsystem;

   private String epic;

   private String searchWord;

   private List<String> storyFiles;

   private List<String> allBranchFiles;

   private String cloneDirectory;

}
