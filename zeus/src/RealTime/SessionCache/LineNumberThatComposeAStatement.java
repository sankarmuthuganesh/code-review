package RealTime.SessionCache;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchStmt;


public class LineNumberThatComposeAStatement {
	
	public  Map<Integer, List<String>> statementsByLine(File projectDir, String sessionMethodCallPassed){
		AtomicInteger flag=new AtomicInteger(0);
		AtomicInteger bulkFlag=new AtomicInteger(0);
		List<String> listOfLines=new ArrayList<>();
		final String  sessionMethodCall=sessionMethodCallPassed;
		new DirExplorer((level, path, file) -> {return path.endsWith(".java");},(level,path,file) ->{
			try{
				new NodeIterator(new NodeIterator.NodeHandler() {
					@Override
					public boolean handle(Node node) throws ParseException, IOException  {
						if(!( node instanceof BlockStmt)&&(node instanceof Statement)&&(!( node instanceof SwitchStmt))){
							if(node.removeComment().toString().contains(sessionMethodCall)){
								//Caution only shows beginning of line for a statement
								listOfLines.add(node.getBegin().get().line+"-"+node.getEnd().get().line);
								flag.set(1);
								String stringNode=node.removeComment().toString().toLowerCase();
								
								
								//JavaParser
								//CompilationUnit reff=new CompilationUnit();
								//passNode(reff);
								//
								//System.out.println(reff.getArgs());
							}
							return false;
						}
						else{
							return true;
						}
					}
				}).explore(JavaParser.parse(file));
			}catch(ParseException |IOException e){
				new RuntimeException(e);
			}
		}).explore(projectDir);
		Map<Integer,List<String>> flagMap=new HashMap<>();
		flagMap.put(flag.get(), listOfLines);
		return flagMap;
	}
	
	
}
