package RealTime.Optimus.SourceCode.JavaCodeReviewParts;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class JavaCodeReviewPartOne {
    PrettyPrinterConfiguration removeContainedComments = new PrettyPrinterConfiguration();

    public void checkForCSRFAttacks(MethodDeclaration declaration, List<String> warningList) {

        boolean isOverride = declaration
                .getAnnotations()
                .stream()
                .filter(data -> data.getNameAsString().equals("Override")
                        || data.getNameAsString().equals("RequestMapping")).findFirst().isPresent();
        if (declaration.isPublic() && !isOverride) {
            warningList
                    .add("Line No : "
                            + declaration.getBegin().get().line
                            +
                            " : HTTP method for methods in Controller should be specified explicitly. Or all HTTP methods will be accepted as valid methods, which makes the request vulnerable to CSRF attacks. Basically this behavior is needless for WEB application,"
                            +
                            " and it is a well-known evidence that there is no consideration for security.Use @RequestMapping annotation to specify method type.");
        }
    }

    public void checkForNullpointerExceptionInCatchBlock(MethodDeclaration declaration, List<String> errorList) {
        removeContainedComments.setPrintComments(false);
        if (declaration.getBody().get().toString(removeContainedComments).contains("catch (NullPointerException")) {
            errorList.add("Line No : "
                    + declaration.getBegin().get().line
                    + " : Don't catch nullpointer exception instead handle it");
        }
    }

    public void checkForOptionalMethods(MethodDeclaration declaration, List<String> errorList) {
        removeContainedComments.setPrintComments(false);
        if (declaration.getBody().get().toString(removeContainedComments).contains("orElseGet(null)") ||
                declaration.getBody().get().toString().contains("orElseThrow(null)")) {
            errorList.add("Line No : "
                    + declaration.getBegin().get().line
                    + " :orElseGet(null) and orElseThrow(null) usage throws "
                    + "nullpointer exception.");
        }
    }

    public void checkForZoneIdsystemDefault(MethodDeclaration declaration, List<String> errorList) {
        removeContainedComments.setPrintComments(false);
        if (declaration.getBody().get().toString(removeContainedComments).contains("ZoneId.systemDefault()")) {
            errorList.add("Line No : "
                    + declaration.getBegin().get().line
                    + " :found (ZoneId.systemDefault()) instead  get time zone from usercontext");
        }
    }

}
