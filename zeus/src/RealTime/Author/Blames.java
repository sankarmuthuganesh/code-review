package RealTime.Author;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import RealTime.Author.BlameDetails;
import RealTime.Author.BlamesUsingCommits;

public class Blames {
public Map<String, List<String>> getExactAuthor(String lineNumber, String absolutePath){
	String authorName;
	List<BlameDetails> totalDetails = new BlamesUsingCommits().getBlameDetails(absolutePath);
//	String buggyLines=	new BuggyLinesGetter().getBuggyLines(lineNumber, absolutePath);
	
	if(!lineNumber.trim().isEmpty()){
		authorName=totalDetails.stream().filter(authors -> authors.getCommitedLineNumbers().contains(lineNumber)).findFirst().get().getAuthorName();
	}
	else{
		authorName="";
	}

	
	Map<String,List<String>> responsibleAndTotalAuthors=new HashMap<>();
	
	responsibleAndTotalAuthors.put(authorName,totalDetails.stream().map(authors ->  authors.getAuthorName()).collect(Collectors.toList()));
	return responsibleAndTotalAuthors;
}
}
