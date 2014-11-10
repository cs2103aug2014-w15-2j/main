//@author A0119379R

package infrastructure;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import infrastructure.Constant.COMMAND_TYPE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import model.*;

public class NerParser {
	private AbstractSequenceClassifier<CoreLabel> classifierTag;
	private AbstractSequenceClassifier<CoreLabel> classifierCommand;
	private AbstractSequenceClassifier<CoreLabel> classifierTime;
	private AbstractSequenceClassifier<CoreLabel> classifierPriority;

	private AbstractSequenceClassifier<CoreLabel> classifierTimePicker;
	private AbstractSequenceClassifier<CoreLabel> classifierCommandPicker;
	private AbstractSequenceClassifier<CoreLabel> classifierDescriptionPicker;
	private AbstractSequenceClassifier<CoreLabel> classifierTagPicker;
	private AbstractSequenceClassifier<CoreLabel> classifierPriorityPicker;

	private Properties props;
	private AnnotationPipeline pipeline;

	private boolean isTimeChanged = false;
	private boolean isTagChanged = false;
	private boolean isDescriptionChanged = false;
	private boolean isPriorityChanged = false;

	private static boolean isTimeModelUpdate = false;
	private static boolean isTagModelUpdate = false;
	private static boolean isDescriptionModelUpdate = false;
	private static boolean isIndexModelUpdate = false;
	private static boolean isPriorityModelUpdate = false;
	private static boolean isCommandModelUpdate = false;
	
	
/**
 * ==========================================================================================================================
 *  Constructor and Initialization
 * ==========================================================================================================================
 */
	/**
	 * Constructor
	 * 
	 * In the constructor, NER models and Time parser will be loaded for subsequence use.
	 */
	public NerParser() {
		loadNerModels();
		loadTimeParser();
	}

	/**
	 * loadTimeParser:
	 * load the NLP time parser into the parser.
	 */
	private void loadTimeParser() {
		// Time parsers
		props = new Properties();
		props.put(Constant.TIME_PARSER_SUTIME_BINDERS, Constant.TIME_PARSER_ARGUMENT_2);
		props.put(Constant.TIME_PARSER_SUTIME_RULES, Constant.TIME_PARSER_FILE_PATH);
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator(Constant.TIME_PARSER_KEY_SUTIME, props));
	}

	
	/**
	 * loadNerModels
	 * load the user's customized natural language model into the system
	 */
	private void loadNerModels() {
		ArrayList<AbstractSequenceClassifier<CoreLabel>> classifiers = new ArrayList<AbstractSequenceClassifier<CoreLabel>>();
		
		for (int i = 0; i < 11; i ++) {
			try {
				classifiers.add(CRFClassifier.getClassifierNoExceptions(Constant.GZS_USER[i]));
			} catch (Exception e) {
				updateModal(Constant.PROPS_USER[i]);
				classifiers.add(CRFClassifier.getClassifierNoExceptions(Constant.GZS_USER[i]));
			}
		}
		
		
		//get the corresponding classifier
		//NOTE: classifier.get(4) and classifier.get(9) are for NLP index parsing,
		//		however, the NLP index parser is finally taken out of the system.
		classifierTimePicker = classifiers.get(0);
		classifierDescriptionPicker = classifiers.get(1);
		classifierTagPicker = classifiers.get(2);
		classifierPriorityPicker = classifiers.get(3);
		classifierCommandPicker = classifiers.get(5);
		classifierTime = classifiers.get(6);
		classifierTag = classifiers.get(7);
		classifierPriority = classifiers.get(8);
		classifierCommand = classifiers.get(10);
	}

	
/**
 * ==========================================================================================================================
 *  NER pickers
 * ==========================================================================================================================
 */

	/**
	 * pickCommand:
	 * pick out the command fragments and translate to the COMMAND_TYPE enumeration
	 * 
	 * @param userInputString			the String that user directly input to the system
	 * @return							the COMMAND_TYPE enumeration representation of the user command
	 * @throws CommandFailedException	CommandFailedException thrown when the subsequent parseCommand method goes wrong
	 */
	public COMMAND_TYPE pickCommand(String userInputString) throws CommandFailedException {
		
		userInputString = userInputString.toLowerCase();
		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_COMMAND);
		
		String directParseCommand = NerParser.pickTheTagged(userInputString,
				Constant.XML_TAG_COMMAND);
		
		ArrayList<String> commandList = new ArrayList<String>();
		if (directParseCommand != null) {
			//this means the user has input a sentence with <COMMAND> </COMMAND> tags to force the system to learn
			commandList.add(directParseCommand);
		} else {
			String xmlStr = classifierCommandPicker.classifyToString(
					userInputString, Constant.PARSING_STYLE_INLINE_XML, false);
			//System.err.println("XML STRING - pickCommand: " + xmlStr);
			HashMap<String, ArrayList<String>> result = NerParser
					.parseToMap(xmlStr);
			commandList = result.get(Constant.MAP_KEY_COMMAND);
		}
		
		
		if (commandList == null || commandList.size() == 0) {
			return COMMAND_TYPE.ADD;
		} else {
			return this.parseCommand(commandList);
		}
	}

	/**
	 * pickTimeInterval:
	 * pick out the date fragments from an unparsed input string and 
	 * translate to a TimeInterval object
	 * 
	 * @param userInputString			the String that user directly input to the system
	 * @return							a TimeInterval object representing the time of this task
	 * @throws CommandFailedException	CommandFailedException thrown when the subsequent time parsing or TimeInterval constructing goes wrong.
	 */
	public TimeInterval pickTimeInterval(String userInputString) throws CommandFailedException {
		
		this.isTimeChanged = false;
		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_TIME);

		String directParseTime = NerParser.pickTheTagged(userInputString,
				Constant.XML_TAG_TIME);
		if (directParseTime != null) {
			this.isTimeChanged = true;
			ArrayList<String> results = new ArrayList<String>(Arrays.asList(directParseTime.split(Constant.SPLITOR_COMMA)));
			TimeInterval returningInterval = parseTimeInterval(results);
			if (isDeadlineTask(results) || returningInterval.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
				Calendar c = UtilityMethod.dateToCalendar(returningInterval.getEndDate());
				if (c.get(Calendar.HOUR_OF_DAY) == Constant.CALENDAR_START_HOUR && c.get(Calendar.MINUTE) == Constant.CALENDAR_START_MINUTE) {
					c.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
					c.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
					return new TimeInterval(Constant.DEADLINE_START_DATE, c.getTime());
				} else {
					return new TimeInterval(Constant.DEADLINE_START_DATE, returningInterval.getEndDate());	
				}
			} else {
				return returningInterval;
			}
		}

		ArrayList<String> resultList = new ArrayList<String>();
		String xmlStr = classifierTimePicker.classifyToString(userInputString,
				Constant.PARSING_STYLE_INLINE_XML, false);
//		System.err.println("XML STRING - pickDate: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NerParser
				.parseToMap(xmlStr);
		resultList = result.get(Constant.MAP_KEY_DATE);

		if (resultList == null) {
			return new TimeInterval();
		} else {
			this.isTimeChanged = true;
			TimeInterval returningInterval = parseTimeInterval(resultList);
			if (isDeadlineTask(resultList) || returningInterval.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
				Calendar c = UtilityMethod.dateToCalendar(returningInterval.getEndDate());
				if (c.get(Calendar.HOUR_OF_DAY) == Constant.CALENDAR_START_HOUR && c.get(Calendar.MINUTE) == Constant.CALENDAR_START_MINUTE) {
					c.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
					c.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
					return new TimeInterval(Constant.DEADLINE_START_DATE, c.getTime());
				} else {
					return new TimeInterval(Constant.DEADLINE_START_DATE, returningInterval.getEndDate());
				}
			} else {
				return returningInterval;
			}
		}
	}


	/**
	 * pickDescription:
	 * pick out the description segments from an unparsed user input String
	 * 
	 * @param userInputString	the String that user directly input to the system
	 * @return					a String representing the task description
	 */
	public String pickDescription(String userInputString)
			throws CommandFailedException {
		this.isDescriptionChanged = false;
		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_DESCRIPTION);
		
		String directParseDescription = NerParser.pickTheTagged(userInputString,
				Constant.XML_TAG_DESCRIPTION);
		if (directParseDescription != null) {
			this.isDescriptionChanged = true;
			return directParseDescription;
		}

		String xmlStr = classifierDescriptionPicker.classifyToString(
				userInputString, Constant.PARSING_STYLE_INLINE_XML, false);
		//System.err.println("XML STRING - pickDescription: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NerParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get(Constant.MAP_KEY_DESCRIPTION);
		if (resultList == null || resultList.size() == Constant.LIST_SIZE_EMPTY) {
			return Constant.EMPTY_STRING;
		} else {
			this.isDescriptionChanged = true;
			return resultList.get(Constant.LIST_INDEX_FIRST);
			
		}
	}
	
	/**
	 * pickIndex:
	 * pick out the index segment and translate it to integer
	 * no NLP involved in this method
	 * 
	 * @param userInputString			the String that user directly input to the system
	 * @return							a list of integers representing the indices contained in the input string
	 * @throws CommandFailedException	CommandFailedException thrown when the index couldn't be interpreted.
	 */
	public ArrayList<Integer> pickIndex(String userInputString) throws CommandFailedException {
		String indexString = UtilityMethod.removeFirstWord(userInputString);
		ArrayList<Integer> results = new ArrayList<Integer>();
		try {
			String[] indices = indexString.split(Constant.SPLITOR_SPACE);
			for (String thisIndex : indices) {
				Integer index = new Integer(Integer.parseInt(thisIndex.trim()));
				if (!results.contains(index)) {
					results.add(index);
				}
			}
		} catch (Exception e1) {
			if (results.size() != Constant.LIST_SIZE_EMPTY) {
				return results;
			} 
			
			try {
				String[] indices = indexString.split(Constant.SPLITOR_COMMA);
				for (String thisIndex : indices) {
					results.add(Integer.parseInt(thisIndex.trim()));
				}
			} catch (Exception e2) {
				if (results.size() != Constant.LIST_SIZE_EMPTY) {
					return results;
				}
				throw new CommandFailedException(Constant.EXCEPTION_MESSAGE_INDEX_NOT_PARSABLE);
			}
		}
		return results;
	}
	

//@author A0119379-unused
//This method is aborted for the unreliable performance. 
//We decided to limit the flexibility to some extend to unsure the reliability.
//
//
//	/**
//	 * pick out the index segment and translate it to integer
//	 * NLP involved in this method
//	 * 
//	 * @param userInputString			the String that user directly input to the system
//	 * @return							a list of integers representing the indices contained in the input string
//	 * @throws CommandFailedException	CommandFailedException thrown when the index couldn't be interpreted.
//	 */
//	public ArrayList<Integer> pickIndex(String userInputString, String a) throws CommandFailedException {
//
//		userInputString = userInputString.toLowerCase();
//		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_INDEX);
//		String directParseIndex = NerParser.pickTheTagged(userInputString,
//				Constant.XML_TAG_INDEX);
//		try {
//			if (directParseIndex != null) {
//				ArrayList<String> results = new ArrayList<String>();
//				results.add(directParseIndex);
//				return parseIndex(results);
//			}
//
//			String xmlStr = classifierIndexPicker.classifyToString(
//					userInputString, "inlineXML", false);
//			System.err.println("XML STRING - pickIndex: " + xmlStr);
//			HashMap<String, ArrayList<String>> result = NerParser
//					.parseToMap(xmlStr);
//			ArrayList<String> resultList = result.get("INDEX");
//			if (resultList == null || resultList.size() == 0) {
//				throw new CommandFailedException("No index found!");
//			} else {
//				return parseIndex(resultList);
//			}
//
//		} catch (Exception e) {
//			String xmlStr = classifierIndexPicker.classifyToString(
//					userInputString, "inlineXML", false);
//			//System.err.println("XML STRING - pickIndex: " + xmlStr);
//			HashMap<String, ArrayList<String>> result = NerParser
//					.parseToMap(xmlStr);
//			ArrayList<String> resultList = result.get("INDEX");
//			if (resultList == null || resultList.size() == 0) {
//				throw new CommandFailedException("No index found!");
//			} else {
//				return parseIndex(resultList);
//			}
//		}
//	}

	
	/**
	 * pickTag:
	 * pick out the tag segments from an unparsed user input string
	 * 
	 * @param userInputString	the String that user directly input to the system
	 * @return					a list of String representing the tags contains in the user input string
	 */
	public ArrayList<String> pickTag(String userInputString) {
		this.isTagChanged = false;
		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_TAG);
		String directParseTag = NerParser.pickTheTagged(userInputString,
				Constant.XML_TAG_TAG);
		if (directParseTag != null) {
			this.isTagChanged = true;
			ArrayList<String> results = new ArrayList<String>();
			results.add(directParseTag);
			return parseTag(results);
		}

		String xmlStr = classifierTagPicker.classifyToString(userInputString,
				Constant.PARSING_STYLE_INLINE_XML, false);
		//System.err.println("XML STRING - pickTag: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NerParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get(Constant.MAP_KEY_TAG);
		if (resultList == null || resultList.size() == Constant.LIST_SIZE_EMPTY) {
			return new ArrayList<String>();
		} else {
			this.isTagChanged = true;
			return parseTag(resultList);
		}
	}

	
	/**
	 * pickPriority:
	 * pick out the priority segments from the unparsed user input string
	 * 
	 * @param userInputString	the String that user directly input to the system
	 * @return					an integer representing the priority for the task
	 */
	public int pickPriority(String userInputString) {
		
		this.isPriorityChanged = false;
		userInputString = removeTheTagged(userInputString, Constant.XML_TAG_PRIORITY);
		String directParsePriority = NerParser.pickTheTagged(userInputString,
				Constant.XML_TAG_PRIORITY);
		if (directParsePriority != null) {
			this.isPriorityChanged = true;
//			System.err.println("DIRECT_PARSE_PRIORITY - pickPriority: " + directParsePriority);
			return parsePriority(directParsePriority);
		}

		String xmlStr = classifierPriorityPicker.classifyToString(
				userInputString, Constant.PARSING_STYLE_INLINE_XML, false);
//		System.err.println("XML STRING - pickPriority: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NerParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get(Constant.MAP_KEY_PRIORITY);
		if (resultList == null || resultList.size() == Constant.LIST_SIZE_EMPTY) {
			return Constant.PRIORITY_DEFAULT;
		} else {
			this.isPriorityChanged = true;
			return parsePriority(resultList.get(Constant.LIST_INDEX_FIRST));
		}
	}
	
	
/**
 * ==========================================================================================================================
 *  Methods used to write back to the training file for learning
 * ==========================================================================================================================
 */
	
	/**
	 * pickTheTagged:
	 * pick out the String segment that is tagged by a user 
	 * (which the user want to force the system to interpreted the given string as the give type)
	 * 
	 * @param inputString	the String that user directly input to the system
	 * @param type			the String indicates the tag (<type> and </type>)
	 * @return				the String segment wrapped between the two tag
	 */
	public static String pickTheTagged(String inputString, String type) {
		String prefix = Constant.TAG_OPEN_FORMER + type + Constant.TAG_CLOSE;
		String postfix = Constant.TAG_OPEN_LATTER + type + Constant.TAG_CLOSE;
		int prefixIndex = inputString.indexOf(prefix);
		int postfixIndex = inputString.indexOf(postfix);
		
		if (prefixIndex == Constant.LIST_INDEX_NOT_EXISTING || postfixIndex == Constant.LIST_INDEX_NOT_EXISTING) {
			return null;
		} else {
			System.out.println(inputString);
			return inputString.substring(prefixIndex + prefix.length(),
					postfixIndex);
		}
	}

	/**
	 * removeTheTagged:
	 * remove the segment wrapped in a pair of tags, the tags will be removed as well 
	 * 
	 * @param inputString	the String that user directly input to the system
	 * @param t			the String indicates the tag (<t> and </t>)
	 * @return				the String segment wrapped between the two tag
	 */
	public static String removeTheTagged (String inputString, String t) {
		String[] types = {Constant.XML_TAG_DESCRIPTION, Constant.XML_TAG_TIME, Constant.XML_TAG_TAG,
		                  Constant.XML_TAG_PRIORITY, Constant.XML_TAG_INDEX, Constant.XML_TAG_COMMAND};
		for (String type : types) {
			if (!type.equals(t)) {
				String prefix = Constant.TAG_OPEN_FORMER + type + Constant.TAG_CLOSE;
				String postfix = Constant.TAG_OPEN_LATTER + type + Constant.TAG_CLOSE;
				int prefixIndex = inputString.indexOf(prefix);
				int postfixIndex = inputString.indexOf(postfix);
				if (prefixIndex != Constant.LIST_INDEX_NOT_EXISTING && postfixIndex != Constant.LIST_INDEX_NOT_EXISTING) {
					inputString = inputString.substring(Constant.LIST_INDEX_FIRST, prefixIndex).trim() + Constant.SPLITOR_SPACE + inputString.substring(postfixIndex + postfix.length()).trim();
				}
			}
		}
		
		return inputString.trim();
	}

	/**
	 * translate a mixed list to a map containing a list with single tags
	 * e.g	[["task", "DESCRIPTION"], ["today", "DATE"]] will be translated to
	 * 		[["task", "DESCRIPTION"], ["today", "O"]] and [["task", "O"], ["today", "DATE"]]
	 * 
	 * @param wordPairs		a list of pair, whose head is a tag and tail is the tagged word
	 * @return				a map containing a serious of ArrayList, in which only a specific tag exists.
	 */
	public static HashMap<String, ArrayList<Pair<String, String>>> demux(ArrayList<Pair<String, String>> wordPairs) {
		HashMap<String, ArrayList<Pair<String, String>>> listMap = new HashMap<String, ArrayList<Pair<String, String>>>();
		ArrayList<Pair<String, String>> timeList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> tagList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> descriptionList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> priorityList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> indexList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> commandList = new ArrayList<Pair<String, String>>();
		
		for (Pair<String, String> p : wordPairs) {
			switch (p.tail) {
			case Constant.XML_TAG_TIME:
				//a trivial pair is a pair whose tail is XML_TAG_DEFAULT: "O"
				Pair<String, String> trivialPair1 = new Pair<String, String>(p.head, Constant.XML_TAG_DEFAULT);
				timeList.add(p);
				tagList.add(trivialPair1);
				descriptionList.add(trivialPair1);
				priorityList.add(trivialPair1);
				indexList.add(trivialPair1);
				commandList.add(trivialPair1);
				isTimeModelUpdate = true;
				break;

			case Constant.XML_TAG_TAG:
				Pair<String, String> trivialPair2 = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				tagList.add(p);
				timeList.add(trivialPair2);
				descriptionList.add(trivialPair2);
				priorityList.add(trivialPair2);
				indexList.add(trivialPair2);
				commandList.add(trivialPair2);
				isTagModelUpdate = true;
				break;

			case Constant.XML_TAG_DESCRIPTION:
				Pair<String, String> trivialPair3 = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				descriptionList.add(p);
				tagList.add(trivialPair3);
				timeList.add(trivialPair3);
				priorityList.add(trivialPair3);
				indexList.add(trivialPair3);
				commandList.add(trivialPair3);
				isDescriptionModelUpdate = true;
				break;

			case Constant.XML_TAG_PRIORITY:
				Pair<String, String> trivialPair4 = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				priorityList.add(p);
				tagList.add(trivialPair4);
				descriptionList.add(trivialPair4);
				timeList.add(trivialPair4);
				indexList.add(trivialPair4);
				commandList.add(trivialPair4);
				isPriorityModelUpdate = true;
				break;

			case Constant.XML_TAG_INDEX:
				Pair<String, String> trivialPair5 = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				indexList.add(p);
				tagList.add(trivialPair5);
				descriptionList.add(trivialPair5);
				priorityList.add(trivialPair5);
				timeList.add(trivialPair5);
				commandList.add(trivialPair5);
				isIndexModelUpdate = true;
				break;

			case Constant.XML_TAG_COMMAND:
				Pair<String, String> trivialPair6 = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				
				commandList.add(p);
				indexList.add(trivialPair6);
				tagList.add(trivialPair6);
				descriptionList.add(trivialPair6);
				priorityList.add(trivialPair6);
				timeList.add(trivialPair6);
				isCommandModelUpdate = true;
				break;
			
			default:
				Pair<String, String> trivialPair = new Pair<String, String>(
						p.head, Constant.XML_TAG_DEFAULT);
				commandList.add(trivialPair);
				indexList.add(trivialPair);
				tagList.add(trivialPair);
				descriptionList.add(trivialPair);
				priorityList.add(trivialPair);
				timeList.add(trivialPair);
			}
		}
		
		listMap.put(Constant.XML_TAG_TIME, timeList);
		listMap.put(Constant.XML_TAG_DESCRIPTION, descriptionList);
		listMap.put(Constant.XML_TAG_PRIORITY, priorityList);
		listMap.put(Constant.XML_TAG_INDEX, indexList);
		listMap.put(Constant.XML_TAG_TAG, tagList);
		listMap.put(Constant.XML_TAG_COMMAND, commandList);
		return listMap;
	}
	
	/**
	 * interpretXml
	 * interpret the XML string to a list of pair, whose head is a word and tail is the corresponding tag
	 * 
	 * @param xmlString		a well tagged XML string
	 * @return				an ArrayList of pairs [word, tag];
	 */
	public static ArrayList<Pair<String, String>> interpretXml(String xmlString) {
		
//		System.out.println("\n\n original: " + xmlString);
		xmlString = tokenize(xmlString);
		
//		System.out.println("\n\n\n\n\n\n xmlString: interpretXML: " + xmlString + "\n\n\n\n");

		String[] wordArray = xmlString.split(Constant.SPLITOR_SPACE);
		ArrayList<Pair<String, String>> wordPairs = new ArrayList<Pair<String, String>>();

		String currentKey = Constant.XML_TAG_DEFAULT;

		for (String currentWord : wordArray) {
			
			if (currentWord.contains(Constant.TAG_OPEN_LATTER) && currentWord.contains(Constant.TAG_CLOSE)
					&& currentWord.indexOf(Constant.TAG_CLOSE) > currentWord.indexOf(Constant.TAG_OPEN_LATTER)) {
				currentKey = Constant.XML_TAG_DEFAULT;

			} else if (currentWord.contains(Constant.TAG_OPEN_FORMER) && currentWord.contains(Constant.TAG_CLOSE)
					&& currentWord.indexOf(Constant.TAG_CLOSE) > currentWord.indexOf(Constant.TAG_OPEN_FORMER)) {
				if (!currentKey.equals(Constant.XML_TAG_DEFAULT)) {
					//System.err.println("interpretXML: currentKey != defaultKey ----- one word can only have a single tag");
					return null;
				}
				currentKey = currentWord.substring(
						currentWord.indexOf(Constant.TAG_OPEN_FORMER) + 1, currentWord.indexOf(Constant.TAG_CLOSE))
						.trim();
				setTrueModelUpdateIndicator(currentKey);
			} else if (!currentWord.equals(Constant.EMPTY_STRING)) {
				wordPairs.add(new Pair<String, String>(currentWord.trim(),
						currentKey.trim()));
			}
		}

		return wordPairs;
	}

	/**
	 * setTrueModelUpdateIndicator:
	 * set corresponding model update indicator to true
	 * 
	 * @param currentKey 	a string indicating the model update indicator
	 */
	private static void setTrueModelUpdateIndicator(String currentKey) {
		System.out.println(currentKey);
		switch (currentKey) {
		case Constant.XML_TAG_COMMAND:
			isCommandModelUpdate = true;
			break;
			
		case Constant.XML_TAG_DESCRIPTION:
			isDescriptionModelUpdate = true;
			break;
			
		case Constant.XML_TAG_INDEX:
			isIndexModelUpdate = true;
			break;
			
		case Constant.XML_TAG_PRIORITY:
			isPriorityModelUpdate = true;
			break;
			
		case Constant.XML_TAG_TAG:
			isTagModelUpdate = true;
			break;
			
		case Constant.XML_TAG_TIME:
			isTimeModelUpdate = true;
			break;
		}
	}
	
	/**
	 * tokenize:
	 * Tokenize a given string by insert spaces before or after some specific characters
	 * 
	 * @param userInputString	a string input by users
	 * @return
	 */
	private static String tokenize(String userInputString) {
		String[] symbols = {Constant.SPLITOR_COMMA, Constant.SPLITOR_DOT};
		
		userInputString = userInputString.replaceAll(Constant.TAG_OPEN_FORMER, Constant.TAG_OPEN_FORMER_SPACE);
		userInputString = userInputString.replaceAll(Constant.TAG_CLOSE, Constant.TAG_CLOSE_SPACE);
		
		for (String s : symbols) {
			userInputString = userInputString.replaceAll(s, Constant.SPLITOR_SPACE + s + Constant.SPLITOR_SPACE);
		}
		
		return userInputString;
	}
	
	/**
	 * updateTsvFile
	 * update a specific tsv file
	 * 
	 * @param list				a list contains the content to write 
	 * @param filePath			a filePath to write
	 * @throws IOException		IOException thrown is there are problems occurring during the IO process
	 */
	public static void updateTsvFile(ArrayList<Pair<String, String>> list,
			String filePath) throws IOException {

//		System.out.println("write to file path: " + filePath);
		
		FileWriter fw = new FileWriter(new File(filePath), true);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.newLine();
		for (Pair<String, String> p : list) {
			String thisLine = p.head + Constant.SPLITOR_TAB + p.tail;
			bw.newLine();
			bw.write(thisLine);
		}
		bw.newLine();
		
		//System.err.println("Finish writting!");

		bw.flush();
		bw.close();
		fw.close();
	}
	
	/**
	 * updateTsvFile
	 * update all the training data files relevant to the given xmlString
	 * 
	 * @param xmlString		update the tsv files with the content of a well tagged XML string
	 * @throws IOException	IOException thrown when problems occur during the IO process
	 */
	public static void updateTsvFile(String xmlString) throws IOException {
		//System.err.println("INPUT - updateTsvFile: " + xmlString);
		HashMap<String, ArrayList<Pair<String, String>>> listMap = demux(interpretXml(xmlString));
		ArrayList<Pair<String, String>> timeList = listMap
				.get(Constant.XML_TAG_TIME);
		ArrayList<Pair<String, String>> descriptionList = listMap
				.get(Constant.XML_TAG_DESCRIPTION);
		ArrayList<Pair<String, String>> tagList = listMap
				.get(Constant.XML_TAG_TAG);
		ArrayList<Pair<String, String>> indexList = listMap
				.get(Constant.XML_TAG_INDEX);
		ArrayList<Pair<String, String>> priorityList = listMap
				.get(Constant.XML_TAG_PRIORITY);
		ArrayList<Pair<String, String>> commandList = listMap
				.get(Constant.XML_TAG_COMMAND);
		
		if (isTimeModelUpdate) {
			//System.err.println("time list is not empty");
			updateTsvFile(timeList, Constant.FILE_PATH_TIME_PICKER_USER);
		}

		if (isDescriptionModelUpdate) {
			//System.err.println("description List is not empty");
			updateTsvFile(descriptionList,
					Constant.FILE_PATH_DESCRIPTION_PICKER_USER);
		}

		if (isTagModelUpdate) {
			//System.err.println("tag List is not empty");
			updateTsvFile(tagList, Constant.FILE_PATH_TAG_PICKER_USER);
		}

		if (isIndexModelUpdate) {
			//System.err.println("index List is not empty");
			updateTsvFile(indexList, Constant.FILE_PATH_INDEX_PICKER_USER);
		}

		if (isPriorityModelUpdate) {
			//System.err.println("priorityList is not empty");
			updateTsvFile(priorityList, Constant.FILE_PATH_PRIORITY_PICKER_USER);
		}
		
		if (isCommandModelUpdate) {
			//System.err.println("commandList is not empty");
			updateTsvFile(commandList, Constant.FILE_PATH_COMMAND_PICKER_USER);
		}
	}
	
	/**
	 * updateModal
	 * Regenerate the NLP model(*.gz file) with the property file
	 * This method will call the main method of CRFClassifier
	 * 
	 * @param propFilePath	the path of the property file
	 * @return				a boolean indicates whether the process is finished successfully
	 */
	public static boolean updateModal(String propFilePath) {
		try {
			//two parameters needed to feed CRFClassifier.main
			String[] arguments = new String[2];
			arguments[0] = "-prop";
			arguments[1] = propFilePath;
			CRFClassifier.main(arguments);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * updateModal:
	 * update all the NLP models (*.gz files)
	 */
	public static void updateModal() {
		updateModal(Constant.FILE_PATH_TIME_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_TAG_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_DESCRIPTION_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_PRIORITY_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_COMMAND_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_INDEX_PICKER_PROP_USER);
	}
	
/**
 * ==========================================================================================================================
 *  Methods will be directly called by MainViewController
 * ==========================================================================================================================
 */
	
	/**
	 * ADD: parse a task from the given string used when adding an task
	 * this method will by called when executing an add operation
	 * 
	 * @param userInputString			a string directly input by the user
	 * @return							a Task object specified by the given input string
	 * @throws CommandFailedException	CommandFailedException thrown if the subsequent parsing actions go wrong
	 */

	public Task getTask(String userInputString) {

		TimeInterval timeInterval;
		try {
			timeInterval = this.pickTimeInterval(userInputString);
		} catch (CommandFailedException e) {
			timeInterval = new TimeInterval();
		}
		
		String description;
		try {
			description = this.pickDescription(userInputString);
		} catch (CommandFailedException e) {
			description = Constant.EMPTY_STRING;
		}
		
		ArrayList<String> tag = this.pickTag(userInputString);
		
		int priority = this.pickPriority(userInputString);
		
		if (tag.isEmpty()) {
			tag.add(Constant.TASK_TAG_DEFAULT);
		}

		return new Task(description, priority, tag, timeInterval);
	}

	/**
	 * UPDATE: get updated keys and values
	 * this method will by called when executing an update operation
	 * 
	 * @param userInputStirng			a string directly input by the user
	 * @return							a HashMap, whose entries specific the key to update and the new value
	 * @throws CommandFailedException	CommandFailedException thrown if the subsequent parsing actions go wrong 
	 */
	public HashMap<String, Object> getUpdatedTaskMap(String userInputString) {

		TimeInterval timeInterval;
		try {
			timeInterval = this.pickTimeInterval(userInputString);
		} catch (CommandFailedException e) {
			timeInterval = new TimeInterval();
			this.isTimeChanged = false;
		}
		ArrayList<String> tag = this.pickTag(userInputString);
		String description;
		try {
			description = this.pickDescription(userInputString);
		} catch (CommandFailedException e) {
			description = Constant.EMPTY_STRING;
		}
		int priority = this.pickPriority(userInputString);

		HashMap<String, Object> updateAttributes = new HashMap<String, Object>();

		if (this.isTimeChanged) {
			//System.err.println("getUpdatedTaskMap: time updated to " + timeInterval);
			updateAttributes.put("time_interval", timeInterval);
		}

		if (this.isTagChanged) {
			//System.err.println("getUpdatedTaskMap: tag updated to " + tag);
			updateAttributes.put("tag", tag);
		}

		if (this.isDescriptionChanged) {
			//System.err.println("getUpdatedTaskMap: description updated to " + description);
			updateAttributes.put("description", description);
		}

		if (this.isPriorityChanged) {
			//System.err.println("getUpdatedTaskMap: priority updated to " + priority);
			updateAttributes.put("priority", priority);
		}

		return updateAttributes;
	}
	
	/**
	 * SEARCH: parse a search constraint used when searching for tasks
	 * this method will by called when executing an search operation
	 * 
	 * @param userIntputStirng			a string directly input by the user
	 * @return							a constraint specified by the given string
	 * @throws CommandFailedException	CommandFailedException thrown if the subsequent parsing actions go wrong
	 */
	public Constraint getConstraint(String userInputString) {
		TimeInterval timeInterval;
		try {
			timeInterval = this.pickTimeInterval(userInputString);
			String keyword = Constant.EMPTY_STRING;
			//System.err.println("timeInterval - getConstraint: " + timeInterval.toString());
			if (timeInterval.equals(new TimeInterval())) {
				keyword = UtilityMethod.removeFirstWord(userInputString);
			}
			String[] keywords = keyword.split(Constant.SPLITOR_SPACE);
			return new Constraint(keywords, timeInterval);
		} catch (CommandFailedException e) {
			String[] keywords = UtilityMethod.removeFirstWord(userInputString).split(Constant.SPLITOR_SPACE);
			return new Constraint(keywords, new TimeInterval());
		}

	}

	
/**
 * ==========================================================================================================================
 *  Methods used for specific task component parsing
 * ==========================================================================================================================
 */
	//time parsing
	
	/**
	 * parseTimeStringToXML
	 * Break the given String and remove irrelevant words.
	 * 
	 * @param timeString	a string containing the picked-out time segment
	 * @return				a XML string with tags (<DATE> and </DATE>)
	 */
	public String parseTimeStringToXML(String timeString) {
		return classifierTime.classifyToString(timeString, Constant.PARSING_STYLE_INLINE_XML, false);
	}
	
	/**
	 * parseTimeListToDate
	 * Using the natural language processor to translate the picked out time input fragments to date
	 * 
	 * @param timeList	a list of time strings
	 * @return			a list of Date objects interpreted from the corresponding time strings
	 */
	public ArrayList<Date> parseTimeListToDate(ArrayList<String> timeList) {

		ArrayList<Date> results = new ArrayList<Date>();
		SimpleDateFormat format = new SimpleDateFormat(Constant.DATE_FORMAT_BASE);
		String stringForToday = format.format(Calendar.getInstance().getTime());

		for (String text : timeList) {
			Annotation annotation = new Annotation(text);
			annotation.set(CoreAnnotations.DocDateAnnotation.class,
					stringForToday);
			pipeline.annotate(annotation);
			List<CoreMap> timexAnnsAll = annotation
					.get(TimeAnnotations.TimexAnnotations.class);
			for (CoreMap cm : timexAnnsAll) {
				String interpretedTimeString = cm
						.get(TimeExpression.Annotation.class).getTemporal()
						.toString();
				Date d = parseTimeStringToDate(interpretedTimeString);
				Calendar c = UtilityMethod.dateToCalendar(d);
				if (d != null) {
					if (c.get(Calendar.SECOND) == Constant.CALENDAR_WEEK_IN_SECOND) {
						//System.err.println("parsing weeks");
						c.add(Calendar.DATE, Constant.CALENDAR_WEEK_START_DAY_OFFSET);
						Date d1 = c.getTime();
						c.add(Calendar.WEEK_OF_YEAR, Constant.CALENDAR_INCREMENT_ONE);
						c.add(Calendar.DATE, Constant.LIST_INDEX_NOT_EXISTING);
						c.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
						c.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
						Date d2 = c.getTime();
						results.add(d1);
						results.add(d2);
					} else if (c.get(Calendar.SECOND) == Constant.CALENDAR_MONTH_IN_SECOND){
						Date d1 = c.getTime();
						c.add(Calendar.MONTH, Constant.CALENDAR_INCREMENT_ONE);
						c.add(Calendar.DATE, Constant.LIST_INDEX_NOT_EXISTING);
						c.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
						c.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
						Date d2 = c.getTime();
						results.add(d1);
						results.add(d2);
					} else if (c.get(Calendar.SECOND) == Constant.CALENDAR_YEAR_IN_SECOND){
						Date d1 = c.getTime();
						c.add(Calendar.YEAR, Constant.CALENDAR_INCREMENT_ONE);
						c.add(Calendar.DATE, Constant.LIST_INDEX_NOT_EXISTING);
						c.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
						c.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
						Date d2 = c.getTime();
						results.add(d1);
						results.add(d2);
					} else {
						results.add(c.getTime());
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * parseTimeStringToDate
	 * Parse a time string to a Date Object (called by parseTimeListToDate)
	 * 
	 * @param timeString	a string specified a time
	 * @return				a Date object create with the time
	 */
	private Date parseTimeStringToDate(String timeString) {
		// possible formats:
		// 2014-10-15T14:00
		// 2014-10-24-WXX-5
		// 2014-10-24
		// 2014-02
		// 2014

		System.err.println("INPUT TIME STRING - parseStringToDate: " + timeString);

		Date date = null;
		try {
			if (timeString.length() == Constant.DATE_STRING_LENGTH_YEAR) {
				date = new SimpleDateFormat(Constant.DATE_FORMAT_YEAR, Locale.ENGLISH)
						.parse(timeString);
				Calendar c = UtilityMethod.dateToCalendar(date);
				c.set(Calendar.SECOND, Constant.CALENDAR_YEAR_IN_SECOND);
				date = c.getTime();
			} else if (timeString.length() == Constant.DATE_STRING_LENGTH_MONTH) {
				date = new SimpleDateFormat(Constant.DATE_FORMAT_YEAR_MONTH, Locale.ENGLISH)
						.parse(timeString);
				Calendar c = UtilityMethod.dateToCalendar(date);
				c.set(Calendar.SECOND, Constant.CALENDAR_MONTH_IN_SECOND);
				date = c.getTime();
			} else if (timeString.length() == Constant.DATE_STRING_LENGTH_BASE) {
				date = new SimpleDateFormat(Constant.DATE_FORMAT_BASE, Locale.ENGLISH)
						.parse(timeString);
			} else if (timeString.length() == Constant.DATE_STRING_LENGTH_WEEK) {
				timeString = timeString.replaceFirst(Constant.DATE_SPECIAL_CHAR_W, Constant.EMPTY_STRING);
				date = new SimpleDateFormat(Constant.DATE_FORMAT_WEEK, Locale.ENGLISH)
						.parse(timeString);
				Calendar c = UtilityMethod.dateToCalendar(date);
				c.set(Calendar.SECOND, Constant.CALENDAR_WEEK_IN_SECOND);
				date = c.getTime();
				//System.err.println("Parsing week: " + date);
			} else if (timeString.length() == Constant.DATE_STRING_LENGTH_MINUTE) {
				if (timeString.charAt(Constant.DATE_SPECIAL_CHAR_T_INDEX) == Constant.DATE_SPECIAL_CHAR_T) {
					timeString = timeString.replace(Constant.DATE_SPECIAL_CHAR_T_STRING, Constant.SPLITOR_SPACE);
					date = new SimpleDateFormat(Constant.DATE_FORMAT_MINUTE,
							Locale.ENGLISH).parse(timeString);
				} else {
					timeString = timeString.substring(0, Constant.DATE_SPECIAL_CHAR_T_INDEX);
					date = new SimpleDateFormat(Constant.DATE_FORMAT_BASE, Locale.ENGLISH)
							.parse(timeString);
				}
			} else {
				Constant.logger.log(Level.INFO, "unrecognized format: " + timeString);
				// will return null
			}
		} catch (ParseException e) {
			// will return null
			Constant.logger.log(Level.INFO, "parseStringToDate gets expection, returns null");
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * parseTimeInterval
	 * Get a TimeInterval object from a list of time strings
	 * 
	 * @param timeStringList			a list of time strings
	 * @return							a TimeInterval object specified by the list of strings
	 * @throws CommandFailedException	CommandFailedException thrown if the process goes wrong
	 */
	public TimeInterval parseTimeInterval(ArrayList<String> timeStringList) throws CommandFailedException {
		ArrayList<Date> dates = parseTimeListToDate(timeStringList);
		assert (dates != null);
		TimeInterval interval = new TimeInterval();

		if (dates.size() == Constant.DATE_SIZE_ONE) {
			Calendar c = UtilityMethod.dateToCalendar(dates.get(Constant.LIST_INDEX_FIRST));
			if (c.get(Calendar.HOUR_OF_DAY) != Constant.CALENDAR_START_HOUR || c.get(Calendar.MINUTE) != Constant.CALENDAR_START_MINUTE) {
				interval = new TimeInterval(Constant.DEADLINE_START_DATE, c.getTime());
			} else {
				Calendar cEnd = UtilityMethod.dateToCalendar(dates.get(Constant.LIST_INDEX_FIRST));
				cEnd.set(Calendar.HOUR_OF_DAY, Constant.CALENDAR_END_HOUR);
				cEnd.set(Calendar.MINUTE, Constant.CALENDAR_END_MINUTE);
				interval = new TimeInterval(c.getTime(), cEnd.getTime());
			}
			
		} else if (dates.size() == Constant.DATE_SIZE_TWO) {
			Date d0 = dates.get(0);
			Date d1 = dates.get(1);
			interval = new TimeInterval(
					UtilityMethod.selectEarlierDate(d0, d1),
					UtilityMethod.selectLaterDate(d0, d1));
		} else if (dates.size() == Constant.DATE_SIZE_THREE) {
			Calendar c1 = UtilityMethod.dateToCalendar(dates.get(0));
			Calendar c2 = UtilityMethod.dateToCalendar(dates.get(1));
			Calendar c3 = UtilityMethod.dateToCalendar(dates.get(2));
			int differentIndex = UtilityMethod.selectDifferentDate(c1, c2, c3);
			switch (differentIndex) {
			case 1:
				c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH));
				c3.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH));
				Date startDate1 = UtilityMethod.selectEarlierDate(c2.getTime(),
						c3.getTime());
				Date endDate1 = UtilityMethod.selectLaterDate(c2.getTime(),
						c3.getTime());
				interval = new TimeInterval(startDate1, endDate1);
				break;

			case 2:
				c1.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH));
				c3.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH));
				Date startDate2 = UtilityMethod.selectEarlierDate(c1.getTime(),
						c3.getTime());
				Date endDate2 = UtilityMethod.selectLaterDate(c1.getTime(),
						c3.getTime());
				interval = new TimeInterval(startDate2, endDate2);
				break;

			case 3:
				c2.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH),
						c3.get(Calendar.DAY_OF_MONTH));
				c1.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH),
						c3.get(Calendar.DAY_OF_MONTH));
				Date startDate3 = UtilityMethod.selectEarlierDate(c2.getTime(),
						c1.getTime());
				Date endDate3 = UtilityMethod.selectLaterDate(c2.getTime(),
						c1.getTime());
				interval = new TimeInterval(startDate3, endDate3);
				break;

			default:
				break;
			}
		} else {
			throw new CommandFailedException(Constant.EXCEPTION_MESSAGE_INVALID_TIME_INTERVAL_ARGUMENT);
		}

		return interval;
	}

	
	//others
	
	/**
	 * parseTag
	 * Return a list of tags given list of string
	 * 
	 * @param tagStringlist		a list of tag strings
	 * @return					a list of tags
	 */
	private ArrayList<String> parseTag(ArrayList<String> tagStringlist) {

		ArrayList<String> results = new ArrayList<String>();

		for (String tagMine : tagStringlist) {
			if (tagMine.indexOf(Constant.SPLITOR_SPACE) == Constant.LIST_INDEX_NOT_EXISTING) {
				results.add(tagMine);
			} else {
				String parsedTagString = classifierTag.classifyToString(tagMine,
						Constant.PARSING_STYLE_INLINE_XML, false);
				HashMap<String, ArrayList<String>> tagMap = parseToMap(parsedTagString);
				ArrayList<String> tagList = tagMap.get(Constant.MAP_KEY_TAG);
				if (tagList != null) {
					for (String tag : tagList) {
						results.add(tag);
					}
				}
			}
		}

		return results;
	}

	/**
	 * parsePriority
	 * Return a priority value given a priority string
	 * 
	 * @param priorityString	a string potentially contains the priority
	 * @return					a integer that represents the priority
	 */
	private int parsePriority(String priorityString) {

		String parsedPriorityString = classifierPriority.classifyToString(
				priorityString, Constant.PARSING_STYLE_INLINE_XML, false);
		HashMap<String, ArrayList<String>> cmdMap = parseToMap(parsedPriorityString);
		int result = Constant.PRIORITY_INVALID;
		for (String command : cmdMap.keySet()) {
			if (!command.equals(Constant.MAP_KEY_COMMAND)) {
				result = parsePriorityFromFormattedString(command);
			}
		}

		if (result == Constant.PRIORITY_INVALID) {
			return Constant.PRIORITY_DEFAULT;
		} else {
			return result;
		}

	}
	
	/**
	 * parsePriorityFromFormattedString
	 * parse priority from a formatted tag to a integer
	 * 
	 * @param formattedPriorityTag 		a formatted priority tag
	 * @return							a integer represents the priority, PRIORITY_INVALID if no formatted tag found
	 */
	public static int parsePriorityFromFormattedString(String formattedPriorityTag) {
		if (formattedPriorityTag.equalsIgnoreCase(Constant.PRIORITY_STRING_HIGH)) {
			return Constant.PRIORITY_HIGH;
		} else if (formattedPriorityTag.equalsIgnoreCase(Constant.PRIORITY_STRING_MEDIUM)) {
			return Constant.PRIORITY_MEDIUM;
		} else if (formattedPriorityTag.equalsIgnoreCase(Constant.PRIORITY_STRING_LOW)) {
			return Constant.PRIORITY_LOW;
		} else {
			return Constant.PRIORITY_INVALID;
		}
	}

	/**
	 * parseCommand
	 * Return a COMMAND_TYPE enumeration given a list of commands string
	 * when there are more than one commands in the list, only first one will be accepted
	 * 
	 * @param commandList				a list of string that potentially contains the commands 
	 * @return							the first valid result of the list
	 */
	private COMMAND_TYPE parseCommand(ArrayList<String> commandList) {

		ArrayList<String> results = new ArrayList<String>();

		for (String cmd : commandList) {
			String parsedTagString = classifierCommand.classifyToString(cmd,
					Constant.PARSING_STYLE_INLINE_XML, false);
			HashMap<String, ArrayList<String>> cmdMap = parseToMap(parsedTagString);
			for (String command : cmdMap.keySet()) {
				if (!command.equals(Constant.MAP_KEY_COMMAND)) {
					results.add(command);
				}
			}
		}

		if (results.size() >= 1) {
			//System.err.println("PARSED CMD - parseCommand: " + results.get(0).toLowerCase());
			return determineCommandType(results.get(Constant.LIST_INDEX_FIRST).toLowerCase());
		} else {
			return COMMAND_TYPE.ADD;
		}
	}
	
	
//@author A0119379R-unused
//the NLP index parser, discarded for its unreliable performance.
//
//	/**
//	 * Return a list of Integer representing the picked out index
//	 * @param indexMines
//	 * @return
//	 * @throws CommandFailedException
//	 */
//	private ArrayList<Integer> parseIndex (ArrayList<String> indexMines) throws CommandFailedException {
//		try {
//			ArrayList<Integer> results = new ArrayList<Integer>();
//			for (String indexMine : indexMines) {
//				String parsedIndexString = classifierIndex.classifyToString(indexMine,
//						"inlineXML", false);
//				HashMap<String, ArrayList<String>> indexMap = parseToMap(parsedIndexString);
//				ArrayList<String> indexList = indexMap.get("INDEX");
//				if (indexList != null) {
//					for (String indexString : indexList) {
//						results.add(Integer.parseInt(indexString));
//					}
//				}
//			}
//
//			return results;
//		} catch (Exception e) {
//			throw new CommandFailedException("Unparsable Integer");
//		}
//	}
	
	
/**
 * ==========================================================================================================================
 *  Other methods used during NER parsing
 * ==========================================================================================================================
 */
	
	/**
	 * isDeadlineTask
	 * check if a given string contains a deadline task
	 * 
	 * @param timeStringList	a list of time string
	 * @return					a boolean to indicate if the time represents a deadline
	 */
	private static boolean isDeadlineTask(ArrayList<String> timeStringList) {
		for (String userInputString : timeStringList) {
			userInputString = userInputString.toLowerCase();
			if (userInputString.contains(Constant.DEADLINE_INDICATOR_BY) 
					|| userInputString.contains(Constant.DEADLINE_INDICATOR_UNTIL) 
					|| userInputString.contains(Constant.DEADLINE_INDICATOR_TILL) 
					|| userInputString.contains(Constant.DEADLINE_INDICATOR_BEFORE)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * parseToMap
	 * return the key-value map tagged in the xmlString
	 * 
	 * @param xmlString		a well tagged XML string
	 * @return				return the key-value map tagged in the xmlString
	 */
	public static HashMap<String, ArrayList<String>> parseToMap(String xmlString) {
		assert (xmlString != null);
		assert (xmlString.length() > Constant.XML_STRING_MIN_LENGTH);
		if (xmlString == null || xmlString.length() <= Constant.XML_STRING_MIN_LENGTH) {
			//System.err.println("Invalid input");
			return new HashMap<String, ArrayList<String>>();
		}
		
		HashMap<String, ArrayList<String>> taskMap = new HashMap<String, ArrayList<String>>();
		taskMap.put(Constant.MAP_KEY_COMMAND, new ArrayList<String>());
		// get rid of the first and last character
		xmlString = xmlString.substring(1, xmlString.length() - 2);
		String[] xmlSegments = xmlString.split(Constant.TAG_OPEN_LATTER);
		// string format: ADD>Add, ADD> <DESCRIPTION> ... , DESCRIPTION>
		// <DATE>...

		int NumberOfSegments = xmlSegments.length - 1;

		for (int i = 0; i < NumberOfSegments; i++) {
			String segment = xmlSegments[i];
			String nextSegment = xmlSegments[i + 1];
			String key = nextSegment.substring(0, nextSegment.indexOf(Constant.TAG_CLOSE));
			String temp = segment.replaceFirst(
					segment.substring(0, segment.indexOf(Constant.TAG_CLOSE) + 1), Constant.EMPTY_STRING);

			String value = null;
			if (temp.indexOf(Constant.TAG_CLOSE) != Constant.LIST_INDEX_NOT_EXISTING) {
				value = temp.replaceFirst(
						temp.substring(0, temp.indexOf(Constant.TAG_CLOSE) + 1), Constant.EMPTY_STRING);
			} else {
				value = temp;
			}

			if (taskMap.get(key) == null) {
				taskMap.put(key, new ArrayList<String>());
			}
			taskMap.get(key).add(value);
		}

		return taskMap;
	}

	
	/**
	 * determine the command type with the given string
	 * 
	 * @param commandTypeString		a string that might contain the user command
	 * @return						an COMMAND_TYPE enumeration representing the corresponding command
	 */
	public static COMMAND_TYPE determineCommandType(String commandTypeString) {
		switch (commandTypeString) {
			case Constant.COMMAND_STRING_LOG_IN:
				return COMMAND_TYPE.LOG_IN;
			
			case Constant.COMMAND_STRING_LOG_OUT:
				return COMMAND_TYPE.LOG_OUT;
				
			case Constant.COMMAND_STRING_CREATE_ACCOUNT:
				return COMMAND_TYPE.CREATE_ACCOUNT;
				
			case Constant.COMMAND_STRING_DELETE_ACCOUNT:
				return COMMAND_TYPE.DELETE_ACCOUNT;
				
			case Constant.COMMAND_STRING_HELP:
				return COMMAND_TYPE.HELP;
				
			case Constant.COMMAND_STRING_EXIT:
				return COMMAND_TYPE.EXIT;
				
			case Constant.COMMAND_STRING_ADD:
				return COMMAND_TYPE.ADD;
				
			case Constant.COMMAND_STRING_UPDATE:
				return COMMAND_TYPE.UPDATE;
				
			case Constant.COMMAND_STRING_DELETE:
				return COMMAND_TYPE.DELETE;
				
			case Constant.COMMAND_STRING_DISPLAY:
				return COMMAND_TYPE.DISPLAY;

			case Constant.COMMAND_STRING_SEARCH:
				return COMMAND_TYPE.SEARCH;
				
			case Constant.COMMAND_STRING_UNDO:
				return COMMAND_TYPE.UNDO;
				
			case Constant.COMMAND_STRING_REDO:
				return COMMAND_TYPE.REDO;
	
			case Constant.COMMAND_STRING_CLEAR:
				return COMMAND_TYPE.CLEAR;	
	
			case Constant.COMMAND_STRING_LOG_IN_ALT:
				return COMMAND_TYPE.LOG_IN;
				
			case Constant.COMMAND_STRING_LOG_OUT_ALT:
				return COMMAND_TYPE.LOG_OUT;
				
			case Constant.COMMAND_STRING_NLP:
				return COMMAND_TYPE.NLP;
				
			case Constant.COMMAND_STRING_EMPTY_TRASH:
				return COMMAND_TYPE.EMPTY_TRASH;
				
			case Constant.COMMAND_STRING_RELOAD:
				return COMMAND_TYPE.RELOAD;
				
			case Constant.COMMAND_STRING_DONE:
				return COMMAND_TYPE.DONE;
				
			case Constant.COMMAND_STRING_RECOVER:
				return COMMAND_TYPE.RECOVER;
				
			default:
				return COMMAND_TYPE.HELP;
		}
	}
}
