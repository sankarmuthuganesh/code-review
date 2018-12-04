package RealTime.UnusedConstants;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
public class JSUnusedConstants {
	static List<String> constantsList=new ArrayList<>();
	//    static Set<String> unusedInitializeList=new HashSet<>();
	static List<String> unusedListConstant=new ArrayList<>();
	//    static List<String> variableList=new ArrayList<>();
	static private List<Integer> lineNumbersOfUnusedConstants;
	static String pathOfFile="C:\\HUE\\WorkSpace\\Develop\\internal-tool-sync\\Zeus\\src\\RealTime\\UnusedConstants\\kkk.js";
	static boolean usedConstantFlag;
static String currentConstant;
	public static void main(String args[]) throws FileNotFoundException, IOException{
		JSUnusedConstants NonAuthor=new JSUnusedConstants();
		CompilerEnvirons env=new CompilerEnvirons();
		env.setRecordingLocalJsDocComments(true);
		env.setRecordingComments(true);
		env.setStrictMode(false);
		env.setAllowMemberExprAsFunctionName(true);
		env.setAllowSharpComments(true);
		env.setGeneratingSource(true);
		env.setIdeMode(true);
		AstRoot node =new Parser(env).parse(new FileReader(pathOfFile),null, 1);
		FunctionCallVisit visitor =NonAuthor.new FunctionCallVisit();
		FunctionCall visitorFunction =NonAuthor.new FunctionCall();
		//InitializeVisit initializeVisit=NonAuthor.new InitializeVisit();

		node.visitAll(visitor);
		for(String constant:constantsList){
			currentConstant=constant;
			usedConstantFlag=false;
			node.visitAll(visitorFunction);
			if(!usedConstantFlag){
				unusedListConstant.add(constant);
			}
		}
		
		//node.visitAll(initializeVisit);
		System.out.println(constantsList);
		
		System.out.println(unusedListConstant);
		//                System.out.println(variableList);
		//                System.out.println(unusedInitializeList);



	}
	public class FunctionCallVisit implements NodeVisitor{
		@Override
		public boolean visit(AstNode node) {
			if(node.getType() == Token.COLON&&node.getType()!=Token.FUNCTION){
				String constants = node.toSource();        
				String[] splitedValue = constants.split(":");
				if(splitedValue[0].trim().matches("^[A-Z._]*")){
					String constantsValues = splitedValue[0].trim();
					constantsList.add(constantsValues);
				}                
			}
			return true;
		}
	}

	//    public class InitializeVisit implements NodeVisitor{
	//        @Override
	//        public boolean visit(AstNode node) {
	//            if(node.getType() == Token.ASSIGN){
	//                Assignment assignment = (Assignment)node;
	//                String value=assignment.getLeft().toSource();
	//                if(!value.contains("wap")&&!value.contains("prototype")&&value.startsWith("this.")){
	//                    variableList.add(value);
	//                }
	//            }
	//            return true;
	//        }
	//    }

	public class FunctionCall implements NodeVisitor{
		@Override
		public boolean visit(AstNode node) {
			if(node.getType() == Token.FUNCTION){
				String functionContents = node.toSource();    
					Pattern p=Pattern.compile("\\b"+currentConstant+"\\b");
					Matcher m=p.matcher(functionContents);
					if(functionContents.contains("."+currentConstant)&&m.find()){
						//unusedListConstant.add(constantsList.get(i));
						usedConstantFlag=true;
					}
				}
			
			//                        String initializeContents = node.toSource();    
			//                for(int i=0;i<variableList.size();i++){
			//                    Pattern p=Pattern.compile("\\b"+variableList.get(i)+"\\b");
			//                    Matcher m=p.matcher(initializeContents);
			//                    if(!initializeContents.contains(variableList.get(i))&&m.find()){
			//                        unusedInitializeList.add(variableList.get(i));
			//                    }
			//                    
			//                }
			return true;
		}
	}
	
	//Get the line Number
	public class GetLineNumber implements NodeVisitor{
	

		@Override
		public boolean visit(AstNode node) {
			if(node.getType() == Token.COLON&&node.getType()!=Token.FUNCTION){
				String constants = node.toSource();        
				String[] splitedValue = constants.split(":");
				if(splitedValue[0].trim().matches("^[A-Z._]*")){
					String constantsValues = splitedValue[0].trim();
					if(unusedListConstant.contains(constantsValues)){
						lineNumbersOfUnusedConstants.add(node.getLineno());
					}
					
				}                
			}
			return true;
		}
	}
	
}

