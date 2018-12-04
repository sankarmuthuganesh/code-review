package RealTime.Optimus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import RealTime.Entity.BugDetails;

import com.google.common.base.CharMatcher;

public class OptimusLogicToBugSeperation {
public List<BugDetails> makeLineNumberAndCorrespodingBugDetails(OptimusBugsOfAFile optimusBugs, String absolutePath, String httpPath, String fileName){
		
		List<BugDetails> bugDetailsList=new ArrayList<>();
		
		//Error Classifier
	   //-----------------
		optimusBugs.getErrors().stream().sequential().forEach(error -> {
			String lineNumber;
			String bug = error;
			String category = error.substring(error.lastIndexOf("|")+1);
			//Assumes the Bug string Line Number is given typically as Line No : 1234
			if(error.contains("Line No")){
				int indexWhereLineNumberStarts = error.indexOf("Line No");	
				lineNumber= CharMatcher.DIGIT.retainFrom(error.substring(indexWhereLineNumberStarts, indexWhereLineNumberStarts+14));
			}else{
				lineNumber=StringUtils.EMPTY;
			}
			
			String buggyLines="";
					//new BuggyLinesGetter().getBuggyLines(lineNumber, absolutePath);
			
			//Setting it to BugDetails
			BugDetails bugDet=new BugDetails();
			bugDet.setHttpPathOfFile(httpPath);
			bugDet.setSeverityOfBug("Error");
			bugDet.setBug(bug);
			bugDet.setBugCategory(category);
			bugDet.setLineNumber(lineNumber);
			bugDet.setBuggyLines(buggyLines);
			bugDet.setFileName(fileName);
			bugDetailsList.add(bugDet);
		});
		
		//Warnings Classifier
	   //--------------------
		optimusBugs.getWarnings().stream().sequential().forEach(error -> {
			String lineNumber;
			String bug = error;
			String category = error.substring(error.lastIndexOf("|")+1);
			//Assumes the Bug string Line Number is given typically as Line No : 1234
			if(error.contains("Line No")){
				int indexWhereLineNumberStarts = error.indexOf("Line No");	
				lineNumber= CharMatcher.DIGIT.retainFrom(error.substring(indexWhereLineNumberStarts, indexWhereLineNumberStarts+14));
			}else{
				lineNumber=StringUtils.EMPTY;
			}
			
			String buggyLines="";
					//new BuggyLinesGetter().getBuggyLines(lineNumber, absolutePath);
			
			//Setting it to BugDetails
			BugDetails bugDet=new BugDetails();
			bugDet.setSeverityOfBug("Warning");
			bugDet.setHttpPathOfFile(httpPath);
			bugDet.setBug(bug);
			bugDet.setBuggyLines(buggyLines);
			bugDet.setBugCategory(category);
			bugDet.setLineNumber(lineNumber);
			bugDet.setFileName(fileName);
			bugDetailsList.add(bugDet);
		});

		return bugDetailsList;

	}
}
