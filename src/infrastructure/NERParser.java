package infrastructure;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;  
import edu.stanford.nlp.ie.crf.CRFClassifier;  
import edu.stanford.nlp.ling.CoreLabel;  

public class NERParser {
	static AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions("ner-model.ser.gz");
	static String parse (String content) {
		return classifier.classifyToString(content);
	}
}
