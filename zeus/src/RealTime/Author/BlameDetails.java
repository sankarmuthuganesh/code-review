package RealTime.Author;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class BlameDetails {
	// The Commit Reference
	private String commitID;
	// The Author of Commit
	private String authorName;
	// When it is Committed
	private Date commitTime;
	// The Mail of Author
	private String mailID;
	// The Commited Lines
	private List<String> commitedLines;
	// The Commited Line Numbers
	private List<String> commitedLineNumbers;
	
	
}
