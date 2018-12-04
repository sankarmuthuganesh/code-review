package RealTime.CheckTrial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;




public class HoverButtonFind {
    private  FileInputStream fileReader = null;
    private  List<XmlNode> xmlNodeList = new ArrayList<>();
    
	public  boolean getSearchAndHover(String filePath) {
		File file=new File(filePath);
        try {
            this.fileReader = new FileInputStream(file);
            FileReader fileRead = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileRead);
            bufferedReader.close();
            parseXmlNodes();
            //findErrorsAndWarnings();
            this.fileReader.close();
            
            
            
        //    Map<String, Integer> tagNamesAndLine = this.xmlNodeList.stream().collect(Collectors.toMap(XmlNode::getXmlTagName, XmlNode::getStartLine));
            
           List<String> xmlTagNameList = this.xmlNodeList.stream().map(XmlNode::getXmlTagName).collect(Collectors.toList());
           if(xmlTagNameList.contains("wap-input-group")&&(xmlTagNameList.contains("wap-inbox-hover-button")
        		   ||xmlTagNameList.contains("wap-inbox-hover-menu-button")
        		   ||xmlTagNameList.contains("wap-inbox-hover-menu")
        		   )){
        	   return true;
           }else{
        	   return false;
           }
            
          
        } catch (Exception exception) {
        	return false;
        }

    }

    /**
     * parseXmlNodes method parse the xml file and make it into list of xmlNodes
     * 
     * @throws IOException
     */
    private  void parseXmlNodes() throws IOException {
        char character;
        int lineNumber = 1;
        int startLine = 1;
        StringBuilder tagBuffer = new StringBuilder();
        boolean ignoreFlag = false;
        char ignoreCharacter = ' ';
        while (this.fileReader.available() > 0) {
            character = (char)this.fileReader.read();
            if (character == '>' && !ignoreFlag) {
                tagBuffer.append(character);
                insertXmlNode(tagBuffer.toString().trim(), lineNumber, startLine);
                tagBuffer = new StringBuilder();
                startLine = lineNumber + 1;
            } else if (character == '\n') {
                lineNumber++;
            } else if (!ignoreFlag && (character == '"' || character == '\'')) {
                ignoreFlag = true;
                ignoreCharacter = character;
                tagBuffer.append(character);
            } else if (ignoreFlag && character == ignoreCharacter) {
                ignoreFlag = false;
                tagBuffer.append(character);
            } else {
                tagBuffer.append(character);
            }
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
    private  void insertXmlNode(String node, int endLine, int startLine) throws IOException {
        Map<String, String> attributesList = new HashMap<>();
        String tagName = null;
        Pattern tagNamePattern = Pattern.compile("^\\<([a-z\\-:]+)");
   Matcher matches;
        matches = tagNamePattern.matcher(node);
        if (matches.find()) {
            tagName = matches.group(1);
        }
        Pattern attributePattern = Pattern.compile("\\b([a-z\\-:]+)\\s*=\\s*('|\")");
        matches = attributePattern.matcher(node);
        while (matches.find()) {
            String tailString = node.substring(matches.end());
            String value = tailString.substring(0, tailString.indexOf(matches.group(2)));
            attributesList.put(matches.group(1), value);
        }
        if (tagName != null && !attributesList.isEmpty()) {
            XmlNode xmlNode = new HoverButtonFind().new XmlNode();
            xmlNode.attributes = attributesList;
            xmlNode.xmlTag = node;
            xmlNode.xmlTagName = tagName;
            xmlNode.startLine = startLine;
            xmlNode.endLine = endLine;
            this.xmlNodeList.add(xmlNode);
        }

    }
@Getter
    private class XmlNode {
        public String xmlTag;
        public String xmlTagName;
        public int startLine;
        public int endLine;
        public Map<String, String> attributes;
    }

	 
}
