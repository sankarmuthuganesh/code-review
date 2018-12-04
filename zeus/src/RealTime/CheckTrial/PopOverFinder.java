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




public class PopOverFinder {
    private  FileInputStream fileReader = null;
    private  List<XmlNode> xmlNodeList = new ArrayList<>();
    
	public  List<String> getSimaPopOvers(String filePath) {
		List<String> listOfLines=new ArrayList<>();
		File file=new File(filePath);
        try {
            this.fileReader = new FileInputStream(file);
            FileReader fileRead = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileRead);
            bufferedReader.close();
            parseXmlNodes();
            //findErrorsAndWarnings();
            this.fileReader.close();
            
           // System.out.println(xmlNodeList);
            this.xmlNodeList.stream().filter(xmlTag -> xmlTag.xmlTagName.equals("wap-popover"))
            .forEach(tag ->{
            	Optional<Entry<String, String>> targetAttri = tag.attributes.entrySet().stream().filter(attribute -> attribute.getKey().equals("target-id")).findFirst();
            	if(targetAttri.isPresent()){
            		String popOverTarget = targetAttri.get().getValue();
            		
            		this.xmlNodeList.stream().filter(xmlTagAn -> xmlTagAn.xmlTagName.startsWith("sima-grid-")).forEach(simaGrid ->{
            			if(popOverTarget.equals(simaGrid.attributes.get("id"))){
            				listOfLines.add(String.valueOf(tag.startLine));
            			}
            			if(simaGrid.xmlTagName.equals("sima-grid-popover")){
            				listOfLines.add(String.valueOf(tag.startLine));
            			}
            		});
            		
            		
            		//System.out.println(popOverTarget);
            	}
            });;
        } catch (Exception exception) {
        }
        return listOfLines;
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
            XmlNode xmlNode = new PopOverFinder().new XmlNode();
            xmlNode.attributes = attributesList;
            xmlNode.xmlTag = node;
            xmlNode.xmlTagName = tagName;
            xmlNode.startLine = startLine;
            xmlNode.endLine = endLine;
            this.xmlNodeList.add(xmlNode);
        }

    }

    private class XmlNode {
        public String xmlTag;
        public String xmlTagName;
        public int startLine;
        public int endLine;
        public Map<String, String> attributes;
    }

	 
}
