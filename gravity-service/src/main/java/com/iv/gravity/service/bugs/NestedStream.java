package com.iv.gravity.service.bugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.iv.gravity.entity.BugDetails;
import com.iv.gravity.entity.FileUnit;
import com.iv.gravity.enums.BugCategory;

public class NestedStream {

   public List<FileUnit> getNestedLoops(List<FileUnit> storyFiles) {

      return storyFiles.stream().map(file -> {
         List<BugDetails> bugList = new ArrayList<>();
         File fileToCheck = new File(file.getAbsolutePath());
         if (fileToCheck.getName().endsWith(".java")) {
            String exactFileName = fileToCheck.getName().replace(".java", StringUtils.EMPTY);
            // Has to Exlude Unnecessary Files.
            try {
               CompilationUnit compilationUnit = JavaParser.parse(fileToCheck);
               List<String> streamMethods = new ArrayList<String>();
               List<String> daoMethods = new ArrayList<String>();

               Set<String> lineNumberSet = new HashSet<>();
               compilationUnit.findAll(MethodDeclaration.class).stream().forEach(statements -> {
                  Optional<BlockStmt> methodBody = statements.getBody();
                  if (methodBody.isPresent()) {
                     NodeList<Statement> checkValue = methodBody.get().getStatements();
                     if (checkValue.stream().anyMatch(predicate -> predicate.toString().contains("stream") || predicate.isForStmt()
                        || predicate.isWhileStmt() || predicate.isForeachStmt())) {
                        streamMethods.add(statements.getNameAsString());
                     }
                     if (checkValue.stream().anyMatch(daoCheck -> daoCheck.toString().contains("Dao"))) {
                        daoMethods.add(statements.getNameAsString());
                     }
                  }
               });

               compilationUnit.findAll(MethodCallExpr.class).stream().filter(predicate -> predicate.toString().contains("stream")).forEach(action -> {
                  int count = 0;
                  Pattern streamCheck = Pattern.compile("stream");
                  Matcher m = streamCheck.matcher(action.toString());
                  while (m.find()) {
                     count++;
                  }
                  if (count >= 2) {
                     BugDetails nestedLoop = new BugDetails();
                     nestedLoop.setLineNumber(String.valueOf(action.getBegin().get().line));
                     nestedLoop.setSeverityOfBug("Critical");
                     nestedLoop.setBugCategory(BugCategory.PERFORMANCE_ISSUE.toString());
                     nestedLoop.setBug("Nested Stream Should be avoided");
                     nestedLoop.setFileName(file.getFileName());
                     nestedLoop.setRemotePathOfFile(file.getRemotePath());
                     bugList.add(nestedLoop);

                  }
                  if (streamMethods.stream().anyMatch(predicate -> predicate.contains(action.toString()))) {
                     streamMethods.add(action.getNameAsString());
                  }
                  if (StringUtils.containsIgnoreCase(action.toString(), "Dao.")) {
                     BugDetails nestedLoop = new BugDetails();
                     nestedLoop.setLineNumber(String.valueOf(action.getBegin().get().line));
                     nestedLoop.setSeverityOfBug("Critical");
                     nestedLoop.setBugCategory("ProhibitedUsages");
                     nestedLoop.setBug("DB Hit inside stream is prohibited");
                     nestedLoop.setFileName(file.getFileName());
                     nestedLoop.setRemotePathOfFile(file.getRemotePath());
                     bugList.add(nestedLoop);
                  }

                  streamMethods.stream().forEach(method -> {
                     if (action.toString().contains(method) && !action.toString().contains("." + method)) {
                        BugDetails nestedLoop = new BugDetails();
                        nestedLoop.setLineNumber(String.valueOf(action.getBegin().get().line));
                        nestedLoop.setSeverityOfBug("Critical");
                        nestedLoop.setBugCategory(BugCategory.PERFORMANCE_ISSUE.toString());
                        nestedLoop.setBug("Nested Stream Should be avoided");
                        nestedLoop.setFileName(file.getFileName());
                        nestedLoop.setRemotePathOfFile(file.getRemotePath());
                        bugList.add(nestedLoop);

                     }
                  });
                  daoMethods.stream().forEach(daoCall -> {
                     if (action.toString().contains(daoCall)) {
                        BugDetails nestedLoop = new BugDetails();
                        nestedLoop.setLineNumber(String.valueOf(action.getBegin().get().line));
                        nestedLoop.setSeverityOfBug("Critical");
                        nestedLoop.setBugCategory("ProhibitedUsages");
                        nestedLoop.setBug("DB Hit inside stream is prohibited");
                        nestedLoop.setFileName(file.getFileName());
                        nestedLoop.setRemotePathOfFile(file.getRemotePath());
                        bugList.add(nestedLoop);

                     }

                  });
               });

               if (CollectionUtils.isNotEmpty(bugList)) {
                  Set<String> checkData = new HashSet<>();
                  bugList.removeIf(check -> !checkData.add(check.getLineNumber()));
                  file.getBugDetailsList().addAll(bugList);
               }
            }
            catch (FileNotFoundException e) {
               // Cannot find the file in the specified absolute path.
            }
         }
         return file;
      }).distinct().collect(Collectors.toList());

   }

}
