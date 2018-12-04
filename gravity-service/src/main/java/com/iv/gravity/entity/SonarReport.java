package com.iv.gravity.entity;

import java.util.List;
import lombok.Data;
import org.sonarqube.ws.Issues.Issue;

@Data
public class SonarReport {

   private List<Issue> sonarIssues;

   private String analyzedTime;

}
