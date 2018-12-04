package com.iv.gravity.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.sonarqube.ws.Issues.Issue;
import org.sonarqube.ws.WsCe.ActivityResponse;
import org.sonarqube.ws.WsCe.Task;
import org.sonarqube.ws.WsMeasures.ComponentWsResponse;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.HttpException;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.ce.ActivityWsRequest;
import org.sonarqube.ws.client.issue.SearchWsRequest;
import org.sonarqube.ws.client.measure.ComponentWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.google.common.collect.ImmutableSet;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.iv.cortex.context.ContextBean;
import com.iv.cortex.date.DateStyle;
import com.iv.cortex.date.DateTimeUtils;
import com.iv.cortex.date.TimeStyle;
import com.iv.cortex.util.EnvReader;
import com.iv.gravity.dao.GravityDao;
import com.iv.gravity.entity.BlameDetails;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.CortexStoryDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.entity.OptimusBugsOfAFile;
import com.iv.gravity.entity.SonarReport;
import com.iv.gravity.entity.StoryReport;
import com.iv.gravity.service.bugfixer.BugFixer;
import com.iv.gravity.service.bugfixer.Fix;
import com.iv.gravity.service.bugs.AvoidKeywordBugs;
import com.iv.gravity.service.bugs.ExternalBugFinder;
import com.iv.gravity.service.bugs.IndexError;
import com.iv.gravity.service.bugs.NestedStream;
import com.iv.gravity.service.bugs.OptimusJavaCodeReview;
import com.iv.gravity.service.bugs.OptimusJavaScriptCodeReview;
import com.iv.gravity.service.bugs.OptimusXmlCodeReview;
import com.iv.gravity.service.bugs.SeperateUnnecessaryConstantsJava;
import com.iv.gravity.service.bugs.UnnecessaryClassFieldJava;
import com.iv.gravity.service.bugs.UnnecessaryJavaScript;
import com.iv.gravity.service.utilities.AuthorFind;
import com.iv.gravity.service.utilities.BlamesUsingCommits;
import com.iv.gravity.service.utilities.FindOtherEpicRelatedFiles;
import com.iv.gravity.service.utilities.HotDeployWriteSynchronizer;
import com.iv.gravity.service.utilities.PieChart;
import com.iv.gravity.service.utilities.ReadWriteSynchronizer;
import com.iv.gravity.service.utilities.TargetSpecificFiles;

@RestController
@RequestMapping("/gravity")
public class GravityController {

   @Autowired
   private GravityDao database;

   @Autowired
   private TargetSpecificFiles manipulateDomain;

   @Autowired
   private AuthorFind findAuthor;

   @Autowired
   private BlamesUsingCommits blameDetails;

   @Autowired
   private ReadWriteSynchronizer readWriteSynchronizer;

   @Autowired
   private HotDeployWriteSynchronizer hotDeployBugFinderDetailsSync;

   @Autowired
   private PieChart pieChart;

   @Autowired
   private ExternalBugFinder externalBugFinder;

   @Autowired
   private BugFixer bugFixer;

   @Autowired
   private AvoidKeywordBugs avoidKeywordBugs;

   @Autowired
   private UnnecessaryClassFieldJava unnecessaryClassFieldJava;

   @Autowired
   private SeperateUnnecessaryConstantsJava seperateUnnecessaryConstantsJava;

   @Autowired
   private NestedStream nestedStream;

   @Autowired
   private IndexError indexError;

   private static final String GRAVITY_SERVER_HOME_DIRECTORY = System.getProperty("catalina.home") + File.separator + "Gravity" + File.separator;

   @GetMapping(value = "/getStoriesOfUser/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
   public List<CortexStoryDetails> getStoriesOfUser(@PathVariable String username) {
      return database.getStoriesOfUserFromCortex(username);
   }

   @PostMapping(value = "/getStoriesOfManager/{manager}/{offset}/{searchKey}/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
   public List<String> getStoriesOfManager(@PathVariable String manager, @PathVariable String offset, @PathVariable String searchKey,
      @PathVariable String limit) {
      return database.getManagerStories(manager, offset, limit, searchKey);
   }

   @GetMapping(value = "/lookForPreviousRun/{storyId}/{branch}/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
   public StoryReport lookForPreviousRun(@PathVariable String storyId, @PathVariable String branch, @PathVariable String username) {
      return database.lookForPreviousRun(storyId, branch, username);

   }

   @PostMapping(value = "/findBugs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public List<FileUnit> findBugs(@RequestBody Map<String, Object> storyDetails) throws JsonProcessingException {
      Map<String, String> repositories = (Map<String, String>) storyDetails.get("repositories");

      // Cleaning Old Files, if exists
      cleanOldClones();

      // Decide GitIP, username, privatetoken
      String gitIP = String.valueOf(storyDetails.get("repositoryGITURL")).trim();
      if (!ApiConnector.getGIT_URLS().contains(gitIP)) {
         gitIP = ApiConnector.GIT_136_URL;
      }
      String gitUsername = ApiConnector.getUsername(gitIP);
      String gitPrivateToken = ApiConnector.getPrivateToken(gitIP);

      // Cloning the Files
      File repoCloneDirectory = new File(GRAVITY_SERVER_HOME_DIRECTORY + System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5));
      cloneABranch(gitIP, gitUsername, gitPrivateToken, repositories.get("primary"), String.valueOf(storyDetails.get("branch")), repoCloneDirectory);

      List<String> absolutePathOfAllFiles = new ArrayList<>();
      absolutePathOfAllFiles = getAbsolutePaths(repoCloneDirectory.getAbsolutePath(), absolutePathOfAllFiles);

      // Get List of LicenseGroup, License, Subsystem, Epic in the Branch.
      // Map<String, Map<String, Map<String, Set<String>>>> branchCategorisationDetails
      // =
      // classifyUsingController(absolutePathOfAllFiles);

      // Getting Story Files
      // Set<String> storyFiles = getStoryFiles(repositories.get("primary"),
      // (String) storyDetails.get("branch"), (String) storyDetails.get("licenseGroup"),
      // (String) storyDetails.get("license"), (String) storyDetails.get("subsystem"),
      // (String) storyDetails.get("epic"), absolutePathOfAllFiles);

      Set<String> storyFiles = manipulateDomain.getAllDomainFiles(absolutePathOfAllFiles, String.valueOf(storyDetails.get("licenseGroup")),
         String.valueOf(storyDetails.get("license")), String.valueOf(storyDetails.get("subsystem")), String.valueOf(storyDetails.get("epic")));

      List<FileUnit> storyFileUnits = new ArrayList<>();
      Map<String, List<Fix>> fileAndFixes = new HashMap<>();
      if (CollectionUtils.isNotEmpty(storyFiles)) {
         // Finding Bugs
         String gitHome = repoCloneDirectory.getAbsolutePath();
         FileRepositoryBuilder builder = new FileRepositoryBuilder();
         builder.setMustExist(true);
         builder.setGitDir(new File(gitHome, ".git"));
         Repository repo = null;
         try {
            repo = builder.build();
         }
         catch (IOException ioe) {
            // Cannot Open Git Repository
         }

         // File Name and Its Parsed Content
         // Map<String, CompilationUnit> parsedJavaFiles = new HashMap<>();

         for (String file : storyFiles) {
            storyFileUnits.add(new FileUnit(file, repositories.get("primary"), (String) storyDetails.get("branch"),
               (String) storyDetails.get("licenseGroup"), (String) storyDetails.get("license"), (String) storyDetails.get("subsystem"),
               (String) storyDetails.get("epic"), "http://" + gitIP + "/", gitHome, repo, findAuthor, blameDetails));

            // Parse Files
            // if(file.endsWith(".java")) {
            // try {
            // parsedJavaFiles.put(new File(file).getName(), JavaParser.parse(new File(file)));
            // } catch (FileNotFoundException e) {
            // }
            // }
         }
         repo.close();

         try {
            getOptimusBugs(storyFileUnits);
         }
         catch (Exception e) {

         }
         try {
            getUnnecessaryFieldsConstants(storyFileUnits, fileAndFixes);
         }
         catch (Exception e) {

         }
         try {
            getIndexFileErrors(storyFileUnits, absolutePathOfAllFiles);
         }
         catch (Exception e) {

         }

         try {
            avoidKeywordBugs.findBug(storyFileUnits);
         }
         catch (Exception e) {

         }

         try {
            nestedStreamFinder(storyFileUnits);
         }
         catch (Exception e) {

         }
         try {
            getSonarBugs(storyFileUnits, repositories.get("primary"), String.valueOf(storyDetails.get("branch")));
         }
         catch (Exception e) {

         }
         storyFileUnits.forEach(file -> {
            file.setTotalBugs(file.getBugDetailsList().size());
            file.setCriticalBugs((int) file.getBugDetailsList().stream().filter(bug -> bug.getSeverityOfBug().equals("Critical")).count());
            file.setMajorBugs((int) file.getBugDetailsList().stream().filter(bug -> bug.getSeverityOfBug().equals("Major")).count());
            file.setMinorBugs((int) file.getBugDetailsList().stream().filter(bug -> bug.getSeverityOfBug().equals("Minor")).count());
         });
         // List<HttpMessageConverter<?>> messageConverters = new
         // ArrayList<HttpMessageConverter<?>>();
         // //Add the Jackson Message converter
         // MappingJackson2HttpMessageConverter converter = new
         // MappingJackson2HttpMessageConverter();
         // // Note: here we are making this converter to process any kind of response,
         // // not only application/*json, which is the default behaviour
         // converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
         // messageConverters.add(converter);
         // converter.setObjectMapper(mapper);

         // Inserting in to db

         Map<String, Object> storyReport = new HashMap<>();
         storyReport.putAll(storyDetails);
         storyReport.remove("repositories");
         storyReport.put("repository", repositories.get("primary"));
         storyReport.put("additionalData", new HashMap<>());
         storyReport.put("totalBugs", (int) storyFileUnits.stream().flatMap(files -> files.getBugDetailsList().stream()).count());
         storyReport.put("critical", (int) storyFileUnits.stream().flatMap(files -> files.getBugDetailsList().stream())
            .filter(bugs -> bugs.getSeverityOfBug().equals("Critical")).count());
         storyReport.put("major", (int) storyFileUnits.stream().flatMap(files -> files.getBugDetailsList().stream())
            .filter(bugs -> bugs.getSeverityOfBug().equals("Major")).count());
         storyReport.put("minor", (int) storyFileUnits.stream().flatMap(files -> files.getBugDetailsList().stream())
            .filter(bugs -> bugs.getSeverityOfBug().equals("Minor")).count());

         Map<String, List<String>> fixDetails = new HashMap<>();
         try {
            fixDetails = externalBugFinder.hotDeployedBugFinder(storyFileUnits);
         }
         catch (Exception e1) {

         }
         // Fix Bugs
         byte[] fixedFiles = null;
         try {
            fixedFiles = bugFixer.fixBug(fileAndFixes, fixDetails);
         }
         catch (IOException e) {
         }
         try {
            database.insertAnalysisIntoDB(storyReport, storyFileUnits, fixedFiles);
         }
         catch (Exception e) {
            // Cannot insert analysis report in to db.
         }
         FileUtils.deleteQuietly(repoCloneDirectory);
         // FileDeleteStrategy.FORCE.deleteQuietly(repoCloneDirectory);
      }
      else {
         FileUtils.deleteQuietly(repoCloneDirectory);
         return null;
      }

      return storyFileUnits;
   }

   private void doAndSkipExceptions(Function<List<FileUnit>, Void> bugFinderLogic, List<FileUnit> storyFileUnits) {
      try {
         bugFinderLogic.apply(storyFileUnits);
      }
      catch (final Exception ignore) {
         // If Any Exceptions happened in logic, continue with the next logic
      }
   }

   private void getSonarBugs(List<FileUnit> storyFileUnits, String repository, String branch) throws JsonProcessingException {
      Map<String, Object> sonarConnect = new HashMap<>();
      if (branch.toLowerCase().startsWith("chennai-") || branch.equals("develop")) {
         sonarConnect.put("sonarIP", ApiConnector.SONAR_209_URL);
         sonarConnect.put("username", ApiConnector.SONAR_209_USERNAME);
         sonarConnect.put("password", ApiConnector.SONAR_209_USERNAME);
         // Usually be - "com.worksap.company:" + repository + ":" + branch;
         sonarConnect.put("projectKey", "com.worksap.company:" + repository + ":" + branch);
      }
      else {

         sonarConnect.put("sonarIP", ApiConnector.SONAR_233_URL);
         sonarConnect.put("username", ApiConnector.SONAR_233_USERNAME);
         sonarConnect.put("password", ApiConnector.SONAR_233_PASSWORD);
         // Usually be - "com.worksap.company:" + repository + ":" + branch;
         sonarConnect.put("projectKey", "com.worksap.company:" + repository + ":" + repository + "/" + branch);
      }
      sonarConnect.put("fileNamesFilter", storyFileUnits.stream().map(FileUnit::getFileName).collect(Collectors.toList()));
      SonarReport sonarReport = findSonarBugs(sonarConnect);
      List<Issue> sonarIssues = sonarReport.getSonarIssues();
      if (CollectionUtils.isNotEmpty(sonarIssues)) {
         Map<String, List<Issue>> fileAndIssues = sonarIssues.stream().collect(Collectors.groupingBy(Issue::getComponent, Collectors.toList()));

         fileAndIssues.entrySet().stream().forEach(sonar -> {
            storyFileUnits.stream().forEach(file -> {
               if (sonar.getKey().contains(file.getFileName())) {
                  List<Issue> fileIssues = sonar.getValue();
                  List<BugDetails> bugDetailsList = new ArrayList<>();
                  fileIssues.forEach(issue -> {
                     BugDetails bug = new BugDetails();
                     bug.setFileName(file.getFileName());
                     bug.setBugCategory("SonarIssue");
                     bug.setLineNumber(String.valueOf(issue.getLine()));
                     bug.setRemotePathOfFile(
                        "http://" + sonarConnect.get("sonarIP") + "/" + "code?id=" + sonar.getKey().replace(":", "%3A").replace("/", "%2F"));
                     bug.setBug(issue.getMessage() + " Rule: " + issue.getRule());
                     String severity = issue.getSeverity().toString().toLowerCase();
                     if (severity.equals("blocker")) {
                        severity = "critical";
                     }
                     bug.setSeverityOfBug(severity.substring(0, 1).toUpperCase() + severity.substring(1));
                     bugDetailsList.add(bug);
                  });
                  file.getBugDetailsList().addAll(bugDetailsList);
               }
            });
         });

         // storyFileUnits.stream().forEach(file ->{
         // if(fileAndIssues.containsKey(file.getFileName())){
         // List<Issue> sonarIssuesOfFile = fileAndIssues.get(file.getFileName());
         // sonarIssuesOfFile.stream().forEach(issue ->{
         // BugDetails bug = new BugDetails();
         // bug.setFileName(file.getFileName());
         // bug.setBugCategory("Sonar");
         // bug.setLineNumber(String.valueOf(issue.getLine()));
         // String url = sonarConnect.get("sonarIP") + "code?id=" +
         // javaMap.getKey().replace(":",
         // "%3A").replace("/", "%2F");
         // bug.setRemotePathOfFile(remotePathOfFile);
         // bug.setBug(bug);
         // bug.setSeverityOfBug(issue.getSeverity());
         // });
         // }
         // });
      }
   }

   private void getUnnecessaryFieldsConstants(List<FileUnit> storyFileUnits, Map<String, List<Fix>> fileAndFixes) {
      // Within the File
      unnecessaryClassFieldJava.findUnnecessaryFields(storyFileUnits, fileAndFixes);
      // Seperate Constants File
      seperateUnnecessaryConstantsJava.getUnnecessaryConstants(storyFileUnits, fileAndFixes);
      // JavaScript Unused Constants
      new UnnecessaryJavaScript().findUnusedJS(storyFileUnits, fileAndFixes);
   }

   private void getOptimusBugs(List<FileUnit> storyFileUnits) {
      storyFileUnits.forEach(fileUnit -> {
         OptimusBugsOfAFile optimusBugs = new OptimusBugsOfAFile();
         if (fileUnit.getNatureOfFile().equals("Java")) {
            try {
               OptimusJavaCodeReview javaFileReview = new OptimusJavaCodeReview(fileUnit.getAbsolutePath());
               optimusBugs.setErrors(javaFileReview.getErrorList());
               optimusBugs.setWarnings(javaFileReview.getWarningList());
            }
            catch (Exception e) {
               // Interruption in finding errors in Java File.
            }
         }
         else if (fileUnit.getNatureOfFile().equals("JavaScript")) {
            OptimusJavaScriptCodeReview jsCodeReview = new OptimusJavaScriptCodeReview();
            try {
               jsCodeReview.findBugs(new File(fileUnit.getAbsolutePath()));
               optimusBugs.setErrors(jsCodeReview.getErrorList());
               optimusBugs.setWarnings(jsCodeReview.getWarningList());
            }
            catch (Exception e) {
               // Interruption in finding errors in Javascript File.
            }
         }
         else if (fileUnit.getNatureOfFile().equals("XML")) {
            String fileName = fileUnit.getFileName();
            if (fileName.endsWith(".xml") && !fileName.endsWith(".ja.xml") && !fileName.endsWith(".en.xml") && !fileName.equals("pom.xml")) {
               try {
                  OptimusXmlCodeReview xmlCodeReview = new OptimusXmlCodeReview(new File(fileUnit.getAbsolutePath()));
                  optimusBugs.setErrors(xmlCodeReview.getErrorList());
                  optimusBugs.setWarnings(xmlCodeReview.getWarningList());
               }
               catch (Exception e) {
                  // Interruption in finding errors in XML File.
               }

            }
         }
         fileUnit.setOptimusBugs(optimusBugs);
      });
   }

   private void getIndexFileErrors(List<FileUnit> storyFileUnits, List<String> branchFiles) {
      indexError.getJoinErrorFiles(storyFileUnits, branchFiles);
   }

   // private void getSessionUsages(List<FileUnit> storyFileUnits) {
   // new SessionUsage().sessionFind(storyFileUnits);
   // }

   private void nestedStreamFinder(List<FileUnit> storyFileUnits) {
      nestedStream.getNestedLoops(storyFileUnits);
   }

   private void cleanOldClones() {
      // To prevent Clone directory from Flooding
      try {
         File tempDir = new File(GRAVITY_SERVER_HOME_DIRECTORY);
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

   private void cloneABranch(String gitIP, String username, String privateToken, String repository, String branch, File repoCloneDirectory) {
      try {
         Git clone = Git.cloneRepository().setURI("http://gitlab-ci-token:" + privateToken + "@" + gitIP + "/root/" + repository + ".git")
            .setBranch(branch).setDirectory(repoCloneDirectory)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, privateToken)).call();
         clone.close();
      }
      catch (Exception e) {
         // Cannot Clone the Particular Branch - @branch@ in the Repository -
         // @repository@
      }
   }

   @GetMapping("/getRepositoryList")
   public List<String> getRepositoryList(String gitIP, String privateToken) {
      List<String> listofRepositories = new ArrayList<>();
      try {
         GitlabAPI gitLabApi = GitlabAPI.connect("http://" + gitIP + "/", privateToken, TokenType.PRIVATE_TOKEN);
         List<GitlabProject> projects = gitLabApi.getProjects();
         for (GitlabProject project : projects) {
            listofRepositories.add(project.getName());
         }
      }
      catch (IOException e) {

      }
      return listofRepositories;
   }

   @GetMapping(value = "/getBranchesOfRepository/{gitIP}/{repository}")
   public List<String> getBranchesOfRepository(@PathVariable String gitIP, @PathVariable String repository) {
      String username = ApiConnector.getUsername(gitIP);
      String privateToken = ApiConnector.getPrivateToken(gitIP);
      List<String> branches = new ArrayList<>();
      Collection<Ref> call;
      try {
         call = Git.lsRemoteRepository().setHeads(true).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, privateToken))
            .setRemote("http://gitlab-ci-token:" + privateToken + "@" + gitIP + "/root/" + repository + ".git").call();
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

   /*
    * classifyUsingController is used to find List of LicenseGroup, License, Subsystem, Epic.
    * 
    * @param List<String> listOfBranchFiles - List of All Absolute Paths of the Branch Files
    * 
    * @return Map<String, Map<String, Map<String, Set<String>>>> LicenseGroup, License, Subsystem, Epic Details
    * Dependent Structure.
    */
   private Map<String, Map<String, Map<String, Set<String>>>> classifyUsingController(List<String> listOfBranchFiles) {
      // License Group, License, Subsystem, Epic
      Map<String, Map<String, Map<String, Set<String>>>> structureMap = new HashMap<>();
      listOfBranchFiles.stream().filter(file -> file.contains("-front")).forEach(filePath -> {
         Path path = Paths.get(filePath);
         Iterator<Path> iteratorOfPath = path.iterator();
         List<String> foldersOfAFile = new ArrayList<>();
         while (iteratorOfPath.hasNext()) {
            foldersOfAFile.add(iteratorOfPath.next().toString());
         }
         if (foldersOfAFile.contains("controller")) {
            int controllerIndex = foldersOfAFile.indexOf("controller");
            try {
               String licenseGroup = foldersOfAFile.get(controllerIndex + 1);
               String license = foldersOfAFile.get(controllerIndex + 2);

               String subsystem = foldersOfAFile.get(controllerIndex + 3);
               String epic = foldersOfAFile.get(controllerIndex + 4);
               if (structureMap.containsKey(licenseGroup)) {
                  if (structureMap.get(licenseGroup).containsKey(license)) {
                     if (structureMap.get(licenseGroup).get(license).containsKey(subsystem)) {
                        if (!epic.contains(".")) {
                           structureMap.get(licenseGroup).get(license).get(subsystem).add(epic);
                        }
                     }
                     else {
                        if (!subsystem.contains(".")) {
                           Set<String> epics = new HashSet<>();
                           if (!epic.contains(".")) {
                              epics.add(epic);
                           }
                           structureMap.get(licenseGroup).get(license).put(subsystem, epics);
                        }
                     }
                  }
                  else {
                     Map<String, Set<String>> subsystemMap = new HashMap<>();
                     if (!subsystem.contains(".")) {
                        Set<String> epics = new HashSet<>();
                        if (!epic.contains(".")) {
                           epics.add(epic);
                        }
                        subsystemMap.put(subsystem, epics);
                     }
                     structureMap.get(licenseGroup).put(license, subsystemMap);
                  }
               }
               else {
                  Map<String, Map<String, Set<String>>> licenseMap = new HashMap<>();
                  Map<String, Set<String>> subsystemMap = new HashMap<>();
                  if (!subsystem.contains(".")) {
                     Set<String> epics = new HashSet<>();
                     if (!epic.contains(".")) {
                        epics.add(epic);
                     }
                     subsystemMap.put(subsystem, epics);
                  }
                  licenseMap.put(license, subsystemMap);
                  structureMap.put(licenseGroup, licenseMap);
               }
            }
            catch (IndexOutOfBoundsException e) {
               // UnCategorised Files in Controller Folder Throws This
               // Exception.
            }
         }
      });
      return structureMap;
   }

   private Set<String> getStoryFiles(String repository, String branch, String licenseGroup, String license, String subsystem, String epic,
      List<String> allFilePaths) {

      List<String> epicFilesList = allFilePaths.stream().sequential().filter(file ->

      file.contains(licenseGroup) && file.contains(license) && file.contains(subsystem) && file.contains(epic))
         // .map(file -> file.substring(file.lastIndexOf(File.separator) + 1))
         .collect(Collectors.toList());

      // Adding Import Files of Epic Files
      Map<String, Set<String>> dependentFilesMap = new FindOtherEpicRelatedFiles().findDependentFiles(epicFilesList);
      List<String> otherAccessedFiles = new ArrayList<>();
      if (dependentFilesMap.containsKey(repository)) {
         Set<String> otherDependentFiles = dependentFilesMap.get(repository);
         otherDependentFiles.stream().forEach(dependentFile -> {
            String fileName = dependentFile + ".java";
            allFilePaths.stream().filter(file -> file.endsWith(".java")).sequential().forEach(file -> {
               if (file.endsWith(fileName) || file.endsWith(dependentFile + "Impl.java")) {
                  otherAccessedFiles.add(file);
               }
            });
         });
      }
      List<String> otherRepoDependentFiles = new ArrayList<>();

      // dependentFilesMap.entrySet().stream().filter(repo ->
      // !repo.getKey().equals(repository)).forEach(otherRepo ->
      // {
      // boolean oneNintyRepo = false;
      // String gitConnectionURL;
      // if (otherRepo.getKey().contains("hue-spec-design") ||
      // otherRepo.getKey().contains("hue-spec-process")
      // || otherRepo.getKey().contains("hue-roadmap-develop")) {
      // gitConnectionURL = "192.168.41.190";
      // oneNintyRepo = true;
      // } else {
      // gitConnectionURL = "192.168.41.136";
      // }
      //
      // String catalinaHome = System.getProperty("catalina.home") + File.separator
      // + "Gravity"
      // + File.separator
      // + System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5);
      // File cloneDirectory = new File(catalinaHome);
      // cloneABranch(cloneDirectory, otherRepo.getKey(), "river", gitConnectionURL);
      // List<String> absolutePathOfAllFiles = new ArrayList<>();
      // absolutePathOfAllFiles = getAbsolutePaths(cloneDirectory.getAbsolutePath(),
      // absolutePathOfAllFiles);
      // absolutePathOfAllFiles.stream().forEach(file -> {
      // otherRepo.getValue().stream().forEach(otherRepoFile -> {
      // if (file.endsWith(otherRepoFile + ".java")) {
      // otherRepoDependentFiles.add(file);
      // }
      // });
      // });
      // });
      // Adding Used Dto, Index Files.
      List<String> dtoIndexList = new ArrayList<>();
      otherAccessedFiles.stream().filter(file -> file.endsWith("DaoImpl.java")).forEach(daoimpl -> {
         try {
            JavaParser.parse(new File(daoimpl)).findAll(ImportDeclaration.class).stream()
               .filter(
                  importOfDao -> importOfDao.getName().getIdentifier().endsWith("Dto") || importOfDao.getName().getIdentifier().endsWith("Index"))
               .forEach(dtoOrIndex -> {
                  dtoIndexList.add(dtoOrIndex.getName().getIdentifier() + ".java");
               });
            ;
         }
         catch (Exception e) {
            // Cannot Parse DaoImpl File.
         }
      });
      dtoIndexList.stream().forEach(dtoIndex -> {
         allFilePaths.stream().filter(filePath -> new File(filePath).getName().equals(dtoIndex)).forEach(dtoPath -> {
            otherAccessedFiles.add(dtoPath);
         });
      });

      epicFilesList.addAll(otherAccessedFiles);
      epicFilesList.addAll(otherRepoDependentFiles);
      Set<String> filteredStoryFiles = epicFilesList.stream()
         .filter(file -> (file.endsWith("xml") && !file.endsWith("en.xml") && !file.endsWith("ja.xml") && !file.endsWith("pom.xml"))
            || file.endsWith(".java") ||
            // file.endsWith(".jsp") ||
            file.endsWith(".js")
         // file.endsWith(".jsx") ||
         // file.endsWith(".css") ||
         // file.endsWith(".less")
         ).collect(Collectors.toSet());

      return filteredStoryFiles;
   }

   @GetMapping(value = "/findSonarBugs", consumes = MediaType.APPLICATION_JSON_VALUE)
   public SonarReport findSonarBugs(@RequestBody Map<String, Object> storyDetails) throws JsonProcessingException {
      Map<String, Object> issuesAndTime = new HashMap<>();
      String sonarIP = (String) storyDetails.get("sonarIP");
      String username = (String) storyDetails.get("username");
      String password = (String) storyDetails.get("password");
      // Usually be - "com.worksap.company:" + repository + ":" + branch;
      String projectKey = (String) storyDetails.get("projectKey");
      List<String> fileNamesFilter = (List<String>) storyDetails.get("fileNamesFilter");

      HttpConnector httpConnector = HttpConnector.newBuilder().url("http://" + sonarIP + "/").credentials(username, password).build();

      WsClient wsClient = WsClientFactories.getDefault().newClient(httpConnector);
      // ComponentRequest
      ComponentWsRequest componentWsRequest = new ComponentWsRequest();
      componentWsRequest.setComponentKey(projectKey);
      componentWsRequest.setMetricKeys(Arrays.asList("bugs", "vulnerabilities", "code_smells"));

      ComponentWsResponse componentWsResponse = null;
      try {
         componentWsResponse = wsClient.measures().component(componentWsRequest);
      }
      catch (HttpException e) {
         // Cannot fetch SonarRun Time
      }

      // Issues
      SearchWsRequest issues = new SearchWsRequest();
      List<String> componentKeys = Arrays.asList(projectKey);
      List<String> bugTypes = Arrays.asList("BUG", "VULNERABILITY", "CODE_SMELL");
      List<String> severities = Arrays.asList("BLOCKER", "MAJOR", "MINOR", "CRITICAL");
      issues.setComponentKeys(componentKeys);
      issues.setTypes(bugTypes);
      issues.setStatuses(Arrays.asList("OPEN"));
      issues.setSeverities(severities);
      int pageCount = (int) (wsClient.issues().search(issues).getTotal() / 100 + 1);
      List<Issue> totalIssueList = new ArrayList<>();
      for (int iterator = 1; iterator <= pageCount; iterator++) {
         issues.setPage(iterator);
         totalIssueList.addAll(wsClient.issues().search(issues).getIssuesList());
      }

      List<Issue> issuesOfEpic = totalIssueList.stream().filter(issue -> {
         String fileName = issue.getComponent().substring(issue.getComponent().lastIndexOf("/") + 1);
         if (fileNamesFilter.contains(fileName)) {
            return true;
         }
         return false;
      }).collect(Collectors.toList());

      // ActivityRequest
      ActivityWsRequest activityRequest = new ActivityWsRequest();
      activityRequest.setComponentId(componentWsResponse.getComponent().getId());
      ActivityResponse activityResponse = wsClient.ce().activity(activityRequest);
      List<Task> taskList = activityResponse.getTasksList();
      Optional<Task> executedDate = taskList.stream().collect(Collectors.maxBy(Comparator.comparing(Task::getExecutedAt)));
      String dateFromSonar = "";
      if (executedDate.isPresent()) {
         dateFromSonar = executedDate.get().getExecutedAt();
      }
      else {
      }
      SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
      simpleFormat.setTimeZone(TimeZone.getDefault());
      Date form = null;
      try {
         form = simpleFormat.parse(dateFromSonar);
         form.setHours(form.getHours() - 3);
         form.setMinutes(form.getMinutes() - 34);
         form.setSeconds(form.getSeconds() + 7);
      }
      catch (ParseException e) {
      }
      DateFormat formatter = DateFormat.getDateTimeInstance();
      issuesAndTime.put("timerun", formatter.format(form));
      issuesAndTime.put("issues", issuesOfEpic);
      SonarReport report = new SonarReport();
      report.setSonarIssues(issuesOfEpic);
      report.setAnalyzedTime(formatter.format(form));
      return report;
   }

   @PostMapping(value = "/downloadAnalysis/{storyId}/{branch}/{username}/{currentlyreAnalyzed}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM_VALUE)
   public byte[] downloadAnalysis(@PathVariable String storyId, @PathVariable String branch, @PathVariable String username,
      @PathVariable String currentlyreAnalyzed) throws DocumentException, IOException {
      ByteArrayOutputStream zippedOutput = new ByteArrayOutputStream();
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zippedOutput);

      StoryReport storyReport;
      // Get Details From DB.
      String analysisTime = "-Current Report";
      if (storyId.contains("LastHistory")) {
         storyId = storyId.replace("-LastHistory", StringUtils.EMPTY);
         storyReport = database.getPreviousAnalysis(storyId, branch, username, currentlyreAnalyzed);
         analysisTime = "-" + DateTimeUtils.format(
            OffsetDateTime.parse(storyReport.getTimeAnalyzed().replace(" ", "T"), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toZonedDateTime(),
            DateStyle.LONG, TimeStyle.SHORT, false) + "-Report";
         // storyReport.setStoryId(storyId+"("+analysisTime+")");
      }
      else {
         storyReport = database.downloadStoryReport(storyId, branch, username);
      }
      if (Objects.nonNull(storyReport)) {
         List<FileUnit> bugDetails = storyReport.getFilesWithReport();
         Map<String, Integer> javaChart = bugDetails.stream().filter(storyFiles -> storyFiles.getNatureOfFile().equals("Java"))
            .flatMap(javaStoryFile -> javaStoryFile.getBugDetailsList().stream())
            .collect(Collectors.groupingBy(BugDetails::getBugCategory, Collectors.summingInt(s -> 1)));

         Map<String, Integer> jsChart = bugDetails.stream().filter(storyFiles -> storyFiles.getNatureOfFile().equals("JavaScript"))
            .flatMap(jsFile -> jsFile.getBugDetailsList().stream())
            .collect(Collectors.groupingBy(BugDetails::getBugCategory, Collectors.summingInt(s -> 1)));

         Map<String, Integer> xmlChart = bugDetails.stream().filter(storyFiles -> storyFiles.getNatureOfFile().equals("XML"))
            .flatMap(xmlFile -> xmlFile.getBugDetailsList().stream())
            .collect(Collectors.groupingBy(BugDetails::getBugCategory, Collectors.summingInt(s -> 1)));
         byte[] javaChartArray = pieChart.generatePieChart(javaChart);
         byte[] jsChartArray = pieChart.generatePieChart(jsChart);
         byte[] xmlChartArray = pieChart.generatePieChart(xmlChart);
         ZipOutputStream zipStream;
         try {
            zipStream = new ZipOutputStream(bufferedOutputStream);
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/JavaBugs-Statistics.pdf"));
            zipStream.write(javaChartArray, 0, javaChartArray.length);
            zipStream.closeEntry();
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/JsBugs-Statistics.pdf"));
            zipStream.write(jsChartArray, 0, jsChartArray.length);
            zipStream.closeEntry();
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/XMLBugs-Statistics.pdf"));
            zipStream.write(xmlChartArray, 0, xmlChartArray.length);
            zipStream.closeEntry();

            int javaFiles = (int) bugDetails.stream().filter(file -> file.getNatureOfFile().equals("Java")).count();
            int jsFiles = (int) bugDetails.stream().filter(file -> file.getNatureOfFile().equals("JavaScript")).count();
            int xmlFiles = (int) bugDetails.stream().filter(file -> file.getNatureOfFile().equals("XML")).count();
            int javaLineCount = bugDetails.stream().filter(file -> file.getNatureOfFile().equals("Java"))
               .collect(Collectors.summingInt(file -> file.getNumberOfLines()));
            int jsLineCount = bugDetails.stream().filter(file -> file.getNatureOfFile().equals("JavaScript"))
               .collect(Collectors.summingInt(file -> file.getNumberOfLines()));
            int xmlLineCount = bugDetails.stream().filter(file -> file.getNatureOfFile().equals("XML"))
               .collect(Collectors.summingInt(file -> file.getNumberOfLines()));
            int numberOfLines = bugDetails.stream().collect(Collectors.summingInt(file -> file.getNumberOfLines()));
            int numberOfFiles = bugDetails.size();
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/BugFiles/"));
            zipStream.closeEntry();

            for (FileUnit bugFile : bugDetails) {
               ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
               Document document = new Document();
               List<Paragraph> paragraphList = new ArrayList<Paragraph>();
               PdfWriter.getInstance(document, pdfOut);
               document.open();
               Font font = new Font();
               font.setSize(15);
               Font fontForBug = new Font();
               fontForBug.setSize(18);
               fontForBug.setStyle(Font.UNDERLINE);
               document.addTitle(bugFile.getFileName());
               document.add(new Paragraph(bugFile.getFileName(), fontForBug));
               document.add(new Paragraph("Author : " + bugFile.getAuthorOfFile(), fontForBug));
               document.add(new Paragraph("\n"));
               document.add(new Paragraph("Commit Authors : " + bugFile.getBlamesOfTheFile().stream().map(BlameDetails::getAuthorName).distinct()
                  .collect(Collectors.toList()).toString().replace("[", "").replace("]", ""), font));
               document.add(new Paragraph("Total Bugs: " + bugFile.getBugDetailsList().size(), font));
               Anchor fileGit = new Anchor("->Git Link");
               fileGit.setReference(bugFile.getRemotePath());
               document.add(fileGit);
               document.add(new Paragraph("\n"));

               if (bugFile.getBugDetailsList().size() != 0) {
                  document.add(new Paragraph("Bugs in File", font));
               }

               bugFile.getBugDetailsList().stream().forEach(action -> {
                  try {
                     Anchor p = new Anchor("@line " + action.getLineNumber() + action.getBug(), font);
                     p.setReference(action.getRemotePathOfFile() + "#L" + action.getLineNumber());
                     document.add(p);
                     document.add(new Paragraph("\n"));
                  }
                  catch (Exception e) {
                  }
               });
               Font tradeFont = new Font();
               tradeFont.setSize(6);
               tradeFont.setStyle(Font.UNDERLINE);
               tradeFont.setColor(BaseColor.GREEN);
               document.add(new Paragraph("Facile Work Through \u00A9 Gravity", tradeFont));
               document.close();

               try {
                  zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/BugFiles/" + bugFile.getFileName() + ".pdf"));
                  zipStream.write(pdfOut.toByteArray(), 0, pdfOut.toByteArray().length);
                  zipStream.closeEntry();
               }
               catch (IOException e) {
                  // Cannot Create a Zip of Charts
               }

            }
            String readme = "There are " + numberOfFiles + " files with " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(numberOfLines)
               + " lines in this Story.  ";
            String readmetwo = "\n" + javaFiles + " Java Files(" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(javaLineCount) + " lines), "
               + jsFiles + " JavaScript Files(" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(jsLineCount) + " lines), "
               + NumberFormat.getNumberInstance(Locale.ENGLISH).format(xmlFiles) + " XML Files(" + xmlLineCount + " lines).";
            byte[] notepadWrite = (readme + readmetwo).getBytes(Charset.defaultCharset());
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/Details-Readme.txt"));
            zipStream.write(notepadWrite, 0, notepadWrite.length);
            zipStream.closeEntry();

            byte[] overviewExcel = makeAnOverviewSheetPrevious(bugDetails);
            zipStream.putNextEntry(new ZipEntry(storyId + analysisTime + "/BugOverview.xlsx"));
            zipStream.write(overviewExcel, 0, overviewExcel.length);
            zipStream.closeEntry();
            zipStream.close();
         }
         catch (IOException e) {
            // Cannot Create a Zip of Charts
         }
      }
      bufferedOutputStream.close();
      zippedOutput.close();
      return zippedOutput.toByteArray();
   }

   private byte[] makeAnOverviewSheetPrevious(List<FileUnit> buggyStoryFiles) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      XSSFWorkbook mainWorkbook = new XSSFWorkbook();
      // Creating an Overview Sheet
      XSSFSheet overViewSheet = mainWorkbook.createSheet("Overview");
      AtomicInteger overViewRowCount = new AtomicInteger(0);
      CellStyle overViewHeaderStyle = createCellStyle(overViewSheet, 32, true);
      CellStyle overViewStyle = createCellStyle(overViewSheet, 16, true);

      // Total Bugs Writing
      String totalBugs = String.valueOf(buggyStoryFiles.stream().flatMap(file -> file.getBugDetailsList().stream()).count());
      CellStyle totalBugStyle = overViewSheet.getWorkbook().createCellStyle();
      XSSFFont totalBugsFont = overViewSheet.getWorkbook().createFont();
      totalBugsFont.setBold(true);
      totalBugsFont.setFontHeightInPoints((short) 32);
      totalBugsFont.setColor(IndexedColors.RED.getIndex());
      totalBugStyle.setFont(totalBugsFont);
      totalBugStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
      totalBugStyle.setFillPattern(FillPatternType.BIG_SPOTS);
      totalBugStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      totalBugStyle.setAlignment(HorizontalAlignment.CENTER);
      CellRangeAddress mergedCell = new CellRangeAddress(0, 0, 10, 11);
      overViewSheet.addMergedRegion(mergedCell);
      RegionUtil.setBorderBottom(BorderStyle.THICK, mergedCell, overViewSheet);
      Row totalBugsRow = overViewSheet.createRow(0);
      Cell totalBugsCell = createCell(totalBugsRow, 10, totalBugs + " Bugs", totalBugStyle);

      overViewSheet.setColumnWidth(10, 100 * 180);
      overViewSheet.setColumnWidth(11, 100 * 35);

      Row headerOverviewRow = overViewSheet.createRow(overViewRowCount.incrementAndGet());
      Cell rowCell = createCell(headerOverviewRow, 10, "File", overViewHeaderStyle);
      Cell findingCell = createCell(headerOverviewRow, 11, "Bugs", overViewHeaderStyle);

      CellStyle styleColor = overViewSheet.getWorkbook().createCellStyle();
      styleColor.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
      styleColor.setFillPattern(FillPatternType.BIG_SPOTS);
      XSSFFont fontOverview = overViewSheet.getWorkbook().createFont();
      fontOverview.setBold(true);
      fontOverview.setFontHeightInPoints((short) 32);
      styleColor.setFont(fontOverview);
      rowCell.setCellStyle(styleColor);
      findingCell.setCellStyle(styleColor);

      Comparator<FileUnit> bugCompare = (a, b) -> {
         return Integer.compare(a.getBugDetailsList().size(), b.getBugDetailsList().size());
      };

      buggyStoryFiles.stream().sorted(bugCompare.reversed()).forEach(file -> {
         Row repositoryRow = overViewSheet.createRow(overViewRowCount.incrementAndGet());
         String filename = file.getFileName();
         createCell(repositoryRow, 10, filename.substring(filename.indexOf("-") + 1, filename.length()), overViewStyle);

         // Findings Count
         int count = file.getBugDetailsList().size();

         Workbook overviewWorkbook = new XSSFWorkbook();
         CreationHelper overviewHelper = overviewWorkbook.getCreationHelper();
         Cell countCell = repositoryRow.createCell(11);
         CellStyle hlinkstyle = mainWorkbook.createCellStyle();
         XSSFFont hlinkfont = mainWorkbook.createFont();
         hlinkfont.setUnderline(XSSFFont.U_SINGLE);
         hlinkfont.setColor(IndexedColors.BLUE.index);
         hlinkfont.setBold(true);
         hlinkfont.setFontHeightInPoints((short) 16);
         hlinkstyle.setAlignment(HorizontalAlignment.CENTER);
         hlinkstyle.setFont(hlinkfont);
         countCell.setCellStyle(hlinkstyle);
         countCell.setCellValue(String.valueOf(count));
         Hyperlink linkOverview = overviewHelper.createHyperlink(HyperlinkType.FILE);

         linkOverview.setAddress("BugFiles/" + file.getFileName() + ".pdf");
         countCell.setHyperlink(linkOverview);

      });

      CellStyle cellStyles = overViewSheet.getWorkbook().createCellStyle();
      XSSFFont fonts = overViewSheet.getWorkbook().createFont();
      fonts.setBold(true);
      fonts.setFontHeightInPoints((short) 8);
      cellStyles.setFont(fonts);
      Row rowww = overViewSheet.createRow(overViewRowCount.incrementAndGet());
      Cell cellTitlee = rowww.createCell(0);
      cellTitlee.setCellValue("Facile work through Gravity");
      cellTitlee.setCellStyle(cellStyles);

      try {
         mainWorkbook.write(outputStream);
         mainWorkbook.close();
      }
      catch (IOException e) {
      }
      return outputStream.toByteArray();
   }

   // To Create a Cell
   private static Cell createCell(Row row, int cellNumber, String value, CellStyle style) {
      Cell cell = row.createCell(cellNumber);
      cell.setCellStyle(style);
      cell.setCellValue(value);
      return cell;
   }

   // To Create a CellStyle
   private static CellStyle createCellStyle(XSSFSheet sheet, int fontSize, boolean bold) {
      CellStyle style = sheet.getWorkbook().createCellStyle();
      XSSFFont font = sheet.getWorkbook().createFont();
      font.setBold(bold);
      font.setFontHeightInPoints((short) fontSize);
      style.setFont(font);
      return style;
   }

   @GetMapping(value = "/getFileReport/{storyId}/{branch}/{fileName}/{username}")
   public FileUnit getFileReport(@PathVariable String storyId, @PathVariable String branch, @PathVariable String fileName,
      @PathVariable String username) {
      return database.getFileReport(storyId, branch, username, fileName);
   }

   @GetMapping(value = "/getStoryAnalysisHistory/{branch}/{storyId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public List<StoryReport> getStoryAnalysisHistory(@PathVariable String branch, @PathVariable String storyId) {
      return database.getStoryAnalysisHistory(storyId, branch);
   }

   @GetMapping(value = "/getGITURL/{repository}", produces = MediaType.APPLICATION_JSON_VALUE)
   public String getGITURL(@PathVariable String repository) {
      return database.getGitURLOfRepository(repository);
   }

   @PutMapping(value = "/addGravityBugRule/{username}/{avoidKeyword}/{reason}/{severity}")
   public void addGravityBugRule(@PathVariable String username, @PathVariable String avoidKeyword, @PathVariable String reason,
      @PathVariable String severity, HttpServletRequest request, HttpServletResponse response) {
      String bugReference = request.getHeader("reference");
      String timeNow = String.valueOf(OffsetDateTime.now(ZoneId.systemDefault()));
      if (!Arrays.asList(avoidKeyword, reason, username, severity).contains(StringUtils.EMPTY)) {
         String addRuleArray[] = new String[] { avoidKeyword, reason, username, bugReference, severity, timeNow };
         try {
            readWriteSynchronizer.synchronizedAccess(addRuleArray);
            Path path = Paths.get(getClass().getClassLoader().getResource("Avoid_Keyword_Bugs.xls").toURI());
            getNotifiedOfBugsAdded(avoidKeyword + " keyword avoid usage added", path.toFile().getName(),
               FileUtils.readFileToByteArray(path.toFile()));
         }
         catch (IOException | URISyntaxException e) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
         }
      }
      else {
         response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      }
   }

   @PostMapping(value = "/addGravityBugRuleJar/{username}")
   public void addGravityBugRuleJar(@PathVariable String username, HttpServletResponse response,
      @RequestParam("files") MultipartFile[] bugLogicJars) {
      Map<String, File> jarsAdded = new LinkedHashMap<>();
      String timeNow = String.valueOf(OffsetDateTime.now(ZoneId.systemDefault()));
      Path path;
      try {
         path = Paths.get(getClass().getClassLoader().getResource("hotdeploybugfinderjars").toURI());
         for (MultipartFile bugJar : bugLogicJars) {
            if (bugJar.getOriginalFilename().endsWith(".jar")) {
               String randomJarName = RandomStringUtils.randomAlphabetic(10) + ".jar";
               File jar = new File(path.toString() + File.separator + randomJarName);
               bugJar.transferTo(jar);
               jarsAdded.put(bugJar.getOriginalFilename(), jar);
               hotDeployBugFinderDetailsSync.synchronizedAccess(new String[] { bugJar.getOriginalFilename(), randomJarName, username, timeNow });
            }
         }
         if (!jarsAdded.isEmpty()) {
            File[] jarFiles = jarsAdded.values().toArray(new File[] {});
            byte[] zippedJars = packToZip(jarFiles);
            String fileNames = jarsAdded.keySet().stream().collect(Collectors.joining(","));
            getNotifiedOfBugsAdded(fileNames + " jars added", "Added_Jars.zip", zippedJars);
         }
      }
      catch (URISyntaxException | IOException e1) {
         response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      }
   }

   @PostMapping(value = "/downloadAvoidKeywordFile")
   @Produces(MediaType.APPLICATION_OCTET_STREAM_VALUE)
   public byte[] downloadAvoidKeywordFile() throws URISyntaxException, IOException {
      Path path = Paths.get(getClass().getClassLoader().getResource("Avoid_Keyword_Bugs.xls").toURI());
      File file = path.toFile();
      return FileUtils.readFileToByteArray(file);
   }

   @PostMapping(value = "/downloadStoryBugFixedFiles/{storyId}/{branch}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM_VALUE)
   public byte[] downloadStoryBugFixedFiles(@PathVariable String storyId, @PathVariable String branch) {
      return database.getBugFixedFiles(storyId, branch);
   }

   @PostMapping(value = "/downloadHotDeployNecessaryFiles")
   @Produces(MediaType.APPLICATION_OCTET_STREAM_VALUE)
   public byte[] downloadHotDeployNecessaryFiles() throws URISyntaxException, IOException {
      Path path = Paths.get(getClass().getClassLoader().getResource("Bug_Creation_Necessary_Files.zip").toURI());
      File file = path.toFile();
      return FileUtils.readFileToByteArray(file);
   }

   private HttpStatus getNotifiedOfBugsAdded(String mailContent, String fileName, byte[] bugFinderFile) throws IOException {

      Set<String> receiverUserIds = new HashSet<>();
      receiverUserIds.add("sankraja");

      Set<String> ccUserIds = new HashSet<>();

      ImmutableSet<String> bcc = ImmutableSet.of("sankraja");

      Map<String, Object> notificationMap = new HashMap<>();
      notificationMap.put("notificationName", "FeedBack");
      notificationMap.put("notificationType", 21);
      notificationMap.put("ccUserId", ccUserIds);
      notificationMap.put("recevierUserId", receiverUserIds);
      notificationMap.put("bccUserId", bcc);
      notificationMap.put("mailSubject", "Gravity Bug Add Notification");
      notificationMap.put("mailBodyPlain", "Hi, This is to notify you, " + mailContent + " as a bug rule to Gravity, Find Attachemnt for Details!");

      Map<String, byte[]> filesToAttach = new HashMap<>();
      filesToAttach.put(fileName, bugFinderFile);
      notificationMap.put("attachmentFileMap", filesToAttach);

      HttpHeaders headers = getHeaderWithAuthentication();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(byteOut);
      out.writeObject(notificationMap);

      HttpEntity<Object> requestEntity = new HttpEntity<>(byteOut.toByteArray(), headers);

      RestTemplate gravityCustomRest = new RestTemplate();
      ResponseEntity<Void> responseEntity = gravityCustomRest.exchange(
         EnvReader.getGlobalPropertyValue("notification").concat("/notificationApi/sendMailWithAttachMent"), HttpMethod.POST, requestEntity,
         Void.class);
      // As an alternative, custom mail sender can be used.
      return responseEntity.getStatusCode();

   }

   private byte[] packToZip(File[] files) throws IOException {
      ByteArrayOutputStream zippedOutput = new ByteArrayOutputStream();
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zippedOutput);
      ZipOutputStream zipStream = new ZipOutputStream(bufferedOutputStream);

      for (File file : files) {
         try {
            zipStream.putNextEntry(new ZipEntry(file.getName()));
            zipStream.write(FileUtils.readFileToByteArray(file));
            zipStream.closeEntry();
         }
         catch (IOException e) {
         }
      }
      zipStream.close();
      bufferedOutputStream.close();
      zippedOutput.close();
      return zippedOutput.toByteArray();
   }

   private HttpHeaders getHeaderWithAuthentication(MediaType... mediaTypes) {
      String windowId = Objects.nonNull(ContextBean.getWindowId()) ? ContextBean.getWindowId() : "";
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Arrays.asList(mediaTypes));
      headers.set("ipAddress", ContextBean.getIpAddress());
      if (Objects.nonNull(ContextBean.getUserToken())) {
         headers.set("userToken", ContextBean.getUserToken());
         headers.set("sid", ContextBean.getSid());
         headers.set("serviceName", ContextBean.getServiceName());
         headers.set("ivwindowid", windowId);
      }
      else {
         if (Objects.nonNull(ContextBean.getApiToken())) {
            headers.set("apiToken", ContextBean.getApiToken());
            headers.set("sid", ContextBean.getSid());
            headers.set("serviceName", ContextBean.getServiceName());
            headers.set("ivwindowid", windowId);
         }
      }
      return headers;
   }
}
