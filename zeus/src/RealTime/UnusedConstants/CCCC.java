/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package RealTime.UnusedConstants;

import java.io.File;
import java.io.FileReader;
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

import RealTime.Entity.BugDetails;









/**
 * 
 * @version $Id$
 */
public class CCCC 
{
     List<String> constantsList = new ArrayList<>();
     List<String> unusedListConstant = new ArrayList<>();
     private List<Integer> lineNumbersOfUnusedConstants = new ArrayList<>();;
     String currentConstant;
     boolean usedConstantFlag;
    public  void get()
    {
        List<BugDetails> bugList = new ArrayList<>();

                    File theFileToSearch = new File("C:\\HUE\\WorkSpace\\Develop\\Zeus\\src\\RealTime\\UnusedConstants\\kkk.js");
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
                        
                        node.visitAll(new NodeVisitor(){
                            @Override
                            public boolean visit(AstNode functionNode)
                            {
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
                            node.visitAll(new NodeVisitor(){
                                @Override
                                public boolean visit(AstNode unusedConstantNode){
                                    if (unusedConstantNode.getType() == Token.FUNCTION) {
                                        String functionContents = unusedConstantNode.toSource();
                                        Pattern p = Pattern.compile(currentConstant);
                                        // Pattern p = Pattern.compile("\\b" + currentConstant + "\\b");
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
                        node.visitAll(new NodeVisitor(){
                            @Override
                            public boolean visit(AstNode lineNumberNode){
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
                        
                        for (int lineNumber : lineNumbersOfUnusedConstants) {
                            BugDetails unnecessaryConstant = new BugDetails();
                            unnecessaryConstant.setLineNumber(String.valueOf(lineNumber));
                            unnecessaryConstant.setSeverityOfBug("Warning");
                            unnecessaryConstant.setBugCategory("UnnecessaryConstants");
                            unnecessaryConstant.setBug("These JS Constants are Unnecessary");
                            unnecessaryConstant.setFileName("kkk");
                            unnecessaryConstant.setHttpPathOfFile("asdfsafd");
                            bugList.add(unnecessaryConstant);
                        }
                        if (!bugList.isEmpty()) {
                        }
                        lineNumbersOfUnusedConstants.clear();
                    }
                    bugList.stream().forEach(System.out::println); 
    }
}
