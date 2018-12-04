package com.iv.gravity.service.bugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class JavaCodeReviewNew {

   private final PrettyPrinterConfiguration removeContainedComments = new PrettyPrinterConfiguration();

   ;

   public List<String> getBugs(String filePath) {
      // Bugs
      Map<String, List<String>> errorAndWarningList = new HashMap<>();
      List<String> errorList = new ArrayList<>();
      List<String> warningList = new ArrayList<>();

      // Parse
      File file = new File(filePath);
      String currentFileName = file.getName().replace(".java", "");
      try {
         CompilationUnit parsedFile = JavaParser.parse(file);
         if (!currentFileName.endsWith("Config") && !currentFileName.endsWith("Util")) {
            check(currentFileName, parsedFile, errorList, warningList);
         }
      }
      catch (FileNotFoundException e) {
         // Cannot find the file in the specified path.
      }

      return errorList;

   }

   private void check(String currentFileName, CompilationUnit parsedFile, List<String> errorList, List<String> warningList) {
      // Storage
      List<String> globalCollectionList = Arrays.asList("List", "Set", "Map");
      List<String> globalPrimitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
         "Boolean", "Integer");
      String[] restrictedCacheManager = { "CompanyCacheManager", "RedisCacheManager", "EhcacheManager", "ServiceSessionCacheSharedService" };
      List<String> restrictedCacheManagerList = Arrays.asList(restrictedCacheManager);
      ArrayList<Map<String, Object>> localVariableDetailsList = new ArrayList<>();
      ArrayList<String> localStringVariableNameList = new ArrayList<>();
      Map<String, Integer> stringConstantProperUseCheckMap2 = new HashMap<>();
      List<String> stringConstantProperUseCheckList = new ArrayList<>();
      Map<String, String> bigDecimalCheckMap = new HashMap<>();
      Map<String, Integer> stringConstantProperUseCheckMap = new HashMap<>();
      ArrayList<Map<String, Object>> variableDetailsList = new ArrayList<>();
      boolean isGeneratedByCobalt = true;
      boolean implementedHueSerializable = true;
      boolean extendsApplicationContents = true;
      boolean isDataorSetterorGetter = false;
      // variables used for slf4j proper usage check
      boolean isSlfAndLogUsed = false;
      int slfTemp1 = 0;
      Set<String> stringDuplicatesCheckingSet = new HashSet<>();

      if (parsedFile.getPackageDeclaration().isPresent()) {
         String projectPath = parsedFile.getPackageDeclaration().get().getNameAsString();
         try {
            if (!currentFileName.endsWith("Dto") && !currentFileName.endsWith("Dao") && !currentFileName.endsWith("DaoImpl")) {
               // Import Visitor
               parsedFile.findAll(ImportDeclaration.class).stream().forEach(declaration -> {
                  String importedClass = declaration.getName().getIdentifier();
                  // check for usage of reflection by checking imported
                  // classes.
                  if (declaration.getNameAsString().contains("java.lang.reflect")) {
                     warningList.add("Line no:  " + declaration.getBegin().get().line + " : Found usage of Reflection.|ImportIssue");
                  }
                  else if (declaration.getNameAsString().contains("com.google.gson")) {
                     warningList.add("Line no:  " + declaration.getBegin().get().line
                        + " : Found usage of com.google.gson (please get a confirmation from CR Team about using it).|ImportIssue");
                  }
                  // check for usage of wildcard imports
                  if (declaration.getNameAsString().contains("*")) {
                     warningList.add("Line no:  " + declaration.getBegin().get().line + " :Don't use wild .|ImportIssue");
                  }
                  if (importedClass.equals("KeyValueAccess")) {
                     errorList.add("Should not use KeyValueAccess in files other than Dao and DaoImpl.|ImportIssue");
                  }
                  if (importedClass.endsWith("Dto")) {
                     if (currentFileName.endsWith("Controller")) {
                        errorList.add("Should not use any Dto in controller.|ImportIssue");
                     }
                     else {
                        warningList.add("Should not use any Dto in files other than Dao, DaoImpl and Entity.|ImportIssue");
                     }
                  }
               });
            }
            // MethodVariableVisitor Visitor
            parsedFile.findAll(VariableDeclarationExpr.class).stream().forEach(localVariable -> {
               Map<String, Object> variableDetailsMap = new HashMap<>();
               String variableName = localVariable.getVariables().get(0).getNameAsString();
               String variableType = localVariable.getElementType().asString().split("<")[0];
               String variableValue = "";
               if (localVariable.getVariables().get(0).getInitializer().isPresent()) {
                  variableValue = localVariable.getVariables().get(0).getInitializer().toString();
               }
               variableDetailsMap.put("variableName", variableName);
               variableDetailsMap.put("variableType", variableType);
               if (localVariable.getVariables().get(0).getInitializer().isPresent()) {
                  variableDetailsMap.put("variableValue", variableValue);
               }
               else {
                  variableDetailsMap.put("variableValue", "");
               }
               if (localVariable.toString().startsWith("final")) {
                  variableDetailsMap.put("isFinal", true);
               }
               else {
                  variableDetailsMap.put("isFinal", false);
               }
               variableDetailsMap.put("line", String.valueOf(localVariable.getBegin().get().line));

               localVariableDetailsList.add(variableDetailsMap);
               if (variableType.equals("String")) {

                  localStringVariableNameList.add(localVariable.getVariables().get(0).getNameAsString());
               }
            });

            // ClassVariableVisitor Visitor
            parsedFile.findAll(FieldDeclaration.class).stream().forEach(declaration -> {
               removeContainedComments.setPrintComments(false);
               getVariableDetails(declaration, bigDecimalCheckMap, stringConstantProperUseCheckList, stringConstantProperUseCheckMap,
                  variableDetailsList);
               declaration.getAnnotations().forEach(annotation -> {
                  if (annotation.toString(removeContainedComments).equals("@Autowired")) {
                     errorList.add("Line no:  " + annotation.getBegin().get().line + " : Declaration"
                        + " :  Please remove @Autowired, use @RequiredArgsConstructor(onConstructor = @__(@Autowired))|AnnotationIssue");
                  }
               });
            });

            // FileName Check
            for (ClassOrInterfaceDeclaration declaration : parsedFile.findAll(ClassOrInterfaceDeclaration.class)) {

               checkClassName(declaration, projectPath, implementedHueSerializable, extendsApplicationContents, errorList);
               if (declaration.getJavadocComment().isPresent()) {
                  String classDoc = declaration.getJavadocComment().get().toString();
                  if (!classDoc.contains("generated by Cobalt Tool.") || !classDoc.contains("Cortex")) {
                     isGeneratedByCobalt = false;
                     checkClassDocs(declaration, errorList);
                  }
               }

               checkAnnotationUsed(declaration, projectPath, errorList, isDataorSetterorGetter, isSlfAndLogUsed);

            }

            // Method Check
            for (MethodDeclaration declaration : parsedFile.findAll(MethodDeclaration.class)) {
               String camelCasePattern = "\\b[a-z]";
               Pattern pattern = Pattern.compile(camelCasePattern);
               Matcher matcher = pattern.matcher(declaration.getName().asString().trim());
               if (!matcher.find()) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Method name should be in camelCase|NamingIssue");
               }

               if (currentFileName.endsWith("ServiceImpl") && !declaration.getAnnotations().toString().contains("@Override")
                  && !declaration.isPrivate()) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + ": '" + declaration.getName()
                     + "' Function should be private other than overrided method|MethodIssue");
               }

               // The rule no more exists.
               // checkMethodLog(declaration,path);
               // checkForReflectionUsage(declaration);
               // if(String.valueOf(path).endsWith("Controller")){
               // }

               checkForSuppressWarnings(declaration, errorList);
               checkForKeyValueMethodUsage(declaration, projectPath, errorList, warningList);
               if (!isGeneratedByCobalt) {
                  checkMethodDocs(declaration, errorList, warningList, currentFileName);
               }
               checkMethodNameForIndexEntity(declaration, errorList, currentFileName);
               checkForRestrictedUsage(declaration, errorList, warningList, currentFileName);
               checkForStringAddition(declaration, errorList, warningList, slfTemp1, isSlfAndLogUsed, stringConstantProperUseCheckList,
                  stringConstantProperUseCheckMap2, stringDuplicatesCheckingSet, localStringVariableNameList);
               checkForMethodParameterCount(declaration, errorList);
               checkForMethodParameterTypeAndName(declaration, errorList);

               if (declaration.getBody() != null) {
                  // value of no of lines in a method
                  int noOfLines = declaration.getEnd().get().line - declaration.getBegin().get().line;
                  // line no of the method
                  int beginLine = declaration.getBegin().get().line;
                  // name of the method
                  String methodName = declaration.getName().asString();
                  checkForThreadUsage(declaration, errorList);
                  checkForNoOfLinesInaMethod(noOfLines, beginLine, methodName, warningList);
                  checkForProperBigDecimalUsage(declaration, errorList, bigDecimalCheckMap);
                  checkForCasting(declaration, warningList);
                  // JavaCodeReviewPartOne.checkForNullpointerExceptionInCatchBlock(declaration,
                  // errorList);
               }

            }
            if (!isGeneratedByCobalt) {
               // BlockComment Visitor
               parsedFile.findAll(BlockComment.class).stream().forEach(blockCommentDeclaration -> {
                  errorList.add("Line no:  " + blockCommentDeclaration.getBegin().get().line + " :  Remove the Multi-line comments|CommentsIssue");
               });
            }
            // CatchBlock Visitor
            parsedFile.findAll(CatchClause.class).stream().forEach(declaration -> {
               removeContainedComments.setPrintComments(false);
               if (declaration.getBody() != null) {
                  String catchBlock = declaration.getBody().toString(removeContainedComments);
                  if (!(catchBlock.contains("log.info") || catchBlock.contains("log.warn") || catchBlock.contains("log.error")
                     || catchBlock.contains("log.trace") || catchBlock.contains("log.debug") || catchBlock.contains("throw"))) {
                     warningList
                        .add("Line no:  " + declaration.getBegin().get().line + " :  Logger  is Missing in Catch Clause|LoggerInCatchMissing");
                  }
                  // new rule to check if both log and throw present.
                  if (!(catchBlock.contains("log.info") || catchBlock.contains("log.warn") || catchBlock.contains("log.error")
                     || catchBlock.contains("log.trace") || catchBlock.contains("log.debug")) && !catchBlock.contains("throw")) {
                     warningList
                        .add("Line no:  " + declaration.getBegin().get().line + " : Either use throw or log instead of using both|AvoidableUsages");
                  }
                  // end
                  if (catchBlock.contains("printStackTrace")) {
                     errorList.add("Line no:  " + declaration.getBegin().get().line
                        + " :  printStackTrace should not be used. Use log instead|ProhibitedUsages");
                  }
               }
            });

            checkVariables(errorList, warningList, currentFileName, implementedHueSerializable, extendsApplicationContents, isDataorSetterorGetter,
               isGeneratedByCobalt, stringConstantProperUseCheckMap2, globalCollectionList, globalPrimitiveAndWrapperList, variableDetailsList,
               localVariableDetailsList, restrictedCacheManagerList);
         }
         catch (UnsupportedOperationException exceptionObject) {
            errorList.add("Please remove the empty semicolon in the imports and in other places if found, and rerun the optimus tool.|ImportIssue");
         }
         catch (Exception generalExceptionInstance) {
            // errorList.add("Cannot Find Bugs in File "
            // + currentFileName
            // + "due to "
            // + generalExceptionInstance.getMessage());
         }
      }
      else {
         return;
      }
      checkPackage(parsedFile, errorList, warningList);
      // New check for no of lines in a class file.
      long temp = ((long) (parsedFile.getEnd().get().line)) - parsedFile.getBegin().get().line;
      // warningList.add("The No of Lines in this file :" + temp);
      if (temp > 1000) {
         warningList.add("No of lines of class exceeds 1000, so please consider this and check the class file has a single role.For queries"
            + " check the ticket 53393.|ExceedingLinesOfCode");
      }

      if (isSlfAndLogUsed && slfTemp1 != 1) {
         errorList.add("@Slf4j is declared, but if  logger is not used, please remove it|AnnotationIssue");
      }
   }

   private void checkPackage(CompilationUnit parsedFile, List<String> errorList, List<String> warningList) {
      if (!parsedFile.getPackageDeclaration().get().getNameAsString().startsWith("com.worksap.company")) {
         errorList.add("Line no:  " + parsedFile.getPackageDeclaration().get().getBegin().get().line
            + " :  Package name should starts with com.worksap.company|PackageIssue");
      }
   }

   private void getVariableDetails(FieldDeclaration declaration, Map<String, String> bigDecimalCheckMap,
      List<String> stringConstantProperUseCheckList, Map<String, Integer> stringConstantProperUseCheckMap,
      ArrayList<Map<String, Object>> variableDetailsList) {
      Map<String, Object> variableDetailsMap = new HashMap<>();
      removeContainedComments.setPrintComments(false);
      if (declaration.getVariables().get(0).getInitializer().isPresent()) {
         String valueInStringFormat = declaration.getVariables().get(0).getInitializer().get().toString();
         if (valueInStringFormat.equals("0") || valueInStringFormat.equals("1") || valueInStringFormat.equals("10")) {
            if (valueInStringFormat.equals("0")) {
               bigDecimalCheckMap.put(declaration.getVariables().get(0).getNameAsString(), "ZERO");
            }
            else if (valueInStringFormat.equals("1")) {
               bigDecimalCheckMap.put(declaration.getVariables().get(0).getNameAsString(), "ONE");
            }
            else if (valueInStringFormat.equals("10")) {
               bigDecimalCheckMap.put(declaration.getVariables().get(0).getNameAsString(), "TEN");
            }
         }
      }

      variableDetailsMap.put("variableName", declaration.getVariables().get(0).getNameAsString());
      variableDetailsMap.put("variableType", declaration.getElementType().asString().split("<")[0]);
      if (declaration.getVariables().get(0).getInitializer().isPresent()) {
         variableDetailsMap.put("variableValue", declaration.getVariables().get(0).getInitializer().get().toString());
      }
      else {
         variableDetailsMap.put("variableValue", "");
      }

      // variable name adding to the stringConstantProperUseCheckList for checking
      // whether the declared constant
      // is used more than two times if not then to show not use constants for that
      // variable.
      if (declaration.toString().contains("static final String") || declaration.toString().contains("final static String")) {
         stringConstantProperUseCheckList.add(declaration.getVariables().get(0).getNameAsString());
         stringConstantProperUseCheckMap.put(declaration.getVariables().get(0).getNameAsString(), 0);
      }
      // ends here

      variableDetailsMap.put("line", String.valueOf(declaration.getBegin().get().line));
      variableDetailsMap.put("hasComment", declaration.hasComment());
      if (declaration.toString().contains("static final") || declaration.toString().contains("final static")) {
         variableDetailsMap.put("isGlobal", true);
      }
      else {
         variableDetailsMap.put("isGlobal", false);
      }
      if (declaration.toString().contains("private")) {
         variableDetailsMap.put("isPrivate", true);
      }
      else {
         variableDetailsMap.put("isPrivate", false);
      }
      if (declaration.toString().contains("protected")) {
         variableDetailsMap.put("isProtected", true);
      }
      else {
         variableDetailsMap.put("isProtected", false);
      }
      // new final keyword presence added to the map
      if (declaration.toString().contains("final")
         && (!declaration.toString().contains("static final") || !declaration.toString().contains("final static"))) {
         variableDetailsMap.put("isFinal", true);
      }
      else {
         variableDetailsMap.put("isFinal", false);
      }
      // ends here
      variableDetailsList.add(variableDetailsMap);
   }

   private void checkClassName(ClassOrInterfaceDeclaration declaration, String path, boolean implementedHueSerializable,
      boolean extendsApplicationContents, List<String> errorList) {
      String projectPath = path;
      // check for hueserializable implementation.
      if (declaration.getImplementedTypes().toString().contains("HueSerializable")
         || declaration.getImplementedTypes().toString().contains("Serializable")) {
         implementedHueSerializable = false;
      }
      // end
      // check for ApplicationContents extends.
      if (declaration.getExtendedTypes().toString().contains("ApplicationContents")) {
         extendsApplicationContents = false;
      }
      // end

      if ((projectPath.contains(".constant") || projectPath.endsWith(".constant"))) {
         if (!declaration.getName().asString().endsWith("Constant") && !declaration.getName().asString().endsWith("Constants")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line
               + " :  Class name of the Constant file should ends with Constant or Constants|FileNamingIssue");
         }
      }
      else if (projectPath.contains("service")) {
         if (declaration.isInterface()) {
            if (!declaration.getName().asString().endsWith("Service")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Interface name should ends with Service|FileNamingIssue");
            }
         }
         else {
            if (!projectPath.endsWith(".constant")
               && !(declaration.getImplementedTypes().toString().contains("Service") && declaration.getName().asString().endsWith("ServiceImpl"))) {
               errorList.add("Line no:  " + declaration.getBegin().get().line
                  + " :  Class name should ends with ServiceImpl and implemented interface name should ends with Service|FileNamingIssue");
            }
         }
      }
      else if (projectPath.contains("dao")) {
         if (declaration.isInterface()) {
            if (!declaration.getName().asString().endsWith("Dao")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Interface name should ends with Dao|FileNamingIssue");
            }
         }
         else {
            if (!projectPath.endsWith(".constant")
               && !(declaration.getImplementedTypes().toString().contains("Dao") && declaration.getName().asString().endsWith("DaoImpl"))) {
               errorList.add("Line no:  " + declaration.getBegin().get().line
                  + " :  Class name should ends with DaoImpl and implemented interface name should ends with Dao|FileNamingIssue");
            }
         }
      }
      else if (projectPath.contains("controller")) {
         if (declaration.getExtendedTypes().toString().contains("Controller") && !declaration.getName().asString().endsWith("Controller")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Controller name should ends with Controller|FileNamingIssue");
         }
      }
      else if (projectPath.contains("dto")) {
         if (!declaration.getName().asString().endsWith("Dto") && !declaration.getName().asString().endsWith("Index")) {
            errorList.add(
               "Line no:  " + declaration.getBegin().get().line + " :  Dto and Index name should ends with Dto,Index respectively|FileNamingIssue");
         }
      }
      else if (projectPath.contains("type")) {
         if (!declaration.getImplementedTypes().toString().contains("HueSerializable")) {
            errorList
               .add("Line no:  " + declaration.getBegin().get().line + " :  HueSerializable must be implemented in Model and Beans|ModelBeansIssue");
         }
      }
   }

   private void checkClassDocs(ClassOrInterfaceDeclaration declaration, List<String> errorList) {
      List<Comment> commentsList = declaration.getAllContainedComments();
      commentsList.stream().filter(Comment::isLineComment)
         .forEach(comment -> errorList.add("Line no:  " + comment.getBegin().get().line + " :  Line comment is not allowed|LineCommentIssue"));

      if (declaration.getJavadocComment().isPresent()) {
         String comment = declaration.getJavadocComment().get().toString();
         if (!comment.replaceAll("\\*", "").replaceAll("/", "").trim().startsWith(declaration.getName().asString())) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Comments should only start with Class Name '"
               + declaration.getName() + "'|DocsIssue");
         }
         if (!comment.contains("@author")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @author in the comments|DocsIssue");
         }
         if (!comment.contains("@since")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @since in the comments|DocsIssue");
         }

      }

      else {
         errorList.add("Line no:  " + declaration.getBegin().get().line + " :  No Comments added for the Class|DocsIssue");
      }
   }

   private void checkAnnotationUsed(ClassOrInterfaceDeclaration declaration, String packagePath, List<String> errorList,
      boolean isDataorSetterorGetter, boolean isSlfAndLogUsed) {
      String packageName = packagePath;
      removeContainedComments.setPrintComments(false);
      List<String> annotationsUsedList = declaration.getAnnotations().stream().map(annotation -> {
         return annotation.toString(removeContainedComments);
      }).collect(Collectors.toList());

      // Logic related to skip global variable check
      if (annotationsUsedList.contains("@Data") || annotationsUsedList.contains("@Setter") || annotationsUsedList.contains("@Getter")) {
         isDataorSetterorGetter = true;
      }

      // The code is commented because the rule no more exist.
      // if(!packageName.contains("dao") && !declaration.isInterface()
      // && !declaration.getName().endsWith("Dto") &&
      // !declaration.getName().endsWith("Index") ){
      // if(!annotationsUsedList.contains("@Slf4j")){
      // warningList.add("Line no: " + declaration.getBegin().get().line
      // + " : @Slf4j is Mandatory,Do not use other loggers");
      // }
      // }

      if (packageName.contains("dto") && (declaration.getName().asString().endsWith("Dto") || declaration.getName().asString().endsWith("Index"))
         && !annotationsUsedList.contains("@Entity")) {
         errorList.add("Line no:  " + declaration.getBegin().get().line + " :  @Entity is Mandatory for Dto and Index files|DTOIssue");
      }
      if (!packageName.contains("dto") && !packageName.contains("dao") && annotationsUsedList.contains("@Entity")) {
         errorList.add(
            "Line no:  " + declaration.getBegin().get().line + " :  @Entity should not be used other than Dto,Index and DaoImpl|AnnotationIssue");
      }
      if (declaration.getName().asString().endsWith("Impl") && annotationsUsedList.contains("@AllArgsConstructor")) {
         errorList.add(
            "Line no:  " + declaration.getBegin().get().line + " :  Use @RequiredArgsConstructor instead of @AllArgsConstructor|AnnotationIssue");
      }
      // check for slf4j
      // start
      if (annotationsUsedList.contains("@Slf4j")) {
         isSlfAndLogUsed = true;
      }
      // end
   }

   private void checkForSuppressWarnings(MethodDeclaration declaration, List<String> errorList) {
      declaration.getAnnotations().stream().filter(input -> input.getNameAsString().startsWith("Suppress")).forEach(data -> {
         errorList.add("Line No  :" + data.getBegin().get().line + " : Try not to use " + data.getNameAsString() + ", fix them|AnnotationIssue");
      });
   }

   private void checkForKeyValueMethodUsage(MethodDeclaration declaration, Object path, List<String> errorList, List<String> warningList) {
      removeContainedComments.setPrintComments(false);
      if (String.valueOf(path).contains("dao")) {
         if (declaration.toString(removeContainedComments).contains(".search(")
            || declaration.toString(removeContainedComments).contains(".searchAll(")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Remove search, searchAll functions in the method '"
               + declaration.getName() + "'. Use searchWithIterator() instead|ProhibitedUsages");
         }
         if (declaration.toString(removeContainedComments).contains("SearchConditions.ALL()")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Don't use SearchConditions.ALL()|ProhibitedUsages");
         }

         // new check for searchAllWithIterator.
         // start
         if (declaration.toString(removeContainedComments).contains("searchAllWithIterator(")) {
            warningList.add("Line no:  " + declaration.getBegin().get().line
               + " :searchAllWithIterator found.Please get confirmation from wap side before using it or consult CR team|ProhibitedUsages");
         }
         // end

         if (declaration.toString(removeContainedComments).contains("IteratorUtils.toList")) {
            // usage of IteratorUtils.toList is not allowed.
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Don't use IteratorUtils.toList|ProhibitedUsages");
         }
      }
   }

   private void checkMethodDocs(MethodDeclaration declaration, List<String> errorList, List<String> warningList, String currentFileName) {
      if (!declaration.getAnnotations().toString().contains("@Override")) {
         if (declaration.getComment() != null) {
            String commentContent = declaration.getJavadocComment().get().toString();
            if (!declaration.getJavadocComment().isPresent()) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " : Comments is missing for the function '"
                  + declaration.getName().asString() + "'|DocsIssue");
            }

            if (!commentContent.replaceAll("\\*", "").replaceAll("/", "").trim().startsWith(declaration.getName().asString())) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Comments should only start with Function Name '"
                  + declaration.getName().asString() + "'|DocsIssue");
            }

            declaration.getParameters().stream().forEach(param -> {
               String paramName = param.getNameAsString();
               String comment = commentContent.replaceAll(" ", "");
               if (!comment.contains("@param" + paramName)) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @param '" + paramName + "' in the function '"
                     + declaration.getName().asString() + "'|DocsIssue");
               }
            });

            declaration.getThrownExceptions().stream().forEach(value -> {
               String throwsValue = value.asString();
               String comment = commentContent.replaceAll(" ", "");
               if (!comment.contains("@throws" + throwsValue)) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @throws '" + throwsValue + "' in the function '"
                     + declaration.getName().asString() + "'|DocsIssue");
               }
            });

            if (!declaration.getType().asString().trim().equals("void") && !declaration.getDeclarationAsString().contains(" abstract ")) {
               if (!commentContent.contains("@return")) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing return statement for the function '"
                     + declaration.getName().asString() + "'|DocsIssue");
               }
               else {
                  String commentString;
                  String returnParam;
                  int size = commentContent.split("\n").length;
                  for (int index = 0; index < size; index++) {
                     commentString = commentContent.split("\n")[index];
                     if (commentString.contains("@return")) {
                        returnParam = commentString.split("@return", 2)[1].trim();
                        if (returnParam.equals("")) {
                           errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing return param for the function '"
                              + declaration.getName().asString() + "'|DocsIssue");
                        }
                     }
                  }
               }
            }
         }
         else {
            // skipping comment check for formDto or formIndex method in the
            // IndexEntity file
            if (!currentFileName.contains("IndexEntity")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " : Comments is missing for the function '"
                  + declaration.getName().asString() + "'|DocsIssue");
            }
            else {
               if (!declaration.getName().asString().equalsIgnoreCase("formIndex") && !declaration.getName().asString().equalsIgnoreCase("formDto")) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " : Comments is missing for the function '"
                     + declaration.getName().asString() + "'|DocsIssue");
               }
            }
         }
      }
   }

   private void checkMethodNameForIndexEntity(MethodDeclaration declaration, List<String> errorList, String currentFileName) {
      // The below if statement is used to check indexEntity contains formDto Method or
      // Not.
      if (currentFileName.endsWith("IndexEntity")) {
         if (declaration.getName().asString().equalsIgnoreCase("formDto") || declaration.getName().asString().equalsIgnoreCase("formIndex")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + ": '" + declaration.getName().asString()
               + " is not needed in the indexEntity class file.|AvoidableUsages");
         }
      }
   }

   private void checkForRestrictedUsage(MethodDeclaration declaration, List<String> errorList, List<String> warningList, String currentFileName) {
      String intLiteralRegex = "(.*)([0-9]\\.*)(.*)";
      Pattern intLiteralpattern = Pattern.compile(intLiteralRegex);
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody().isPresent()) {
         BlockStmt block = declaration.getBody().get();
         block.getStatements().stream().forEach(statement -> {
            if (statement instanceof ForStmt) {
               ForStmt forStatement = (ForStmt) statement;
               warningList
                  .add("Line no:  " + forStatement.getBegin().get().line + ":  For Loop should not be used. Use Streams instead.|AvoidableUsages");
            }
            else if (statement instanceof ForeachStmt) {
               ForeachStmt forEachStatement = (ForeachStmt) statement;
               warningList.add(
                  "Line no:  " + forEachStatement.getBegin().get().line + ":  For-each Loop should not be used. Use Streams instead|AvoidableUsages");
            }
            else if (statement instanceof IfStmt) {
               IfStmt ifStatement = (IfStmt) statement;
               if (ifStatement.getCondition() != null) {
                  Matcher intLiteralMatcher = intLiteralpattern.matcher(statement.toString());
                  String condition = ifStatement.getCondition().toString(removeContainedComments).replace(" ", "");
                  if (condition.replace(" ", "").contains("==null") || condition.replace(" ", "").contains("!=null")) {
                     warningList.add("Line no:  " + ifStatement.getCondition().getBegin().get().line
                        + " : Use Objects.nonNull() instead of == null and !=null|AvoidableUsages");
                  }
                  if (intLiteralMatcher.find()) {
                     warningList.add("Line no:  " + ifStatement.getCondition().getBegin().get().line
                        + " :  HardCoded Integer value : Use Constants instead of using Integer literal|AvoidableUsages");
                  }
                  if (condition.replace(" ", "").contains("==true") || condition.replace(" ", "").contains("!=true")
                     || condition.replace(" ", "").contains("==false") || condition.replace(" ", "").contains("!=false")
                     || condition.replace(" ", "").contains("==Boolean.TRUE") || condition.replace(" ", "").contains("!=Boolean.TRUE")
                     || condition.replace(" ", "").contains("==Boolean.FALSE") || condition.replace(" ", "").contains("!=Boolean.FALSE")
                     || condition.replace(" ", "").contains(".equals(Boolean.FALSE)") || condition.replace(" ", "").contains(".equals(Boolean.TRUE)")
                     || condition.replace(" ", "").contains(".equals(true)") || condition.replace(" ", "").contains(".equals(false)")) {
                     errorList.add(
                        "Line no:  " + ifStatement.getCondition().getBegin().get().line + " : boolean comparision should be avoided|AvoidableUsages");
                  }
               }
            }
            else if (statement instanceof SwitchStmt) {
               SwitchStmt switchStmnt = (SwitchStmt) statement;
               switchStmnt.getEntries().stream().forEach(data -> {
                  if (!data.toString().contains("break;") && !data.toString().contains("return ") && !data.toString().contains("return;")) {
                     errorList.add("Line No  :" + data.getBegin().get().line + " The break statement is missing in this block.|SyntacticalIssue");
                  }
               });
            }
            else {
               Matcher intLiteralMatcher = intLiteralpattern.matcher(statement.toString());
               if (intLiteralMatcher.find()) {
                  warningList.add("Line no:  " + statement.getBegin().get().line
                     + " :  HardCoded Integer value : Use Constants instead of using Integer literal|AvoidableUsages");
               }
            }

         });
      }
      if (currentFileName.endsWith("Controller")) {
         if (declaration.getParameters() != null) {
            declaration.getParameters().stream().filter(
               param -> (param.getAnnotations().toString().contains("@RequestBody") || param.getAnnotations().toString().contains("@RequestParam")))
               .forEach(param -> {
                  if (param.getType().asString().startsWith("Map<") || param.getType().asString().startsWith("List<Map<")
                     || param.getType().asString().startsWith("ArrayList<Map<")) {
                     errorList.add("Line no:  " + param.getBegin().get().line + " :  Use Pojo instead of using Map or List of Map|MethodIssue");
                  }
               });
         }
      }
   }

   private void checkForStringAddition(MethodDeclaration declaration, List<String> errorList, List<String> warningList, int slfTemp1,
      boolean isSlfAndLogUsed, List<String> stringConstantProperUseCheckList, Map<String, Integer> stringConstantProperUseCheckMap2,
      Set<String> stringDuplicatesCheckingSet, ArrayList<String> localStringVariableNameList) {
      String stringLiteralRegex = "(\\.\\()?\"";
      Pattern stringLiteralpattern = Pattern.compile(stringLiteralRegex);
      ArrayList<String> variableNameList = localStringVariableNameList;
      ArrayList<Map<String, String>> finalList = new ArrayList<Map<String, String>>();
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody() != null) {
         String decl = declaration.getBody().get().toString(removeContainedComments);
         if (decl.contains("? true : false") || decl.contains("?true:false") || decl.contains("? false : true") || decl.contains("?false:true")) {
            errorList.add(" Line No :  " + declaration.getBegin().get().line + ": Method :" + declaration.getName().asString()
               + " : Unnecessary Ternary Operator (? true : false) or (? false : true)|AvoidableUsages");
         }
         else if (decl.replace(" ", "").contains("?true:false") || decl.replace(" ", "").contains("?false||true")) {
            errorList.add(" Line No :  " + declaration.getBegin().get().line + ": Method :" + declaration.getName().asString()
               + " : Unnecessary Ternary Operator (? true : false) or (? false : true)|AvoidableUsages");
         }
      }
      if (declaration.getBody().isPresent()) {
         declaration.getBody().get().getChildNodes().stream().forEach(data -> {
            checkAddition(data, finalList);
         });
      }
      finalList.stream().forEach(line -> {

         // newly added to check proper constant usage
         stringConstantProperUseCheckList.stream().forEach(constant -> {
            String completeLine = line.get("methodLine");
            String[] completeLineSplitted = completeLine.split("[:;()\\s@,&.?$+-]+");
            int doubleQuoteIgnoringValue = 3;
            for (String completeLineSplittedItem : completeLineSplitted) {
               if (!completeLineSplittedItem.equals("")) {
                  String doubleQuoteCheckString = completeLineSplittedItem.substring(0, 1);
                  if (doubleQuoteCheckString.equals(String.valueOf('"'))
                     && (completeLineSplittedItem.charAt(completeLineSplittedItem.length() - 1) != '"')) {
                     doubleQuoteIgnoringValue = 1;
                  }
                  else if ((completeLineSplittedItem.charAt(completeLineSplittedItem.length() - 1)) == '"') {
                     doubleQuoteIgnoringValue = 3;
                  }
                  if (doubleQuoteIgnoringValue == 3) {
                     if (completeLineSplittedItem.equals(constant)) {
                        if (stringConstantProperUseCheckMap2.containsKey(constant)) {
                           stringConstantProperUseCheckMap2.put(constant, 2);
                        }
                        else {
                           stringConstantProperUseCheckMap2.put(constant, 1);
                        }
                     }
                  }
               }
            }
         });
         // ends here

         // new check for the usage of immutable map in the buildAllForneusViewId
         if (declaration.getName().asString().equals("buildAllForneusViewId")) {
            if (line.get("methodLine").contains("new HashMap")) {
               errorList.add("Line no:  " + line.get("line") + " :  Use immutablemap instead of " + line.get("methodLine")
                  + ".Please refer Java_Coding_Standards Do_not_construct_ForneusViewId_dynamically in infowiki.|FrameworkViolations");
            }
         }
         // ends here

         Matcher matcher = stringLiteralpattern.matcher(line.get("methodLine"));
         while (matcher.find()) {
            int startIndex = matcher.end();
            String str = matcher.group(1);
            // new check : duplicates strings are checked and error thrown.
            if (matcher.find() && str == null) {
               String duplicateString = line.get("methodLine").substring(startIndex, matcher.start());
               if (!stringDuplicatesCheckingSet.add(duplicateString)) {
                  errorList.add("Line no:  " + line.get("line") + " :  HardCoded String value : String literal '"
                     + line.get("methodLine").substring(startIndex, matcher.start()) + "' is hardcoded|VariableIssue");
               }
            }
            // ends here only set is added to check that
         }

         if (line.get("methodLine").contains("System.out.print")) {
            errorList.add("Line no:  " + line.get("line") + ":  Remove Print statement in the line or block|ProhibitedUsages");
         }
         if (line.get("methodLine").contains("'")) {
            warningList.add("Line no:  " + line.get("line") + ":  Single quoted string is used|AvoidableUsages");
         }
         if (line.get("methodLine").contains("+") && line.get("methodLine").contains("=")) {
            String[] splittedVariable = line.get("methodLine").split("=");
            if (splittedVariable.length > 1) {
               splittedVariable = splittedVariable[1].split("\\+");
               int splitCount = 0;
               for (int splitIndex = 0; splitIndex < splittedVariable.length; splitIndex++) {
                  if (variableNameList.contains(splittedVariable[splitIndex].trim())) {
                     splitCount++;
                  }
               }
               if (splitCount >= 2) {
                  warningList.add("Line no:  " + line.get("line") + " :  String variables added|VariableIssue");
               }
            }
         }
      });

      // check for proper usage of slf4j
      if (declaration.getBody().isPresent()) {
         if (isSlfAndLogUsed) {
            String methodBody = declaration.getBody().get().toString(removeContainedComments);
            if (methodBody.contains("log.info") || methodBody.contains("log.error") || methodBody.contains("log.debug")) {
               slfTemp1 = 1;
            }
         }
      }
      // end
   }

   private void checkAddition(Node node, ArrayList<Map<String, String>> lineList) {
      node.getChildNodes().stream().forEach(data -> {
         if (data.getChildNodes().size() > 3) {
            checkAddition(data, lineList);
         }
         else {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("methodLine", data.toString());
            dataMap.put("line", String.valueOf(data.getBegin().get().line));
            lineList.add(dataMap);
         }
      });
   }

   private void checkForMethodParameterCount(MethodDeclaration declaration, List<String> errorList) {
      long parametersCount = declaration.getParameters().stream().count();
      if (parametersCount > 7) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " : In method " + declaration.getName().asString()
            + " found more than 7 parameters, but the " + "allowed count is atmost 7|MethodIssue");
      }
   }

   private void checkForMethodParameterTypeAndName(MethodDeclaration declaration, List<String> errorList) {
      removeContainedComments.setPrintComments(false);
      String camelCasePattern = "\\b[a-z]";
      Pattern pattern = Pattern.compile(camelCasePattern);
      declaration.getParameters().forEach(param -> {
         String paramType = param.getType().toString(removeContainedComments);
         if (paramType.equals("HttpServletRequest") || paramType.equals("HttpServletResponse")) {
            errorList
               .add("Line no:  " + declaration.getBegin().get().line + ": Should not use HttpServletRequest , HttpServletResponse |ProhibitedUsages");
         }
         if (paramType.contains("<")) {
            paramType = paramType.split("<")[0];
         }
         String paramName = param.getNameAsString();
         int lineNumber = declaration.getBegin().get().line;

         // method parameters check for camelCase updated
         Matcher match = pattern.matcher(paramName);
         if (!match.find()) {
            errorList.add("Line no:  " + param.getBegin().get().line + " :  Parameter name should be in lowerCamelCase|NamingIssue");
         }
         if (paramType.endsWith("List")) {
            if (!paramName.endsWith("List") && !paramName.endsWith("LIST") && !paramName.endsWith("Lists") && !paramName.endsWith("LISTS")) {
               errorList.add("Line no:  " + lineNumber + " :  Parameter '" + paramName + "' is not ending with List|NamingIssue");
            }
         }
         else if (paramType.endsWith("Map")) {
            if (!paramName.endsWith("Map") && !paramName.endsWith("MAP")) {
               errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Map|NamingIssue");
            }
         }
         else if (paramType.endsWith("Set")) {
            if (!paramName.endsWith("Set") && !paramName.endsWith("SET")) {
               errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Set|NamingIssue");
            }
         }
         else if (paramType.endsWith("Queue")) {
            if (!paramName.endsWith("Queue") && !paramName.endsWith("QUEUE")) {
               errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Queue|NamingIssue");
            }
         }
         else if (paramType.endsWith("Deque")) {
            if (!paramName.endsWith("Deque") && !paramName.endsWith("DEQUE")) {
               errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Deque|NamingIssue");
            }
         }

      });
   }

   private void checkForThreadUsage(MethodDeclaration declaration, List<String> errorList) {
      removeContainedComments.setPrintComments(false);
      if (declaration.toString(removeContainedComments).contains("Thread")) {
         // usage of thread is not safe.
         errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Usage of Thread found|ProhibitedUsages");
      }
   }

   private void checkForNoOfLinesInaMethod(int noOfLines, int beginLine, String methodName, List<String> warningList) {
      if (noOfLines > 100) {
         warningList.add("Line No:  " + beginLine + "  " + methodName + " method has more than 100 lines, so if the logic is independent"
            + " then split the method to improve readability.For queries check ticket 53393|MethodIssue");
      }
   }

   private void checkForProperBigDecimalUsage(MethodDeclaration declaration, List<String> errorList, Map<String, String> bigDecimalCheckMap) {
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(0)")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
            + " found 'new BigDecimal(0)' instead use BigDecimal.ZERO|AvoidableUsages");
      }
      else if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(1)")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
            + " found 'new BigDecimal(1)' instead use BigDecimal.ONE|AvoidableUsages");
      }
      else if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(10)")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
            + " found 'new BigDecimal(10)' instead use BigDecimal.TEN|AvoidableUsages");
      }
      bigDecimalCheckMap.entrySet().stream().forEach(keyValue -> {
         if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(" + keyValue.getKey() + ")")) {
            errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found 'new BigDecimal(" + keyValue.getKey() + ")' instead use BigDecimal." + keyValue.getValue() + "|AvoidableUsages");
         }
      });
   }

   private void checkForCasting(MethodDeclaration declaration, List<String> warningList) {
      removeContainedComments.setPrintComments(false);
      List<String> primitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
         "Boolean", "Integer");
      String stringToCheck = declaration.getBody().get().toString(removeContainedComments);
      if (stringToCheck.contains("class.cast(")) {
         warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName()
            + " found type casting(class.cast)|ProhibitedUsages");
      }
      primitiveAndWrapperList.stream().forEach(primitiveAndWrapper -> {
         if (stringToCheck.contains("(" + primitiveAndWrapper + ")")) {
            warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found type casting|ProhibitedUsages");
         }
      });
      List<String> collectionList = Arrays.asList("List", "Map", "Set", "Collection");
      collectionList.stream().forEach(collection -> {
         if (stringToCheck.contains("(" + collection + "<") && stringToCheck.contains(">)")) {
            warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found type casting|ProhibitedUsages");
         }
      });
   }

   private void checkVariables(List<String> errorList, List<String> warningList, String currentFileName, boolean implementedHueSerializable,
      boolean extendsApplicationContents, boolean isDataorSetterorGetter, boolean isGeneratedByCobalt,
      Map<String, Integer> stringConstantProperUseCheckMap2, List<String> globalCollectionList, List<String> globalPrimitiveAndWrapperList,
      ArrayList<Map<String, Object>> variableDetailsList, ArrayList<Map<String, Object>> localVariableDetailsList,
      List<String> restrictedCacheManagerList) throws IOException {

      String camelCasePattern = "\\b[a-z]";
      Pattern pattern = Pattern.compile(camelCasePattern);
      Set<String> uniqueEntriesSet = new HashSet<>();

      variableDetailsList.stream().forEach(variable -> {
         String variableType = (String) variable.get("variableType");
         String variableName = (String) variable.get("variableName");
         String variableValue = (String) variable.get("variableValue");
         String lineNumber = (String) variable.get("line");
         Boolean hasComment = (Boolean) variable.get("hasComment");
         Boolean isGlobal = (Boolean) variable.get("isGlobal");
         Boolean isPrivate = (Boolean) variable.get("isPrivate");
         Boolean isFinal = (Boolean) variable.get("isFinal");
         Boolean isProtected = (Boolean) variable.get("isProtected");

         checkVariableType(variableType, variableName, variableValue, lineNumber, restrictedCacheManagerList, errorList);

         if (!(currentFileName.endsWith("Dto") || currentFileName.endsWith("Index") || currentFileName.endsWith("Entity")
            || currentFileName.endsWith("Constant") || currentFileName.endsWith("Constants") || currentFileName.endsWith("Model")
            || currentFileName.endsWith("model") || currentFileName.endsWith("Type") || currentFileName.endsWith("type")
            || currentFileName.endsWith("enum") || currentFileName.endsWith("Enum") || currentFileName.endsWith("Arguments")
            || currentFileName.endsWith("arguments")) && implementedHueSerializable && extendsApplicationContents && !isDataorSetterorGetter) {
            // new check for global variables in a class:
            List<String> collectionList = Arrays.asList("List", "Set", "Map");
            List<String> primitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
               "Boolean", "Integer");
            if ((isPrivate && isFinal) || (isPrivate && isGlobal) || isPrivate) {
               if (collectionList.contains(variableType)) {
                  errorList.add("Line no:  " + lineNumber + " :  Collections are not allowed to set as a Global Variable|ClassFieldIssue");
               }
               else if (((isPrivate && isFinal) || isPrivate) && primitiveAndWrapperList.contains(variableType) && !isGlobal) {
                  errorList.add(
                     "Line no:  " + lineNumber + " :  If the '" + variableType + "' is used as a constant then it should be private static final "
                        + "or else it is considered as a global variable and are not allowed|ClassFieldIssue");
                  // (variableType.equals("String")||variableType.equals("int")||variableType.equalsIgnoreCase("long")||
                  // variableType.equals("double")||variableType.equals("short")||variableType.equalsIgnoreCase("boolean")||variableType.equals("Integer"))
               }

            }
            if (variableType.equals("boolean") || variableType.equals("Boolean")) {
               errorList.add("Line no:  " + lineNumber + " :  no need to declare boolean in constants instead use directly|ClassFieldIssue");
            }
         }
         // ends here

         // new check for currentVersion in a class:
         List<String> currentVersionList = Arrays.asList("CURRENT_VERSION");
         if ((currentFileName.endsWith("Dto") || currentFileName.endsWith("Index") || currentFileName.endsWith("Entity")
            || currentFileName.endsWith("IndexEntity")) && currentVersionList.contains(variableName)) {
            errorList
               .add("Line no:  " + lineNumber + " :  The variable '" + variableName + "' is not needed in dto or index or entity file.|DTOIssue");
            // ends here
         }
         else {
            if (isGlobal) {
               if (!variableName.replace("_", "").equals(variableName.replace("_", "").toUpperCase())) {
                  warningList
                     .add("Line no:  " + lineNumber + " :  static final variable '" + variableName + "' should be in UPPER_CASE|ConstantIssue");
               }
               if (variableName.length() < 3) {
                  errorList.add("Line no:  " + lineNumber + " :  variable should be minimum 3 characters in length|NamingIssue");
               }
               if (!uniqueEntriesSet.add(variableValue)) {
                  warningList.add("Line no:  " + lineNumber + " :  Duplicate constant variable with same value '" + variableName + "'|ConstantIssue");
               }
            }
            else {
               Matcher matcher = pattern.matcher(variableName);
               if (!matcher.find()) {
                  errorList.add("Line no:  " + lineNumber + " :  Variable name should be in lowerCamelCase|NamingIssue");
               }
               // commented because the comment check is duplicated(as it is always
               // checked below.
               // if(this.currentFileName.endsWith("Entity")
               // ||this.currentFileName.endsWith("Dto")
               // ||this.currentFileName.endsWith("Model") ||
               // this.currentFileName.endsWith("Index") ||
               // this.currentFileName.endsWith("Vo")){
               // if(!hasComment){
               // errorList.add("Line no: " + lineNumber +
               // " : Comment is missing for the variable '"
               // + variableName + "'");
               // }
               // }
            }

            if (!isGeneratedByCobalt) {
               if (!hasComment) {
                  errorList.add("Line no:  " + lineNumber + " :  Comment is missing for the variable '" + variableName + "'|DocsIssue");
               }
            }
            if (variableType.equals("String") && variableValue.equals("\"\"")) {
               errorList.add(
                  "Line no:  " + lineNumber + " :  Dont use Constant for empty string. Use org.apache.commons.lang3.StringUtils.EMPTY|ConstantIssue");
            }
            if (variableType.equals("KeyValueAccess") && variableName.equals("kva")) {
               errorList.add("Line no:  " + lineNumber + " :  Variable name should not be kva|DTOIssue");
            }
            if (!currentFileName.endsWith("Constant") && !currentFileName.endsWith("Constants") && !isPrivate && !isProtected) {
               errorList.add("Line no:  " + lineNumber + " :  Global variable should be private.|ClassFieldIssue");
            }
            if (currentFileName.endsWith("Impl") || currentFileName.endsWith("Controller")) {
               if (!isFinal && !globalCollectionList.contains(variableType) && !globalPrimitiveAndWrapperList.contains(variableType)) {
                  errorList.add("Line no:  " + lineNumber + " :  Bean should be final.|ClassFieldIssue");
               }
            }
         } // current version else check ends here
      });

      localVariableDetailsList.stream().forEach(variable -> {
         String variableType = (String) variable.get("variableType");
         String variableName = (String) variable.get("variableName");
         String variableValue = (String) variable.get("variableValue");
         String lineNumber = (String) variable.get("line");
         boolean isFinal = (boolean) variable.get("isFinal");
         Matcher matcher = pattern.matcher(variableName);
         if (!matcher.find()) {
            errorList.add("Line no:  " + lineNumber + " :  Variable name should be in lowerCamelCase|NamingIssue");
         }
         if (variableType.equals("String") && variableValue.equals("\"\"")) {
            errorList.add("Line no:  " + lineNumber + " :  Use org.apache.commons.lang3.StringUtils.EMPTY for Empty String|VariableIssue");
         }
         if (isFinal) {
            warningList.add("Line no:  " + lineNumber + " :  Local variable should not be final variable|VariableIssue");
         }
         if (variableName.length() < 3) {
            errorList.add("Line no:  " + lineNumber + " :  variable should be minimum 3 characters in length|NamingIssue");
         }
         checkVariableType(variableType, variableName, variableValue, lineNumber, restrictedCacheManagerList, errorList);
      });

      // //newly added to check proper constant usage
      stringConstantProperUseCheckMap2.entrySet().stream().filter(action -> action.getValue().equals(1))
         .forEach(result -> errorList.add("String constant variable : '" + result.getKey() + "' is used only once, "
            + "so don't put in constants(if this constant is used in a condition please ignore this error)|ConstantIssue"));
      // ends here

   }

   private void checkVariableType(String variableType, String variableName, String variableValue, String lineNumber,
      List<String> restrictedCacheManagerList, List<String> errorList) {
      if (restrictedCacheManagerList.contains(variableType)) {
         errorList.add("Line no:  " + lineNumber + " :  Use of '" + variableType + "' is restricted|VariableIssue");
      }
      else if (variableType.endsWith("List")) {
         if (!variableName.endsWith("List") && !variableName.endsWith("LIST") && !variableName.endsWith("Lists") && !variableName.endsWith("LISTS")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable '" + variableName + "' is not ending with List|NamingIssue");
         }
         if (variableValue.equals("Lists.newArrayList()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new ArrayList() instead|VariableIssue");
         }
      }
      else if (variableType.endsWith("Map")) {
         if (!variableName.endsWith("Map") && !variableName.endsWith("MAP")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Map|NamingIssue");
         }
         if (variableValue.equals("Maps.newHashMap()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new HashMap() instead|VariableIssue");
         }
      }
      else if (variableType.endsWith("Set")) {
         if (!variableName.endsWith("Set") && !variableName.endsWith("SET")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Set|NamingIssue");
         }
         if (variableValue.equals("Sets.newHashSet()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new Set() instead|VariableIssue");
         }
      }
      else if (variableType.endsWith("Queue")) {
         if (!variableName.endsWith("Queue") && !variableName.endsWith("QUEUE")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Queue|NamingIssue");
         }
      }
      else if (variableType.endsWith("Deque")) {
         if (!variableName.endsWith("Deque") && !variableName.endsWith("DEQUE")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Deque|NamingIssue");
         }
      }
   }

}
