package com.iv.gravity.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloneDetailsEntity {

   private String repository;

   private String branch;

   private Map<String, Map<String, Map<String, Set<String>>>> branchCategorisationDetails;

   private List<String> allFilePaths;

   private String cloneDirectory;

}
