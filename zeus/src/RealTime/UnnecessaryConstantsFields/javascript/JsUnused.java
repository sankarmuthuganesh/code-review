package RealTime.UnnecessaryConstantsFields.javascript;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;

public class JsUnused {
    static List<String> constantsList = new ArrayList<>();
    static  List<String> unusedListConstant = new ArrayList<>();
    static private List<Integer> lineNumbersOfUnusedConstants = new ArrayList<>();;
    static String currentConstant;
    static boolean usedConstantFlag;

    public Map<String, Map<MultiKey, List<String>>> findUnusedJS(
            Map<String, Map<MultiKey, List<String>>> groupedFilesTotal) {
        Map<String, Map<MultiKey, List<String>>> groupedFilesTotalFindings = new HashMap<>();
        groupedFilesTotal
                .entrySet()
                .stream()
                .forEach(
                        repo -> {
                            Map<MultiKey, List<String>> listOfFindings = new HashMap<>();
                            repo.getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branch -> {
                                                List<String> listOfFindingsBranch = new ArrayList<>();
                                                int countOfFiles = 0;
                                                while (countOfFiles < branch.getValue().size()) {
                                                    File theFileToSearch = new File(branch.getValue().get(countOfFiles));
                                                    if (theFileToSearch.getName().endsWith(".js")) {
                                                        CompilerEnvirons env = new CompilerEnvirons();
                                                        env.setRecordingLocalJsDocComments(true);
                                                        env.setRecordingComments(true);
                                                        env.setStrictMode(false);
                                                        env.setAllowMemberExprAsFunctionName(true);
                                                        env.setAllowSharpComments(true);
                                                        env.setGeneratingSource(true);
                                                        env.setIdeMode(true);
                                                        AstRoot node = null;
                                                        try {
                                                            node = new Parser(env).parse(
                                                                    new FileReader(theFileToSearch), null, 1);
                                                        } catch (Exception e) {
                                                        }
                                                        FunctionCallVisit visitor = new JsUnused().new FunctionCallVisit();
                                                        FunctionCall visitorFunction = new JsUnused().new FunctionCall();
                                                        GetLineNumber line = new JsUnused().new GetLineNumber();
                                                        node.visitAll(visitor);
                                                        for (String constant : constantsList) {
                                                            currentConstant = constant;
                                                            usedConstantFlag = false;
                                                            node.visitAll(visitorFunction);
                                                            if (!usedConstantFlag) {
                                                                unusedListConstant.add(constant);
                                                            }
                                                        }
                                                        node.visitAll(line);
                                                        constantsList = new ArrayList<>();
                                                        unusedListConstant = new ArrayList<>();
                                                        for (int lineNumber : lineNumbersOfUnusedConstants) {
                                                            listOfFindingsBranch.add(branch.getValue()
                                                                    .get(countOfFiles)
                                                                    + "#L"
                                                                    + lineNumber);
                                                        }
                                                        lineNumbersOfUnusedConstants = new ArrayList<>();
                                                    }
                                                    countOfFiles++;
                                                }
                                                listOfFindings.put(branch.getKey(), listOfFindingsBranch);
                                            });
                            groupedFilesTotalFindings.put(repo.getKey(), listOfFindings);
                        });
        return groupedFilesTotalFindings;
    }

    public class FunctionCallVisit implements NodeVisitor {
        @Override
        public boolean visit(AstNode node) {
            if (node.getType() == Token.COLON && node.getType() != Token.FUNCTION) {
                String constants = node.toSource();
                String[] splitedValue = constants.split(":");
                if (splitedValue[0].trim().matches("^[A-Z._]*")) {
                    String constantsValues = splitedValue[0].trim();
                    constantsList.add(constantsValues);
                }
            }
            return true;
        }
    }

    public class FunctionCall implements NodeVisitor {
        @Override
        public boolean visit(AstNode node) {
            if (node.getType() == Token.FUNCTION) {
                String functionContents = node.toSource();
//                Pattern p = Pattern.compile("\\b" + currentConstant + "\\b");
                Pattern p = Pattern.compile(currentConstant);
                Matcher m = p.matcher(functionContents);
                if (functionContents.contains("." + currentConstant) && m.find()) {
                    // unusedListConstant.add(constantsList.get(i));
                    usedConstantFlag = true;
                }
            }

            return true;
        }
    }

    // Get the line Number
    public class GetLineNumber implements NodeVisitor {
        @Override
        public boolean visit(AstNode node) {
            if (node.getType() == Token.COLON && node.getType() != Token.FUNCTION) {
                String constants = node.toSource();
                String[] splitedValue = constants.split(":");
                if (splitedValue[0].trim().matches("^[A-Z._]*")) {
                    String constantsValues = splitedValue[0].trim();
                    if (unusedListConstant.contains(constantsValues)) {
                        lineNumbersOfUnusedConstants.add(node.getLineno());
                    }
                }
            }
            return true;
        }
    }
}
