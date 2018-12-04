package RealTime.KeywordSearch;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import RealTime.Entity.FileUnit;
import RealTime.Entity.SearchResult;

import com.google.common.io.Files;

public class FindKeywordInAFileFU {
	public List<FileUnit> findTheKeyword(List<FileUnit> files,String keyword) {
		List<FileUnit> searchResults = files.stream().filter(file ->{
			boolean foundFlag=false;
			int lineCount = 1; // UTF-8
			List<String> listOfLinesInFile;
			try {
				listOfLinesInFile = Files.readLines(new File(file.getAbsolutePath()), Charset.defaultCharset());
				for (String eachLineInFile : listOfLinesInFile) {
					// Pattern Matcher is not Used. For Loose Search Comfortability.
					// Case Insensitive Search.
					if (eachLineInFile.toLowerCase().contains(keyword.toLowerCase())) {
						SearchResult searches=new SearchResult();
						searches.setLineNumber(String.valueOf(lineCount));
						file.setSearches(Arrays.asList(searches));
						foundFlag=true;
					}
					lineCount++;
				}
			} catch (Exception e) {
			}
			return foundFlag;
		}).collect(Collectors.toList());
		return searchResults;
	}
}
