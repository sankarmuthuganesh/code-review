package RealTime.Entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import RealTime.Author.BlameDetails;
import RealTime.Author.BlamesUsingCommits;
import RealTime.Author.JavaAuthor;
import RealTime.Author.JsAuthor;
import RealTime.GitAccess.Progress;
import RealTime.GitAccess.ProgressLife;
import RealTime.GroupingBy.FolderIteratorFinder;
import RealTime.Optimus.OptimusBugsOfAFile;
import RealTime.Optimus.OptimusLogicToBugSeperation;
import RealTime.SubSystemEpicSplit.FileCategoryFinder;

import com.github.javaparser.ParseException;

@Getter
@Setter
public class FileUnit {

	public FileUnit(String absolutePath,String repository,String branch,Progress life,Map<String, Map<String, Map<String, Set<String>>>> classification) {
		this.isCobaltFile=false;
		this.life=life;
		this.setAbsolutePath(absolutePath, repository, branch);
		
		groupCategories(absolutePath,classification);
		decideTypeOfFile(absolutePath);
		getAuthor(absolutePath);
		this.getBlameDetails();
	}

	//--------------------REPOSITORY, BRANCH, LICENSE GROUP, LICENSE, SUBSYSTEM, EPIC--------------------------------//
	//The repository the File belongs
	public String repository;
	//The branch the File belongs
	public String branch;
	//License Group - SCM,AC
	private String licenseGroup;
	//License - Procurement,Project
	private String license;
	//Subsystem
	private String subSystem;
	//EpicName
	private String epicName;

	//--------------------FILES DETAILS--------------------------------//
	//Git URL Path
	private String httpPath;
	//Local Absolute Path where File exists - Usually the %temp% folder
	public String absolutePath;
	//File Name
	private String fileName;
	//Cobalt File or Not
	private boolean isCobaltFile;
	//Author of the File - @author mentioned
	private String authorOfFile;
	//Type of File(Java, Js, XML, LESS,CSS)
	private String natureOfFile;

	//--------------------BUG DETAILS--------------------------------//
	//Catgory,Bug,LineNumber
	private List<BugDetails> bugDetailsList;
	//Total Errors
	private String totalErrorsInThisFile;
	//TotalWarnings
	private String totalWarningInThisFile;

	// -------FROM COMMIT LOG---------//
	private List<BlameDetails> blamesOfTheFile;

	//Git Instance
	protected Progress life;
	
	//Search Results
	private List<SearchResult> searches;
	/*
	 * Author of a file is Manipulated if it Java or JavaScript File
	 */
	private void getAuthor(String absolutePath) {
		if(natureOfFile.equals("Java")){
			JavaAuthor java=new JavaAuthor();
			this.authorOfFile=java.getJavaAuthor(absolutePath);
			this.isCobaltFile=java.isCobaltFile;
		}
		else if(natureOfFile.equals("JavaScript")){
			JsAuthor js=new JsAuthor();
			this.authorOfFile=js.getJsAuthor(absolutePath);
		}
		else{
			//No Author Manipulation can be done!.
			//Because they dont contain author.
			this.authorOfFile="NoAuthor";
		}
	}


	/*
	 * setAbsolutePath sets the AbsolutePath. It calculates
	 * fileName
	 * licenseGroup 
	 * TypeOfFile
	 * 
	 */
	public void setAbsolutePath(String absolutePath,String repository,String branch){
		this.repository=repository;
		this.branch=branch;
		this.absolutePath=absolutePath;
		String fileName=absolutePath.substring(absolutePath.lastIndexOf(File.separator)+1);
		this.fileName=fileName;
		// Because this approach is not portable. So another approach is followed.
		/*
		 * Assumption: StringUtils.ordinalIndexOf(absolutePath, "/", 7) - The Branch is Cloned in %temp% folder.
		 * C:\Users\sankraja\AppData\Local\Temp\river-2042580735792076063\hue-scm-project-biz\src\main\java\com\worksap\company\hue\scm\biz\project\approvalflow\listener\cost-profit-list-header.js
		 * It is replaced until 7th \
		 * 
		 */
		//this.httpPath=(absolutePath.replace(absolutePath.substring(0, StringUtils.ordinalIndexOf(absolutePath, "\\", 7)), "http://192.168.41.136/root/"+repository+"/blob/"+branch)).replace("\\", "/");
		//May be the below alternative approach (gitHomeFolderPath) is to the point.
		//this.httpPath=(absolutePath.replace(absolutePath.substring(0, absolutePath.indexOf(branch)+branch.length()), this.life.getGitConnection()+"root/"+repository+"/blob/"+branch)).replace(File.separator, "/");
		String gitHomeFolderPath=new FolderIteratorFinder().getGitHome(absolutePath);
		this.httpPath=(absolutePath.replace(gitHomeFolderPath, this.life.getGitConnection()+"root/"+repository+"/blob/"+branch+"/")).replace(File.separator, "/");
	}

	private void groupCategories(String pathOfFile, Map<String, Map<String, Map<String, Set<String>>>> classification) {

		//Alternative Approach using Controller is followed.
		//		FileCategoryFinder category=new FileCategoryFinder();
		//		category.getFileLGLSEC(pathOfFile);
		//		this.license=category.license;
		//		this.licenseGroup=category.licenseGroup;
		//		this.subSystem=category.subSystem;
		//		this.epicName=category.epicName;

		Map<String, Set<String>> groupings = classification.get(this.repository).get(this.branch);
		Set<String> licenses = groupings.get("licenses");
		Set<String> licenseGroups = groupings.get("licenseGroups");
		Set<String> subsystems = groupings.get("subsystems");
		Set<String> epics = groupings.get("epics");

		Path path = Paths.get(pathOfFile);
		Iterator<Path> iteratorOfPath = path.iterator();
		List<String> foldersOfAFile = new ArrayList<>();
		while (iteratorOfPath.hasNext()) {
			foldersOfAFile.add(iteratorOfPath.next().toString());
		}
		try{
			this.license=CollectionUtils.intersection(foldersOfAFile, licenses).stream().findFirst().get();
		}catch(NoSuchElementException e){
			this.license="UncategorisedLicense";
		}
		try{
			this.licenseGroup=CollectionUtils.intersection(foldersOfAFile, licenseGroups).stream().findFirst().get();
		}catch(NoSuchElementException e){
			this.licenseGroup="UncategorisedLicenseGroup";
		}
		try{
			this.subSystem=CollectionUtils.intersection(foldersOfAFile, subsystems).stream().findFirst().get();
		}catch(NoSuchElementException e){
			this.subSystem="UncategorisedSubsystem";
		}
		try{
			this.epicName=CollectionUtils.intersection(foldersOfAFile, epics).stream().findFirst().get();
		}catch(NoSuchElementException e){
			this.epicName="UncategorisedEpic";
		}
	}

	private void decideTypeOfFile(String pathOfFile){
		if(pathOfFile.endsWith(".java")){
			this.natureOfFile="Java";
		}else if(pathOfFile.endsWith(".js")){
			this.natureOfFile="JavaScript";
		}else if(pathOfFile.endsWith(".xml")){
			this.natureOfFile="XML";
		}else if(pathOfFile.endsWith(".less")){
			this.natureOfFile="Less";
		}else if(pathOfFile.endsWith(".css")){
			this.natureOfFile="CSS";
		}
	}

	public void setOptimusBugs(OptimusBugsOfAFile optimusBugs){
		this.makeBugDetailsFromOptimus(optimusBugs);
	}

	public void makeBugDetailsFromOptimus(OptimusBugsOfAFile optimusBugs){
		this.bugDetailsList=new OptimusLogicToBugSeperation().makeLineNumberAndCorrespodingBugDetails(optimusBugs,this.absolutePath,this.httpPath,this.fileName);
		this.totalErrorsInThisFile=String.valueOf(optimusBugs.getErrors().size());
		this.totalWarningInThisFile=String.valueOf(optimusBugs.getWarnings().size());	
	}

	private void getBlameDetails() {
		//Commented Because a better approach to get details of the file is got - blame
		//this.authorsOfCommit=new DetailsUsingCommits().getCommitDetails(this.absolutePath);
		this.blamesOfTheFile=new BlamesUsingCommits().getBlameDetails(this.absolutePath);
	}
}
