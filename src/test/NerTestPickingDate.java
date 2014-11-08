package test;

import static org.junit.Assert.*;

import org.junit.Test;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


public class NerTestPickingDate {
	static final String TERMINATOR = "\n";
	static AbstractSequenceClassifier<CoreLabel> classifierOverall = CRFClassifier.getClassifierNoExceptions("src/NLPTraining/overall-ner-model.ser.gz");
	static AbstractSequenceClassifier<CoreLabel> classifierTag = CRFClassifier.getClassifierNoExceptions("NLPTraining/tag-ner-model.ser.gz");
	static AbstractSequenceClassifier<CoreLabel> classifierCommand = CRFClassifier.getClassifierNoExceptions("NLPTraining/command-ner-model.ser.gz");
	
	
	/**
	 * These test cases tests whether the natural language processor can pick out the DATE fragment like "this Friday"/"this Wednesday";
	 */
	@Test
	public void testDate1_1() {
		//1-1 test when "Friday" is not capitalized
		String content1 = "Add gourp meeting this friday";
		String expected1 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>this friday</DATE> ";
		String results1 = classifierOverall.classifyToString(content1, "inlineXML", false);
		System.out.println("testOverall 1-1:");
		System.out.println("expects: " + expected1);
		System.out.println("results: " + results1);
		
		assertEquals(expected1 + TERMINATOR, results1);		
	}
	
	@Test
	public void testDate1_2(){
		//1-2 test when "Wednesday" is not capitalized
		String content2 = "Add gourp meeting this Wednesday";
		String expected2 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>this Wednesday</DATE> ";
		String results2 = classifierOverall.classifyToString(content2, "inlineXML", false);
		System.out.println("testOverall 1-2:");
		System.out.println("expects: " + expected2);
		System.out.println("results: " + results2);
		
		assertEquals(expected2 + TERMINATOR, results2);
	}
	
	@Test
	public void testDate1_3(){
		//1-3 test when an abbreviation is used
		String content3 = "Add gourp meeting this Wed";
		String expected3 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>this Wed</DATE> ";
		String results3 = classifierOverall.classifyToString(content3, "inlineXML", false);
		System.out.println("testOverall 1-3:");
		System.out.println("expects: " + expected3);
		System.out.println("results: " + results3);
		
		assertEquals(expected3 + TERMINATOR, results3);
	}
	
	
	/**
	 * these test cases test whether the natural language processor can pick out the DATE fragment like "October 31"
	 */
	@Test
	public void testDate2_1() {
		
		//2-1 Date formated as "MMMM dd"
		String content1 = "Add gourp meeting October 31";
		String expected1 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>October 31</DATE> ";
		String results1 = classifierOverall.classifyToString(content1, "inlineXML", false);
		System.out.println("testOverall 2-1:");
		System.out.println("expects: " + expected1);
		System.out.println("results: " + results1);
		
		assertEquals(expected1 + TERMINATOR, results1);
	}
	
	@Test
	public void testDate2_2() {
		//2-2 Date formated as "MMMM dd", but with abbreviation
		String content2 = "Add gourp meeting Oct 31";
		String expected2 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>Oct 31</DATE> ";
		String results2 = classifierOverall.classifyToString(content2, "inlineXML", false);
		System.out.println("testOverall 2-2:");
		System.out.println("expects: " + expected2);
		System.out.println("results: " + results2);
		
		assertEquals(expected2 + TERMINATOR, results2);
	}

	@Test
	public void testDate2_3() {
		//2-3 Date formated as "dd MMMM", but with abbreviation
		String content3 = "Add gourp meeting 31 Oct";
		String expected3 = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>31 Oct</DATE> ";
		String results3 = classifierOverall.classifyToString(content3, "inlineXML", false);
		System.out.println("testOverall 2-3:");
		System.out.println("expects: " + expected3);
		System.out.println("results: " + results3);
		
		assertEquals(expected3 + TERMINATOR, results3);
	}
	
	/**
	 * these test cases test whether the natural language processor can pick out the DATE fragment like "today"/"tomorrow"
	 */
	@Test
	public void testDate3_1() {
		String content = "Add gourp meeting today";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>today</DATE> ";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall 3-1:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
	
	@Test
	public void testDate3_2() {
		String content = "Add gourp meeting tomorrow";
		String expected = "<COMMAND>Add</COMMAND> <DESCRIPTION>gourp meeting</DESCRIPTION> <DATE>tomorrow</DATE> ";
		String results = classifierOverall.classifyToString(content, "inlineXML", false);
		System.out.println("testOverall 3-2:");
		System.out.println("expects: " + expected);
		System.out.println("results: " + results);
		
		assertEquals(expected + TERMINATOR, results);
	}
}
