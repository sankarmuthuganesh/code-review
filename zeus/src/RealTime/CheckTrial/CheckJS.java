package RealTime.CheckTrial;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class CheckJS {
public static void main(String[] args) throws IOException {
	File lkj=new File("C:\\Gravity\\Clones\\Temp\\hue-scm-procurement\\river\\hue-scm-procurement-front\\src\\main\\webapp\\js\\scm\\procurement\\acceptancemanagement\\acceptancemanagement\\collectiveregistration\\collective-registration-framset-input.js");
	
	
	String stringFIle = FileUtils.readFileToString(lkj);
	  Pattern p = Pattern.compile("\\b" + "LINE_CLEAR_BUTTON" + "\\b");
      Matcher m = p.matcher(stringFIle);
      if ( m.find()) {
          // unusedListConstant.add(constantsList.get(i));
          System.out.println("Found");
      }
      System.out.println("NFound");
}
}
