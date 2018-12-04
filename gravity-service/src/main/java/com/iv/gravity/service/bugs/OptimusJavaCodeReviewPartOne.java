package com.iv.gravity.service.bugs;

import java.util.List;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.iv.gravity.enums.BugCategory;

/**
 * @version Oct 24, 2018
 * @author optimus team
 */
public class OptimusJavaCodeReviewPartOne {

   PrettyPrinterConfiguration removeContainedComments = new PrettyPrinterConfiguration();

   public void checkForCSRFAttacks(MethodDeclaration declaration, List<String> warningList) {

      boolean isOverride = declaration.getAnnotations().stream()
         .filter(data -> data.getNameAsString().equals("Override") || data.getNameAsString().equals("RequestMapping")).findFirst().isPresent();
      if (declaration.isPublic() && !isOverride) {
         warningList.add("Line No : " + declaration.getBegin().get().line
            + " : HTTP method for methods in Controller should be specified explicitly. Or all HTTP methods will be accepted as valid methods, which makes the request vulnerable to CSRF attacks. Basically this behavior is needless for WEB application,"
            + " and it is a well-known evidence that there is no consideration for security.Use @RequestMapping annotation to specify method type."
            + "|" + BugCategory.SECURITY_ISSUE.toString());
      }
   }

   public void checkForNullpointerExceptionInCatchBlock(MethodDeclaration declaration, List<String> errorList) {
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody().get().toString(removeContainedComments).contains("catch (NullPointerException")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " : Don't catch nullpointer exception instead handle it" + "|"
            + BugCategory.EXCEPTION_HANDLING_ISSUE.toString());
      }
   }

   public void checkForOptionalMethods(MethodDeclaration declaration, List<String> errorList) {
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody().get().toString(removeContainedComments).contains("orElseGet(null)")
         || declaration.getBody().get().toString().contains("orElseThrow(null)")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " :orElseGet(null) and orElseThrow(null) usage throws "
            + "nullpointer exception." + "|" + BugCategory.EXCEPTION_HANDLING_ISSUE.toString());
      }
   }

   public void checkForZoneIdsystemDefault(MethodDeclaration declaration, List<String> errorList) {
      removeContainedComments.setPrintComments(false);
      if (declaration.getBody().get().toString(removeContainedComments).contains("ZoneId.systemDefault()")) {
         errorList.add("Line No : " + declaration.getBegin().get().line + " :found (ZoneId.systemDefault()) instead  get time zone from usercontext"
            + "|" + BugCategory.HARDCODE_ISSUE.toString());

      }
   }

   public void checkForLocale(MethodDeclaration declaration, List<String> errorList) {
      if (declaration.getBody().get().toString().contains("Locale.ENGLISH") || declaration.getBody().get().toString().contains("Locale.JAPANESE")) {
         errorList.add("Line No : " + declaration.getBegin()
            + " :found (LOCALE usage) instead  get it from usercontext, incase of MLString see per http://192.168.41.191/mediawiki/index.php/Usage_of_ML_String"
            + "|" + BugCategory.HARDCODE_ISSUE.toString());
      }
   }

   // Since this point was added in detail to Gravity.
   // public static void checkForMisuseOfAutoindexTable(String
   // packageName,ClassOrInterfaceDeclaration
   // declaration,List<String> errorList,List<String> warningList,List<String>
   // annotationsUsedList) {
   //
   // AtomicBoolean checkOtherMisuseOfAutoindexTable=new AtomicBoolean(true);
   //
   // if(packageName.contains("dto") && (declaration.getName().toString().endsWith("Dto")
   // ||
   // declaration.getName().toString().endsWith("Index"))){
   // annotationsUsedList.stream().peek(operan -> {
   // if(operan.contains("@Join")
   // &&(operan.contains("Index.class")||operan.contains("IndexDto.class"))){
   // errorList.add("Line no: " + declaration.getBegin()
   // + " : " +operan+ " An index class should not be used as join of another index");
   // checkOtherMisuseOfAutoindexTable.set(false);
   // }
   // }).filter(annotation ->
   // (annotation.contains("@AutoIndex")&&(annotation.contains("IndexDto.class")||annotation.contains("Index.class")))).forEach(data
   // -> {
   // errorList.add("Line no: " + declaration.getBegin()
   // + " : " +data+ " Indexing an index class should not be done");
   // checkOtherMisuseOfAutoindexTable.set(false);
   // });
   // if(checkOtherMisuseOfAutoindexTable.get()){
   // warningList.add("Check the cases for AutoIndex creation see --> per
   // http://huewiki/mediawiki/index.php/How_to_create_HUE_Index_data#Misuses_of_Autoindex");
   // }
   // }
   // }

}
