package RealTime.UnnecessaryConstantsFields.javascript;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;

public class JsPerfect {
    static List<String> constantList = new ArrayList<>();

    public static void main(String args[]) {
        String author = StringUtils.EMPTY;
        File jsFile = new File(
                "C:\\HUE\\WorkSpace\\Develop\\Zeus\\src\\RealTime\\UnnecessaryConstantsFields\\javascript\\CheckJs.js");
        CompilerEnvirons env = new CompilerEnvirons();
        env.setRecordingLocalJsDocComments(true);
        env.setRecordingComments(true);
        env.setStrictMode(false);
        env.setAllowMemberExprAsFunctionName(true);
        env.setAllowSharpComments(true);
        env.setGeneratingSource(true);
        env.setIdeMode(true);
        try {
            AstRoot root = new Parser(env).parse(
                    new FileReader(jsFile), null, 1);
            FunctionCallVisit visitor = new JsPerfect().new FunctionCallVisit();
            root.visit(visitor);
            System.out.println(constantList.size());
            // root.forEach(node -> {
            // if (node.getType() == (Token.FUNCTION)) {
            // System.out.println(node.getLineno());
            // }
            // });
            //
            // String classDoc = root.getComments().first().getValue();
            // String[] commentLines = classDoc.split("\\r?\\n");
            // for (int commentLine = 0; commentLine < commentLines.length; commentLine++) {
            // if (commentLines[commentLine].contains("@author")) {
            // String authorLine = commentLines[commentLine];
            // author = authorLine.replace("*", "").replace("@author", "").trim();
            // }
            // }

            // Token.RETURN - Extracts Return Statement.

            // if (node.getType() == Token.GETPROP) {
            // if (node.toSource().endsWith("_")) {
            // System.out.println(node.toSource().substring(node.toSource().lastIndexOf(".") + 1));
            // System.out.println("-----------------------");
            // }
            //
            // }

            // Object property access
            // if (node.getType() == Token.GETELEM) {
            // System.out.println(node.toSource());
            // System.out.println("-----------------------");
            //
            // }

            // FunctionCalls
            // Token.CALL

            // Return
            // Token.RETURN
            // Token.NAME
            // Token.NUMBER
            // Token.STRING
            // Token.SHEQ- === assignments
            // Token.ARRAYLIT
            // Token.OBJECTLIT
        } catch (Exception e) {
        }

        // System.out.println(author);
    }

    public class FunctionCallVisit implements NodeVisitor {
        @Override
        public boolean visit(AstNode node) {
        	
             if (node.getType() == Token.CALL) {
             if (node.toSource().endsWith("_")) {
             System.out.println(node.toSource().substring(node.toSource().lastIndexOf(".") + 1));
             //System.out.println("-----------------------");
             }
            
             }
        	
        	
//            if (node.getType() == Token.COLON && node.getType() != Token.FUNCTION) {
//                System.out.println(node.toSource());
//                System.out.println("-----------------------");
//            }
            // if (node.getType() == Token.COLON && node.getType() != Token.FUNCTION) {
            // String constants = node.toSource();
            // String[] splitedValue = constants.split(":");
            // if (splitedValue[0].trim().matches("^[A-Z._]*")) {
            // String constantsValues = splitedValue[0].trim();
            // constantList.add(constantsValues);
            // }
            // }
            return true;
        }
    }
}