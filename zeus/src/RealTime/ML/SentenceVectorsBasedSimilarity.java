//package RealTime.ML;
//
//import java.io.IOException;
//
//import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
//import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
//import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.ops.transforms.Transforms;
//
//public class SentenceVectorsBasedSimilarity implements SimilarityMeasure {
//static final ParagraphVectors PARAGRAPHVECS=loadParagraphVectors();
//static final String PARAGRAPHVECTORMODELPATH="";
//	@Override
//	public double getSimilarity(String sentence1, String sentence2) {
//		double predictedScore=0;
//		if (PARAGRAPHVECS != null) {
//			try{
//				INDArray inferredVectorA = produceParagraphVectorOfGivenSentence(sentence1);
//				INDArray inferredVectorB = produceParagraphVectorOfGivenSentence(sentence2);
//				predictedScore=Transforms.cosineSim(inferredVectorA, inferredVectorB);
//			}catch(Exception e){
////				StringMetric metric=StringMetrics.qGramsDistance();
////				predictedScore=metric.compare(sentence1,sentence2);
//			}
//		}
//		return predictedScore;
//	}
//
//	public INDArray produceParagraphVectorOfGivenSentence(String sentence) {
//		INDArray inferredVectorA = null;
//		if (PARAGRAPHVECS != null) {
//			inferredVectorA = PARAGRAPHVECS.inferVector(sentence.toLowerCase()
//					.trim());
//			return inferredVectorA;
//		}
//		return inferredVectorA;
//	}
//	private static ParagraphVectors loadParagraphVectors(){
//		ParagraphVectors paragraphVectors=null;
//		try{
//			paragraphVectors=WordVectorSerializer.readParagraphVectors(PARAGRAPHVECTORMODELPATH);
//			 TokenizerFactory t = new DefaultTokenizerFactory();
//			 t.setTokenPreProcessor(new CommonPreprocessor());
//			 paragraphVectors.setTokenizerFactory(t);
//			 paragraphVectors.getConfiguration().setIterations(10);
//		}catch(IOException e){
//			
//		}
//		return paragraphVectors;
//	}
//	public static void main(String[] args) {
//		
//	}
//}
