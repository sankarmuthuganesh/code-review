package RealTime.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BugDetails{
	//Git URL Path
	private String httpPathOfFile;
	//File Name
	private String fileName;
	//Severity Of Bug
	public String severityOfBug;
	//Category Of Bug
	public String bugCategory;
	//Line Number Where Bug Happened
	public String lineNumber;
	// The Bug Explanation
	public String bug;
	// The Buggy Lines in the File
	public String buggyLines;
}