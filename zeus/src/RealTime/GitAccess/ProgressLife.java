package RealTime.GitAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import RealTime.Entity.FileUnit;

@Getter
@Setter
/*
 * ProgressLife is used for GIT Login, Repository Listing, Branches Getting, Cloning Branches,
 * Making Absolute Paths for the Cloned Files, Constructing a Map for it., Writing to Excel File the
 * Output from a Functionality
 * 
 * @author SankarS
 */
public class ProgressLife {
  // Path where all the Storgae of Gravity is going to Happen. Cloned Copy,
  private String gravityStoragaePath;

  // GIT Fields.
  private String gitConnectionURL;
  private GitlabAPI gitLabApi;
  private static final HttpTransport transport = new NetHttpTransport();
  private CredentialsProvider userIdentity;

  // Cloned Directory Path List.
  private List<String> clonedDirectoryPaths;

  private List<String> listofRepositories;
  private List<String> selectedRepositories;

  private Map<String, List<String>> selectedReposAndAllTheirBranches;
  private Map<String, List<String>> selectedReposAndTheirSelectedBranches;
  // This MultiKey contains branch name(river-1709) and it local cloned Folder
  // Path(C:\Users\sankraja\AppData\Local\Temp\river-1709-476400270321782671)
  private Map<String, Map<MultiKey, List<String>>> repoItsbranchesAndTheirListofFiles;

  // ********** Stage 1-Login **********//
  /*
   * checkLoginValidity is used to Login with the GIT Username and Password provided.
   * 
   * @param String gitUserName - GIT Username.
   * 
   * @param String gitPassword - GIT Password.
   * 
   * @param String gitURL - GIT URL.
   * 
   * @return Map<Boolean,String> loginDetailsMap - Map of LoginVerify and the user Full Name.
   */
  public Map<Boolean, String> checkLoginValidity(String gitUserName, String gitPassword,
      String gitURL) {
    this.gitConnectionURL = "http://" + gitURL + "/";
    this.userIdentity = new UsernamePasswordCredentialsProvider(gitUserName, gitPassword);

    Map<Boolean, String> loginCheckDetailsMap = new LinkedHashMap<>();
    try {
      Map<TokenType, String> typeandToken =
          obtainAccessToken(this.gitConnectionURL, gitUserName, gitPassword, false);
      Entry<TokenType, String> entry = typeandToken.entrySet().iterator().next();
      this.gitLabApi = GitlabAPI.connect(this.gitConnectionURL, entry.getValue(), entry.getKey());
      loginCheckDetailsMap.put(true, gitLabApi.getUser().getUsername());
    } catch (Exception e) {
      loginCheckDetailsMap.put(false, "");
      e.printStackTrace();
    }
    return loginCheckDetailsMap;
  }

  // ********** Stage 2-RepositoriesGetting **********//
  /*
   * getListOfRepositories is used to Retrieve all Repositories of the User.
   * 
   * @return List<String> listOfProjects - List of Repositories of the User.
   */
  public List<String> getListOfRepositories() {
    this.listofRepositories = new ArrayList<>();
    try {
      List<GitlabProject> projects;
      projects = gitLabApi.getProjects();
      for (GitlabProject project : projects) {
        this.listofRepositories.add(project.getName());
      }
    } catch (IOException e) {
      // Cannot get List of Repositories. And Hence Number of Repositories will be 0.!
    }
    return this.listofRepositories;
  }

  // ********** Stage 3-GettingAllBranchesForSelectedRepositories **********//

  /*
   * getAllBranches is used to Retrieve all Branches for the Repositories provided.
   * 
   * @param List<String> selectedRepositories - User Selected Repositories.
   * 
   * @return Map<String, List<String>> selectedReposAndAllTheirBranches - Map of Repositories and
   * their corresponding Branches.
   */
  public Map<String, List<String>> getAllBranches(List<String> selectedRepositories) {

    this.selectedRepositories = selectedRepositories;
    this.selectedReposAndAllTheirBranches = new HashMap<>();

    this.selectedRepositories.forEach(repository -> {
      try {
        this.selectedReposAndAllTheirBranches.put(repository, getBranches(repository));
      } catch (Exception e) {

      }
    });

    return this.selectedReposAndAllTheirBranches;
  }

  // ********** Stage 3.1-GettingBranchesOfARepository **********//
  public List<String> getBranches(String repository) {
    List<String> branches = new ArrayList<>();
    Collection<Ref> call;
    try {
      call =
          Git.lsRemoteRepository().setHeads(true).setCredentialsProvider(this.userIdentity)
              .setRemote(this.gitConnectionURL + "root/" + repository + ".git").call();
      for (Ref ref : call) {
        branches.add(ref.getName());
      }
    } catch (GitAPIException e) {
      e.printStackTrace();
      // Cannot get List of Branches. And Hence Number of Branches will be 0.!
    }
    return branches;
  }

  // ********** Stage 4-StoringSelectedBranchesOfTheRepositories **********//
  /*
   * gettingTheSelectedBranches, maps selected Repositories to the selected Branches - by the User.
   * 
   * @param Map<Integer,List<String>> modeAndBranches - Mode of Select(Manually Selected Branches,
   * or Used Versions to Select Branches)
   * 
   * @return Map<String, List<String>> selectedReposAndTheirSelectedBranches - Selected Repositories
   * and Their Corresponding Selected Branches.
   */
  public Map<String, List<String>> gettingTheSelectedBranches(
      Map<Integer, List<String>> modeAndBranches) {

    this.selectedReposAndTheirSelectedBranches = new HashMap<>();

    // User Chosen Branches Manually Since it is a Single Repository.
    if (modeAndBranches.containsKey(1)) {
      Entry<String, List<String>> singleSelectedRepo =
          this.selectedReposAndAllTheirBranches.entrySet().iterator().next();
      // Assumption - modeAndBranches.get(1) is returned as refs/heads/river-1709 and not in any
      // other format
      this.selectedReposAndTheirSelectedBranches.put(singleSelectedRepo.getKey(),
          modeAndBranches.get(1));
    }

    // User Chosen Branches Using Version, may be a Single Repository or Multiple Repositories.
    else if (modeAndBranches.containsKey(2)) {
      this.selectedReposAndAllTheirBranches
          .entrySet()
          .stream()
          .forEach(
              repository -> {
                List<String> selectedBranches = new ArrayList<>();
                String inputVersions;
                inputVersions = modeAndBranches.get(2).get(0);
                int index = 0;
                while (index < repository.getValue().size()) {
                  String branchName = repository.getValue().get(index).replace("refs/heads/", "");
                  String branchVersion =
                      repository.getValue().get(index).replace("refs/heads/river-", "");
                  // Warning - Some Branches are named as river-7609 but are
                  // actually dummy branches. For Such Branches filteration
                  // this can be added to if condition,
                  // but has a flaw that branches corresponding to Years 2017,2018,2019 respectively
                  // can only
                  // be manipulated..
                  // -
                  // &&(branchVersion.startsWith("17"))&&(branchVersion.startsWith("18"))&&(branchVersion.startsWith("19"))
                  if ((branchName.startsWith("river-")) && (branchName.length() == 10)
                      && (branchVersion.matches("-?\\d+(\\.\\d+)?"))) {
                    Integer version = Integer.parseInt(branchVersion);
                    // Lesser Than the River Version Given
                    if (!((inputVersions.contains(">") && inputVersions.contains("<")))
                        && (inputVersions.startsWith("<"))
                        && (version.compareTo(Integer.parseInt(inputVersions.replace("<", ""))) < 0)) {
                      selectedBranches.add(repository.getValue().get(index));
                    }
                    // Greater Than the River Version Given
                    if (!((inputVersions.contains(">") && inputVersions.contains("<")))
                        && (inputVersions.startsWith(">"))
                        && (version.compareTo(Integer.parseInt(inputVersions.replace(">", ""))) > 0)) {
                      selectedBranches.add(repository.getValue().get(index));
                    }
                    // Between the River Versions Given
                    if ((inputVersions.contains(">") && inputVersions.contains("<"))
                        && ((version.compareTo(Integer.parseInt(inputVersions.substring(1, 5))) > 0) && (version
                            .compareTo(Integer.parseInt(inputVersions.substring(6, 10))) < 0))) {
                      selectedBranches.add(repository.getValue().get(index));
                    }
                    // For River Versions Given as Seperated By Comma
                    else if (inputVersions.contains(",")) {
                      String[] givenVersions = inputVersions.split(",");
                      for (String branch : givenVersions) {
                        if (version == Integer.parseInt(branch)) {
                          selectedBranches.add(repository.getValue().get(index));
                        }
                      }
                    }
                    // For a Single River Version Given
                    else if ((!(inputVersions.contains(",")) && (!inputVersions.contains(">")) && (!inputVersions
                        .contains("<"))) && !(inputVersions.equals("river"))) {
                      if (version == Integer.parseInt(inputVersions)) {
                        selectedBranches.add(repository.getValue().get(index));
                      }
                    }
                  }
                  // For the River Version Given as - river
                  else if (inputVersions.equals("river")) {
                    String versionriver = branchName;
                    if (versionriver.equals("river")) {
                      selectedBranches.add(repository.getValue().get(index));
                    }
                  }
                  index++;
                }
                this.selectedReposAndTheirSelectedBranches.put(repository.getKey(),
                    selectedBranches);
              });
    }
    return this.selectedReposAndTheirSelectedBranches;
  }

  // ********** Stage 5-CloningTheBranches **********//
  /*
   * cloneBranches, Clones all Branches and makes a Map of Repositories and Branches and their
   * corresponding AbsolutePaths of all Files.
   * 
   * @return List<FileUnit> allFiles - All File Units with each containing bunch of Details.
   */
  public Map<String, Map<MultiKey, List<String>>> cloneBranches() {
    this.repoItsbranchesAndTheirListofFiles = new HashMap<>();
    this.clonedDirectoryPaths = new ArrayList<>();

    // Calculating the Total Number of Branches - selected Branches for the selected Repositories. -
    // For Progress
    // Update.
    Collection<List<String>> repositoryBranches =
        this.selectedReposAndTheirSelectedBranches.values();
    int totalBranches = 0;
    for (List<String> branchList : repositoryBranches) {
      totalBranches += branchList.size();
    }
    // Cloning the Branches And Getting Absolute Paths of All Files.
    this.selectedReposAndTheirSelectedBranches
        .entrySet()
        .stream()
        .forEach(repository -> {
          Map<MultiKey, List<String>> branchAndListofFiles = new HashMap<>();
          for (String branch : repository.getValue()) {
            List<String> absolutePathOfAllFiles = new ArrayList<>();
            // Commented Beacuse Now Storage is Changed to User Working Directory.
            // File tempDirectory = Files.createTempDir();
            File cloneDirectory =
                getServerProjectDir(repository.getKey(), branch.replace("refs/heads/", ""));
            this.clonedDirectoryPaths.add(cloneDirectory.getAbsolutePath());
            if (!Files.exists(cloneDirectory.toPath())) {
              Path clonePath = Paths.get(cloneDirectory.getAbsolutePath());
              try {
                Files.createDirectories(clonePath);
              } catch (IOException e) {
                // Cannot Create ClonePath Directory.
              }
              try {
                Git.cloneRepository()
                    .setURI(this.gitConnectionURL + "root/" + repository.getKey() + ".git")
                    .setBranchesToClone(repository.getValue())
                    .setBranch(branch)
                    .setProgressMonitor(
                        new TextProgressMonitor(new PrintWriter(new File(gravityStoragaePath
                            + File.separator + "CloneProgress.txt")))).setDirectory(cloneDirectory)
                    .setCredentialsProvider(this.userIdentity).call();
              } catch (Exception e) {
                // Cannot Clone the Particular Branch - @branch@ in the Repository - @repository@
              }
            } else {
              pullTheBranch(repository.getKey(), branch.replace("refs/heads/", ""));
            }
            absolutePathOfAllFiles =
                getAbsolutePaths(cloneDirectory.getAbsolutePath(), absolutePathOfAllFiles);
            // This MultiKey contains branch name(river-1709) and it local cloned Folder
            // Path(C:\Users\sankraja\AppData\Local\Temp\156400270321782671-0)
            MultiKey branchAndLocalBranchName =
                new MultiKey(branch.replace("refs/heads/", ""), cloneDirectory.getAbsolutePath());
            branchAndListofFiles.put(branchAndLocalBranchName, absolutePathOfAllFiles);
          }
          this.repoItsbranchesAndTheirListofFiles.put(repository.getKey(), branchAndListofFiles);
        });
    // returns FileUnits List - Each FileUnit is Packed With Bunch Of Details.
    // return makeFileUnits(this.repoItsbranchesAndTheirListofFiles);
    return this.repoItsbranchesAndTheirListofFiles;
  }

  // ********** Stage 5.1-GetTheDirectoryWhereTheCloneHasToHappen **********//
  /*
   * getServerProjectDir get the Directory while working on a Server. As of Now it is not
   * Considered.
   */
  public File getServerProjectDir(String repository, String branch) {
    String catalinaHome = System.getProperty("catalina.home");
    String gravityPath = catalinaHome + File.separator + "Gravity";
    String projectPath = gravityPath + File.separator + repository + File.separator + branch;
    gravityStoragaePath = gravityPath;
    return new File(projectPath);
  }

  /*
   * getSystemProjectDir will get the Current Working Directory of the System.
   */
  public File getSystemProjectDir(String repository, String branch) {
    String localWorkingDirectory = System.getProperty("user.dir");
    String gravityPath = localWorkingDirectory + File.separator + "Gravity";
    String projectPath = gravityPath + File.separator + repository + File.separator + branch;
    gravityStoragaePath = gravityPath;
    return new File(projectPath);
  }

  // ********** Stage 5.2-PullIfTheBranchIsAlreadyCloned **********//
  /*
   * Pull a Particular Branch in a Repository if it is Already Cloned.
   */
  private void pullTheBranch(String repository, String branch) {
    try {
      Git git = Git.open(getServerProjectDir(repository, branch));
      git.pull().setCredentialsProvider(this.userIdentity).call();
      git.close();
    } catch (IOException | GitAPIException e) {
    }
  }

  // ********** Stage 5.3-GettingAbsolutePathofAllFiles **********//
  /*
   * getAbsolutePaths is used to get all File's Absolute Path in the Directory(Cloned Copy) given.
   * 
   * @param String directory - the Local Cloned Directory Path
   * 
   * @param List<String> absolutePathOfAllFiles - Absolute
   * 
   * @return List<String> absolutePathOfAllFiles - List of File Absolute Path
   */
  private static List<String> getAbsolutePaths(String directory, List<String> absolutePathOfAllFiles) {
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

  // ********** Stage 5.4-MakeFileUnits **********//
  /*
   * makeFileUnits is used to make List of FileUnits for all File's Absolute Path for Repository and
   * Branch.
   * 
   * @param Map<String, Map<MultiKey, List<String>>> repoItsbranchesAndTheirListofFiles -
   * Repository, Branch and List of Absolute File Paths
   * 
   * @return List<FileUnit> allFiles - All File Units with each containing bunch of Details.
   */
  // private static List<FileUnit> makeFileUnits(
  // Map<String, Map<MultiKey, List<String>>> repoItsbranchesAndTheirListofFiles) {
  // List<FileUnit> allFiles = new ArrayList<>();
  // repoItsbranchesAndTheirListofFiles
  // .entrySet()
  // .stream()
  // .forEach(
  // repository -> {
  // repository
  // .getValue()
  // .entrySet()
  // .stream()
  // .forEach(
  // branch -> {
  // for (String fileAbsolutePath : branch.getValue()) {
  // allFiles.add(new FileUnit(fileAbsolutePath, repository.getKey(),
  // branch.getKey().getKey(0).toString()));
  // }
  // });
  // });
  // return allFiles;
  // }

  // ********** Stage 6-ConvertingAbsolutePathstoURLs **********//
  /*
   * makeListOfUrls converts the Tool Output Findings with Absolute Paths to URLs of the
   * Corresponding Files.
   * 
   * @param Map<String, Map<MultiKey, List<String>>> repoBranchesListOfFindings - Absolute Paths of
   * Findings of Branches For Repositories.
   */// This MultiKey contains branch name(river-1709) and it local cloned Folder
  // Path(C:\Users\sankraja\AppData\Local\Temp\river-1709-476400270321782671)
  public Map<String, Map<String, List<String>>> makeListOfUrls(
      Map<String, Map<MultiKey, List<String>>> repoBranchesListOfFindings) {
    // Repository, Branch, ListOfGitURLs
    Map<String, Map<String, List<String>>> repositoryAndBranchFindings = new HashMap<>();
    repoBranchesListOfFindings
        .entrySet()
        .stream()
        .forEach(
            repository -> {
              Map<String, List<String>> branchAndFindingURLs = new HashMap<>();
              repository
                  .getValue()
                  .entrySet()
                  .stream()
                  .forEach(
                      branch -> {
                        List<String> listOfURL = new ArrayList<>();
                        if (branch.getValue().size() != 0) {
                          int printErrors = 0;
                          while (printErrors < branch.getValue().size()) {
                            listOfURL.add((branch
                                .getValue()
                                .get(printErrors)
                                .replace(
                                    branch.getKey().getKey(1).toString(),
                                    this.gitConnectionURL + "root/" + repository.getKey()
                                        + "/blob/" + branch.getKey().getKey(0).toString()).replace(
                                "\\", "/")));
                            printErrors++;
                          }
                          branchAndFindingURLs.put(branch.getKey().getKey(0).toString(), listOfURL);
                        } else {
                          listOfURL.add("No Findings in this branch");
                          branchAndFindingURLs.put(branch.getKey().getKey(0).toString(), listOfURL);
                        }
                      });
              repositoryAndBranchFindings.put(repository.getKey(), branchAndFindingURLs);
            });
    return repositoryAndBranchFindings;
  }

  // ********** Stage 7-WritingOutputToExcelFile **********//
  /*
   * writeInExcelFile writes the Tool Output Findings to a Excel File.
   * 
   * @param Map<String, Map<String, List<String>>> repositoryAndBranchFindings - Repository, Branch,
   * ListOfGitURLs.
   * 
   * @param String functionality - the Functionality invoked by User
   */
  public void writeInExcelFile(Map<String, Map<String, List<String>>> repositoryAndBranchFindings,
      String functionality) {

    XSSFWorkbook mainWorkbook = new XSSFWorkbook();
    // Creating an Overview Sheet
    XSSFSheet overViewSheet = mainWorkbook.createSheet("Overview");
    AtomicInteger overViewRowCount = new AtomicInteger(0);
    CellStyle overViewHeaderStyle = createCellStyle(overViewSheet, 32, true);
    CellStyle overViewStyle = createCellStyle(overViewSheet, 16, true);

    overViewSheet.setColumnWidth(10, 100 * 93);
    overViewSheet.setColumnWidth(11, 100 * 93);

    Row headerOverviewRow = overViewSheet.createRow(overViewRowCount.incrementAndGet());
    createCell(headerOverviewRow, 10, "Repository", overViewHeaderStyle);
    createCell(headerOverviewRow, 11, "Total Findings", overViewHeaderStyle);

    repositoryAndBranchFindings
        .entrySet()
        .stream()
        .forEach(repository -> {
          Row repositoryRow = overViewSheet.createRow(overViewRowCount.incrementAndGet());
          createCell(repositoryRow, 10, repository.getKey(), overViewStyle);

          // Findings Count
            long count =
                repository.getValue().entrySet().stream()
                    .flatMap(branch -> branch.getValue().stream())
                    .filter(noFinding -> !noFinding.equals("No Findings in this branch"))
                    .collect(Collectors.counting());
            createCell(repositoryRow, 11, String.valueOf(count), overViewStyle);

          });

    repositoryAndBranchFindings.entrySet().stream().forEach(repository -> {
      AtomicInteger rowCount = new AtomicInteger(0);
      String repoName = repository.getKey();

      // Sheet Names with Length greater than 32 will Throw IllegalArgumentException when getSheet
      // method is
      // called.
        if (repoName.length() > 32) {
          repoName = repoName.substring(0, 31);
        }
        // Create a Sheet for a Repository
        XSSFSheet sheet = mainWorkbook.createSheet(repoName);

        repository.getValue().entrySet().stream().forEach(branch -> {
          // Branch Name Write
            CellStyle branchStyle = createCellStyle(sheet, 16, true);
            Row headerRow = sheet.createRow(rowCount.incrementAndGet());
            createCell(headerRow, 0, branch.getKey(), branchStyle);

            // If there are no Findings for the Branch
            if (branch.getValue().get(0).equals("No Findings in this branch")) {
              Row noFindingsRow = sheet.createRow(rowCount.incrementAndGet());
              Cell cellTitlee = noFindingsRow.createCell(0);
              cellTitlee.setCellValue("No Findings in this branch");

              // If there are Findings in the Branch
            } else {
              for (String findingURL : branch.getValue()) {
                Row findingsRow = sheet.createRow(rowCount.incrementAndGet());
                Cell findingsCell = findingsRow.createCell(0);
                Workbook contentsBook = new XSSFWorkbook();
                CreationHelper createHelper = contentsBook.getCreationHelper();
                Hyperlink gitLinkOfFile = createHelper.createHyperlink(HyperlinkType.URL);
                gitLinkOfFile.setAddress(findingURL);
                findingsCell.setHyperlink(gitLinkOfFile);
                findingsCell.setCellValue(findingURL.substring(findingURL.lastIndexOf("/") + 1));
                rowCount.incrementAndGet();
              }
            }
          });

        // The TradeMark Write
        CellStyle gravityStyle = createCellStyle(sheet, 8, true);
        Row gravityWorkRow = sheet.createRow(rowCount.incrementAndGet());
        createCell(gravityWorkRow, 0, "Facile work through Gravity", gravityStyle);
      });

    // Write the WorkBook to a File
    String fileName = functionality;
    Path OutputFolder = Paths.get(gravityStoragaePath + File.separator + "GravityOutput");
    try {
      Files.createDirectories(OutputFolder);
    } catch (Exception e1) {
    }
    try (FileOutputStream outputStream =
        new FileOutputStream(OutputFolder.toString() + File.separator + fileName + ".xlsx")) {
      mainWorkbook.write(outputStream);
      mainWorkbook.close();
    } catch (IOException e) {
      // Error Writing the WorkBook Contents to a Excel File.
    }
  }

  // To Create a Cell
  private static void createCell(Row row, int cellNumber, String value, CellStyle style) {
    Cell cell = row.createCell(cellNumber);
    cell.setCellStyle(style);
    cell.setCellValue(value);
  }

  // To Create a CellStyle
  private static CellStyle createCellStyle(XSSFSheet sheet, int fontSize, boolean bold) {
    CellStyle style = sheet.getWorkbook().createCellStyle();
    Font font = sheet.getWorkbook().createFont();
    font.setBold(bold);
    font.setFontHeightInPoints((short) fontSize);
    style.setFont(font);
    return style;
  }

  public static Map<TokenType, String> obtainAccessToken(String gitlabUrl, String username,
      String password, boolean sudoScope) throws IOException {
    Map<TokenType, String> tokenTypeAndToken = new HashMap<>();
    try {
      final OAuthGetAccessToken tokenServerUrl =
          new OAuthGetAccessToken(gitlabUrl + "/oauth/token"
              + (sudoScope ? "?scope=api%20sudo" : ""));
      final TokenResponse oauthResponse =
          new PasswordTokenRequest(transport, JacksonFactory.getDefaultInstance(), tokenServerUrl,
              username, password).execute();
      tokenTypeAndToken.put(TokenType.ACCESS_TOKEN, oauthResponse.getAccessToken());
      return tokenTypeAndToken;
    } catch (TokenResponseException e) {
      if (sudoScope && e.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
        // Fallback for pre-10.2 gitlab versions
        final GitlabSession session = GitlabAPI.connect(gitlabUrl, username, password);
        tokenTypeAndToken.put(TokenType.PRIVATE_TOKEN, session.getPrivateToken());
        return tokenTypeAndToken;
      } else {
        throw new GitlabAPIException(e.getMessage(), e.getStatusCode(), e);
      }
    }
  }

  /*
   * classifyUsingController is used to find List of LicenseGroup, License, Subsystem, Epic.
   * 
   * @param List<String> listOfBranchFiles - List of All Absolute Paths of the Branch Files
   * 
   * @return Map<String, Map<String, Map<String, Set<String>>>> LicenseGroup, License, Subsystem,
   * Epic Details Dependent Structure.
   */
  public Map<String, Map<String, Map<String, Set<String>>>> classifyUsingController(
      List<String> listOfBranchFiles, boolean oneNintyRepo) {
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
          if (oneNintyRepo) {
            subsystem = foldersOfAFile.get(controllerIndex + 4);
            epic = foldersOfAFile.get(controllerIndex + 5);
          }

          if (structureMap.containsKey(licenseGroup)) {
            if (structureMap.get(licenseGroup).containsKey(license)) {
              if (structureMap.get(licenseGroup).get(license).containsKey(subsystem)) {
                if (!epic.contains(".")) {
                  structureMap.get(licenseGroup).get(license).get(subsystem).add(epic);
                }
              } else {
                if (!subsystem.contains(".")) {
                  structureMap.get(licenseGroup).get(license).put(subsystem, new HashSet<>());
                }
              }
            } else {
              Map<String, Set<String>> subsystemMap = new HashMap<>();
              structureMap.get(licenseGroup).put(license, subsystemMap);
            }
          } else {
            Map<String, Map<String, Set<String>>> licenseMap = new HashMap<>();
            structureMap.put(licenseGroup, licenseMap);
          }
          // Commented Because More Dependendent Structure is Expected.
          // licenseGroupList.add(foldersOfAFile.get(controllerIndex + 1));
          // licenseList.add(foldersOfAFile.get(controllerIndex + 2));
          // subsystemList.add(foldersOfAFile.get(controllerIndex + 3));
          // epicList.add(foldersOfAFile.get(controllerIndex + 4));
      } catch (IndexOutOfBoundsException e) {
        // UnCategorised Files in Controller Folder Throws This Exception.
      }
    }
  } );
    return structureMap;
  }
}
