package RealTime.Bugs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

public class BuggyLinesGetter {
	public String getBuggyLines(String lineRange, String absolutePath) {
		StringBuilder buggyLines = new StringBuilder();
		if(lineRange.contains("-")){
			//Getting the Buggy Lines
			String[] range=lineRange.split("-");
			int beginLine = Integer.parseInt(CharMatcher.DIGIT.retainFrom(range[0]));
			int endLine = Integer.parseInt(CharMatcher.DIGIT.retainFrom(range[1]));
			while(beginLine<=endLine){
				//For Large Files
				try(Stream<String> lines=Files.lines(Paths.get(absolutePath))){
					buggyLines.append(lines.skip(beginLine-1).findFirst().get());
				}
				catch(IOException e){

				}

				/*				//For Small Files
			try {
				buggyLines.append(FileUtils.readLines(new File(absolutePath)).get(beginLine));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

				beginLine++;
			}	
		}
		else if(StringUtils.isNotEmpty(lineRange)){
			try {
				System.out.println("For getting Buggy Lines "+absolutePath);
				buggyLines.append(FileUtils.readLines(new File(absolutePath)).get(Integer.parseInt(lineRange)));
			} catch (NumberFormatException | IOException e) {
			
			}
		}		
		return buggyLines.toString();
	}
}
