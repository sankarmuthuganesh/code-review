//package RealTime.CheckTrial;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Optional;
//
//
//
//
//import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
//import org.deeplearning4j.models.word2vec.Word2Vec;
//import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
//import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
//import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
//import org.nd4j.linalg.ops.transforms.Transforms;
//
//
//
//
//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.body.FieldDeclaration;
//import com.github.javaparser.ast.body.MethodDeclaration;
//import com.github.javaparser.ast.expr.VariableDeclarationExpr;
//import com.github.javaparser.ast.stmt.BlockStmt;
//import com.github.javaparser.printer.PrettyPrinterConfiguration;
//import com.google.common.base.Splitter;
//
//import freemarker.template.utility.CollectionUtils;
//
//
//public class MLTokenizer {
//	 static PrettyPrinterConfiguration removeUnnecessary = new PrettyPrinterConfiguration();
//	public static void main(String[] args) throws FileNotFoundException {
//		removeUnnecessary.setPrintComments(false);
//		removeUnnecessary.setIndent("");
//		removeUnnecessary.setEndOfLineCharacter("*");
//		removeUnnecessary.setColumnAlignFirstMethodChain(false);
//		removeUnnecessary.setColumnAlignParameters(false);
//		removeUnnecessary.setOrderImports(false);
//		removeUnnecessary.setPrintJavadoc(false);
//		
//		File fileToUnderstand = new File("C:\\Gravity\\Clones\\Temp\\hue-scm-project\\river-1804\\hue-scm-project-biz\\src\\main\\java\\com\\worksap\\company\\hue\\scm\\biz\\project\\service\\projectmonthlyclosingmanagement\\projectmonthlyclosing\\ProjectMonthlyClosingApiServiceImpl.java");
//		CompilationUnit parsedFile = JavaParser.parse(fileToUnderstand);
//		
//		//MethodAndTransformed Contents
//		Map<String,List<String>> methodAndContents=new HashMap<>();
//		
//		//List Class Fields And Types
//		Map<String,String> fieldTypeAndName=new HashMap<>();
//		parsedFile.findAll(FieldDeclaration.class).stream().forEach(field ->{
//			fieldTypeAndName.put(field.getVariables().get(0).getNameAsString(), field.getElementType().toString());
//		});
//		
//		//Method Reading
//		parsedFile.findAll(MethodDeclaration.class).stream().forEach(method ->{
//			//VariablesAndItsTypes in Method
//			Map<String,String> variableTypeAndName=new HashMap<>();
//			method.getParameters().stream().forEach(param ->{
//				variableTypeAndName.put(param.getNameAsString(), param.getType().asString());
//			});
//				Optional<BlockStmt> methodBody = method.getBody();
//				if(methodBody.isPresent()){
//					BlockStmt bodyBlock = methodBody.get();
//					String oldMethodBody=bodyBlock.toString(removeUnnecessary);
//					bodyBlock.findAll(VariableDeclarationExpr.class).stream().forEach(var ->{
//						variableTypeAndName.put(var.getVariables().get(0).getNameAsString(),var.getElementType().toString());
//					});
//					for(Entry<String, String> vars:variableTypeAndName.entrySet()){
//						oldMethodBody=oldMethodBody.replaceAll( vars.getKey(),vars.getValue());
//					}
//					for(Entry<String, String> classField:fieldTypeAndName.entrySet()){
//						oldMethodBody=oldMethodBody.replaceAll(classField.getKey(),classField.getValue());
//					}
//					methodAndContents.put(method.getNameAsString(), Splitter.on("*").splitToList(oldMethodBody));
//					}
//		});
//		
//		Map<String,Word2Vec> methodWordVecs=new HashMap<>();
//		
//		
//		
//		
//		methodAndContents.entrySet().stream().forEach(transformedMethod ->{
//			List<String> methodContentOld = transformedMethod.getValue();
//			List<String> methodContent = new ArrayList<>();
//			methodContentOld.stream().forEach(word ->{
//				if(!word.isEmpty()){
//					methodContent.add(word);
//				}
//			});
//			 String filePath = "C:\\HUE\\WorkSpace\\Develop\\Zeus\\src\\resources\\raw.txt";
//			 SentenceIterator iter=null;
//			try {
//				iter 	= new org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator(methodContent);
//				//iter = new BasicLineIterator(filePath);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("-------------------------------------------");
//			System.out.println(methodContent);
//			System.out.println("-------------------------------------------");
//			 TokenizerFactory t = new DefaultTokenizerFactory();
//			 t.setTokenPreProcessor(new CommonPreprocessor());
//			 Word2Vec vec = new Word2Vec.Builder()
//			 .minWordFrequency(5)
//			 .iterations(1)
//			 .layerSize(100)
//			 .seed(42)
//			 .windowSize(5)
//			 .iterate(iter)
//			 .tokenizerFactory(t)
//			 .build();
//			 vec.fit();
//			 //List<String> lst = vec.wordsNearestSum("day", 10);
//			// System.out.println("10 Words closest to 'try': {}"+ lst);
//			 System.out.println(vec.getWordVectorsMean(methodContent));
//			 System.out.println("-------------------------------------------------------------------------------");
//			 List<String> lkj=Arrays.asList("if","try","List<Enityt>");
//			 System.out.println(vec.getWordVectorsMean(lkj));
//			System.out.println(cosineSimForSentence(vec,methodContent,lkj));
//		//	 methodWordVecs.put(transformedMethod.getKey(), vec);
//		});
//	}
//	
//	public static double cosineSimForSentence(Word2Vec vector, List<String> methodContentOne,List<String> methodContentTwo){
//		try{
//			return Transforms.cosineSim(vector.getWordVectorsMean(methodContentOne), vector.getWordVectorsMean(methodContentTwo));
//		}catch(Exception e){
//		}
//		return Transforms.cosineSim(vector.getWordVectorsMean(methodContentOne), vector.getWordVectorsMean(methodContentTwo));
//	}
//}
//
