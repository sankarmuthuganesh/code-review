package RealTime.GitAccess;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.gitlab.api.models.GitlabUser;
import RealTime.UI.RepositoryBranchSelection;
import com.github.javaparser.ParseException;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


@Getter
@Setter

public class Progress {
  private static final HttpTransport transport = new NetHttpTransport();
	private String gravityStoragaePath="C:\\HUE\\Application\\apache-tomcat-8.0.37\\Gravity";
	 String gitConnection;
	/**
	 * The currentFileName
	 */
	public static String currentFileName;
	private final String userPrivateToken="ohLKos1F6wyf1yPr81iy";
	/**
	 * The input
	 */
	public static Scanner input=new Scanner(System.in);

	/**
	 * The localBranchPathsList
	 */
	List<String> localBranchPathsList=new ArrayList<>();

	/**
	 * The repoItsbranchesAndTheirListofFiles
	 */
	public static Map<String,Map<MultiKey,List<String>>> repoItsbranchesAndTheirListofFiles=new LinkedHashMap<>();
	/**
	 * The county
	 */
	static int county=0;
	/**
	 * The listOfProjects
	 */
	List<String> listOfProjects=new ArrayList<>();
	/**
	 * The gitlabSession
	 */
	GitlabAPI gitlabSession;

	/**
	 * The gitUserNameField
	 */
	private String gitUserNameField;
	/**
	 * The user
	 */
	CredentialsProvider user;
	/**
	 * The gitPasswordField
	 */
	private String gitPasswordField;
	/**
	 * The selectedProjects
	 */
	public static List<String> selectedProjects=new ArrayList<>();
	/**
	 * The selectedRepoAndItsBranches
	 */
	private Map<String, List<String>> selectedRepoAndItsBranches=new LinkedHashMap<>();
	/**
	 * The selectedRepoAndItsSelectedBranches
	 */
	public Map<String, List<String>> selectedRepoAndItsSelectedBranches=new LinkedHashMap<>();

	/**
	 * checkLoginValidity 
	 * @param gitUserName
	 * @param gitPassword
	 * @param gitURL 
	 * @return
	 * Map<Boolean,String>
	 */
	public Map<Boolean,String> checkLoginValidity(String gitUserName,String gitPassword, String gitURL) {
		this.gitConnection="http://"+gitURL+"/";
		gitUserNameField=gitUserName;
		gitPasswordField=gitPassword;
		Map<Boolean,String> loginCheckDetailsMap=new LinkedHashMap<>();
		try{
		 Map<TokenType, String> typeandToken =
		        obtainAccessToken(this.gitConnection, gitUserNameField, gitPasswordField, false);
		    Entry<TokenType, String> entry = typeandToken.entrySet().iterator().next();
		    gitlabSession = GitlabAPI.connect(this.gitConnection, entry.getValue(), entry.getKey());
		    //loginCheckDetailsMap.put(true, gitlabSession.getUser().getName());
		}catch(Exception e){
		  loginCheckDetailsMap.put(false, "");
		  e.printStackTrace();
		}
		return loginCheckDetailsMap;
	}

	/**
	 * getListOfRepositories 
	 * @return
	 * @throws IOException
	 * List<String>
	 */
	public List<String> getListOfRepositories() throws IOException{
		List<GitlabProject> projects=gitlabSession.getProjects();
		for(GitlabProject project:projects){
			listOfProjects.add(project.getName());
		}
		return listOfProjects;
	}

	/**
	 * makeListOfBranchesForRepos 
	 * @param selectedReposIndices
	 * void
	 */
	public void makeListOfBranchesForRepos(List<String> selectedReposIndices){
		selectedProjects.clear();
		selectedReposIndices.stream().forEach(index -> {
			selectedProjects.add(listOfProjects.get(Integer.parseInt(index)));
		});
		selectedProjects.forEach(repo -> {
			try {
				selectedRepoAndItsBranches.put(repo,getAllBranchesForTheSelectedRepository(repo));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * getAllBranchesForTheSelectedRepository 
	 * @param repoSiName
	 * @return
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * List<String>
	 */
	public List<String> getAllBranchesForTheSelectedRepository(String repoSiName) throws InvalidRemoteException, TransportException, GitAPIException{		
		List<String >branches=new ArrayList<>();
		user = new UsernamePasswordCredentialsProvider(gitUserNameField, gitPasswordField);
		try{
			Collection<Ref> call = Git.lsRemoteRepository().setHeads(true).setCredentialsProvider(user).setRemote(gitConnection+"root/"+repoSiName+".git").call();
			int numberOfBranches=0;
			for(Ref ref : call){
				branches.add(ref.getName());
				//System.out.println("Branch: "+ref+" "+ref.getName()+" "+ref.getObjectId().getName());
				numberOfBranches++;
			}
		}
		catch(TransportException e){
		}
		return branches;
	}

	/**
	 * decideSelectedReposAndMakeCorrespondingBranches 
	 * @param repoCollection
	 * @return
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * Map<String,List<String>>
	 */
	public Map<String, List<String>> decideSelectedReposAndMakeCorrespondingBranches(Map<String, List<String>> repoCollection) throws InvalidRemoteException, TransportException, GitAPIException {
		selectedRepoAndItsBranches.clear();
		if(repoCollection.containsKey("singleRepo")){
			String singleSelectedRepo=repoCollection.get("singleRepo").get(0);
			selectedProjects.clear();
			selectedProjects.add(singleSelectedRepo);
			selectedRepoAndItsBranches.put(singleSelectedRepo,getAllBranchesForTheSelectedRepository(singleSelectedRepo));
		}
		else if(repoCollection.containsKey("multipleRepos")){
			makeListOfBranchesForRepos(repoCollection.get("multipleRepos"));
		}
		return selectedRepoAndItsBranches;
	}

	// GETTING THE GIT CLONE OF ALL INITIALLY AND THEN LISTING THE BRANCHES
	/**
	 * gettingTheSelectedBranches 
	 * @param indexAndSelectedBranches
	 * @param whichMethodIsChosen
	 * @return
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * Map<String,List<String>>
	 */
	public  Map<String, List<String>> gettingTheSelectedBranches(Map<Integer,List<String>> indexAndSelectedBranches, AtomicInteger whichMethodIsChosen) throws InvalidRemoteException, TransportException, GitAPIException, IOException, ParseException, InterruptedException{

		selectedRepoAndItsSelectedBranches.clear();
		if(indexAndSelectedBranches.containsKey(1)&&whichMethodIsChosen.get()==0){
			Entry<String, List<String>> singleSelectedBranch = selectedRepoAndItsBranches.entrySet().iterator().next();
			List<String> selectedBranches=new ArrayList<>();
			indexAndSelectedBranches.get(1).stream().forEach(index -> {
				selectedBranches.add(singleSelectedBranch.getValue().get(Integer.parseInt(index)));
			});
			selectedRepoAndItsSelectedBranches.put(singleSelectedBranch.getKey(), selectedBranches);
		}

		else if(indexAndSelectedBranches.containsKey(2)){

			selectedRepoAndItsBranches.entrySet().stream().forEach(repo ->{
				List<String> selectedBranches=new ArrayList<>();

				String inputVersions;
				inputVersions=indexAndSelectedBranches.get(2).get(0);
				int dummyIndexOne=0;
				List<Integer> versions=new ArrayList<>();
				System.out.println(repo.getValue());
				while(dummyIndexOne<repo.getValue().size()){

					if((repo.getValue().get(dummyIndexOne).replace("refs/heads/", "").startsWith("river-"))&&(repo.getValue().get(dummyIndexOne).replace("refs/heads/", "").length()==10)&&(repo.getValue().get(dummyIndexOne).replace("refs/heads/river-", "").matches("-?\\d+(\\.\\d+)?"))){
						Integer version=Integer.parseInt(repo.getValue().get(dummyIndexOne).replace("refs/heads/", "").replace("river-", ""));

						if(!((inputVersions.contains(">")&&inputVersions.contains("<")))&&(inputVersions.startsWith("<"))&&(version.compareTo(Integer.parseInt(inputVersions.replace("<", "")))< 0)){
							selectedBranches.add(repo.getValue().get(dummyIndexOne));
						}
						if(!((inputVersions.contains(">")&&inputVersions.contains("<")))&&(inputVersions.startsWith(">"))&&(version.compareTo(Integer.parseInt(inputVersions.replace(">", "")))> 0)){
							selectedBranches.add(repo.getValue().get(dummyIndexOne));
						}

						if((inputVersions.contains(">")&&inputVersions.contains("<"))&&((version.compareTo(Integer.parseInt(inputVersions.substring(1, 5)))> 0)&&(version.compareTo(Integer.parseInt(inputVersions.substring(6, 10)))< 0))){
							selectedBranches.add(repo.getValue().get(dummyIndexOne));
						}
						else if(inputVersions.contains(",")){
							String[] givenVersions = inputVersions.split(",");
							for(String bran:givenVersions){
								if(version==Integer.parseInt(bran)){
									selectedBranches.add(repo.getValue().get(dummyIndexOne));
								}
							}
						}
						else if((!(inputVersions.contains(","))&&(!inputVersions.contains(">"))&&(!inputVersions.contains("<")))&&!(inputVersions.equals("river"))){
							if(version==Integer.parseInt(inputVersions)){
								selectedBranches.add(repo.getValue().get(dummyIndexOne));
							}
						}	
					}
					
					else if(inputVersions.equals("river")){
						String versionriver=repo.getValue().get(dummyIndexOne).replace("refs/heads/", "");
						if(versionriver.equals("river")){
							selectedBranches.add(repo.getValue().get(dummyIndexOne));
						}
					}
					dummyIndexOne++;
				}	
				selectedRepoAndItsSelectedBranches.put(repo.getKey(), selectedBranches);
			});
		}
		return selectedRepoAndItsSelectedBranches;
	}


	/**
	 * @author sankraja
	 *
	 */
	public class Task extends SwingWorker<Void, Void> {
		JFrame branchJFrame;
		int filterFlag;
		Map<String,String> filterMap;
		public Task(JFrame frame){
			this.branchJFrame=frame;
		}
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ParseException {
			repoItsbranchesAndTheirListofFiles.clear();
			localBranchPathsList.clear();
			//Initialize progress property.
			setProgress(0);

			//FOR CLONING ALL THE BRANCHES AND FINDING THE FILES CONTATINING THE STRING
			AtomicInteger OutercountProgress=new AtomicInteger(1);
			AtomicInteger OuterpercentCompleted=new AtomicInteger(0);
			AtomicInteger OutertotalProgress =new AtomicInteger(0);
			RepositoryBranchSelection progressObj=new RepositoryBranchSelection();
			Collection<List<String>> branchListColl = selectedRepoAndItsSelectedBranches.values();
			AtomicInteger totalBranchSize=new AtomicInteger(0);
			for(List<String> branchList:branchListColl){
				totalBranchSize.set(totalBranchSize.get()+branchList.size());
			}
			selectedRepoAndItsSelectedBranches.entrySet().stream().forEach(repo -> {
				Map<MultiKey,List<String>> branchAndListofFiles=new LinkedHashMap<>();
				for(String branch:repo.getValue()){
					List<String> allFilesInTheDirectoryPath=new ArrayList<>();
					Path localBranchPath=Paths.get("C:\\Gravity\\Clones\\Temp\\"+repo.getKey()+"\\"+branch.replace("refs/heads/", "")+"\\");
					try {
//						localBranchPath = File.createTempFile(repo.getKey()+"-h"+branch+"-","");
//						if(!localBranchPath.delete()){
//							try {
//								throw new IOException("Could not delete temporary file"+localBranchPath);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
						if(!Files.exists(localBranchPath)){
							Files.createDirectories(localBranchPath);
							try {
								Git gitBranch= Git.cloneRepository().setURI(gitConnection+"root/"+repo.getKey()+".git")
										.setProgressMonitor(new TextProgressMonitor(new PrintWriter(new File("D:\\text.txt"))))
										.setBranch(branch).setDirectory(localBranchPath.toFile())
//										.setCredentialsProvider(user).call();
										.setCredentialsProvider(new UsernamePasswordCredentialsProvider("sundaravelvar", userPrivateToken)).call();
							} catch (Exception e) {	
								e.printStackTrace();
							}
						}else{
							FileRepositoryBuilder fileRepoBuilder = new FileRepositoryBuilder();
							Repository repoLocal = null;
							try {
								repoLocal = fileRepoBuilder.setGitDir(new File("C:\\Gravity\\Clones\\Temp\\"+repo.getKey()+"\\"+branch.replace("refs/heads/", "")+"\\.git")).readEnvironment().findGitDir().build();
							} catch (IOException e) {
								//***Error Reading Repository
							}
							Git gitUtils = new Git(repoLocal);
							PullCommand pullCmd = gitUtils.pull().setCredentialsProvider(user);
							
							PullResult result=pullCmd.call();
							FetchResult pp = result.getFetchResult();
							MergeResult mm = result.getMergeResult();
							System.out.println("------------------------------------------------");
							System.out.println(repo.getKey()+"\\"+branch);
							System.out.println(mm.getMergeStatus());	
						}

						localBranchPathsList.add(localBranchPath.toString());
						OutertotalProgress.set(OutertotalProgress.get()+ OutercountProgress.get());
						OuterpercentCompleted.set((int)(OutertotalProgress.get()*100/totalBranchSize.get()));
						setProgress(OuterpercentCompleted.get());
						
						//System.out.println("-----------------------------------------Cheking the files in the Branch: "+localBranchPath.toString()+"------------------------------------------");
						try {
							allFilesInTheDirectoryPath=listAllFiles(localBranchPath.toString(),allFilesInTheDirectoryPath,filterFlag,filterMap);
							//allFilesInTheDirectoryPath=listAllFiles("C:\\Users\\sankraja\\Downloads\\hue-spec-design-ticket-7544-\\hue-spec-design-ticket-7544",allFilesInTheDirectoryPath,filterFlag,filterMap);
						} catch (Exception e) {
						}
						MultiKey branchAndLocalBranchName=new MultiKey(branch.replace("refs/heads/", ""),localBranchPath.toString());
						branchAndListofFiles.put(branchAndLocalBranchName, allFilesInTheDirectoryPath);	
					} catch (Exception e1) {
					}
					//System.out.println("localbranch"+localBranchPath.toString().replace(File.separator, "\\\\"));
				}
				repoItsbranchesAndTheirListofFiles.put(repo.getKey(), branchAndListofFiles);

			});
			//return branchAndListofFiles;
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			//startButton.setEnabled(true);
			branchJFrame.setCursor(null);//turn off the wait cursor
			branchJFrame.setVisible(false);
			branchJFrame.dispose();
			// taskOutput.append("Done!\n");
			RepositoryBranchSelection.putFiles(branchJFrame);
		}
	}


	/*public Map<MultiKey, List<String>> cloningTheBranches() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ParseException{
		branchAndListofFiles=new LinkedHashMap<>();
		//FOR CLONING ALL THE BRANCHES AND FINDING THE FILES CONTATINING THE STRING
		int countProgress=1;
		int percentCompleted=0;
		int totalProgress =0;
		Branch progressObj=new Branch();
		for(String branch:selectedBranches){
			List<String> allFilesInTheDirectoryPath=new ArrayList<>();
			File localBranchPath=File.createTempFile(branch, "Repository");
			//System.out.println("localbranch"+localBranchPath.toString().replace(File.separator, "\\\\"));
			if(!localBranchPath.delete()){
				throw new IOException("Could not delete temporary file"+localBranchPath);
			}

			Git gitBranch= Git.cloneRepository().setURI("http://192.168.41.136/root/"+selectedRepository+".git").setBranchesToClone(selectedBranches).setBranch(branch).setDirectory(localBranchPath).setCredentialsProvider(user).call();

				totalProgress += countProgress;
				percentCompleted = (int)(totalProgress*100/selectedBranches.size());
				progressObj.setProgress(percentCompleted);

			//System.out.println("-----------------------------------------Cheking the files in the Branch: "+localBranchPath.toString()+"------------------------------------------");
			allFilesInTheDirectoryPath=listAllFiles(localBranchPath.toString(),allFilesInTheDirectoryPath);
			MultiKey branchAndLocalBranchName=new MultiKey(branch.replace("refs/heads/", ""),localBranchPath.toString());
			branchAndListofFiles.put(branchAndLocalBranchName, allFilesInTheDirectoryPath);	
		}
		return branchAndListofFiles;
	}*/




	/**
	 * makeListOfUrlsAndCreateExcel 
	 * @param repoBranchesListOfFindings
	 * @throws FileNotFoundException
	 * @throws IOException
	 * void
	 */
	public void makeListOfUrlsAndCreateExcel(Map<String, Map<MultiKey, List<String>>> repoBranchesListOfFindings) throws FileNotFoundException, IOException{
		Map<String, Map<String, List<String>>> resultCollectionTotal=new LinkedHashMap<>();
		repoBranchesListOfFindings.entrySet().stream().forEach(repo -> {
			Map<String,List<String>> resultCollection=new LinkedHashMap<>();
			repo.getValue().entrySet().stream().forEach(eachSet -> {
				List<String> listOfURL=new ArrayList<>();
				if(eachSet.getValue().size()!=0){
					int printErrors=0;
					while(printErrors<eachSet.getValue().size()){
						listOfURL.add((eachSet.getValue().get(printErrors).replace(eachSet.getKey().getKey(1).toString().replace(File.separator, "\\\\"), gitConnection+"root/"+repo.getKey()+"/blob/"+eachSet.getKey().getKey(0).toString()).replace("\\\\", "/")));
						printErrors++;
					}
					resultCollection.put(eachSet.getKey().getKey(0).toString(), listOfURL);
				}
				else{
					listOfURL.add("No Findings in this branch");
					resultCollection.put(eachSet.getKey().getKey(0).toString(), listOfURL);
				}
			});
			resultCollectionTotal.put(repo.getKey(), resultCollection);
		});


		writeInExcelFile(resultCollectionTotal);
	}		



	//GETTING ALL THE FILES ABSOLUTE PATH IN A FOLDER
	/**
	 * listAllFiles 
	 * @param directory
	 * @param allFilesInTheDirectoryPath
	 * @param filterFlag
	 * @param filterMap
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * List<String>
	 */
	public  static List<String> listAllFiles(String directory,List<String> allFilesInTheDirectoryPath, int filterFlag, Map<String, String> filterMap) throws ParseException, IOException{
		File[] files=new File(directory).listFiles();
		for(File file: files){
			if(file.isFile()){
				//CompilationUnit compiledFile = JavaParser.parse(file);
				//currentFileName = file.getName();
				//allFilesInTheDirectoryPath.add(file.getAbsolutePath().replace(File.separator, "\\\\"));&&currentFileName.endsWith(".js")
				//if(currentFileName.endsWith(".java")){  
				String filePath=file.getAbsolutePath()	.replace(File.separator, "\\\\");
				//				boolean exceptions=filePath.toLowerCase().contains("dto")||filePath.toLowerCase().contains("bizcore")||filePath.toLowerCase().contains("dao")|| filePath.toLowerCase().contains("entity")||filePath.toLowerCase().contains("type")||filePath.toLowerCase().contains("vo")||filePath.toLowerCase().contains("util");
				//				if(filterFlag==1){
				//					if(((filePath).contains(filterMap.get("epicName")))){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//					else if(exceptions){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//				}
				//				else if(filterFlag==2){
				//					if((filePath).contains(filterMap.get("subSystem"))){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//					else if(exceptions){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//				}
				//				else if(filterFlag==3){
				//					if(((filePath).contains(filterMap.get("epicName")))&&((filePath).contains(filterMap.get("subSystem")))){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//					else if(exceptions){
				//						allFilesInTheDirectoryPath.add(filePath);
				//					}
				//				}
				//	else{
				allFilesInTheDirectoryPath.add(filePath);
				//	}
				//}
			}
			else if(file.isDirectory()){
				listAllFiles(file.getAbsolutePath(),allFilesInTheDirectoryPath, filterFlag, filterMap);
			}	
		}
		return allFilesInTheDirectoryPath;
	}

	/**
	 * writeInExcelFile 
	 * @param resultCollectionTotal
	 * @throws FileNotFoundException
	 * @throws IOException
	 * void
	 */
	public static void writeInExcelFile(Map<String, Map<String, List<String>>> resultCollectionTotal) throws FileNotFoundException, IOException{
		XSSFWorkbook mainWorkbook=new XSSFWorkbook();
		
		//Creating an Overview Sheet
				XSSFSheet overViewSheet=mainWorkbook.createSheet("Overview");
				AtomicInteger overViewRowCount=new AtomicInteger(0);
				CellStyle overViewHeaderStyle=createCellStyle(overViewSheet,32,true);
				CellStyle overViewStyle=createCellStyle(overViewSheet,16,true);

				overViewSheet.setColumnWidth(10,100*93);
				overViewSheet.setColumnWidth(11,100*93);

				Row headerOverviewRow=overViewSheet.createRow(overViewRowCount.incrementAndGet());
				Cell rowCell = createCell(headerOverviewRow,10,"Repository",overViewHeaderStyle);
				Cell findingCell = createCell(headerOverviewRow,11,"Total Findings",overViewHeaderStyle);

				CellStyle styleColor=overViewSheet.getWorkbook().createCellStyle();
				styleColor.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
				styleColor.setFillPattern(FillPatternType.BIG_SPOTS);
				Font fontOverview=overViewSheet.getWorkbook().createFont();
				fontOverview.setBold(true);
				fontOverview.setFontHeightInPoints((short)32);
				styleColor.setFont(fontOverview);
				rowCell.setCellStyle(styleColor);
				findingCell.setCellStyle(styleColor);
				
				
				Comparator<Map.Entry<String, Map<String, List<String>>>> findingsCompare=(a,b) ->{
				  
				  long counta=a.getValue().entrySet().stream().flatMap(branch -> branch.getValue().stream()).filter(value ->{
                    return !value.equals("No Findings in this branch");
                }).collect(Collectors.counting());
				  long countb=b.getValue().entrySet().stream().flatMap(branch -> branch.getValue().stream()).filter(value ->{
                    return !value.equals("No Findings in this branch");
                }).collect(Collectors.counting());
				  
				  return Long.compare(counta, countb);
				};
				
				resultCollectionTotal.entrySet().stream().sorted(findingsCompare.reversed()).forEach(repository -> {
					Row repositoryRow=overViewSheet.createRow(overViewRowCount.incrementAndGet());
					createCell(repositoryRow,10,repository.getKey(),overViewStyle);

					//Findings Count
					long count=repository.getValue().entrySet().stream().flatMap(branch -> branch.getValue().stream()).filter(value ->{
						return !value.equals("No Findings in this branch");
					}).collect(Collectors.counting());
					
					Cell countCell=repositoryRow.createCell(11);
					countCell.setCellStyle(overViewStyle);
					
					Workbook overviewWorkbook=new XSSFWorkbook();
					CreationHelper overviewHelper=overviewWorkbook.getCreationHelper();
					Hyperlink linkOverview=overviewHelper.createHyperlink(HyperlinkType.DOCUMENT
);
					linkOverview.setAddress("'"+repository.getKey()+"'");
					countCell.setHyperlink(linkOverview);
					countCell.setCellValue(count);
				});

		resultCollectionTotal.entrySet().stream().forEach(repo -> {
			AtomicInteger rowCount=new AtomicInteger(0);

			XSSFSheet sheet=mainWorkbook.createSheet(repo.getKey());

			repo.getValue().entrySet().stream().forEach(input -> {
				CellStyle cellStyle=sheet.getWorkbook().createCellStyle();
				Font font=sheet.getWorkbook().createFont();
				font.setBold(true);
				font.setFontHeightInPoints((short)16);
				cellStyle.setFont(font);
				Row headerRow=sheet.createRow(rowCount.incrementAndGet());
				Cell cellTitle=headerRow.createCell(0);
				cellTitle.setCellStyle(cellStyle);
				cellTitle.setCellValue(input.getKey());

				if(input.getValue().get(0).equals("No Findings in this branch")){
					Row rowww=sheet.createRow(rowCount.incrementAndGet());
					Cell cellTitlee=rowww.createCell(0);
					cellTitlee.setCellValue("No Findings in this branch");

				}else{
					for(String field:input.getValue()){
						Row row=sheet.createRow(rowCount.incrementAndGet());
						//Cell cell=row.createCell(++columnCount);
						Cell cell=row.createCell(0);
						Workbook contentsBook=new XSSFWorkbook();
						CreationHelper createHelper=contentsBook.getCreationHelper();
						Hyperlink link=createHelper.createHyperlink(HyperlinkType.URL);
						//System.out.println(field);
						try{
							link.setAddress(field);
						}catch(IllegalArgumentException e){
							link.setAddress("https://www.google.com/");
							System.out.println(field);
						}
						
					//	link.setAddress(field);
						cell.setHyperlink(link);
						cell.setCellValue(field.substring(field.lastIndexOf("/")+1));
						rowCount.incrementAndGet();
						//}
					}
				}	
			});
			CellStyle cellStyles=sheet.getWorkbook().createCellStyle();
			Font fonts=sheet.getWorkbook().createFont();
			fonts.setBold(true);
			fonts.setFontHeightInPoints((short)8);
			cellStyles.setFont(fonts);
			Row rowww=sheet.createRow(rowCount.incrementAndGet());
			Cell cellTitlee=rowww.createCell(0);
			cellTitlee.setCellValue("Facile work through Gravity v1");
			cellTitlee.setCellStyle(cellStyles);
		});

		String fileName="ToolOutput";
		while(true){
			if(county==0){
				try(FileOutputStream outputStream=new FileOutputStream(fileName+".xlsx")){
					mainWorkbook.write(outputStream);
					Desktop desktop=java.awt.Desktop.getDesktop();
					desktop.open(new File(fileName+".xlsx"));
					county++;
					break;
				}
				catch( IOException | IllegalArgumentException excep){
					county++;
					continue;
				}
			}
			else{
				try(FileOutputStream outputStream=new FileOutputStream(fileName+(county++)+".xlsx")){
					mainWorkbook.write(outputStream);
					Desktop desktop=java.awt.Desktop.getDesktop();
					desktop.open(new File(fileName+(county-1)+".xlsx"));
					break;
				}
				catch( IOException | IllegalArgumentException excep){
					continue;
				}
			}
		}
	}
	// To Create a Cell
		private static Cell createCell(Row row,int cellNumber, String value, CellStyle style){
			Cell cell=row.createCell(cellNumber);
			cell.setCellStyle(style);
			cell.setCellValue(value);
			return cell;
		}

		// To Create a CellStyle
		private static CellStyle createCellStyle(XSSFSheet sheet, int fontSize,boolean bold){
			CellStyle style=sheet.getWorkbook().createCellStyle();
			Font font=sheet.getWorkbook().createFont();
			font.setBold(bold);
			font.setFontHeightInPoints((short)fontSize);
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
}