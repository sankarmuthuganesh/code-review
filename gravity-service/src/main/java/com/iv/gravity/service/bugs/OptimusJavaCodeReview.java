package com.iv.gravity.service.bugs;

import java.io.File;
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
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.iv.gravity.enums.BugCategory;

/**
 * @version Oct 24, 2018
 * @author optimus team
 */
public class OptimusJavaCodeReview {

   PrettyPrinterConfiguration removeContainedComments = new PrettyPrinterConfiguration();

   private String currentFileName;

   private String projectPath;

   private List<String> errorList = new ArrayList<>();

   private List<String> warningList = new ArrayList<>();

   Set<String> stringDuplicatesCheckingSet = new HashSet<>(); // set for checking string
   // duplicates.

   List<String> stringConstantProperUseCheckList = new ArrayList<>(); // list for
   // checking the
   // proper usage of
   // conatnts.

   // variables used for slf4j proper usage check
   private boolean isSlfAndLogUsed = false;

   private int slfTemp1 = 0;

   // end

   ArrayList<String> localStringVariableNameList = new ArrayList<>();

   ArrayList<Map<String, Object>> localVariableDetailsList = new ArrayList<>();

   ArrayList<Map<String, Object>> variableDetailsList = new ArrayList<>();

   String[] restrictedCacheManager = { "CompanyCacheManager", "RedisCacheManager", "EhcacheManager", "ServiceSessionCacheSharedService" };

   List<String> restrictedCacheManagerList = Arrays.asList(restrictedCacheManager);

   Map<String, Integer> stringConstantProperUseCheckMap = new HashMap<>();

   Map<String, Integer> stringConstantProperUseCheckMap2 = new HashMap<>();

   Map<String, String> bigDecimalCheckMap = new HashMap<>(); // map used in the process
   // of bigdecimal proper
   // usage
   // check

   List<String> globalCollectionList = Arrays.asList("List", "Set", "Map");

   List<String> globalPrimitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
      "Boolean", "Integer");

   int checkValue = 0;

   boolean surrogateKeyValueCheck = true;

   // private String tableNameWithoutMstOrTrn;
   boolean runCodeOnce = true;

   List<String> userList = new ArrayList<>();

   boolean implementedHueSerializable = true;

   boolean extendsApplicationContents = true;

   boolean isDataorSetterorGetter = false;

   boolean isGeneratedByCobalt = true;

   boolean isRdbDaoCheck = false;

   boolean isRdbDtoTableCheck = false;

   // int lineInFile = 0;
   List<TypeDeclaration> typeDeclarations;

   boolean isArgsConstructorUsed = false;

   int importLineNumber;

   String rdbTableNameWithoutDto;

   List<String> defaultDtoColumnsList = Arrays.asList("exclusive_control_version", "output_status", "input_user_id", "input_datetime",
      "lastupdate_user_id", "lastupdate_datetime");

   List<String> rdbColumnValues = new ArrayList<>();

   List<String> rdbReservedKeywordsList =

      Arrays.asList("from", "and", "or", "alter", "table", "as", "between", "where", "update", "union", "all", "truncate", "top", "select", "into",
         "distinct", "select", "like", "order by", "full join", "right join", "inner join", "left join", "insert into", "in", "create database",
         "create table", "create index", "create view", "drop database", "drop index", "delete", "drop table", "exists", "group by", "having");

   public OptimusJavaCodeReview(String filePath) throws ParseException, IOException {
      CompilationUnit compiledFile = compileFile(filePath);
      if (!this.currentFileName.endsWith("Config") && !this.currentFileName.endsWith("Util")) {
         check(compiledFile);
      }
   }

   public int getErrorCount() {
      return errorList.size();
   }

   public int getWarningCount() {
      return warningList.size();
   }

   public List<String> getErrorList() {
      return errorList;
   }

   public List<String> getWarningList() {
      return warningList;
   }

   public Map<String, Integer> getCountInfoMap() {
      Map<String, Integer> countInfoMap = new HashMap<>();
      countInfoMap.put("error", getErrorCount());
      countInfoMap.put("warning", getWarningCount());
      return countInfoMap;
   }

   private CompilationUnit compileFile(String filePath) throws ParseException, IOException {
      File file = new File(filePath);
      CompilationUnit compilationUnit = JavaParser.parse(file);
      currentFileName = file.getName().replace(".java", "");
      typeDeclarations = compilationUnit.getTypes().stream().collect(Collectors.toList());
      return compilationUnit;
   }

   private void check(CompilationUnit compiledFile) throws IOException {
      if (compiledFile.getPackageDeclaration().isPresent()) {
         projectPath = compiledFile.getPackageDeclaration().get().getNameAsString();
      }
      else {
         return;
      }

      checkPackage(compiledFile);
      // New check for no of lines in a class file.
      long temp = ((long) (compiledFile.getEnd().get().line)) - compiledFile.getBegin().get().line;
      // warningList.add("The No of Lines in this file :" + temp);
      if (temp > 1000) {
         warningList.add("No of lines of class exceeds 1000, so please consider this and check the class file has a single role.For queries"
            + " check the ticket 53393.|ExceedingLinesOfCode");
      }
      try {
         if (!this.currentFileName.endsWith("Dto") && !this.currentFileName.endsWith("Dao") && !this.currentFileName.endsWith("DaoImpl")) {
            new ImportVisitor().visit(compiledFile, projectPath);
         }
         new MethodVariableVisitor().visit(compiledFile, projectPath);
         new ClassVariableVisitor().visit(compiledFile, projectPath);

         if (this.currentFileName.endsWith("Dto") && isRdbDtoTableCheck && !rdbColumnValues.containsAll(defaultDtoColumnsList)) {
            List<String> usedDefaultColumnList = new ArrayList<>();
            defaultDtoColumnsList.stream().forEach(defaultColumn -> {
               if (rdbColumnValues.contains(defaultColumn)) {
                  usedDefaultColumnList.add(defaultColumn);
               }
            });
            StringBuilder missed = new StringBuilder();
            defaultDtoColumnsList.stream().forEach(val -> {
               if (!usedDefaultColumnList.contains(val)) {
                  missed.append(val);
                  missed.append(", ");
               }
            });
            errorList.add("In File, the mandatory annotation : " + missed.substring(0, missed.length() - 2) + " is missing in the rdb Dto" + "|"
               + BugCategory.TABLE_LAYOUT_ISSUE.toString());
         }

         new FileNameCheck().visit(compiledFile, projectPath);
         new MethodCheck().visit(compiledFile, projectPath);
         if (!isGeneratedByCobalt) {
            new BlockCommentVisitor().visit(compiledFile, projectPath);
         }
         new CatchLogCheck().visit(compiledFile, projectPath);

         checkVariables();
      }
      catch (UnsupportedOperationException exceptionObject) {
         errorList.add("Please remove the empty semicolon in the imports and in other places if found" + "|"
            + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
      }
      catch (Exception generalExceptionInstance) {
         // errorList.add("Cannot Find Bugs in File "
         // + currentFileName
         // + "due to "
         // + generalExceptionInstance.getMessage());
      }
      if (isSlfAndLogUsed && slfTemp1 != 1) {
         warningList.add("@Slf4j is declared, but if  logger is not used, please remove it" + "|" + BugCategory.UNNECESSARY_CODE_ISSUE.toString());
      }
   }

   /**
    * checkPackage method is used to check the package of the file is correct or not.
    * 
    * @param compilationUnit
    * @throws IOException
    */
   private void checkPackage(CompilationUnit compilationUnit) throws IOException {
      if (!compilationUnit.getPackageDeclaration().get().getNameAsString().startsWith("com.worksap.company")) {
         errorList.add("Line no:  " + compilationUnit.getPackageDeclaration().get().getBegin().get().line
            + " :  Package name should starts with com.worksap.company" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
      }
   }

   /**
    * checkVariables method is used to check the variable comments, case etc.
    * 
    * @throws IOException
    */
   private void checkVariables() throws IOException {
      ArrayList<Map<String, Object>> variableDetailsList = new ClassVariableVisitor().getVariableDetailsList();
      ArrayList<Map<String, Object>> localVariableDetailsList = new MethodVariableVisitor().getLocalVariableDetailsList();
      String camelCasePattern = "\\b[a-z]";
      Pattern pattern = Pattern.compile(camelCasePattern);
      Set<String> uniqueEntriesSet = new HashSet<>();

      List<String> collectionTypeList = Arrays.asList("List", "Set", "Map");
      List<String> variableNameList = variableDetailsList.stream()
         .filter(globalVar -> collectionTypeList.contains(globalVar.get("variableType")) || globalVar.get("variableType").toString().endsWith("]")
            || globalVar.get("variableType").toString().startsWith("Immutable"))
         .map(variable -> (String) variable.get("variableName")).collect(Collectors.toList());
      // call to get constructor information if data set for global collection fields is
      // immutable
      if (!(projectPath.contains(".dto") || projectPath.contains(".rdbdto") || projectPath.contains(".type") || projectPath.contains(".entity")
         || projectPath.contains(".enum") || projectPath.contains(".config")) && implementedHueSerializable && extendsApplicationContents
         && !isDataorSetterorGetter) {
         getConstructorInformation(typeDeclarations, variableNameList);
      }

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
         Boolean isGlobalArray = variableType.endsWith("]");

         checkVariableType(variableType, variableName, variableValue, lineNumber);

         if (!(currentFileName.endsWith("Dto") || currentFileName.endsWith("Index") || currentFileName.endsWith("Entity")
            || currentFileName.endsWith("Constant") || currentFileName.endsWith("Constants") || currentFileName.endsWith("Model")
            || currentFileName.endsWith("model") || currentFileName.endsWith("Type") || currentFileName.endsWith("type")
            || currentFileName.endsWith("enum") || currentFileName.endsWith("Enum") || currentFileName.endsWith("Arguments")
            || currentFileName.endsWith("arguments")) && implementedHueSerializable && extendsApplicationContents && !isDataorSetterorGetter) {
            // new check for global variables in a class:
            List<String> collectionList = Arrays.asList("List", "Set", "Map");
            List<String> primitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
               "Boolean", "Integer");
            // if ((isPrivate && isFinal) || (isPrivate && isGlobal) || isPrivate) {
            if (collectionList.contains(variableType)/*
                                                      * && !variableValue.startsWith( "Collections.unmodifiable")
                                                      */) {
               errorList.add("Line no:  " + lineNumber
                  + " : Collections are not allowed to set as a Global Variable. But if required, they should not be modifiable/editable throughout the class. \\n\"\n"
                  + "									+ \"Hence, collection fields should be set as immutable using 'ImmutableList<>/ImmutableSet<>/ImmutableMap<>..' as required."
                  + "|" + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
               checkUseOfArgsConstructorWhenGlobalCollectionField(lineNumber);
               if (variableValue != null && variableValue != "" && !variableValue.startsWith("Immutable")) {
                  if (variableValue.startsWith("Arrays.asList")) {
                     errorList.add("Line no: " + lineNumber
                        + " : When adding new elements to a list that is global, use 'ImmutableList.of()' to make it immutable. \n"
                        + " [Note: If a wrapper class for the list is available for your license group, kindly use it.  Ex: ImmutableListWrapper ]"
                        + "|" + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
                  }
                  else {
                     errorList.add("Line no:  " + lineNumber
                        + " : When adding new elements to a collection use 'ImmutableList.of()/ImmutableSet.of()/ImmutableMap.of()' to make it immutable. \n"
                        + " When assigning a collection type value use the 'copyOf()' method of the respective Immutable collection class. \n"
                        + " [Note: If a wrapper class for the collection type is available for your license group, kindly use it.  Ex: ImmutableListWrapper.copyOf() ]"
                        + "|" + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
                  }
               }
            }
            else if (((isPrivate && isFinal) || isPrivate) && primitiveAndWrapperList.contains(variableType) && !isGlobal) {
               // errorList
               // .add("Line no: "
               // + lineNumber
               // + " : If the '"
               // + variableType
               // + "' is used as a constant then it should be private static final "
               // + "or else it is considered as a global variable and are not
               // allowed|InvalidClassFields");
               // (variableType.equals("String")||variableType.equals("int")||variableType.equalsIgnoreCase("long")||
               // variableType.equals("double")||variableType.equals("short")||variableType.equalsIgnoreCase("boolean")||variableType.equals("Integer"))
            }

         }
         else if (/*
                   * ((isPrivate && isFinal) || isPrivate) &&
                   */ isGlobalArray) {
            errorList.add("Line no : " + lineNumber + " : Do not declare an array as global, instead change to ImmutableList. \n"
               + "When array type required for operational logic, change immutableList back to array as below. \n"
               + "Ex : immutableListObject.stream().toArray(String[]::new)" + "|" + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
            checkUseOfArgsConstructorWhenGlobalCollectionField(lineNumber);
         }

         if (variableType.equals("ServletContext") || variableType.equals("HttpServletRequest") || variableType.equals("HttpServletResponse")
            || variableType.equals("GsonBuilder")) {
            if (variableType.equals("ServletContext")) {
               errorList.add("Line no:  " + lineNumber + " : Don't declare " + variableType
                  + " as global instance. Avoid because this instance field should not be mutable. \n"
                  + " Try to get the servletContext from HttpServletRequest as : request.getServletContext()" + "|"
                  + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
            }
            else if (variableType.equals("HttpServletRequest") || variableType.equals("HttpServletResponse")) {
               errorList.add("Line no:  " + lineNumber + " : Don't declare " + variableType
                  + " as global instance. Avoid because these instance fields should not be mutable. \n"
                  + " Instead, for request and response, you can simply declare as arguments to controller methods." + "|"
                  + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
            }
            else {
               errorList.add("Line no:  " + lineNumber + " : Don't declare " + variableType
                  + " as global instance. Avoid because this instance field should not be mutable. \n"
                  + " Instead initialize the builder in a private method and call the method to get the initialized data" + "|"
                  + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
            }
         }
         if (variableType.equals("boolean") || variableType.equals("Boolean")) {
            errorList.add("Line no:  " + lineNumber + " :  no need to declare boolean in constants instead use directly" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         // ends here

         // new check for currentVersion in a class:
         List<String> currentVersionList = Arrays.asList("CURRENT_VERSION");
         if ((currentFileName.endsWith("Dto") || currentFileName.endsWith("Index") || currentFileName.endsWith("Entity")
            || currentFileName.endsWith("IndexEntity")) && currentVersionList.contains(variableName)) {
            errorList.add("Line no:  " + lineNumber + " :  The variable '" + variableName + "' is not needed in dto or index or entity file." + "|"
               + BugCategory.TABLE_LAYOUT_ISSUE.toString());
            // ends here
         }
         else {
            if (isGlobal) {
               if (!variableName.replace("_", "").equals(variableName.replace("_", "").toUpperCase())) {
                  warningList.add("Line no:  " + lineNumber + " :  static final variable '" + variableName + "' should be in UPPER_CASE" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
               if (variableName.length() < 3) {
                  errorList.add("Line no:  " + lineNumber + " :  variable name should be minimum 3 characters in length" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
               if (!uniqueEntriesSet.add(variableValue)) {
                  warningList.add("Line no:  " + lineNumber + " :  Duplicate constant variable with same value '" + variableName + "'" + "|"
                     + BugCategory.DUPLICATION_ISSUE.toString());
               }
            }
            else {
               Matcher matcher = pattern.matcher(variableName);
               if (!matcher.find()) {
                  errorList.add("Line no:  " + lineNumber + " :  Variable name should be in lowerCamelCase" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
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
                  errorList.add("Line no:  " + lineNumber + " :  Comment is missing for the variable '" + variableName + "'" + "|"
                     + BugCategory.DOCS_ISSUE.toString());
               }
            }
            if (variableType.equals("String") && variableValue.equals("\"\"")) {
               errorList.add("Line no:  " + lineNumber + " :  Dont use Constant for empty string. Use org.apache.commons.lang3.StringUtils.EMPTY"
                  + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
            }
            if (variableType.equals("KeyValueAccess") && variableName.equals("kva")) {
               errorList
                  .add("Line no:  " + lineNumber + " :  Variable name should not be kva" + "|" + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
            }
            if (!this.currentFileName.endsWith("Constant") && !this.currentFileName.endsWith("Constants") && !isPrivate && !isProtected) {
               // errorList.add("Line no: "
               // + lineNumber
               // + " : Global variable should be private.|InvalidClassFields");
            }
            if (this.currentFileName.endsWith("Impl") || this.currentFileName.endsWith("Controller")) {
               if (!isFinal && !globalCollectionList.contains(variableType) && !globalPrimitiveAndWrapperList.contains(variableType)) {
                  // errorList.add("Line no: " + lineNumber + " : Bean should be
                  // final.|InvalidClassFields");
               }
            }
         } // current version else check ends here

         // RDB - to check use of LocalDate, ZonedDateTime and UUID fields in RDB Dto
         // if (currentFileName.endsWith("Dto") && projectPath.contains("dto") &&
         // projectPath.contains("rdb")) {
         if (currentFileName.endsWith("Dto") && isRdbDtoTableCheck) {
            if (variableType.equals("LocalDate")) {
               errorList.add("Line no: " + lineNumber + " :  Declaration " + ": In rdb dto, do not use \"LocalDate\", instead use \"java.sql.Date\""
                  + "|" + BugCategory.TABLE_CONVENTION_VIOLATION_ISSUE.toString());
            }
            if (variableType.equals("ZonedDateTime")) {
               errorList.add(
                  "Line no: " + lineNumber + " :  Declaration " + ": In rdb dto, do not use \"ZonedDateTime\", instead use \"java.sql.Timestamp\""
                     + "|" + BugCategory.TABLE_CONVENTION_VIOLATION_ISSUE.toString());
            }
            if (variableType.equals("UUID")) {
               errorList.add("Line no: " + lineNumber + " :  Declaration " + ": In rdb dto, migrate from type \"UUID\" to \"String\"" + "|"
                  + BugCategory.TABLE_CONVENTION_VIOLATION_ISSUE.toString());
            }
         }
      });

      localVariableDetailsList.stream().forEach(variable -> {
         String variableType = (String) variable.get("variableType");
         String variableName = (String) variable.get("variableName");
         String variableValue = (String) variable.get("variableValue");
         String lineNumber = (String) variable.get("line");
         boolean isFinal = (boolean) variable.get("isFinal");
         Matcher matcher = pattern.matcher(variableName);
         if (!matcher.find()) {
            errorList.add("Line no:  " + lineNumber + " :  Variable name should be in lowerCamelCase" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
         if (variableType.equals("String") && variableValue.equals("\"\"")) {
            errorList.add("Line no:  " + lineNumber + " :  Use org.apache.commons.lang3.StringUtils.EMPTY for Empty String" + "|"
               + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
         }
         if (isFinal) {
            warningList.add("Line no:  " + lineNumber + " :  Local variable should not be final variable" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         if (variableName.length() < 3) {
            errorList.add("Line no:  " + lineNumber + " :  variable should be minimum 3 characters in length" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
         checkVariableType(variableType, variableName, variableValue, lineNumber);
      });

      // //newly added to check proper constant usage
      // stringConstantProperUseCheckMap2
      // .entrySet()
      // .stream()
      // .filter(action -> action.getValue().equals(1))
      // .forEach(
      // result ->
      // warningList.add("String constant variable : '"
      // + result.getKey()
      // + "' is used only once, "
      // + "so don't put in constants(if this constant is used in a condition check
      // please ignore
      // this)|ConstantIssue")
      // );
      // ends here

   }

   /**
    * checkVariableType method is used to check the variable name and use of restricted classes.
    * 
    * @param variableType
    * @param variableName
    * @param variableValue
    * @param lineNumber
    * @throws IOException
    */
   private void checkVariableType(String variableType, String variableName, String variableValue, String lineNumber) {
      if (restrictedCacheManagerList.contains(variableType)) {
         errorList.add(
            "Line no:  " + lineNumber + " :  Use of '" + variableType + "' is restricted" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
      }
      else if (variableType.endsWith("List")) {
         if (!variableName.endsWith("List") && !variableName.endsWith("LIST") && !variableName.endsWith("Lists") && !variableName.endsWith("LISTS")
            && !variableName.endsWith("s") && !variableName.endsWith("S")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable '" + variableName + "' is not ending with List" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
         if (variableValue.equals("Lists.newArrayList()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new ArrayList() instead" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
      }
      else if (variableType.endsWith("Map")) {
         if (!variableName.endsWith("Map") && !variableName.endsWith("MAP") && !variableName.endsWith("s") && !variableName.endsWith("S")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Map" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
         if (variableValue.equals("Maps.newHashMap()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new HashMap() instead" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
      }
      else if (variableType.endsWith("Set")) {
         if (!variableName.endsWith("Set") && !variableName.endsWith("SET") && !variableName.endsWith("s") && !variableName.endsWith("S")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Set" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
         if (variableValue.equals("Sets.newHashSet()")) {
            errorList.add("Line no:  " + lineNumber + " :  '" + variableValue + "' should not be used. Use new Set() instead" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
      }
      else if (variableType.endsWith("Queue")) {
         if (!variableName.endsWith("Queue") && !variableName.endsWith("QUEUE") && !variableName.endsWith("s") && !variableName.endsWith("S")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Queue" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
      }
      else if (variableType.endsWith("Deque")) {
         if (!variableName.endsWith("Deque") && !variableName.endsWith("DEQUE") && !variableName.endsWith("s") && !variableName.endsWith("S")) {
            errorList.add("Line no:  " + lineNumber + " :  Variable Name '" + variableName + "' is not ending with Deque" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }
      }
   }

   /**
    * FileNameCheck class is used to check the file name, class docs and annotations used in the file.
    */
   private class FileNameCheck extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(ClassOrInterfaceDeclaration declaration, Object path) {
         checkClassName(declaration, path);

         if (declaration.getJavadocComment().isPresent()) {
            String classDoc = declaration.getJavadocComment().get().toString();
            if (!classDoc.contains("generated by Cobalt Tool.") || !classDoc.contains("Cortex")) {
               isGeneratedByCobalt = false;
               checkClassDocs(declaration);
            }
         }

         checkAnnotationUsed(declaration, path);
      }

      private void checkClassName(ClassOrInterfaceDeclaration declaration, Object path) {
         String projectPath = (String) path;
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
                  + " :  Class name of the Constant file should ends with Constant or Constants" + "|" + BugCategory.FILE_NAMING_ISSUE.toString());
            }
         }
         else if (projectPath.contains("service")) {
            if (declaration.isInterface()) {
               if (!declaration.getName().asString().endsWith("Service")) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Interface name should ends with Service" + "|"
                     + BugCategory.FILE_NAMING_ISSUE.toString());
               }
            }
            else {
               if (!projectPath.endsWith(".constant") && !(declaration.getImplementedTypes().toString().contains("Service")
                  && declaration.getName().asString().endsWith("ServiceImpl"))) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line
                     + " :  Class name should ends with ServiceImpl and implemented interface name should ends with Service" + "|"
                     + BugCategory.FILE_NAMING_ISSUE.toString());
               }
            }
         }
         else if (projectPath.contains("dao")) {
            if (declaration.isInterface()) {
               if (!declaration.getName().asString().endsWith("Dao")) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Interface name should ends with Dao" + "|"
                     + BugCategory.FILE_NAMING_ISSUE.toString());
               }
            }
            else {
               if (!projectPath.endsWith(".constant")
                  && !(declaration.getImplementedTypes().toString().contains("Dao") && declaration.getName().asString().endsWith("DaoImpl"))) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line
                     + " :  Class name should ends with DaoImpl and implemented interface name should ends with Dao" + "|"
                     + BugCategory.FILE_NAMING_ISSUE.toString());
               }
            }
         }
         else if (projectPath.contains("controller")) {
            if (declaration.getExtendedTypes().toString().contains("Controller") && !declaration.getName().asString().endsWith("Controller")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Controller name should ends with Controller" + "|"
                  + BugCategory.FILE_NAMING_ISSUE.toString());
            }
         }
         else if (projectPath.contains("dto")) {
            if (!declaration.getName().asString().endsWith("Dto") && !declaration.getName().asString().endsWith("Index")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Dto and Index name should ends with Dto,Index respectively" + "|"
                  + BugCategory.FILE_NAMING_ISSUE.toString());
            }
         }
         else if (projectPath.contains("type")) {
            if (!declaration.getImplementedTypes().toString().contains("HueSerializable")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  HueSerializable must be implemented in Model and Beans" + "|"
                  + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
         }
      }

      /**
       * checkClassDocs is used to check the class docs.
       * 
       * @param declaration
       */
      private void checkClassDocs(ClassOrInterfaceDeclaration declaration) {
         List<Comment> commentsList = declaration.getAllContainedComments();
         commentsList.stream().filter(Comment::isLineComment).forEach(comment -> errorList.add("Line no:  " + comment.getBegin().get().line
            + " :  Line comment is not allowed" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString()));

         if (declaration.getJavadocComment().isPresent()) {
            String comment = declaration.getJavadocComment().get().toString();
            if (!comment.replaceAll("\\*", "").replaceAll("/", "").trim().startsWith(declaration.getName().asString())) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Comments should only start with Class Name '"
                  + declaration.getName() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
            }
            if (!comment.contains("@author")) {
               errorList.add(
                  "Line no:  " + declaration.getBegin().get().line + " :  Missing @author in the comments" + "|" + BugCategory.DOCS_ISSUE.toString());
            }
            if (!comment.contains("@since")) {
               errorList.add(
                  "Line no:  " + declaration.getBegin().get().line + " :  Missing @since in the comments" + "|" + BugCategory.DOCS_ISSUE.toString());
            }

         }

         else {
            errorList.add(
               "Line no:  " + declaration.getBegin().get().line + " :  No Comments added for the Class" + "|" + BugCategory.DOCS_ISSUE.toString());
         }
      }

      /**
       * checkAnnotaionUsed is used to check the annotations used.
       * 
       * @param declaration
       * @param packagePath
       * @throws IOException
       */
      private void checkAnnotationUsed(ClassOrInterfaceDeclaration declaration, Object packagePath) {
         String packageName = (String) packagePath;
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

         checkForAutoIndexAnnotation(declaration, packageName);
         if (packageName.contains("dto") && (declaration.getName().asString().endsWith("Dto") || declaration.getName().asString().endsWith("Index"))
            && !annotationsUsedList.contains("@Entity")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  @Entity is Mandatory for Dto and Index files" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         if (!packageName.contains("dto") && !packageName.contains("dao") && annotationsUsedList.contains("@Entity")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  @Entity should not be used other than Dto,Index and DaoImpl" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         if (declaration.getName().asString().endsWith("Impl") && annotationsUsedList.contains("@AllArgsConstructor")) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Use @RequiredArgsConstructor instead of @AllArgsConstructor" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         // check for slf4j
         // start
         if (annotationsUsedList.contains("@Slf4j")) {
            isSlfAndLogUsed = true;
         }
         // end

         checkRdbDtoTableName(declaration, packagePath);
      }

   }

   /**
    * checkRdbDtoTableName method is used to check whether the rdb table name ends with _mst or _trn in rdb
    * 
    * @param declaration
    * @param packagePath
    */
   private void checkRdbDtoTableName(ClassOrInterfaceDeclaration declaration, Object packagePath) {
      // String packageName = (String) packagePath;
      // if (packageName.contains("dto") && packageName.contains("rdb") &&
      // (declaration.getName().endsWith("Dto"))){
      if (declaration.getNameAsString().endsWith("Dto") && isRdbDtoTableCheck) {
         // regex used to convert class name to lower_camel_case excluding keyword
         // "Dto", ex: JavaCodeReviewDto ->
         // java_code_review
         String classNameRegex = "([A-Z][a-z]+)";
         String snakeCaseReplacement = "$1_";
         String tableNameInLowerSnakeCase = declaration.getNameAsString().split("Dto")[0].replaceAll(classNameRegex, snakeCaseReplacement)
            .toLowerCase();
         rdbTableNameWithoutDto = tableNameInLowerSnakeCase.substring(0, tableNameInLowerSnakeCase.lastIndexOf("_"));
         declaration.getAnnotations().forEach(usedAnnotation -> {
            if (usedAnnotation.getName().toString().equals("Table")) {
               NormalAnnotationExpr annoExpr = (NormalAnnotationExpr) usedAnnotation;
               annoExpr.getPairs().stream().forEach(pair -> {
                  if (pair.getNameAsString().equals("name")) {
                     String tableNameValue = pair.getValue().toString().replaceAll("\"", "");
                     // System.out.println("_mst or _trn");
                     // System.out.println("tableNameValue : "+tableNameValue);
                     // System.out.println("rdbTableNameWithoutDto :
                     // "+rdbTableNameWithoutDto);
                     // if (tableNameValue.equals(rdbTableNameWithoutDto+"_mst") ||
                     // tableNameValue.equals(rdbTableNameWithoutDto+"_trn")) {
                     if (tableNameValue.contains(rdbTableNameWithoutDto) && (tableNameValue.endsWith("_mst") || tableNameValue.endsWith("_trn"))) {
                     }
                     else {
                        errorList.add("Line no: " + usedAnnotation.getBegin().get().line
                           + " :  In @Table annotation, table name should be lower_snake_case of dto name(excluding \"Dto\") and must end with either \"_mst\" or \"_trn\" appended to it."
                           + "|" + BugCategory.TABLE_NAMING_ISSUE.toString());
                     }
                  }
               });
            }
         });
      }
   }

   /**
    * checkForMandatoryFields method is used to check whether the mandatory fields in the dto annotation is present or
    * not in rdb table
    * 
    * @param declaration
    * @param path
    */
   private void checkForMandatoryFields(FieldDeclaration declaration, Object path) {
      // if (path.toString().contains("rdb") && (path.toString().contains("dto")) &&
      // currentFileName.endsWith("Dto"))
      // {
      if (currentFileName.endsWith("Dto") && isRdbDtoTableCheck) {
         if (declaration != null) {
            declaration.getAnnotations().stream().forEach(annot -> {
               if ((annot.getNameAsString()).equals("Column")) {
                  NormalAnnotationExpr annoExpression = (NormalAnnotationExpr) annot;
                  annoExpression.getPairs().stream().forEach(param -> {
                     if (param.getName().equals("name")) {
                        String value = param.getValue().toString().trim().replace("\"", "");
                        rdbColumnValues.add(value);
                     }
                  });
               }
            });
         }
      }
      // System.out.println("rdbColumnValues --> "+rdbColumnValues);
   }

   /**
    * checkForAutoIndexAnnotation method is used to check whether the @AutoIndex annotation use is removed in rdb
    * 
    * @param declaration
    * @param path
    */
   private void checkForAutoIndexAnnotation(ClassOrInterfaceDeclaration declaration, String path) {
      // if ((path.contains("rdb") && (path.contains("dto"))
      // &&(declaration.getName().endsWith("Dto"))) ||
      // currentFileName.endsWith("DocSource") ) {
      removeContainedComments.setPrintComments(false);
      if ((declaration.getNameAsString().endsWith("Dto") && isRdbDtoTableCheck) || currentFileName.endsWith("DocSource")) {
         if (declaration != null) {
            declaration.getAnnotations().stream().forEach(action -> {
               if (action.toString(removeContainedComments).equals("@AutoIndex")) {
                  errorList.add("Line no:  " + action.getBegin().get().line + " : Declaration"
                     + " :  Please remove @AutoIndex, instead use @IndexDefinition in rdb" + "|"
                     + BugCategory.TABLE_CONVENTION_VIOLATION_ISSUE.toString());
               }
            });
         }
      }

   }

   /**
    * ClassVariableVisitor contains the method to check the global variables.
    */
   private class ClassVariableVisitor extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(FieldDeclaration declaration, Object path) {
         removeContainedComments.setPrintComments(false);
         getVariableDetails(declaration);
         checkForMandatoryFields(declaration, path);
         declaration.getAnnotations().forEach(annotation -> {
            if (annotation.toString(removeContainedComments).equals("@Autowired")) {
               errorList.add("Line no:  " + annotation.getBegin().get().line + " : Declaration"
                  + " :  Please remove @Autowired, use @RequiredArgsConstructor(onConstructor = @__(@Autowired))" + "|"
                  + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
         });

         // RDB - check surrogate key naming whether of format --> {table_name}_id
         // if (currentFileName.endsWith("Dto") && projectPath.contains("dto") &&
         // projectPath.contains("rdb")) {
         if (currentFileName.endsWith("Dto") && isRdbDtoTableCheck) {
            NormalAnnotationExpr annotationExpr = (NormalAnnotationExpr) declaration.getAnnotations().stream()
               .filter(predicate -> predicate.toString(removeContainedComments).contains("@Column")
                  && predicate.toString(removeContainedComments).contains("name"))
               .findFirst().get();
            String regex = "([A-Z][a-z]+)";
            String replacement = "$1_";
            String fileNameInLowerCase = currentFileName.split("Dto")[0].replaceAll(regex, replacement).toLowerCase();
            if (runCodeOnce) {
               // String expectedKeyWithIdMst = fileNameInLowerCase + "mst_id";
               // String expectedKeyWithIdTrn = fileNameInLowerCase + "trn_id";

               annotationExpr.getPairs().stream().forEach(pair -> {
                  if (pair.getNameAsString().equals("name")) {
                     String surrogateKeyName = pair.getValue().toString().replaceAll("\"", "");
                     // System.out.println("_mst_id or _trn_id");
                     // System.out.println("surrogateKeyName : "+surrogateKeyName);
                     // System.out.println("fileNameInLowerCase :
                     // "+fileNameInLowerCase);
                     // if(expectedKeyWithIdMst.equals(surrogateKeyName) ||
                     // expectedKeyWithIdTrn.equals(surrogateKeyName)){
                     if (surrogateKeyName.contains(fileNameInLowerCase)
                        && (surrogateKeyName.endsWith("_mst_id") || surrogateKeyName.endsWith("_trn_id"))) {
                     }
                     else {
                        errorList.add("Line no:  " + annotationExpr.getBegin().get().line + " : Declaration"
                           + " : First column in Table is a surrogateKey. The surrogate keys name should be {table_name}+\"_id\"" + "|"
                           + BugCategory.TABLE_NAMING_ISSUE.toString());
                     }
                  }
               });
               runCodeOnce = false;
            }
         }

      }

      /**
       * getVariableDetails is used to check the variables used inside the class outside the method.
       * 
       * @param declaration
       * @throws IOException
       */
      private void getVariableDetails(FieldDeclaration declaration) {
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

      private ArrayList<Map<String, Object>> getVariableDetailsList() {
         return variableDetailsList;
      }

   }

   /**
    * MethodCheck class contains the method to check the method log, docs and restricted use.
    */
   private class MethodCheck extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(MethodDeclaration declaration, Object path) {
         String camelCasePattern = "\\b[a-z]";
         Pattern pattern = Pattern.compile(camelCasePattern);
         Matcher matcher = pattern.matcher(declaration.getName().asString().trim());
         if (!matcher.find()) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Method name should be in camelCase" + "|"
               + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
         }

         if (currentFileName.endsWith("ServiceImpl") && !declaration.getAnnotations().toString().contains("@Override") && !declaration.isPrivate()) {
            errorList.add("Line no:  " + declaration.getBegin().get().line + ": '" + declaration.getName()
               + "' Function should be private other than overrided method" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }

         // The rule no more exists.
         // checkMethodLog(declaration,path);
         // checkForReflectionUsage(declaration);
         // if(String.valueOf(path).endsWith("Controller")){
         // }

         checkForSuppressWarnings(declaration);
         checkForKeyValueMethodUsage(declaration, path);
         if (!isGeneratedByCobalt) {
            checkMethodDocs(declaration);
         }
         checkMethodNameForIndexEntity(declaration);
         checkForRestrictedUsage(declaration);
         checkForStringAddition(declaration);
         checkForMethodParameterCount(declaration);
         checkForMethodParameterTypeAndName(declaration);
         checkLimitUsageInStream(declaration, path);

         checkUseBetweenStatement(declaration, path);
         checkForFilterCondition(declaration, path);
         if (declaration.getBody() != null) {
            // value of no of lines in a method
            int noOfLines = declaration.getEnd().get().line - declaration.getBegin().get().line;
            // line no of the method
            int beginLine = declaration.getBegin().get().line;
            // name of the method
            String methodName = declaration.getName().asString();
            checkForThreadUsage(declaration);
            checkForNoOfLinesInaMethod(noOfLines, beginLine, methodName);
            checkForProperBigDecimalUsage(declaration);
            checkForCasting(declaration);
            checkForReservedKeyWordUseRDB(declaration, path);
            checkForquerySingle(declaration, path);
            checkForOnDuplicateKeyAndExecuteAndReturnLastGeneratedKey(declaration, path);
            checkForResultSetGetUsingIndexRDB(declaration, path);
            OptimusJavaCodeReviewPartOne partOne = new OptimusJavaCodeReviewPartOne();
            if (String.valueOf(path).contains("controller")) {
               partOne.checkForCSRFAttacks(declaration, warningList);
            }
            partOne.checkForNullpointerExceptionInCatchBlock(declaration, errorList);
            partOne.checkForOptionalMethods(declaration, errorList);
            partOne.checkForZoneIdsystemDefault(declaration, errorList);
            partOne.checkForLocale(declaration, errorList);
            // JavaCodeReviewPartOne.checkForNullpointerExceptionInCatchBlock(declaration,
            // errorList);
         }

      }

      /**
       * checkForResultSetGetUsingIndexRDB is used to check if index is used for ResultSet in rdb.
       * 
       * @param declaration
       * @param path
       */
      private void checkForResultSetGetUsingIndexRDB(MethodDeclaration declaration, Object path) {
         if (String.valueOf(path).contains("dao") && isRdbDaoCheck) {
            for (Statement eachStatement : declaration.getBody().get().getStatements()) {
               if (eachStatement.toString().startsWith("ResultSetExtractor")) {
                  String statementString = eachStatement.toString().trim().replaceAll("\\s", "");
                  String resultSetValueAssigned = statementString.split("=")[1];
                  boolean isResultSetAliasPresent = resultSetValueAssigned.contains("->");

                  if (isResultSetAliasPresent) {
                     String reference = resultSetValueAssigned.split("->")[0];
                     reference = reference.replace("(", "");
                     reference = reference.replace(")", "");
                     String resultSetReferenceName = reference;

                     if (statementString.contains(resultSetReferenceName + ".get")) {
                        // Pattern patternFormat =
                        // Pattern.compile(resultSetReferenceName + ".get" +
                        // "[a-zA-Z]*[0-9]*" + "[(]" + "[0-9]+");
                        // Matcher matcherPattern =
                        // patternFormat.matcher(statementString);
                        // if (matcherPattern.find()){
                        warningList.add("Line No: " + eachStatement.getBegin().get().line + " : Method: " + declaration.getName()
                           + "()  Please Do Not use " + resultSetReferenceName + ".get(index). For Maintainability, use " + resultSetReferenceName
                           + ".get(\"column_id\")." + "|" + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
                        // }
                     }
                  }
               }
            }
         }
      }

      /**
       * checkForquerySingle is used to suggest the use of querySingle instead of using stream and findFirst in rdb
       * 
       * @param declaration
       * @param path
       */
      private void checkForquerySingle(MethodDeclaration declaration, Object path) {
         if (String.valueOf(path).contains("dao") && isRdbDaoCheck) {
            // if (declaration.getBody() != null) {
            declaration.getBody().get().getStatements().stream().forEach(action -> {
               if (action.toString().contains(".select")
                  && (action.toString().contains("queryList()") && (action.toString().contains(".stream().findFirst()")))) {
                  errorList.add("Line no:  " + action.getBegin().get().line + ": " + " Method : " + declaration.getNameAsString()
                     + " :  Instead of queryList().stream().findFirst() use querySingle()" + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
               }
            });
            // }
         }
      }

      /**
       * checkForOnDuplicateKeyAndExecuteAndReturnLastGeneratedKey is used to check whether both the methods are used
       * simultaneously in a query in rdb.
       * 
       * @param declaration
       * @param path
       */
      private void checkForOnDuplicateKeyAndExecuteAndReturnLastGeneratedKey(MethodDeclaration declaration, Object path) {
         if (String.valueOf(path).contains("dao") && isRdbDaoCheck) {
            declaration.getBody().get().getStatements().stream().forEach(action -> {
               if (action.toString().contains("onDuplicateKey") && (action.toString().contains("executeAndReturnLastGeneratedKey"))) {
                  errorList.add("Line no:  " + action.getBegin().get().line + " : " + " Method: " + declaration.getNameAsString()
                     + "()  Found the use of methods \"onDuplicateKey/onDuplicateKeyWithSameValues\" and \"executeAndReturnLastGeneratedKey\" at same query. Provide any one method. \n"
                     + "  When using the \"onDuplicateKey\" methods' use execute() only. Else, provide the insert and update operations as separate methods."
                     + "|" + BugCategory.TABLE_CONVENTION_VIOLATION_ISSUE.toString());
               }
            });
         }
      }

      // for rdb usage

      /**
       * checkForReservedKeyWordUseRDB is used to check the rdb reserved keyword cases.
       * 
       * @param declaration
       */
      private void checkForReservedKeyWordUseRDB(MethodDeclaration declaration, Object path) {
         if (String.valueOf(path).contains("dao")) {
            if (declaration != null) {
               declaration.getBody().get().getStatements().stream().forEach(action -> {
                  if (action.toString().startsWith("select")) {
                     String[] viswa = action.toString().split(" ");
                     Arrays.stream(viswa).forEach(actionone -> {
                        rdbReservedKeywordsList.stream().forEach(keyWord -> {
                           if (actionone.equals(keyWord)) {
                              errorList.add("Line No : " + action.getBegin().get().line + " : In method " + declaration.getNameAsString()
                                 + "() found the keyword - \"" + keyWord + "\". Provide reserved keywords in UPPERCASE" + "|"
                                 + BugCategory.TABLE_NAMING_ISSUE.toString());
                           }
                        });
                     });
                  }
               });
            }
         }
      }

      /**
       * checkUseBetweenStatement which suggest to use the between condition in the rdb query to validate the date
       * instead of using filter condition in rdb
       * 
       * @param declaration
       * @param path
       */
      private void checkUseBetweenStatement(MethodDeclaration declaration, Object path) {
         if (isRdbDaoCheck) {
            if (path.toString().contains("dao")) {
               declaration.getBody().get().getStatements().forEach(action -> {
                  if (action.toString().contains("DateTimeUtils.between")) {
                     errorList.add("Line No : " + action.getBegin().get().line + " : Method : " + declaration.getName() + " : "
                        + "Avoid using the filter condition to check the start date and end date, instead use 'between' condition in RDB query to validate the date"
                        + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
                  }
               });
            }
         }
      }

      /**
       * checkForFilterCondition which suggest to use the 'eq' condition in the query instead of using the filter
       * condition in rdb
       * 
       * @param declaration
       * @param path
       */
      private void checkForFilterCondition(MethodDeclaration declaration, Object path) {
         if (isRdbDaoCheck) {
            if (path.toString().contains("dao")) {
               declaration.getBody().get().getStatements().forEach(action -> {
                  if (action.toString().contains(".stream") && action.toString().contains(".filter")) {
                     errorList.add("Line No : " + action.getBegin().get().line + " : Method : " + declaration.getName() + ": "
                        + "Avoid using filter() to filter the data obtained from db, instead filter data by providing a 'eq()' condition in the access query"
                        + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
                  }
               });
            }

         }
      }

      /**
       * checkLimitUsageInStream is used to check whether the limit is used for the stream operation
       * 
       * @param declaration
       * @param path
       */
      private void checkLimitUsageInStream(MethodDeclaration declaration, Object path) {
         if (isRdbDaoCheck) {
            if (path.toString().contains("dao")) {
               declaration.getBody().get().getStatements().forEach(action -> {
                  if (action.toString().contains("stream") && action.toString().contains("select")) {
                     int checktwo = action.toString().indexOf(".stream");
                     if (action.toString().substring(checktwo).contains(".limit")) {
                        errorList.add("Line No : " + declaration.getBegin().get().line + " : Method " + " " + ":" + declaration.getName() + ":" + " "
                           + "Avoid set the limit for Stream instead of that set the limit for select" + "|"
                           + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
                     }

                  }
               });
            }
         }
      }

      private void checkForThreadUsage(MethodDeclaration declaration) {
         removeContainedComments.setPrintComments(false);
         if (declaration.toString(removeContainedComments).contains("Thread")) {
            // usage of thread is not safe.
            errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Usage of Thread found" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
      }

      private void checkForSuppressWarnings(MethodDeclaration declaration) {
         declaration.getAnnotations().stream().filter(input -> input.getNameAsString().startsWith("Suppress")).forEach(data -> {
            errorList.add("Line No  :" + data.getBegin().get().line + " : Try not to use " + data.getNameAsString() + ", fix them" + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         });
      }

      /**
       * checkForKeyValueMethodUsage is used to check the correct usage of KeyValueAccess methods in daoImpl files.
       * 
       * @param declaration
       * @param path
       */
      private void checkForKeyValueMethodUsage(MethodDeclaration declaration, Object path) {
         removeContainedComments.setPrintComments(false);
         if (String.valueOf(path).contains("dao")) {
            if (declaration.toString(removeContainedComments).contains(".search(")
               || declaration.toString(removeContainedComments).contains(".searchAll(")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Remove search, searchAll functions in the method '"
                  + declaration.getName() + "'. Use searchWithIterator() instead" + "|" + BugCategory.OBJECT_OUT_OF_MEMORY_ISSUE.toString());
            }
            if (declaration.toString(removeContainedComments).contains("SearchConditions.ALL()")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Don't use SearchConditions.ALL()" + "|"
                  + BugCategory.OBJECT_OUT_OF_MEMORY_ISSUE.toString());
            }

            // new check for searchAllWithIterator.
            // start
            if (declaration.toString(removeContainedComments).contains("searchAllWithIterator(")) {
               warningList.add("Line no:  " + declaration.getBegin().get().line
                  + " :searchAllWithIterator found.Please get confirmation from wap side before using it or consult CR team" + "|"
                  + BugCategory.OBJECT_OUT_OF_MEMORY_ISSUE.toString());
            }
            // end

            if (declaration.toString(removeContainedComments).contains("IteratorUtils.toList")) {
               // usage of IteratorUtils.toList is not allowed.
               errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Don't use IteratorUtils.toList" + "|"
                  + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
         }
      }

      /**
       * checkForMethodParameterTypeAndName method is used to check the casting in codes
       * 
       * @param declaration
       */
      private void checkForMethodParameterTypeAndName(MethodDeclaration declaration) {
         removeContainedComments.setPrintComments(false);
         String camelCasePattern = "\\b[a-z]";
         Pattern pattern = Pattern.compile(camelCasePattern);
         declaration.getParameters().forEach(param -> {
            String paramType = param.getType().toString(removeContainedComments);
            if (paramType.equals("HttpServletRequest") || paramType.equals("HttpServletResponse")) {
               warningList.add("Line no:  " + declaration.getBegin().get().line + ": Should not use HttpServletRequest , HttpServletResponse" + "|"
                  + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
            if (paramType.contains("<")) {
               paramType = paramType.split("<")[0];
            }
            String paramName = param.getNameAsString();
            int lineNumber = declaration.getBegin().get().line;

            // method parameters check for camelCase updated
            Matcher match = pattern.matcher(paramName);
            if (!match.find()) {
               errorList.add("Line no:  " + param.getBegin().get().line + " :  Parameter name should be in lowerCamelCase" + "|"
                  + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
            }
            if (paramType.endsWith("List")) {
               if (!paramName.endsWith("List") && !paramName.endsWith("LIST") && !paramName.endsWith("Lists") && !paramName.endsWith("LISTS")) {
                  errorList.add("Line no:  " + lineNumber + " :  Parameter '" + paramName + "' is not ending with List" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
            }
            else if (paramType.endsWith("Map")) {
               if (!paramName.endsWith("Map") && !paramName.endsWith("MAP")) {
                  errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Map" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
            }
            else if (paramType.endsWith("Set")) {
               if (!paramName.endsWith("Set") && !paramName.endsWith("SET")) {
                  errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Set" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
            }
            else if (paramType.endsWith("Queue")) {
               if (!paramName.endsWith("Queue") && !paramName.endsWith("QUEUE")) {
                  errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Queue" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
            }
            else if (paramType.endsWith("Deque")) {
               if (!paramName.endsWith("Deque") && !paramName.endsWith("DEQUE")) {
                  errorList.add("Line no:  " + lineNumber + " :  Parameter Name '" + paramName + "' is not ending with Deque" + "|"
                     + BugCategory.VARIABLE_METHOD_NAMING_ISSUE.toString());
               }
            }

         });
      }

      /**
       * checkForCasting method is used to check the casting in codes
       * 
       * @param declaration
       */
      private void checkForCasting(MethodDeclaration declaration) {
         removeContainedComments.setPrintComments(false);
         List<String> primitiveAndWrapperList = Arrays.asList("String", "int", "long", "Long", "double", "Double", "short", "Short", "boolean",
            "Boolean", "Integer");
         String stringToCheck = declaration.getBody().get().toString(removeContainedComments);
         if (stringToCheck.contains("class.cast(")) {
            warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName()
               + " found type casting(class.cast)" + "|" + BugCategory.CASTING_ISSUE.toString());
         }
         primitiveAndWrapperList.stream().forEach(primitiveAndWrapper -> {
            if (stringToCheck.contains("(" + primitiveAndWrapper + ")")) {
               warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
                  + " found type casting" + "|" + BugCategory.CASTING_ISSUE.toString());
            }
         });
         List<String> collectionList = Arrays.asList("List", "Map", "Set", "Collection");
         collectionList.stream().forEach(collection -> {
            if (stringToCheck.contains("(" + collection + "<") && stringToCheck.contains(">)")) {
               warningList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
                  + " found type casting" + "|" + BugCategory.CASTING_ISSUE.toString());
            }
         });
      }

      /**
       * checkForMethodParameterCount is used to check the parameter count.
       * 
       * @param declaration
       */
      private void checkForMethodParameterCount(MethodDeclaration declaration) {
         long parametersCount = declaration.getParameters().stream().count();
         if (parametersCount > 7) {
            errorList.add("Line No : " + declaration.getBegin().get().line + " : In method " + declaration.getName().asString()
               + " found more than 7 parameters, but the " + "allowed count is atmost 7" + "|"
               + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
         }
      }

      /**
       * checkForProperBigDecimalUsage is used to check the proper usage of the BigDecimal.
       * 
       * @param declaration
       */
      private void checkForProperBigDecimalUsage(MethodDeclaration declaration) {
         removeContainedComments.setPrintComments(false);
         if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(0)")) {
            errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found 'new BigDecimal(0)' instead use BigDecimal.ZERO" + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
         }
         else if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(1)")) {
            errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found 'new BigDecimal(1)' instead use BigDecimal.ONE" + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
         }
         else if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(10)")) {
            errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
               + " found 'new BigDecimal(10)' instead use BigDecimal.TEN" + "|" + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
         }
         bigDecimalCheckMap.entrySet().stream().forEach(keyValue -> {
            if (declaration.getBody().get().toString(removeContainedComments).contains("new BigDecimal(" + keyValue.getKey() + ")")) {
               errorList.add("Line No : " + declaration.getBegin().get().line + " in method " + declaration.getName().asString()
                  + " found 'new BigDecimal(" + keyValue.getKey() + ")' instead use BigDecimal." + keyValue.getValue() + "|"
                  + BugCategory.UNUTILIZED_RESOURCES_ISSUE.toString());
            }
         });
      }

      /**
       * checkForNoOfLinesInaMethod is used to check the no of lines in a method.
       * 
       * @param noOfLines
       * @param beginLine
       * @param methodName
       */
      private void checkForNoOfLinesInaMethod(int noOfLines, int beginLine, String methodName) {
         if (noOfLines > 100) {
            warningList.add("Line No:  " + beginLine + "  " + methodName + " method has more than 100 lines, so if the logic is independent"
               + " then split the method to improve readability.For queries check ticket 53393" + "|"
               + BugCategory.READABILITY_MAINTAINABILITY_ISSUE.toString());
         }
      }

      // removed because the rule is changed.
      // /**
      // * checkForReflectionUsage method checks for the reflection usage in the source
      // code.
      // * @param declaration
      // */
      // private void checkForReflectionUsage(MethodDeclaration declaration) {
      // if(currentFileName.endsWith("Controller")||currentFileName.endsWith("Impl")||
      // currentFileName.endsWith("controller")||currentFileName.endsWith("impl")){
      // if(declaration.toStringWithoutComments().contains(".class")||
      // declaration.toStringWithoutComments().contains(".getClass")){
      // warningList.add("Line no: " + declaration.getBegin().get().line
      // + " :Found usage of Reflection.");
      // }
      // }
      // }

      /**
       * checkMethodLog method is used to check the method log.
       * 
       * @param declaration
       * @param path
       */
      // removed because log for start and end of method is not mandatory
      // private void checkMethodLog(MethodDeclaration declaration,Object path) {
      // LogUtils.print("JavaCodeReview::checkMethodLog method starts");
      // AtomicInteger count = new AtomicInteger(0);
      // if(!String.valueOf(path).contains("dao") && declaration.getBody() !=null){
      // declaration.getBody().getStmts().stream().forEach(statement->{
      // String line =statement.toStringWithoutComments().trim();
      // if((line.contains("log.info") || line.contains("log.warn")
      // || line.contains("log.trace") || (line.contains("log.debug"))
      // || line.contains("log.error"))){
      // count.getAndIncrement();
      // }
      // });
      // if(count.get() <2){
      // warningList.add("Line no: " + declaration.getBegin().get().line
      // + " : Start or End Logger is missing for the Method '" +declaration.getName()
      // +"'");
      // }
      // }
      //
      // LogUtils.print("JavaCodeReview::checkMethodLog method ends");
      // }

      private void checkMethodNameForIndexEntity(MethodDeclaration declaration) {
         // The below if statement is used to check indexEntity contains formDto Method
         // or Not.
         if (currentFileName.endsWith("IndexEntity")) {
            if (declaration.getName().asString().equalsIgnoreCase("formDto") || declaration.getName().asString().equalsIgnoreCase("formIndex")) {
               errorList.add("Line no:  " + declaration.getBegin().get().line + ": '" + declaration.getName().asString()
                  + " is not needed in the indexEntity class file." + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
            }
         }
      }

      /**
       * checkMethodDocs method is used to check the docs for the method.
       * 
       * @param declaration
       */
      private void checkMethodDocs(MethodDeclaration declaration) {
         if (!declaration.getAnnotations().toString().contains("@Override")) {
            if (declaration.getComment() != null) {
               String commentContent = declaration.getJavadocComment().get().toString();
               if (!declaration.getJavadocComment().isPresent()) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " : Comments is missing for the function '"
                     + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
               }

               if (!commentContent.replaceAll("\\*", "").replaceAll("/", "").trim().startsWith(declaration.getName().asString())) {
                  errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Comments should only start with Function Name '"
                     + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
               }

               declaration.getParameters().stream().forEach(param -> {
                  String paramName = param.getNameAsString();
                  String comment = commentContent.replaceAll(" ", "");
                  if (!comment.contains("@param" + paramName)) {
                     errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @param '" + paramName + "' in the function '"
                        + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
                  }
               });

               declaration.getThrownExceptions().stream().forEach(value -> {
                  String throwsValue = value.asString();
                  String comment = commentContent.replaceAll(" ", "");
                  if (!comment.contains("@throws" + throwsValue)) {
                     errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing @throws '" + throwsValue + "' in the function '"
                        + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
                  }
               });

               if (!declaration.getType().asString().trim().equals("void") && !declaration.getDeclarationAsString().contains(" abstract ")) {
                  if (!commentContent.contains("@return")) {
                     errorList.add("Line no:  " + declaration.getBegin().get().line + " :  Missing return statement for the function '"
                        + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
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
                                 + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
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
                     + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
               }
               else {
                  if (!declaration.getName().asString().equalsIgnoreCase("formIndex")
                     && !declaration.getName().asString().equalsIgnoreCase("formDto")) {
                     errorList.add("Line no:  " + declaration.getBegin().get().line + " : Comments is missing for the function '"
                        + declaration.getName().asString() + "'" + "|" + BugCategory.DOCS_ISSUE.toString());
                  }
               }
            }
         }
      }

      /**
       * checkForRestrictedUsage method is used to check the use of loops and hardcoded values.
       * 
       * @param declaration
       */
      private void checkForRestrictedUsage(MethodDeclaration declaration) {
         String intLiteralRegex = "(.*)([0-9]\\.*)(.*)";
         Pattern intLiteralpattern = Pattern.compile(intLiteralRegex);
         removeContainedComments.setPrintComments(false);
         if (declaration.getBody().isPresent()) {
            BlockStmt block = declaration.getBody().get();
            block.getStatements().stream().forEach(statement -> {
               if (statement instanceof ForStmt) {
                  ForStmt forStatement = (ForStmt) statement;
                  warningList.add("Line no:  " + forStatement.getBegin().get().line + ":  For Loop should not be used. Use Streams instead." + "|"
                     + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
               }
               else if (statement instanceof ForeachStmt) {
                  ForeachStmt forEachStatement = (ForeachStmt) statement;
                  warningList.add("Line no:  " + forEachStatement.getBegin().get().line + ":  For-each Loop should not be used. Use Streams instead"
                     + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
               }
               else if (statement instanceof IfStmt) {
                  IfStmt ifStatement = (IfStmt) statement;
                  if (ifStatement.getCondition() != null) {
                     Matcher intLiteralMatcher = intLiteralpattern.matcher(statement.toString());
                     String condition = ifStatement.getCondition().toString(removeContainedComments).replace(" ", "");
                     if (condition.replace(" ", "").contains("==null") || condition.replace(" ", "").contains("!=null")) {
                        warningList.add("Line no:  " + ifStatement.getCondition().getBegin().get().line
                           + " : Use Objects.nonNull() instead of == null and !=null" + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
                     }
                     if (intLiteralMatcher.find()) {
                        warningList.add("Line no:  " + ifStatement.getCondition().getBegin().get().line
                           + " :  HardCoded Integer value : Use Constants instead of using Integer literal" + "|"
                           + BugCategory.HARDCODE_ISSUE.toString());
                     }
                     if (condition.replace(" ", "").contains("==true") || condition.replace(" ", "").contains("!=true")
                        || condition.replace(" ", "").contains("==false") || condition.replace(" ", "").contains("!=false")
                        || condition.replace(" ", "").contains("==Boolean.TRUE") || condition.replace(" ", "").contains("!=Boolean.TRUE")
                        || condition.replace(" ", "").contains("==Boolean.FALSE") || condition.replace(" ", "").contains("!=Boolean.FALSE")
                        || condition.replace(" ", "").contains(".equals(Boolean.FALSE)")
                        || condition.replace(" ", "").contains(".equals(Boolean.TRUE)") || condition.replace(" ", "").contains(".equals(true)")
                        || condition.replace(" ", "").contains(".equals(false)")) {
                        errorList.add("Line no:  " + ifStatement.getCondition().getBegin().get().line + " : boolean comparision should be avoided"
                           + "|" + BugCategory.PERFORMANCE_ISSUE.toString());
                     }
                  }
               }
               else if (statement instanceof SwitchStmt) {
                  SwitchStmt switchStmnt = (SwitchStmt) statement;
                  switchStmnt.getEntries().stream().forEach(data -> {
                     if (!data.toString().contains("break;") && !data.toString().contains("return ") && !data.toString().contains("return;")) {
                        warningList.add("Line No  :" + data.getBegin().get().line
                           + " The break statement is missing in this block(If the case block logic is similar to the succeeding case block's logic, ignore this warning)."
                           + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
                     }
                  });
               }
               else {
                  Matcher intLiteralMatcher = intLiteralpattern.matcher(statement.toString());
                  if (intLiteralMatcher.find()) {
                     warningList.add("Line no:  " + statement.getBegin().get().line
                        + " :  HardCoded Integer value : Use Constants instead of using Integer literal" + "|"
                        + BugCategory.HARDCODE_ISSUE.toString());
                  }
               }

            });
         }
         if (currentFileName.endsWith("Controller")) {
            if (declaration.getParameters() != null) {
               declaration.getParameters().stream().filter(param -> (param.getAnnotations().toString().contains("@RequestBody")
                  || param.getAnnotations().toString().contains("@RequestParam"))).forEach(param -> {
                     if (param.getType().asString().startsWith("Map<") || param.getType().asString().startsWith("List<Map<")
                        || param.getType().asString().startsWith("ArrayList<Map<")) {
                        errorList.add("Line no:  " + param.getBegin().get().line + " :  Use Pojo instead of using Map or List of Map" + "|"
                           + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
                     }
                  });
            }
         }
      }

      /**
       * checkForStringAdditions method is used to check the addition of string
       * 
       * @param declaration
       */
      private void checkForStringAddition(MethodDeclaration declaration) {
         String stringLiteralRegex = "(\\.\\()?\"";
         Pattern stringLiteralpattern = Pattern.compile(stringLiteralRegex);
         ArrayList<String> variableNameList = new MethodVariableVisitor().getLocalStringVariableNameList();
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
                     warningList.add("Line no:  " + line.get("line") + " :  HardCoded String value : String literal '"
                        + line.get("methodLine").substring(startIndex, matcher.start()) + "' is hardcoded" + "|"
                        + BugCategory.HARDCODE_ISSUE.toString());
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
                     warningList
                        .add("Line no:  " + line.get("line") + " :  String variables added" + "|" + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
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

      /**
       * checkAddition method is used to check the string addition used.
       * 
       * @param node
       * @param lineList
       */
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

   }

   /**
    * CatchLogCheck class contains method to check whether log is used inside the catch block.
    */
   private class CatchLogCheck extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(CatchClause declaration, Object path) {
         removeContainedComments.setPrintComments(false);
         if (declaration.getBody() != null) {
            String catchBlock = declaration.getBody().toString(removeContainedComments);
            if (!(catchBlock.contains("log.info") || catchBlock.contains("log.warn") || catchBlock.contains("log.error")
               || catchBlock.contains("log.trace") || catchBlock.contains("log.debug") || catchBlock.contains("throw"))) {
               warningList.add("Line no:  " + declaration.getBegin().get().line + " :  Logger  is Missing in Catch Clause|LoggerInCatchMissing");
            }
            // new rule to check if both log and throw present.
            if (!(catchBlock.contains("log.info") || catchBlock.contains("log.warn") || catchBlock.contains("log.error")
               || catchBlock.contains("log.trace") || catchBlock.contains("log.debug")) && !catchBlock.contains("throw")) {
               warningList.add("Line no:  " + declaration.getBegin().get().line + " : Either use throw or log instead of using both|AvoidableUsages");
            }
            // end
            if (catchBlock.contains("printStackTrace")) {
               errorList
                  .add("Line no:  " + declaration.getBegin().get().line + " :  printStackTrace should not be used. Use log instead|ProhibitedUsages");
            }
         }
      }

   }

   /**
    * MethodVariableVisitor class contains method to get the variables used inside the method.
    */
   private class MethodVariableVisitor extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(VariableDeclarationExpr localVariable, Object path) {
         getLocalVariableDetails(localVariable);

      }

      /**
       * getLocalVariableDetails method is used to get all the variables used inside the method.
       * 
       * @param localVariable
       */
      private void getLocalVariableDetails(VariableDeclarationExpr localVariable) {
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
      }

      private ArrayList<Map<String, Object>> getLocalVariableDetailsList() {
         return localVariableDetailsList;
      }

      private ArrayList<String> getLocalStringVariableNameList() {
         return localStringVariableNameList;
      }

   }

   /**
    * BlockCommentVisitor class contains method to check the multi line comments used in the file.
    */
   private class BlockCommentVisitor extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(BlockComment blockCommentDeclaration, Object path) {
         errorList.add("Line no:  " + blockCommentDeclaration.getBegin().get().line + " :  Remove the Multi-line comments" + "|"
            + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
      }

   }

   private class ImportVisitor extends VoidVisitorAdapter<Object> {

      @Override
      public void visit(ImportDeclaration declaration, Object path) {

         String importedClass = declaration.getName().getIdentifier();
         // check for usage of reflection by checking imported classes.
         if (declaration.getNameAsString().contains("java.lang.reflect")) {
            warningList.add("Line no:  " + declaration.getBegin().get().line + " : Found usage of Reflection." + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         else if (declaration.getNameAsString().contains("com.google.gson")) {
            warningList.add("Line no:  " + declaration.getBegin().get().line
               + " : Found usage of com.google.gson (please get a confirmation from CR Team about using it)." + "|"
               + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         // check for usage of wildcard imports
         if (declaration.getNameAsString().contains("*")) {
            warningList.add(
               "Line no:  " + declaration.getBegin().get().line + " :Don't use wild ." + "|" + BugCategory.CONVENTION_VIOLATIONS_ISSUE.toString());
         }
         if (importedClass.equals("KeyValueAccess")) {
            errorList
               .add("Should not use KeyValueAccess in files other than Dao and DaoImpl." + "|" + BugCategory.MISUSED_LAYER_LOGIC_ISSUE.toString());
         }
         if (importedClass.endsWith("Dto")) {
            if (currentFileName.endsWith("Controller")) {
               errorList.add("Should not use any Dto in controller." + "|" + BugCategory.MISUSED_LAYER_LOGIC_ISSUE.toString());
            }
            else {
               warningList.add(
                  "Should not use any Dto in files other than Dao, DaoImpl and Entity." + "|" + BugCategory.MISUSED_LAYER_LOGIC_ISSUE.toString());
            }
         }

         if (declaration.getNameAsString().contains("rdb.client.RDBClient") || declaration.getNameAsString().contains("jdbc.core.JdbcTemplate")) {
            isRdbDaoCheck = true;
         }
         if (declaration.getNameAsString().contains("rdb.client.annotation.Table")) {
            isRdbDtoTableCheck = true;
         }

      }

   }

   /**
    * getConstructorInformation method is used to get constructor information for purpose of checking value assigned to
    * global collection fields is immutable
    * 
    * @param typeDeclarations
    * @param variableNameList
    */
   private void getConstructorInformation(List<TypeDeclaration> typeDeclarations, List<String> variableNameList) {
      for (TypeDeclaration typeDec : typeDeclarations) {
         List<BodyDeclaration> members = typeDec.getMembers();
         if (members != null) {
            for (BodyDeclaration member : members) {
               if (member instanceof MethodDeclaration) {
                  MethodDeclaration method = (MethodDeclaration) member;
                  if (method.getBody() != null) {
                     if (method.getBody().isPresent()) {
                        method.getBody().get().getStatements().forEach(statement -> {
                           checkValueAssignedForGlobalCollectionField(statement, variableNameList);
                        });
                     }

                  }
               }
               else if (member instanceof ConstructorDeclaration) {
                  ConstructorDeclaration constructor = (ConstructorDeclaration) member;
                  if (constructor.getBody() != null) {
                     constructor.getBody().getStatements().forEach(statement -> {
                        checkValueAssignedForGlobalCollectionField(statement, variableNameList);
                     });
                  }
               }
            }
         }
      }
   }

   /**
    * checkValueAssignedForGlobalCollectionField method checks if the value set for global collection field is
    * immutable.
    * 
    * @param statement
    * @param variableNameList
    */
   private void checkValueAssignedForGlobalCollectionField(Statement statement, List<String> variableNameList) {
      if (statement instanceof ExpressionStmt) {
         Expression stmtExpression = ((ExpressionStmt) statement).getExpression();
         if (stmtExpression instanceof AssignExpr) {
            AssignExpr assignExpr = (AssignExpr) stmtExpression;
            removeContainedComments.setPrintComments(false);
            if (variableNameList.contains(assignExpr.getTarget().toString(removeContainedComments).replaceAll("this.", ""))) {
               if (!assignExpr.getValue().toString(removeContainedComments).startsWith("Immutable")) {
                  errorList.add("Line no : " + statement.getBegin()
                     + " On assigning value to a global collection type field, the assigned value should be immutable."
                     + " If assigning a collection object like list use 'copyOf()' of ImmutableList class. Else, use 'of()' of ImmutableList class and set the values. \n"
                     + " Note: If a wrapper class for your license-group is available then use it. Ex: ImmutableListWrapper.copyOf()" + "|"
                     + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
               }
            }
         }
      }
   }

   /**
    * checkUseOfArgsConstructorWhenGlobalCollectionField method is used to raise error if global collection type fields
    * are used when @*ArgsConstructor annotation is found in class
    * 
    * @param lineNumber
    */
   private void checkUseOfArgsConstructorWhenGlobalCollectionField(String lineNumber) {
      if (isArgsConstructorUsed) {
         errorList.add("Line no : " + importLineNumber
            + " When collection fields used are global, remove the @*ArgsConstructor annotation and add the constructor of own. Use @Autowired over the constructor. \n"
            + " If enum file, use the AccessLevel annotation value as the access specifier for the constructor added." + "|"
            + BugCategory.MEMORY_MANAGEMENT_ISSUE.toString());
      }
   }

}
