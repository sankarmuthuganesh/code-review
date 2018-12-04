package com.iv.gravity.service.bugs;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;
import com.iv.gravity.service.bugfixer.Fix;

public class UnnecessaryJavaScript {

   List<String> constantsList = new ArrayList<>();

   List<String> unusedListConstant = new ArrayList<>();

   private final List<Integer> lineNumbersOfUnusedConstants = new ArrayList<>();

   String currentConstant;

   boolean usedConstantFlag;

   public void findUnusedJS(List<FileUnit> storyFiles, Map<String, List<Fix>> fileAndFixes) {

      storyFiles.stream().forEach(file -> {
         List<Fix> fixes = new ArrayList<>();
         File theFileToSearch = new File(file.getAbsolutePath());
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
               node = new Parser(env).parse(new FileReader(theFileToSearch), null, 1);
            }
            catch (Exception e) {
            }
            node.visitAll(new NodeVisitor() {
               @Override
               public boolean visit(AstNode functionNode) {
                  if (functionNode.getType() == Token.COLON && functionNode.getType() != Token.FUNCTION) {
                     String constants = functionNode.toSource();
                     String[] splitedValue = constants.split(":");
                     if (splitedValue[0].trim().matches("^[A-Z._]*")) {
                        String constantsValues = splitedValue[0].trim();
                        constantsList.add(constantsValues);
                     }
                  }
                  return true;
               }
            });
            for (String constant : constantsList) {
               currentConstant = constant;
               usedConstantFlag = false;
               node.visitAll(new NodeVisitor() {
                  @Override
                  public boolean visit(AstNode unusedConstantNode) {
                     if (unusedConstantNode.getType() == Token.FUNCTION) {
                        String functionContents = unusedConstantNode.toSource();
                        Pattern p = Pattern.compile(currentConstant);
                        // Pattern p = Pattern.compile("\\b" + currentConstant +
                        // "\\b");
                        Matcher m = p.matcher(functionContents);
                        if (functionContents.contains("." + currentConstant) && m.find()) {
                           // unusedListConstant.add(constantsList.get(i));
                           usedConstantFlag = true;
                        }
                     }

                     return true;
                  }
               });
               if (!usedConstantFlag) {
                  unusedListConstant.add(constant);
               }
            }
            node.visitAll(new NodeVisitor() {
               @Override
               public boolean visit(AstNode lineNumberNode) {
                  if (lineNumberNode.getType() == Token.COLON && lineNumberNode.getType() != Token.FUNCTION) {
                     String constants = lineNumberNode.toSource();
                     String[] splitedValue = constants.split(":");
                     if (splitedValue[0].trim().matches("^[A-Z._]*")) {
                        String constantsValues = splitedValue[0].trim();
                        if (unusedListConstant.contains(constantsValues)) {
                           lineNumbersOfUnusedConstants.add(lineNumberNode.getLineno());
                        }

                     }
                  }
                  return true;
               }
            });
            constantsList.clear();
            unusedListConstant.clear();
            List<BugDetails> bugList = new ArrayList<>();
            for (int lineNumber : lineNumbersOfUnusedConstants) {
               BugDetails unnecessaryConstant = new BugDetails();
               unnecessaryConstant.setLineNumber(String.valueOf(lineNumber));
               unnecessaryConstant.setSeverityOfBug(BugCategory.UNNECESSARY_CODE_ISSUE.getSeverity().toString());
               unnecessaryConstant.setBugCategory(BugCategory.UNNECESSARY_CODE_ISSUE.toString());
               unnecessaryConstant.setBug("This JS Constant is not Used");
               unnecessaryConstant.setFileName(file.getFileName());
               unnecessaryConstant.setRemotePathOfFile(file.getRemotePath());
               bugList.add(unnecessaryConstant);
               fixes.add(Fix.operation("delete").lineNumber(lineNumber));
            }
            if (CollectionUtils.isNotEmpty(bugList)) {
               file.getBugDetailsList().addAll(bugList);
            }
            lineNumbersOfUnusedConstants.clear();
            fileAndFixes.put(file.getAbsolutePath(), fixes);
         }
      });

   }

}
