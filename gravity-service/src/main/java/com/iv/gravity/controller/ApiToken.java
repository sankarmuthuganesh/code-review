package com.iv.gravity.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;

public class ApiToken {

   // JENKINS IP
   // public static final String JENKINS_IP = "192.168.41.77";

   // JENKINS PORT
   // public static final String JENKINS_PORT = "8080";

   // JENKINS URL
   // public static final String JENKINS_URL = "http://" + JENKINS_IP + ":" +
   // JENKINS_PORT ;

   // JENKINS USERNAME
   public static final String JENKINS_USERNAME = "infoview";

   // JENKINS security token to authenticate
   // 53a2a681e75ad3121e59b0cfb9daf6ef
   public static final String JENKINS_TOKEN = "819320a1f0a20e0859e8a093f9ff8c02";

   // Jenkins auth string
   public static final String JENKINS_AUTH_STRING = JENKINS_USERNAME + ":" + JENKINS_TOKEN;

   // Build JOB URL
   public static final String BUILD_JOB_URL = "job/Build/job";

   // Deployment JOB URL
   public static final String DEPLOYMENT_JOB_URL = "job/Deployment/job";

   // Build with param
   public static final String BUILD_WITH_PARAMS = "buildWithParameters";

   // Build url
   public static final String BUILD = "build";

   // last build number
   public static final String LAST_BUILD_NUMBER = "lastBuild/buildNumber/api/json";

   // first build number
   public static final String FIRST_BUILD_NUMBER = "firstBuild/buildNumber/api/json";

   // dsl seed job
   public static final String JOB_DSL_SEED = "job/job-dsl-seed";

   // initialize environment
   public static final String INIT_ENVIRONMENT = "job/Initialize-environment";

   // add msa
   public static final String ADD_MSA = "job/MSA";

   // initialize middleware
   public static final String INIT_MIDDLEWARE = "job/Installation/job/Initialize-middleware";

   // job queue url
   public static final String JOB_QUEUE = "queue/api/json";

   public static void main(String[] args) {
      // List<String> listofRepositories = new ArrayList<>();
      // try {
      // String gitIP = "192.168.40.227";
      // String token = "wy_H5SFiPCP9xvWVd_Qz";
      // String privateToken = "hFP_2jqii9xJXE9kwvEk";
      //
      // String gitIP136 = "192.168.41.136";
      // String token136 = "4DNN6cfJNgFd3VKdx3NU";
      // GitlabAPI gitLabApi =
      // GitlabAPI.connect("http://" + "product-ci" + "/", "ighJcGirPRGbSPqej1V4", TokenType.PRIVATE_TOKEN);
      //// System.out.println(gitLabApi.getUser().getUsername());
      // List<GitlabProject> projects = gitLabApi.getProjects();
      // for (GitlabProject project : projects) {
      // listofRepositories.add(project.getName());
      // }
      // } catch (IOException e) {
      //
      // }
      // System.out.println(listofRepositories.size());
      try {
         Git clone = Git.cloneRepository()
            .setURI("http://gitlab-ci-token:" + "hFP_2jqii9xJXE9kwvEk" + "@" + "192.168.40.227" + "/root/" + "iv-drive-service" + ".git")
            .setBranch(null).setDirectory(new File("D:\\Test\\cloneone"))
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider("cortexuser", "hFP_2jqii9xJXE9kwvEk")).call();
         clone.close();
      }
      catch (Exception e) {
         e.printStackTrace();
         // Cannot Clone the Particular Branch - @branch@ in the Repository -
         // @repository@
      }
   }

}
