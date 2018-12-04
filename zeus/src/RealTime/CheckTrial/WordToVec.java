//package RealTime.CheckTrial;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import org.deeplearning4j.models.word2vec.Word2Vec;
//import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
//import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
//import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
//
//public class WordToVec {
//
//	 
//	 public static void main(String[] args) throws Exception {
//	 
//	 // Gets Path to Text file
//	// String filePath = new ClassPathResource("resources/raw.txt").getFile().getAbsolutePath();
//		 String filePath = "C:\\HUE\\WorkSpace\\Develop\\Zeus\\src\\resources\\raw.txt";
//	 
//	 System.out.println("Load & Vectorize Sentences....");
//	 // Strip white space before and after for each line
//	 SentenceIterator iter = new BasicLineIterator(filePath);
//	 // Split on white spaces in the line to get words
//	 TokenizerFactory t = new DefaultTokenizerFactory();
//	 
//	 /*
//	 CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
//	 So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
//	 Additionally it forces lower case for all tokens.
//	 */
//	 t.setTokenPreProcessor(new CommonPreprocessor());
//	 
//	 System.out.println("Building model....");
//	 Word2Vec vec = new Word2Vec.Builder()
//	 .minWordFrequency(5)
//	 .iterations(1)
//	 .layerSize(100)
//	 .seed(42)
//	 .windowSize(5)
//	 .iterate(iter)
//	 .tokenizerFactory(t)
//	 .build();
//	 
//	 System.out.println("Fitting Word2Vec model....");
//	 vec.fit();
//	 System.out.println("Writing word vectors to text file....");
//	 
//	 // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
//	 System.out.println("Closest Words:");
//	 Collection<String> lst = vec.wordsNearestSum("day", 10);
//	 System.out.println("10 Words closest to 'day': {}"+ lst);
//	System.out.println("Score:   ");
//	 // TODO resolve missing UiServer
//	// UiServer server = UiServer.getInstance();
//	// System.out.println("Started on port " + server.getPort());
//	 }
//	}