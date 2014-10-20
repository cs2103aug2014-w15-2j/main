package test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class NERTest {
	static final String TERMINATOR = " \n";
	static AbstractSequenceClassifier<CoreLabel> classifierOverall = CRFClassifier.getClassifierNoExceptions("NLPTraining/overall-ner-model.ser.gz");
	static AbstractSequenceClassifier<CoreLabel> classifierTag = CRFClassifier.getClassifierNoExceptions("NLPTraining/tag-ner-model.ser.gz");
	static AbstractSequenceClassifier<CoreLabel> classifierCommand = CRFClassifier.getClassifierNoExceptions("NLPTraining/command-ner-model.ser.gz");
	
	
	@Test
	public void testOverall1() {
		String content = "Add gourp meeting, this friday";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> , <DATE>this friday</DATE>";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall1:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
	
	@Test
	public void testOverall2() {
		String content = "Add gourp meeting this friday";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>this friday</DATE>";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall2:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
	
	@Test
	public void testOverall3() {
		String content = "Add gourp meeting today";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>today</DATE>";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall3:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
	
	@Test
	public void testOverall4() {
		String content = "Add gourp meeting tomorrow";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>tomorrow</DATE>";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall4:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
	

}
