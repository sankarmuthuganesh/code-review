package RealTime.Author;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;

public class GiveInputToAuthors {
    public static Map<String, List<String>> giveInputToAuthor(Map<String, Map<MultiKey, List<String>>> fullListOfFiles) {
        Set<String> authors = new HashSet<>();
        Set<String> jsauthors = new HashSet<>();
        Set<String> jsauthorless = new HashSet<>();
        Set<String> authorLess = new HashSet<>();
        Set<String> cobalt = new HashSet<>();

        Map<String, List<String>> authorOut = new HashMap<>();
        Set<String> totalAuthors = new HashSet<>();
        Set<String> totalAuthorLess = new HashSet<>();

        JavaAuthor java = new JavaAuthor();
        JsAuthor js = new JsAuthor();
        fullListOfFiles.entrySet().stream().forEach(repo -> {
            repo.getValue().entrySet().stream().forEach(branch -> {
                for (String file : branch.getValue()) {
                    try {
                        if (file.endsWith(".java")) {

                            String author = java.getJavaAuthor(file);

                            if (!author.isEmpty()) {
                                authors.add(author);
                            }
                            else if (!author.equalsIgnoreCase("Cobalt")) {
                                cobalt.add(author);
                            }
                            else {
                                authorLess.add(author);
                            }

                        }
                        if (file.endsWith(".js")) {

                                jsauthors.add(js.getJsAuthor(file));
                            }
                            // System.out.println(jsAuthorMap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }       );
        });

        // System.out.println("Java Authors-----"+authors);
        // System.out.println("Cobalt-----"+cobalt);
        // System.out.println("Java AuthorLess-----"+authorLess);
        //
        // System.out.println("Js Authors-----"+jsauthors);
        // System.out.println("Js AuthorLess-----"+jsauthorless);
        totalAuthors.addAll(jsauthors);
        totalAuthors.addAll(authors);

        totalAuthorLess.addAll(jsauthorless);
        totalAuthorLess.addAll(authorLess);

        authorOut.put("authors", new ArrayList<>(totalAuthors));
        authorOut.put("authorLess", new ArrayList<>(totalAuthorLess));
        authorOut.put("cobalt", new ArrayList<>(cobalt));
        return authorOut;
    }
}
