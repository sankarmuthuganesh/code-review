package RealTime.Optimus.SourceCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.Token.CommentType;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import RealTime.Logger.LogUtils;

public class JavaScriptCodeReview {
    private BufferedReader linesReader = null;
    private BufferedReader fileReader = null;
    private File file = null;
    private String fileProvide = null;
    private final List<String> errorList = new ArrayList<>();
    private final List<String> warningList = new ArrayList<>();
    private final Set<String> duplicateStringCheck = new HashSet<>(); // set to check the duplicate string
    private final List<String> prohibitedObjectList = Arrays.asList("window", "document", "screen", "history",
            "navigator", "location", "console");
    private final List<String> builtInObjectList = Arrays.asList("String", "Number", "Operators", "Statements", "Math",
            "Date",
            "Array", "Boolean", "Error", "RegExp", "Global", "Conversion", "Window", "Navigator", "Screen", "History",
            "Object", "Location", "Math");

    public void findBugs(File file) throws FileNotFoundException, IOException {
        LogUtils.print("JavaScriptCodeReview::findBugs method starts");
        CompilerEnvirons env = new CompilerEnvirons();
        boolean isGoogScope = false;
        this.file = file;
        this.fileReader = new BufferedReader(new FileReader(file));
        env.setRecordingLocalJsDocComments(true);
        env.setRecordingComments(true);
        env.setStrictMode(false);
        env.setAllowMemberExprAsFunctionName(true);
        env.setAllowSharpComments(true);
        env.setGeneratingSource(true);
        env.setIdeMode(true);
        StreamTokenizer tokenizer = new StreamTokenizer(this.fileReader);
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('$', '$');
        this.validateFile();
        while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            tokenizer.nextToken();
            if (tokenizer.sval != null) {
                if (tokenizer.sval.equals("goog.provide")) {
                    tokenizer.nextToken();
                    tokenizer.nextToken();
                    this.fileProvide = tokenizer.sval.trim();
                } else if (tokenizer.sval.startsWith("goog.net.XhrIo")) {
                    this.errorList.add("Line No: "
                            + tokenizer.lineno()
                            + " Use of function 'goog.net.XhrIo' is restricted.");
                } else if (tokenizer.sval.startsWith("goog.scope")) {
                    isGoogScope = true;
                } else if (tokenizer.sval.endsWith(".innerHTML") || tokenizer.sval.endsWith(".innerText")) {
                    this.errorList.add("Line No: "
                            + tokenizer.lineno()
                            + " Use of function 'innerHTML' and 'innerText' is restricted.");
                }
                // newly added rule for finding deprecated.
                // start
                else if ((tokenizer.sval.startsWith("goog.string.isEmpty") && tokenizer.sval
                        .equals("goog.string.isEmpty"))) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.string.isEmpty' found.Please check https://google.github.io/closure-library/api/goog.string.html");
                } else if (tokenizer.sval.startsWith("goog.string.isEmptyOrWhitespaceSafe")
                        && tokenizer.sval.equals("goog.string.isEmptyOrWhitespaceSafe")) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.string.isEmptyOrWhitespaceSafe' found.Please check https://google.github.io/closure-library/api/goog.string.html");
                } else if (tokenizer.sval.startsWith("goog.string.isEmptySafe")
                        && tokenizer.sval.equals("goog.string.isEmptySafe")) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.string.isEmptySafe' found.Please check https://google.github.io/closure-library/api/goog.string.html");
                } else if (tokenizer.sval.startsWith("goog.dom.getPageScroll")
                        && tokenizer.sval.equals("goog.dom.getPageScroll")) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.dom.getPageScroll' found.Please check https://google.github.io/closure-library/api/goog.dom.html");
                } else if (tokenizer.sval.startsWith("goog.cloneObject") && tokenizer.sval.equals("goog.cloneObject")) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.cloneObject' found.Please check https://google.github.io/closure-library/api/goog.html#global");
                } else if (tokenizer.sval.startsWith("goog.global") && tokenizer.sval.equals("goog.global")) {
                    this.warningList.add("Line No: "
                            + tokenizer.lineno()
                            + " Use of deprecated 'goog.global' found.Please check greeneyeticket 211453");
                } else if (tokenizer.sval.startsWith("goog.json.parse") && tokenizer.sval.equals("goog.json.parse")) {
                    this.errorList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of deprecated 'goog.json.parse' found.Please check https://google.github.io/closure-library/api/goog.json.html");
                } else if (tokenizer.sval.startsWith("goog.math") && tokenizer.sval.equals("goog.math")) {
                    this.warningList
                            .add("Line No: "
                                    + tokenizer.lineno()
                                    + " Use of 'goog.math' found.Check whether you have done calculations in javascript,calculations should be done in service class");
                } else if (tokenizer.sval.startsWith("goog.dom.htmlToDocumentFragment")
                        && tokenizer.sval.equals("goog.dom.htmlToDocumentFragment")) {
                    this.warningList.add("Line No: "
                            + tokenizer.lineno()
                            + " Use of deprecated 'goog.dom.htmlToDocumentFragment'");
                }
                // end
                ArrayList<String> tokenList = new ArrayList<>(Arrays.asList(tokenizer.sval.split("\\.")));

                // start
                // Snippet to find Access of private member in js file.
                for (int i = 0; i < tokenList.size(); i++) {
                    String token = tokenList.get(i);
                    if (token.endsWith("_") && i > 0) {

                        if ((tokenList.get(i - 1).equals("this")) || (tokenList.get(i - 1).equals("prototype"))
                                || (token.equals(token.toUpperCase()))) {
                            // do nothing
                        } else {
                            this.warningList.add("Line No: "
                                    + tokenizer.lineno()
                                    + " Access of private member "
                                    + token
                                    + " found.(if it's not a member of this js class,then should not use that.)");
                        }
                    }
                }
                // end

                // commented because the above snippet this snippet.
                // if(tokenList.stream().anyMatch(
                // strToken->strToken.endsWith("_"))){
                // this.warningList.add("Line No: "+tokenizer.lineno()+" Access of private member found.(if it's not a member of this js class,then should not use that.)");
                //
                // }

                prohibitedObjectList.forEach(object -> {
                    if (tokenizer.sval.startsWith(object + ".") ||
                            tokenizer.sval.equals(object)) {
                        this.warningList.add("Line No: "
                                + tokenizer.lineno()
                                + " Use of browser object '"
                                + tokenizer.sval
                                + "' found(use goog API instead).");
                    }
                });
                builtInObjectList.forEach(builtInObject -> {
                    if (tokenizer.sval.startsWith(builtInObject + ".prototype") ||
                            tokenizer.sval.equals(builtInObject)) {
                        this.warningList.add("Line No: "
                                + tokenizer.lineno()
                                + " Use of browser object or built-in type's prototype modification '"
                                + tokenizer.sval
                                + "' is restricted.");
                    }
                });
            }
        }
        if (!isGoogScope) {
            this.warningList.add("Warning: goog.scope is not found.");
        }
        AstRoot node = new Parser(env).parse(new FileReader(file), file.getPath(), 1);
        JSVoidVisitor visitor = new JSVoidVisitor();
        node.visitAll(visitor);
        visitor.scriptMethods.forEach(method -> {
            if (method.doc != null
                    && !method.doc.sourceCode.startsWith(method.name.substring(method.name.lastIndexOf('.') + 1))) {
                this.errorList.add("Line No: " + method.startLine + " method doc should starts with method name.");
            }
            if (method.isConstructor) {
                AtomicBoolean isExtends = new AtomicBoolean(false);
                if (method.doc != null
                        && !method.doc.annotations.stream().anyMatch(anno -> anno.name.equals("@constructor"))) {
                    this.errorList.add("Line No: " + method.startLine + " @constructor doc is missing.");
                }

                if (method.doc != null && !method.doc.annotations.stream().anyMatch(anno -> {
                    if (anno.name.equals("@extends") &&
                            anno.values.size() > 0 &&
                            anno.values.get(0).length() > 2 &&
                            anno.values.get(0).startsWith("{") &&
                            anno.values.get(0).endsWith("}")) {
                        isExtends.set(true);
                    }
                    return anno.name.equals("@extends");
                })) {
                    this.errorList.add("Line No: " + method.startLine + " @extends doc is missing.");
                } else if (!isExtends.get()) {
                    this.errorList.add("Line No: "
                            + method.startLine
                            + " @extends doc should have inherited class as type.");
                }
            }
            if (method.isPrivate && !method.name.endsWith("_")) {
                this.errorList.add("Line No: " + method.startLine + " private method name should ends with _.");
            } else if (!method.isPrivate && method.name.endsWith("_")) {
                this.errorList.add("Line No: "
                        + method.startLine
                        + " @private doc is missing for method '"
                        + method.name
                        + "'");
            }
            if (method.doc != null && method.doc.annotations.stream().anyMatch(anno -> {
                return anno.name.equals("@return");
            }) && !method.isReturns) {
                this.errorList.add("Line No: "
                        + method.startLine
                        + " unnecessary @return for method '"
                        + method.name
                        + "'");
            } else if (method.isReturns && method.doc != null && !method.doc.annotations.stream().anyMatch(anno -> {
                return anno.name.equals("@return");
            })) {
                this.errorList.add("Line No: "
                        + method.startLine
                        + " @return doc is missing for method '"
                        + method.name
                        + "'");
            } else if (method.isReturns && method.doc != null && method.doc.annotations.stream().anyMatch(anno -> {
                return anno.name.equals("@return");
            })) {
                if (method.doc != null) {
                    ScriptAnnotation annotation = method.doc.annotations.stream()
                            .filter(anno -> anno.name.equals("@return")).findFirst().get();
                    if (!(annotation.values.size() > 1 &&
                            annotation.values.get(0).length() > 2 &&
                            annotation.values.get(0).startsWith("{") &&
                    annotation.values.get(0).endsWith("}"))) {
                        this.errorList.add("Line No: "
                                + method.startLine
                                + " Type is missing for @return doc in method '"
                                + method.name
                                + "'");
                    }
                }
            }
            method.paramList.forEach(param -> {
                AtomicBoolean isTypePresent = new AtomicBoolean(false);
                if (method.doc != null && !method.doc.annotations.stream().anyMatch(anno -> {
                    if (anno.values.size() > 1 &&
                            anno.values.get(0).length() > 2 &&
                            anno.values.get(0).startsWith("{") &&
                            anno.values.get(0).endsWith("}")) {
                        isTypePresent.set(true);
                    }
                    return anno.values.contains(param.sourceCode);
                })) {
                    this.errorList.add("Line No: "
                            + param.lineNumber
                            + " @param is missing for param '"
                            + param.sourceCode
                            + "'");
                }
                if (!isTypePresent.get()) {
                    this.errorList.add("Line No: "
                            + param.lineNumber
                            + " type is missing for param '"
                            + param.sourceCode
                            + "'");
                }
            });
        });
        // this.errorList.addAll(visitor.errorList);
        // this.warningList.addAll(visitor.warningList);
        this.linesReader = new BufferedReader(new FileReader(file));
        warningList.add("The No of Lines in this file :" + linesReader.lines().count());
        LogUtils.print("JavaScriptCodeReview::findBugs method ends");
    }

    private void validateFile() throws IOException {
        LogUtils.print("JavaScriptCodeReview::validateFile method starts");
        String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
        if (!name.matches("[a-z\\-]+[a-z]+")) {
            this.errorList.add("Error: file name is wrong.");
        }
        LogUtils.print("JavaScriptCodeReview::validateFile method ends");
    }

    public List<String> getErrorList() throws IOException {
        LogUtils.print("JavaScriptCodeReview::getErrorList method starts");
        LogUtils.print("JavaScriptCodeReview::getErrorList method ends");
        return this.errorList;
    }

    public List<String> getWarningList() throws IOException {
        LogUtils.print("JavaScriptCodeReview::getWarningList method starts");
        LogUtils.print("JavaScriptCodeReview::getWarningList method ends");
        return this.warningList;
    }

    public int getErrorCount() {
        return this.errorList.size();
    }

    public int getWarningCount() {
        return this.warningList.size();
    }

    public Map<String, Integer> getCountInfoMap() {
        Map<String, Integer> countInfoMap = new HashMap<>();
        countInfoMap.put("error", getErrorCount());
        countInfoMap.put("warning", getWarningCount());
        return countInfoMap;
    }

    private class ScriptMethod {
        public boolean isConstructor = false;
        public boolean isPrivate = false;
        public boolean isReturns = false;
        public int startLine;
        public int endLine;
        public ScriptDoc doc = null;
        public String sourceCode;
        public String name;
        public List<ScriptField> paramList = new ArrayList<>();

        @Override
        public String toString() {
            return this.sourceCode;
        }
    }

    private class ScriptDoc {
        int startLine;
        int endLine;
        List<ScriptAnnotation> annotations = new ArrayList<>();
        String sourceCode = "";

        @Override
        public String toString() {
            return this.sourceCode;
        }
    }

    private class ScriptAnnotation {
        int lineNumber;
        List<String> values = new ArrayList<>();
        String name;

        @Override
        public String toString() {
            return name + "\t" + values;
        }
    }

    private class ScriptField {
        boolean isPrivate;
        ScriptDoc doc = null;
        int lineNumber;
        String sourceCode;

        @Override
        public String toString() {
            return this.sourceCode;
        }
    }

    private class JSVoidVisitor implements NodeVisitor {

        List<ScriptMethod> scriptMethods = new ArrayList<>();
        List<String> valueAnnotationList = Arrays.asList("@param", "@return", "@extends", "@author", "@since");

        List<String> prohibitedCalls = Arrays
                .asList("eval", "with", "alert", "setTimeout", "setInterval", "goog.dom.get", "goog.dom.classes.",
                        "goog.style.", "goog.events.listen", "parseInt", "parseFloat", "blur", "decodeURI",
                        "encodeURI", "keys");

        // private List<String> errorList = new ArrayList<>();
        // private List<String> warningList = new ArrayList<>();

        public List<ScriptMethod> getScriptMethods() throws IOException {
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::getScriptMethods method starts");
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::getScriptMethods method ends");
            return this.scriptMethods;
        }

        private ScriptDoc parseDocs(Comment scriptDoc) {
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::scriptDoc method starts");
            StringTokenizer commentTokens = new StringTokenizer(scriptDoc.toSource());
            ScriptDoc doc = new ScriptDoc();
            List<ScriptAnnotation> annotaionList = new ArrayList<>();
            ScriptAnnotation annotation = null;
            boolean parseAnnoValue = false;
            while (commentTokens.hasMoreTokens()) {
                String token = commentTokens.nextToken();
                if (token.matches("[A-Za-z_\\-\\{\\}@0-9\\.]+")) {
                    if (token.startsWith("@") && token.length() > 1) {
                        if (annotation != null) {
                            annotaionList.add(annotation);
                        }
                        annotation = new ScriptAnnotation();
                        annotation.name = token;
                        if (valueAnnotationList.contains(token)) {
                            parseAnnoValue = true;
                        } else {
                            parseAnnoValue = false;
                        }
                    } else if (parseAnnoValue) {
                        if (token.split("\\}").length > 1) {
                            annotation.values.add(token.substring(0, token.indexOf("}") + 1).replace(" ", ""));
                            annotation.values.add(token.substring(token.indexOf("}") + 1));
                        } else {
                            annotation.values.add(token);
                        }
                    } else {
                        doc.sourceCode += token + " ";
                    }
                }
            }
            if (annotation != null) {
                annotaionList.add(annotation);
            }
            doc.annotations = annotaionList;
            doc.startLine = scriptDoc.getLineno();
            doc.endLine = scriptDoc.getLineno() + scriptDoc.getLength();
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::scriptDoc method ends");
            return doc;
        }

        private List<ScriptField> parseParams(List<AstNode> params) {
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::parseParams method starts");
            List<ScriptField> paramFieldList = new ArrayList<>();
            params.stream().forEach(param -> {
                ScriptField field = new ScriptField();
                field.sourceCode = param.toSource();
                field.lineNumber = param.getLineno();
                paramFieldList.add(field);
            });
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::parseParams method ends");
            return paramFieldList;
        }

        private boolean hasReturnStatement(FunctionNode node) {
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::hasReturnStatement method starts");
            AtomicBoolean hasReturn = new AtomicBoolean(false);
            class ReturnVisitor implements NodeVisitor {
                @Override
                public boolean visit(AstNode visitedNode) {
                    if (visitedNode.getType() == Token.RETURN &&
                            visitedNode.getEnclosingFunction().equals(node)) {
                        ReturnStatement returnStatement = (ReturnStatement)visitedNode;
                        if (returnStatement.getReturnValue() != null) {
                            hasReturn.set(true);
                        }
                    }
                    return true;
                }
            }
            node.visit(new ReturnVisitor());
            LogUtils.print("JavaScriptCodeReview.JSVoidVisitor::hasReturnStatement method ends");
            return hasReturn.get();
        }

        @Override
        public boolean visit(AstNode node) {
            if (node.getType() == Token.ASSIGN) {
                Assignment assignment = (Assignment)node;
                if (assignment.getRight() instanceof Assignment) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + assignment.getLineno()
                            + " Don't summarize variables together.");
                }
                if (assignment.getRight().getType() == Token.FUNCTION) {
                    FunctionNode function = (FunctionNode)assignment.getRight();
                    if (assignment.getLeft().toSource().trim().equals(fileProvide) ||
                            assignment.getLeft().toSource().trim().contains(".prototype.")) {
                        ScriptMethod method = new ScriptMethod();
                        if (assignment.getLeft().toSource().trim().equals(fileProvide)) {
                            method.isConstructor = true;
                        }
                        method.name = assignment.getLeft().toSource();
                        method.sourceCode = assignment.getRight().toSource();
                        method.startLine = assignment.getLineno();
                        method.isReturns = this.hasReturnStatement(function);
                        method.isPrivate = assignment.getLeft().toSource().endsWith("_");
                        if (assignment.getJsDocNode() != null) {
                            method.doc = this.parseDocs(assignment.getJsDocNode());
                        } else {
                            JavaScriptCodeReview.this.errorList.add("Line No: "
                                    + assignment.getLineno()
                                    + " Doc is missing for method '"
                                    + method.name
                                    + "'");
                        }
                        method.paramList = this.parseParams(function.getParams());
                        this.scriptMethods.add(method);
                    }
                } else {
                    if (assignment.getRight().toSource().contains("wap.ri.core.InputFormModelHelper")) {
                        JavaScriptCodeReview.this.errorList
                                .add("Line No: "
                                        + assignment.getLineno()
                                        + " Should not use \"wap.ri.core.InputFormModelHelper\", instead use bindFormHelperChange in FlexibleInputBusinessArea ");
                    }
                }
            } else if (node.getType() == Token.VAR && node instanceof VariableDeclaration) {
                VariableDeclaration varDec = (VariableDeclaration)node;
                List<VariableInitializer> variableList = varDec.getVariables();
                if (variableList.size() > 1) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + varDec.getLineno()
                            + " Don't declare variables together.");
                } else if (variableList.size() == 1) {
                    if (!variableList.get(0).getTarget().toSource().matches("(\\$)?[a-z]+[A-Za-z_]*")) {
                        JavaScriptCodeReview.this.warningList.add("Line No: "
                                + varDec.getLineno()
                                + " Variable name should be in lower camel case '"
                                + variableList.get(0).getTarget().toSource()
                                + "'");
                    }
                    if (variableList.get(0).getTarget().toSource().length() < 3) {
                        JavaScriptCodeReview.this.errorList.add("Line No: "
                                + varDec.getLineno()
                                + " Variable name should be meaningful. '"
                                + variableList.get(0).getTarget().toSource()
                                + "'");
                    }
                }
            } else if (node.getType() == Token.OBJECTLIT) {
                ObjectLiteral objLiteral = (ObjectLiteral)node;
                objLiteral.getElements().forEach(objectProperty -> {
                    if (objectProperty.getLeft() instanceof StringLiteral) {
                        // JavaScriptCodeReview.this.warningList.add("Line No: "+objLiteral.getLineno()+" Don't use string literal as key in object.");
                    }
                });
            } else if (node.getType() == Token.NE || node.getType() == Token.EQ) {
                InfixExpression expr = (InfixExpression)node;
                JavaScriptCodeReview.this.errorList.add("Line No: "
                        + expr.getLineno()
                        + " Don't use '"
                        + expr.toSource()
                        + "'");
            } else if (node.getType() == Token.SHNE || node.getType() == Token.SHEQ) {
                InfixExpression expr = (InfixExpression)node;
                if (expr.getRight().toSource().equals("null") ||
                        expr.getRight().toSource().equals("undefined")) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + expr.getLineno()
                            + " Don't check '"
                            + expr.toSource()
                            + "'.Instead use (!variable)or(variable)");
                } else if (expr.getRight().toSource().equals("true") ||
                        expr.getRight().toSource().equals("false")) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + expr.getLineno()
                            + " Don't do unnecessary boolean comparisions. '"
                            + expr.toSource()
                            + "'");
                }
            } else if (node.getType() == Token.CALL) {
                prohibitedCalls.forEach(call -> {
                    if (node.toSource().startsWith(call) &&
                            !(node.toSource().startsWith("goog.events.listenOnce") ||
                            node.toSource().startsWith("goog.events.listenWith"))) {
                        if (!node.toSource().startsWith("goog.dom.getWindow")) {
                            JavaScriptCodeReview.this.warningList.add("Line No: "
                                    + node.getLineno()
                                    + " Use of prohibited method call '"
                                    + node.toSource()
                                    + "'");
                        }
                    }
                });
            } else if (node.getType() == Token.NUMBER ||
                    node.getType() == Token.STRING) {
                AstNode parent = node.getParent();
                if (node.toSource().startsWith("\"") && node.toSource().endsWith("\"")) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + node.getLineno()
                            + " Single quote is prefered over double quote.");
                }
                if (node.toSource().equalsIgnoreCase("'use strict'")
                        && (node.getParent().getParent().getType() == Token.BLOCK ||
                        node.getParent().getParent().getType() == Token.SCRIPT)) {
                    JavaScriptCodeReview.this.warningList.add("Line No: "
                            + node.getLineno()
                            + " Don't use 'use strict'");
                }
                if (!(parent != null && parent.getParent().getType() == Token.OBJECTLIT)) {
                    if (!node.getParent().toSource().trim().startsWith("goog.")
                            && !node.getParent().toSource().trim().startsWith("goog.require")
                            &&
                            !node.getParent().toSource().trim().startsWith("goog.provide")) {
                        // snippet to check the duplicate string
                        // start
                        if (!duplicateStringCheck.add(node.toSource())) {
                            JavaScriptCodeReview.this.warningList.add("Line No: "
                                    + node.getLineno()
                                    + " Don't hard code literal "
                                    + node.toSource());
                        }
                        // end
                    }

                }
            } else if (node.getType() == Token.VAR) {
                if (node.getEnclosingScope().getType() == Token.SCRIPT) {
                    JavaScriptCodeReview.this.errorList.add("Line No: "
                            + node.getLineno()
                            + " Global variable is not allowed.");
                }
            } else if (node instanceof ForInLoop) {
                JavaScriptCodeReview.this.errorList.add("Line No: " + node.getLineno() + " Don't use for-in loop.");
            } else if (node.getType() == Token.COMMENT) {
                Comment jsComment = (Comment)node;
                if (jsComment.getCommentType() == CommentType.BLOCK_COMMENT ||
                        jsComment.getCommentType() == CommentType.LINE) {
                    JavaScriptCodeReview.this.warningList.add("Line No: "
                            + jsComment.getLineno()
                            + " commented lines found.");
                }
            }
            return true;
        }
    }
}
