package RealTime.GroupingBy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.BugDetails;
import RealTime.Entity.FileUnit;
import RealTime.GitAccess.Progress;
import RealTime.Optimus.OptimusBugsOfAFile;
import RealTime.Optimus.SourceCode.CodeReviewMain;

import com.github.javaparser.ParseException;


public class GroupingBy {


	static List<String> allFilesInTheDirectoryPath=new ArrayList<>();




	public Map<String, Map<MultiKey, List<FileUnit>>> desiredGroup(Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles, Progress gitCall, Map<String, Map<String, Map<String, Set<String>>>> classification){

		Map<String, Map<MultiKey, List<FileUnit>>> finalOut=new HashMap<>();

		finalBrowseThroughFiles.entrySet().stream().forEach(repository ->{
			Map<MultiKey, List<FileUnit>> branchesAndErrors=new HashMap<>();
			repository.getValue().entrySet().stream().forEach(branch ->{
				List<String> filesPathInTheParticularBranch=branch.getValue();
				List<String> filesPathUnnecessaryRemoved=new ArrayList<>();
				filesPathInTheParticularBranch.forEach(pathOfFile ->{
					if(pathOfFile.endsWith(".xml")||pathOfFile.endsWith(".js")||pathOfFile.endsWith(".java")){
						filesPathUnnecessaryRemoved.add(pathOfFile);
					}
				});
				
					boolean isErrorOccured = false;
					try {
						isErrorOccured = CodeReviewMain.checkCodeReview(new HashSet<String>(filesPathUnnecessaryRemoved),new HashSet<String>());
					} catch (Exception e) {
					
					}
					if(!isErrorOccured){
						Map<String, List<String>> errorsMap = CodeReviewMain.errorMap;
						int totalCRErrorCount = CodeReviewMain.totalCRErrorCount;
						int totalCRWarningCount = CodeReviewMain.totalCRWarningCount;
						Map<String, List<String>> warningsMap = CodeReviewMain.warningMap;

						List<FileUnit> totalBuggyFiles=new ArrayList<>();
						Map<String,OptimusBugsOfAFile> fileAndOptimusBugs= new HashMap<>();
						errorsMap.entrySet().stream().forEach(errorsOfAFile ->{
							List<String> errors	=	errorsOfAFile.getValue();
							OptimusBugsOfAFile optimusBugs=new OptimusBugsOfAFile();
							optimusBugs.setErrors(errors);
							fileAndOptimusBugs.put(errorsOfAFile.getKey(), optimusBugs);
						});

						warningsMap.entrySet().stream().forEach(warningsOfAFile ->{
							List<String> warnings	=	warningsOfAFile.getValue();
							OptimusBugsOfAFile optimusBugs;
							if(fileAndOptimusBugs.containsKey(warningsOfAFile.getKey())){
								optimusBugs=fileAndOptimusBugs.get(warningsOfAFile.getKey());
								optimusBugs.setWarnings(warnings);
							}
							else{
								optimusBugs=new OptimusBugsOfAFile();
								optimusBugs.setWarnings(warnings);
							}
							fileAndOptimusBugs.put(warningsOfAFile.getKey(), optimusBugs);
						});


						fileAndOptimusBugs.entrySet().stream().forEach(buggyFile ->{
							FileUnit fileUnit=new FileUnit(buggyFile.getKey(),repository.getKey(),branch.getKey().getKey(0).toString(),gitCall,classification);
							fileUnit.setOptimusBugs(buggyFile.getValue());
							totalBuggyFiles.add(fileUnit);
						});

						System.out.println(totalBuggyFiles);
						branchesAndErrors.put(branch.getKey(), totalBuggyFiles);
					}
					else{
						System.out.println("Some Exception Occured while Reading File");
					}
			
			});
			finalOut.put(repository.getKey(), branchesAndErrors);
		});
		return finalOut;
	}

	public static void main(String args[]) throws IOException, ParseException{
		List<String> bugs=Arrays.asList("Line No: 25 : Declaration :  Please remove @Autowired, use @RequiredArgsConstructor(onConstructor = @__(@Autowired))|AvoidsAndAlternatives",
				"Line No: 23 :  Class name should ends with ServiceImpl and implemented interface name should ends with Service|PackageClassName",
				"Line No: 42 :  Line comment is not allowed|DocsComments",
				"Error: At tag 'l:row' Line No: 171 Required: id should end with tag name.Found: 'item-file-attachment-list-row'|Naming",
				"Line No: 23 :  Comments should only start with Class Name 'AccessibleChecker'|Sankar",
				"Line No: 28 : Comments is missing for the function 'isAccessible'|DocsComments",
				"Line No: 55 : Comments is missing for the function 'kickConditionApi'|DocsComments",
				"Should not use any Dto other than Dao, DaoImpl and Entity files uses for Dto to Entity conversion.|ShouldNotUse",
				"Line No: 75 : Comments is missing for the function 'getConditionValue'|DocsComments",
				"Line No: 25 :  Comment is missing for the variable 'apiKicker'|DocsComments",
				"Warning: Line No: 13 Remove empty line|Violations",
				"Line No: 69 :  Variable 'accessibleValues' is not ending with List|VariableName");
		List<String> bugss=Arrays.asList("Line No: 25 : Declaration :  Please remove @Autowired, use @RequiredArgsConstructor(onConstructor = @__(@Autowired))|AvoidsAndAlternatives",
				"Line No: 23 :  Class name should ends with ServiceImpl and implemented interface name should ends with Service|Sankar",
				"Line No: 42 :  Line comment is not allowed|DocsComments",
				"Error: At tag 'l:row' Line No: 171 Required: id should end with tag name.Found: 'item-file-attachment-list-row'|Muthu",
				"Line No: 23 :  Comments should only start with Class Name 'AccessibleChecker'|Ganesh",
				"Line No: 28 : Comments is missing for the function 'isAccessible'|DocsComments",
				"Line No: 55 : Comments is missing for the function 'kickConditionApi'|DocsComments",
				"Should not use any Dto other than Dao, DaoImpl and Entity files uses for Dto to Entity conversion.|Ganesh",
				"Line No: 75 : Comments is missing for the function 'getConditionValue'|DocsComments",
				"Line No: 25 :  Comment is missing for the variable 'apiKicker'|DocsComments",
				"Warning: Line No: 13 Remove empty line|Violations",
				"Line No: 69 :  Variable 'accessibleValues' is not ending with List|VariableName");
		//File dir=new File("C:\\Users\\sankraja\\AppData\\Local\\Temp\\river-8247406275189310823");
		listAllFiles("C:\\Users\\sankraja\\AppData\\Local\\Temp\\river-4193827434257236501");
		//File kk[]=dir.listFiles();
		List<FileUnit> kl=new ArrayList<>();
		//kl.add(new FileUnit(allFilesInTheDirectoryPath.get(0),"hue-scm-project","river",bugs));
		//kl.add(new FileUnit(allFilesInTheDirectoryPath.get(1),"hue-scm-project","river",bugss));
		for(String file:allFilesInTheDirectoryPath){
			//kl.add(new FileUnit(file,"hue-scm-project","river",bugs));
			//	kl.add(new FileUnit(file,"hue-scm-project","river",bugss));
		}
		//		Map<Object, Map<String, List<BugDetails>>> out = kl.stream().flatMap(fileUnit ->fileUnit.getBugDetailsList().stream()
		//				.map(bugDetails -> new AbstractMap.SimpleImmutableEntry<>(fileUnit, bugDetails)))
		//				.collect(Collectors.groupingBy(e -> e.getKey().getAuthorOfFile(),
		//						Collectors.mapping(Map.Entry::getValue, Collectors.groupingBy(BugDetails::getBugCategory))));

		Map<String, Map<String, List<BugDetails>>> out = kl.stream().collect(Collectors.groupingBy(FileUnit::getAuthorOfFile,
				GroupingBy.flatMapping(fileUnit -> fileUnit.getBugDetailsList().stream(), 
						Collectors.groupingBy(BugDetails::getBugCategory))));



		System.out.println(out);



	}


	//Java 9 - Collector.flatMapping
	static <T,U,A,R> Collector<T,?,R> flatMapping(Function <? super T, ? extends Stream < ? extends U>> mapper, 
			Collector<? super U,A,R> downstream){
		BiConsumer<A,? super U> acc=downstream.accumulator();
		return Collector.of(downstream.supplier(), (a,t) -> {
			try(Stream<? extends U> s=mapper.apply(t)){
				if(s!=null)s.forEachOrdered(u -> acc.accept(a,u));
			}
		}, downstream.combiner(), downstream.finisher(),downstream.characteristics().stream().toArray(Collector.Characteristics[]::new));
	}

	public  static void listAllFiles(String directory) throws ParseException, IOException{
		File[] files=new File(directory).listFiles();
		for(File file: files){
			if(file.isFile()){
				if(file.getAbsolutePath().endsWith(".java")||file.getAbsolutePath().endsWith(".js")||file.getAbsolutePath().endsWith(".xml")||file.getAbsolutePath().endsWith(".less")){
					allFilesInTheDirectoryPath.add(file.getAbsolutePath());
				}
			}
			else if(file.isDirectory()){
				listAllFiles(file.getAbsolutePath());
			}	
		}

	}
}
