package com.iv.gravity.service.bugs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.iv.gravity.enums.BugCategory;

/**
 * XMLCodeReview class used for CR validation in xml
 *
 * @version Oct 24, 2018
 * @author Karthick Gunasekaran
 */
public class OptimusXmlCodeReview {

   private FileInputStream fileReader = null;

   private File file = null;

   private final Pattern attributePattern = Pattern.compile("\\b([a-z\\-:]+)\\s*=\\s*('|\")");

   private final Pattern tagNamePattern = Pattern.compile("^\\<([a-z\\-:]+)");

   private Matcher matches;

   /**
    * prohibitedAttributes list contains attributes those should not be used in xml
    */
   private final List<String> prohibitedAttributes = Arrays.asList("class", "style", "required", "maxlength", "minlength");

   private final List<String> errorList = new ArrayList<>();

   private final List<String> warningList = new ArrayList<>();

   private final List<XmlNode> xmlNodeList = new ArrayList<>();

   public OptimusXmlCodeReview(File file) throws IOException {
      try {
         this.fileReader = new FileInputStream(file);
         FileReader fileRead = new FileReader(file);
         BufferedReader bufferedReader = new BufferedReader(fileRead);
         // warningList.add("The No of Lines in this file :" +
         // bufferedReader.lines().count());
         bufferedReader.close();
         this.file = file;
         this.checkFileProperties();
         this.parseXmlNodes();
         this.findErrorsAndWarnings();
         this.fileReader.close();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   /**
    * parseXmlNodes method parse the xml file and make it into list of xmlNodes
    * 
    * @throws IOException
    */
   private void parseXmlNodes() throws IOException {
      char character;
      int lineNumber = 1;
      int startLine = 1;
      StringBuilder tagBuffer = new StringBuilder();
      boolean ignoreFlag = false;
      char ignoreCharacter = ' ';
      while (fileReader.available() > 0) {
         character = (char) fileReader.read();
         if (character == '>' && !ignoreFlag) {
            tagBuffer.append(character);
            insertXmlNode(tagBuffer.toString().trim(), lineNumber, startLine);
            tagBuffer = new StringBuilder();
            startLine = lineNumber + 1;
         }
         else if (character == '\n') {
            lineNumber++;
         }
         else if (!ignoreFlag && (character == '"' || character == '\'')) {
            ignoreFlag = true;
            ignoreCharacter = character;
            tagBuffer.append(character);
         }
         else if (ignoreFlag && character == ignoreCharacter) {
            ignoreFlag = false;
            tagBuffer.append(character);
         }
         else {
            tagBuffer.append(character);
         }
      }
   }

   /**
    * checkFileProperties method checks the file name and file location(folder structure)
    * 
    * @throws IOException
    */
   private void checkFileProperties() throws IOException {
      String fileName = this.file.getName();
      // String projectPath = "C:\\HUE\\WorkSpace\\Develop\\";
      // String filePath = this.file.getParent().replace(projectPath, "");
      String filePath = this.file.getParent();
      if (!filePath.contains(
         File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator + "templates")) {
         errorList.add("Error: file location is wrong. '" + fileName + "'" + "|" + BugCategory.FOLDER_STRUCTURE_ISSUE.toString());
      }
      if (!fileName.split("\\.")[0].matches("[a-z\\-]+")) {
         errorList.add("Error: filename should only contain lowercase[a-z] letters and hyphen(-)." + "|" + BugCategory.FILE_NAMING_ISSUE.toString());
      }
   }

   /**
    * insertXmlNode method converts xmltag string to xmlNode object
    * 
    * @param node
    * @param endLine
    * @param startLine
    * @throws IOException
    */
   private void insertXmlNode(String node, int endLine, int startLine) throws IOException {
      Map<String, String> attributesList = new HashMap<>();
      String tagName = null;
      matches = tagNamePattern.matcher(node);
      if (matches.find()) {
         tagName = matches.group(1);
      }
      matches = attributePattern.matcher(node);
      while (matches.find()) {
         String tailString = node.substring(matches.end());
         String value = tailString.substring(0, tailString.indexOf(matches.group(2)));
         attributesList.put(matches.group(1), value);
      }
      if (tagName != null && !attributesList.isEmpty()) {
         XmlNode xmlNode = new XmlNode();
         xmlNode.attributes = attributesList;
         xmlNode.xmlTag = node;
         xmlNode.xmlTagName = tagName;
         xmlNode.startLine = startLine;
         xmlNode.endLine = endLine;
         this.xmlNodeList.add(xmlNode);
      }
   }

   /**
    * findErrorsAndWarnings method finds errors and warnings in xml
    * 
    * @throws IOException
    */
   private void findErrorsAndWarnings() throws IOException {
      xmlNodeList.stream().forEach(xmlNode -> {
         prohibitedAttributes.stream().forEach(attribute -> {
            if (xmlNode.attributes.containsKey(attribute)) {
               errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " for forbidden attribute '" + attribute
                  + "'" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
         });
         if (xmlNode.attributes.containsKey("id") && !xmlNode.xmlTagName.startsWith("html")) {
            String id = xmlNode.attributes.get("id");
            String xmlTagName;
            if (id.contains("_")) {
               errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine
                  + "\n\tRequired: for id name only allowed special character is (hyphen-).\n\tFound: '" + id + "'" + "|"
                  + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
            }
            String[] xmlTagNames = xmlNode.xmlTagName.split("[-]");
            if (xmlTagNames.length == 1) {
               xmlTagName = "-" + xmlNode.xmlTagName.substring(xmlNode.xmlTagName.indexOf(":") + 1);
            }
            else {
               xmlTagName = xmlTagNames.length < 3 ? "-" + xmlTagNames[xmlTagNames.length - 1]
                  : xmlTagNames[xmlTagNames.length - 2] + "-" + xmlTagNames[xmlTagNames.length - 1];
            }
            // xmlTagName = xmlNode.xmlTagName.replaceFirst("wap-",
            // "").replaceFirst("ivtl-",
            // "").replaceFirst("[a-z]+:", "");
            if (!id.endsWith(xmlTagName)) {
               warningList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine
                  + "\n\tRequired: id should end with tag name.\n\tFound: '" + id + "'" + "|" + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
            }
         }

         // new check for wap-text-input attribute on-change-dispatch-server is present
         // or not.
         if (xmlNode.xmlTagName.contentEquals("wap-text-input")) {
            if (!xmlNode.attributes.containsKey("on-change-dispatch-server")) {
               // if(xmlNode.attributes.get("on-change-dispatch-server").equals("true")){
               if ((xmlNode.attributes.containsKey("readonly") && xmlNode.attributes.get("readonly").equals("true"))
                  || (xmlNode.attributes.containsKey("disabled") && xmlNode.attributes.get("disabled").equals("true"))) {
                  return;
               }
               else {
                  warningList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine
                     + "\n\tRequired: on-change-dispatch-server(only for input FW) attribute for wap-text-input for details ---> per "
                     + "http://192.168.41.251/redmine/issues/39287" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
               }
            }
         }
         // ends here.

         // new check for wap-single-select-item in the screen
         if (xmlNode.xmlTagName.contentEquals("wap-single-select-item")) {
            errorList.add("Error:'" + xmlNode.xmlTagName + "' At Line No: " + xmlNode.startLine
               + " should not hardcode this tag,it should come from itemid" + "|" + BugCategory.HARDCODE_ISSUE.toString());
         }
         // ends here.

         if (xmlNode.attributes.containsKey("name")) {
            String id = null;
            String actualName = null;
            if (xmlNode.attributes.containsKey("id") && !xmlNode.attributes.get("name").contains(".")) {
               id = xmlNode.attributes.get("id");
               actualName = getName(id);
            }
            if (actualName != null && !actualName.equals(xmlNode.attributes.get("name"))) {
               warningList.add("Warning: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " name should be camelcase of id." + "|"
                  + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
            }
         }
         if (xmlNode.xmlTagName.startsWith("html") && !xmlNode.xmlTagName.endsWith("div")) {
            errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " forbbiden html element found." + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }

         if (xmlNode.xmlTagName.equals("wap-single-select-item")) {
            errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " wap-single-select-item tag is restricted."
               + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }

         if (xmlNode.attributes.containsKey("label")
            && !(xmlNode.attributes.get("label").startsWith("text.") || xmlNode.attributes.get("label").startsWith("$"))) {
            errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " label is hard coded with the value '"
               + xmlNode.attributes.get("label") + "'" + "|" + BugCategory.HARDCODE_ISSUE.toString());
         }
         if (xmlNode.attributes.containsKey("example")
            && !(xmlNode.attributes.get("example").startsWith("text.") || xmlNode.attributes.get("example").startsWith("$"))) {
            errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " example is hard coded with the value '"
               + xmlNode.attributes.get("example") + "'" + "|" + BugCategory.HARDCODE_ISSUE.toString());
         }
         if (xmlNode.attributes.containsKey("hint")
            && !(xmlNode.attributes.get("hint").startsWith("text.") || xmlNode.attributes.get("hint").startsWith("$"))) {
            errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " hint is hard coded with the value '"
               + xmlNode.attributes.get("hint") + "'" + "|" + BugCategory.HARDCODE_ISSUE.toString());
         }

         xmlNode.attributes.entrySet().forEach(entry -> {
            boolean booleanAndSuggestionTitleCheck = entry.getKey().startsWith("has") || entry.getKey().startsWith("suggestion");
            if (entry.getKey().endsWith("title") && !booleanAndSuggestionTitleCheck
               && !(entry.getValue().startsWith("text.") || entry.getValue().startsWith("$"))) {
               errorList.add("Error: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine + " " + entry.getKey()
                  + " is hard coded with the value '" + entry.getValue() + "'" + "|" + BugCategory.HARDCODE_ISSUE.toString());
            }
         });

         if ((xmlNode.attributes.containsKey("label") || xmlNode.attributes.containsKey("example") || xmlNode.attributes.containsKey("hint"))
            && xmlNode.attributes.containsKey("item-id")) {
            warningList.add("Warning: At tag '" + xmlNode.xmlTagName + "' Line No: " + xmlNode.startLine
               + " label,example,hint attributes are not required if item-id present." + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
         }
      });

      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
      AtomicInteger lineNo = new AtomicInteger(1);
      reader.lines().forEach(line -> {

         if (line.contains("<!--")) {
            warningList
               .add("Warning: Line No: " + lineNo + " Remove commented line" + "|" + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
         }
         else if (line.isEmpty()) {
            warningList.add("Warning: Line No: " + lineNo + " Remove empty line" + "|" + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
         }
         lineNo.getAndIncrement();
      });
      reader.close();
   }

   /**
    * getName method returns name(camelCase) of id(snake-case)
    * 
    * @param id
    * @return
    * @throws IOException
    */
   private String getName(String id) {
      try {
         String actualName = null;
         String[] name = id.split("-");
         actualName = name[0];
         for (int i = 1; i < name.length; i++) {
            actualName += name[i].substring(0, 1).toUpperCase() + name[i].substring(1);
         }
         return actualName;
      }
      catch (StringIndexOutOfBoundsException e) {

         warningList.add("Error: At id '" + id + "'Seperate Id With Hyphens" + "|" + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());

         // Had to add to error list
         return id;
      }

   }

   /**
    * getErrorList method returns errorList
    * 
    * @return
    * @throws IOException
    */
   public List<String> getErrorList() throws IOException {
      return this.errorList;
   }

   /**
    * getWarningList method returns warningList
    * 
    * @return
    * @throws IOException
    */
   public List<String> getWarningList() throws IOException {
      return this.warningList;
   }

   public int getErrorCount() {
      return this.errorList.size();
   }

   public int getWarningCount() {
      return this.warningList.size();
   }

   public Map<String, Integer> getCountInfoMap() {
      Map<String, Integer> countInfoMap = new HashMap<String, Integer>();
      countInfoMap.put("error", getErrorCount());
      countInfoMap.put("warning", getWarningCount());
      return countInfoMap;
   }

   /**
    * XmlNode represents XmlTag
    *
    * @author Karthick Gunasekaran
    */
   private class XmlNode {

      public String xmlTag;

      public String xmlTagName;

      public int startLine;

      public int endLine;

      public Map<String, String> attributes;

   }

}
