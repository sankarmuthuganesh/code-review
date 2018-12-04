package com.iv.gravity.controller;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.service.search.FindKeywordInAFile;
import com.iv.gravity.service.utilities.AuthorFind;
import com.iv.gravity.service.utilities.BlamesUsingCommits;

@RestController
@RequestMapping("/gravitygit")
public class GravityGitRestController {

   @Autowired
   private AuthorFind findAuthor;

   @Autowired
   private BlamesUsingCommits blameDetails;

   private static final HttpTransport transport = new NetHttpTransport();

   static Logger logger = Logger.getLogger(GravityGitRestController.class);

   private static final String cloneDirectoryHome = System.getProperty("catalina.home") + File.separator + "Gravity" + File.separator;

   private static String bulkExecutionInProgress = "";

   @GetMapping(value = "/getBulkSearchUser", produces = MediaType.APPLICATION_JSON_VALUE)
   public String getBulkSearchUser() {
      String information;
      if (bulkExecutionInProgress.isEmpty()) {
         information = "No One ";
      }
      else {
         information = bulkExecutionInProgress;
      }
      return information + " is Currently Doing Bulk Search.";
   }

   @PutMapping(value = "/doBulkSearch")
   public void doBulkSearch() {
      bulkExecutionInProgress = "";
   }

   @PostMapping(value = "/searchInGit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public Map<String, List<FileUnit>> findBugs(@RequestBody Map<String, Object> searchDetails) throws JsonProcessingException {
      // Cleaning Old Files, if exists
      cleanOldClones();

      String gitIP = String.valueOf(searchDetails.get("gitIP"));
      String gitUsername = String.valueOf(searchDetails.get("gitUsername"));
      String gitPassword = String.valueOf(searchDetails.get("gitPassword"));
      String repository = String.valueOf(searchDetails.get("repository"));
      String branch = String.valueOf(searchDetails.get("branch"));
      String fileType = String.valueOf(searchDetails.get("fileType"));
      String searchKey = String.valueOf(searchDetails.get("searchKey"));
      String userSearching = String.valueOf(searchDetails.get("userSearching"));
      String[] searchKeys = new String[1];
      if (searchKey.contains(",")) {
         searchKeys = searchKey.split(",");
      }
      else {
         searchKeys[0] = searchKey;
      }
      List<FileUnit> fileUnitsToSearch = new ArrayList<>();
      List<String> repositories = null;
      File singleCloneDir = new File(cloneDirectoryHome + System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5));

      // Searching Algorithm
      FindKeywordInAFile finder = new FindKeywordInAFile();
      Map<String, List<FileUnit>> searchKeyAndFiles = new HashMap<>();
      List<FileUnit> foundFiles;

      // Cloning the Files
      if (repository.equals("all")) {
         // For Healthy Clones
         // if (bulkExecutionInProgress.isEmpty()) {
         List<File> clonedDirectories = new ArrayList<>();
         bulkExecutionInProgress = userSearching + " for " + gitIP;
         // repositories = getRepositoryList(gitIP, gitUsername, gitPassword);
         repositories = (List<String>) searchDetails.get("repositories");
         synchronized (this) {
            for (String repositoryToClone : repositories) {
               File bulkCloneDirectory = new File(cloneDirectoryHome + "Git-" + gitIP + File.separator + repositoryToClone);
               String clonedBranchName;
               if (!Files.exists(bulkCloneDirectory.toPath())) {
                  clonedBranchName = cloneABranch(gitIP, gitUsername, gitPassword, repositoryToClone, null, bulkCloneDirectory);
               }
               else {
                  if (ensureToPull(bulkCloneDirectory)) {
                     clonedBranchName = gitPull(gitUsername, gitPassword, bulkCloneDirectory);
                  }
                  else {
                     FileUtils.deleteQuietly(bulkCloneDirectory);
                     clonedBranchName = cloneABranch(gitIP, gitUsername, gitPassword, repositoryToClone, null, bulkCloneDirectory);
                  }
               }
               clonedDirectories.add(bulkCloneDirectory);
               fileUnitsToSearch
                  .addAll(constructFileUnits(gitIP, repositoryToClone, clonedBranchName.replace("refs/heads/", ""), bulkCloneDirectory, fileType));
            }
         }
         for (String searchKeyInKeys : searchKeys) {
            foundFiles = finder.findTheKeyword(searchKeyInKeys, fileUnitsToSearch);
            searchKeyAndFiles.put(searchKeyInKeys, foundFiles);
         }
         bulkExecutionInProgress = "";
         // Deleting Cloned Directories after searching
         // clonedDirectories.forEach(clonedDirectory ->{
         // FileUtils.deleteQuietly(clonedDirectory);
         // });
         // }
         // else {
         // searchKeyAndFiles.put("bulkInProgress", null);
         // }
      }
      else {
         cloneABranch(gitIP, gitUsername, gitPassword, repository, branch, singleCloneDir);
         fileUnitsToSearch = constructFileUnits(gitIP, repository, branch, singleCloneDir, fileType);
         for (String searchKeyInKeys : searchKeys) {
            foundFiles = finder.findTheKeyword(searchKeyInKeys, fileUnitsToSearch);
            searchKeyAndFiles.put(searchKeyInKeys, foundFiles);
         }
         FileUtils.deleteQuietly(singleCloneDir);
      }
      return searchKeyAndFiles;
   }

   private void cleanOldClones() {
      // To prevent Clone directory from Flooding
      try {
         File tempDir = new File(cloneDirectoryHome);
         File[] tempFiles = tempDir.listFiles();
         for (File tempFile : tempFiles) {
            if (!tempFile.getName().startsWith("Git-")) {
               BasicFileAttributes attributes;
               attributes = java.nio.file.Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
               long howOldTheFileIs = ((FileTime.from(Instant.now())).to(TimeUnit.MINUTES)) - (attributes.creationTime().to(TimeUnit.MINUTES));
               if (howOldTheFileIs > 30) {
                  FileUtils.deleteQuietly(tempFile);
               }
            }
         }
      }
      catch (Exception e) {
         // Some Exception Occured in Cleaning
      }
   }

   private String cloneABranch(String gitIP, String username, String password, String repository, String branch, File repoCloneDirectory) {
      try {
         Git clone = Git.cloneRepository().setBranch(branch).setURI("http://" + gitIP + "/" + "root/" + repository + ".git")
            .setDirectory(repoCloneDirectory).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
         String clonedBranchName = clone.getRepository().getFullBranch();
         clone.close();
         return clonedBranchName;
      }
      catch (Exception e) {
         return "";
         // Cannot Clone the Particular Branch - @branch@ in the Repository -
         // @repository@
      }
   }

   private boolean ensureToPull(File repoCloneDirectory) {
      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      builder.setMustExist(true);
      builder.setGitDir(new File(repoCloneDirectory.getAbsolutePath(), ".git"));
      Repository repo = null;
      try {
         repo = builder.build();
      }
      catch (IOException ioe) {
         // Cannot Open Git Repository
      }
      for (Ref ref : repo.getAllRefs().values()) {
         if (ref.getObjectId() == null)
            continue;
         return true;
      }
      repo.close();
      return false;
   }

   private String gitPull(String username, String password, File repoCloneDirectory) {
      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      builder.setMustExist(true);
      builder.setGitDir(new File(repoCloneDirectory.getAbsolutePath(), ".git"));
      Repository repo = null;
      try {
         repo = builder.build();
      }
      catch (IOException ioe) {
         return "";
         // Cannot Open Git Repository
      }
      Git gitUtils = new Git(repo);
      PullCommand pullCmd = gitUtils.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
      PullResult result;
      try {
         result = pullCmd.call();
         FetchResult pp = result.getFetchResult();
         MergeResult mm = result.getMergeResult();
         logger.info(repoCloneDirectory.getName());
         logger.info(mm.getMergeStatus());
         String pulledBranchName = repo.getFullBranch();
         repo.close();
         return pulledBranchName;
      }
      catch (GitAPIException e) {
         return "";
         // Cannot pull see merge status
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         return "";
      }

   }

   @GetMapping("/getRepositoryList/{gitIP}/{username}/{password}")
   public List<String> getRepositoryList(@PathVariable String gitIP, @PathVariable String username, @PathVariable String password) {
      List<String> listofRepositories = new ArrayList<>();
      try {
         Map<TokenType, String> typeandToken = obtainAccessToken("http://" + gitIP + "/", username, password, false);
         Entry<TokenType, String> entry = typeandToken.entrySet().iterator().next();
         GitlabAPI gitLabApi = GitlabAPI.connect("http://" + gitIP + "/", entry.getValue(), entry.getKey());
         List<GitlabProject> projects = gitLabApi.getProjects();
         for (GitlabProject project : projects) {
            listofRepositories.add(project.getName());
         }
      }
      catch (IOException e) {

      }
      return listofRepositories;
   }

   @GetMapping(value = "/getBranchesOfRepository/{gitIP}/{username}/{password}/{repository}")
   public List<String> getBranchesOfRepository(@PathVariable String gitIP, @PathVariable String username, @PathVariable String password,
      @PathVariable String repository) {
      List<String> branches = new ArrayList<>();
      Collection<Ref> call;
      try {
         call = Git.lsRemoteRepository().setHeads(true).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .setRemote("http://" + gitIP + "/" + "root/" + repository + ".git").call();
         for (Ref ref : call) {
            branches.add(ref.getName().replace("refs/heads/", ""));
         }
      }
      catch (GitAPIException e) {
         // Cannot get List of Branches. And Hence Number of Branches will be 0.!
      }
      return branches;
   }

   /*
    * getAbsolutePaths is used to get all File's Absolute Path in the Directory(Cloned Copy) given.
    * 
    * @param String directory - the Local Cloned Directory Path
    * 
    * @param List<String> absolutePathOfAllFiles - Absolute
    * 
    * @return List<String> absolutePathOfAllFiles - List of File Absolute Path
    */
   private List<String> getAbsolutePaths(String directory, List<String> absolutePathOfAllFiles) {
      File[] files = new File(directory).listFiles();

      for (File file : files) {
         if (file.isFile()) {
            absolutePathOfAllFiles.add(file.getAbsolutePath());
         } // To Exlude .git !file.isHidden()
         else if (file.isDirectory() && !file.isHidden()) {
            getAbsolutePaths(file.getAbsolutePath(), absolutePathOfAllFiles);
         }
      }
      return absolutePathOfAllFiles;
   }

   public static Map<TokenType, String> obtainAccessToken(String gitlabUrl, String username, String password, boolean sudoScope) throws IOException {
      Map<TokenType, String> tokenTypeAndToken = new HashMap<>();
      try {
         final OAuthGetAccessToken tokenServerUrl = new OAuthGetAccessToken(gitlabUrl + "/oauth/token" + (sudoScope ? "?scope=api%20sudo" : ""));
         final TokenResponse oauthResponse = new PasswordTokenRequest(transport, JacksonFactory.getDefaultInstance(), tokenServerUrl, username,
            password).execute();
         tokenTypeAndToken.put(TokenType.ACCESS_TOKEN, oauthResponse.getAccessToken());
         return tokenTypeAndToken;
      }
      catch (TokenResponseException e) {
         if (sudoScope && e.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Fallback for pre-10.2 gitlab versions
            final GitlabSession session = GitlabAPI.connect(gitlabUrl, username, password);
            tokenTypeAndToken.put(TokenType.PRIVATE_TOKEN, session.getPrivateToken());
            return tokenTypeAndToken;
         }
         else {
            throw new GitlabAPIException(e.getMessage(), e.getStatusCode(), e);
         }
      }
   }

   private List<FileUnit> constructFileUnits(String gitIP, String repository, String branch, File repoCloneDirectory, String fileType) {
      List<FileUnit> fileUnits = new ArrayList<>();
      List<String> filePaths = new ArrayList<>();
      getAbsolutePaths(repoCloneDirectory.getAbsolutePath(), filePaths);
      filePaths.stream().filter(filePath -> filePath.endsWith(fileType)).forEach(filePath -> {
         FileUnit fileUnit = new FileUnit(filePath, repository, branch, "", "", "", "", "http://" + gitIP + "/", repoCloneDirectory.getAbsolutePath(),
            null, findAuthor, blameDetails);
         fileUnits.add(fileUnit);
      });
      return fileUnits;
   }

}
