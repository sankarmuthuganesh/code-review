package RealTime.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualErrorCollectionDetailed {
    ActualJavaDetailed totalCategorisedCollectionJava;
    ActualJavaScriptDetailed totalCategorisedCollectionJS;
    ActualXMLDetailed totalCategorisedCollectionXML;
}
