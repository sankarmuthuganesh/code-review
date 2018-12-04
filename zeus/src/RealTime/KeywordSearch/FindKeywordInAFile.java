package RealTime.KeywordSearch;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;



import RealTime.CheckTrial.HoverButtonFind;
import RealTime.CheckTrial.PopOverFinder;


import com.google.common.io.Files;

public class FindKeywordInAFile {
	public Map<String, Map<MultiKey, List<String>>> findTheKeyword(String keyword,
			Map<String, Map<MultiKey, List<String>>> repoBranchesAndListofFiles) {
		Map<String, Map<MultiKey, List<String>>> findingsRepoBranchesAndListofFiles = new HashMap<>();
		repoBranchesAndListofFiles.entrySet().stream().forEach(repository -> {
			Map<MultiKey, List<String>> listOfFindings = new HashMap<>();
			repository.getValue().entrySet().stream().forEach(branch -> {
				List<String> listOfFindingsBranch = new ArrayList<>();
				int countOfFiles = 0;
				while (countOfFiles < branch.getValue().size()) {
				File theFileToSearch = new File(branch.getValue().get(countOfFiles));
					//List<String> foundList=new PopOverFinder().getSimaPopOvers(branch.getValue().get(countOfFiles));
					
//					if(new HoverButtonFind().getSearchAndHover(branch.getValue().get(countOfFiles))){
//						listOfFindingsBranch.add(branch.getValue().get(countOfFiles));
//					}
					
//					for(String foundL:foundList){
//						listOfFindingsBranch.add(branch.getValue().get(countOfFiles) + "#L" + foundL);
//					}
					
					try {
						int lineCount = 1; // UTF-8
						boolean foundReadingLines=false;
						List<String> listOfLinesInFile = Files.readLines(theFileToSearch, Charset.defaultCharset());
						for (String eachLineInFile : listOfLinesInFile) {
							// Pattern Matcher is not Used. For Loose Search Comfortability.
							if (StringUtils.containsIgnoreCase(eachLineInFile, keyword)) {
								listOfFindingsBranch.add(branch.getValue().get(countOfFiles) + "#L" + lineCount);
								foundReadingLines=true;
							}
							lineCount++;
						}
						if(!foundReadingLines){
							String stringContentOfFile=FileUtils.readFileToString(theFileToSearch);
							stringContentOfFile=stringContentOfFile.replaceAll("\\r\\n|\\r|\\n", StringUtils.EMPTY);
							if(StringUtils.containsIgnoreCase(stringContentOfFile, keyword)){
								listOfFindingsBranch.add(branch.getValue().get(countOfFiles));
							}
						}
						
					} catch (Exception e) {
						// Keyword Search. Problem in Reading File @branch.getValue().get(countOfFiles)@ By Lines
					}
					countOfFiles++;
				}
				listOfFindings.put(branch.getKey(), listOfFindingsBranch);
			});
			findingsRepoBranchesAndListofFiles.put(repository.getKey(), listOfFindings);
		});
		return findingsRepoBranchesAndListofFiles;
	}
}
