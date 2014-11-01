package infrastructure;

import dataStructure.*;
import reference.*;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

public class NERParser {
	private AbstractSequenceClassifier<CoreLabel> classifierTag;
	private AbstractSequenceClassifier<CoreLabel> classifierCommand;
	private AbstractSequenceClassifier<CoreLabel> classifierTime;
	private AbstractSequenceClassifier<CoreLabel> classifierPriority;

	private AbstractSequenceClassifier<CoreLabel> classifierTimePicker;
	private AbstractSequenceClassifier<CoreLabel> classifierCommandPicker;
	private AbstractSequenceClassifier<CoreLabel> classifierDescriptionPicker;
	private AbstractSequenceClassifier<CoreLabel> classifierIndexPicker;
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
	
	// private boolean isCategoryChanged = false;

	public NERParser() {
		try {
			copyUserNlpFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// NER parsers
		classifierTag = CRFClassifier
				.getClassifierNoExceptions("NLPTraining/tag-ner-model.ser.gz");
		classifierCommand = CRFClassifier
				.getClassifierNoExceptions("NLPTraining/command-ner-model.ser.gz");
		classifierTime = CRFClassifier
				.getClassifierNoExceptions("NLPTraining/time-ner-model.ser.gz");
		classifierPriority = CRFClassifier
				.getClassifierNoExceptions("NLPTraining/priority-ner-model.ser.gz");

		
		// NER pickers
		try {
			System.err.println("Using user's customized model");
			classifierTimePicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_TIME_PICKER_GZ_USER);
			classifierCommandPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_COMMAND_PICKER_GZ_USER);
			classifierDescriptionPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_DESCRIPTION_PICKER_GZ_USER);
			classifierIndexPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_INDEX_PICKER_GZ_USER);
			classifierTagPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_TAG_PICKER_GZ_USER);
			classifierPriorityPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_PRIORITY_PICKER_GZ_USER);
		} catch (Exception e) {
			System.err.println("Using default model");
			classifierTimePicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_TIME_PICKER_GZ_SOURCE);
			classifierCommandPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_COMMAND_PICKER_GZ_SOURCE);
			classifierDescriptionPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_DESCRIPTION_PICKER_GZ_SOURCE);
			classifierIndexPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_INDEX_PICKER_GZ_SOURCE);
			classifierTagPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_TAG_PICKER_GZ_SOURCE);
			classifierPriorityPicker = CRFClassifier
					.getClassifierNoExceptions(Constant.FILE_PATH_PRIORITY_PICKER_GZ_SOURCE);
		}

		// Time parsers
		props = new Properties();
		props.put("sutime.binders", "0");
		props.put(
				"sutime.rules",
				"NLPTraining/defs.sutime.txt, NLPTraining/english.holidays.sutime.txt, NLPTraining/english.sutime.txt");
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	}

	public String pasreTimeToXML(String content) {
		return classifierTime.classifyToString(content, "inlineXML", false);
	}

	/**
	 * pick out the cmd fragments and translate to the enum
	 * 
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */
	public COMMAND_TYPE pickCommand(String userInputString) throws CommandFailedException {
		
		String directParseCommand = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_COMMAND);
		
		ArrayList<String> commandList = new ArrayList<String>();
		if (directParseCommand != null) {
			commandList.add(directParseCommand);
		} else {
			String xmlStr = classifierCommandPicker.classifyToString(
					userInputString, "inlineXML", false);
			System.err.println("XML STRING - pickCommand: " + xmlStr);
			HashMap<String, ArrayList<String>> result = NERParser
					.parseToMap(xmlStr);
			commandList = result.get("COMMAND");
		}
		
		
		if (commandList == null || commandList.size() == 0) {
			return COMMAND_TYPE.ADD;
		} else {
			return this.parseCommand(commandList);
		}
	}

	/**
	 * pick out the date fragments from an unparsed input string and translate
	 * to TimeInterval
	 * 
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */
	public TimeInterval pickTimeInterval(String userInputString)
			throws CommandFailedException {
		this.isTimeChanged = false;

		String directParseTime = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_TIME);
		if (directParseTime != null) {
			this.isTimeChanged = true;
			ArrayList<String> results = new ArrayList<String>();
			results.add(directParseTime);
			return parseTimeInterval(results);
		}

		ArrayList<String> resultList = new ArrayList<String>();
		String xmlStr = classifierTimePicker.classifyToString(userInputString,
				"inlineXML", false);
		System.err.println("XML STRING - pickDate: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser
				.parseToMap(xmlStr);
		resultList = result.get("DATE");

		if (resultList == null) {
			return new TimeInterval();
		} else {
			this.isTimeChanged = true;
			return parseTimeInterval(resultList);
		}
	}

	/**
	 * pick out the description segments
	 * 
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */
	public String pickDescription(String userInputString)
			throws CommandFailedException {
		this.isDescriptionChanged = false;

		String directParseDescription = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_DESCRIPTION);
		if (directParseDescription != null) {
			this.isDescriptionChanged = true;
			return directParseDescription;
		}

		String xmlStr = classifierDescriptionPicker.classifyToString(
				userInputString, "inlineXML", false);
		System.err.println("XML STRING - pickDescription: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get("DESCRIPTION");
		if (resultList == null || resultList.size() == 0) {
			return "";
		} else {
			this.isDescriptionChanged = true;
			return resultList.get(0);
		}
	}

	/**
	 * pick out the index segment and translate it to integer
	 * 
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */
	public int pickIndex(String userInputString) throws CommandFailedException {

		String directParseIndex = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_INDEX);
		try {
			if (directParseIndex != null) {
				return Integer.parseInt(directParseIndex);
			}

			String xmlStr = classifierIndexPicker.classifyToString(
					userInputString, "inlineXML", false);
			System.err.println("XML STRING - pickIndex: " + xmlStr);
			HashMap<String, ArrayList<String>> result = NERParser
					.parseToMap(xmlStr);
			ArrayList<String> resultList = result.get("INDEX");
			if (resultList == null || resultList.size() == 0) {
				throw new CommandFailedException("No index found!");
			} else {
				return Integer.parseInt(resultList.get(0));
			}

		} catch (Exception e) {
			String xmlStr = classifierIndexPicker.classifyToString(
					userInputString, "inlineXML", false);
			System.err.println("XML STRING - pickIndex: " + xmlStr);
			HashMap<String, ArrayList<String>> result = NERParser
					.parseToMap(xmlStr);
			ArrayList<String> resultList = result.get("INDEX");
			if (resultList == null || resultList.size() == 0) {
				throw new CommandFailedException("No index found!");
			} else {
				return Integer.parseInt(resultList.get(0));
			}
		}
	}

	/**
	 * pick out the tag segments
	 * 
	 * @param userInputString
	 * @return
	 */
	public ArrayList<String> pickTag(String userInputString) {
		this.isTagChanged = false;

		String directParseTag = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_TAG);
		if (directParseTag != null) {
			this.isTagChanged = true;
			ArrayList<String> results = new ArrayList<String>();
			
			String[] stringArrayNoSpace = directParseTag.split(" ");
			for (String s : stringArrayNoSpace) {
				results.add(s.trim());
			}
			
			return parseTag(results);
		}

		String xmlStr = classifierTagPicker.classifyToString(userInputString,
				"inlineXML", false);
		System.err.println("XML STRING - pickTag: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get("TAG");
		if (resultList == null || resultList.size() == 0) {
			return new ArrayList<String>();
		} else {
			this.isTagChanged = true;
			return parseTag(resultList);
		}
	}

	/**
	 * pick out the priority segments
	 * 
	 * @param userInputString
	 * @return
	 */
	public int pickPriority(String userInputString) {
		this.isPriorityChanged = false;

		String directParsePriority = NERParser.pickTheTagged(userInputString,
				Constant.XML_TAG_PRIORITY);
		if (directParsePriority != null) {
			this.isPriorityChanged = true;
			return parsePriority(directParsePriority);
		}

		String xmlStr = classifierPriorityPicker.classifyToString(
				userInputString, "inlineXML", false);
		System.err.println("XML STRING - pickPriority: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser
				.parseToMap(xmlStr);
		ArrayList<String> resultList = result.get("PRIORITY");
		if (resultList == null || resultList.size() == 0) {
			return Constant.PRIORITY_DEFAULT;
		} else {
			this.isPriorityChanged = true;
			return parsePriority(resultList.get(0));
		}
	}
	
	public static String pickTheTagged(String inputString, String type) {
		String prefix = "<" + type + ">";
		String postfix = "</" + type + ">";
		int prefixIndex = inputString.indexOf(prefix);
		int postfixIndex = inputString.indexOf(postfix);

		System.err.println("INPUTSTRING: pickTheTagged: " + inputString);
		System.err.println("PREFIX: pickTheTagged: " + prefix);
		System.err.println("POSTFIX: pickTheTagged: " + postfix);
		if (prefixIndex == -1 || postfixIndex == -1) {
			return null;
		} else {
			return inputString.substring(prefixIndex + prefix.length(),
					postfixIndex);
		}
	}
	

	/**
	 * parse a task from the given string used when adding an task
	 * 
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */

	public Task getTask(String userInputString) throws CommandFailedException {

		TimeInterval timeInterval = this.pickTimeInterval(userInputString);
		ArrayList<String> tag = this.pickTag(userInputString);
		String description = this.pickDescription(userInputString);
		int priority = this.pickPriority(userInputString);
		String category = null;

		int repeatedPeriod = Constant.REPEATED_PERIOD_DEFAULT;

		return new Task(description, category, priority, repeatedPeriod, tag,
				timeInterval);
	}

	
	/**
	 * parse a search constraint used when searching for tasks
	 * 
	 * @param userIntputStirng
	 * @return
	 * @throws CommandFailedException
	 */
	public Constraint getConstraint(String userInputString)
			throws CommandFailedException {
		TimeInterval timeInterval = this.pickTimeInterval(userInputString);
		String keyword = "";
		if (timeInterval.equals(new TimeInterval())) {
			keyword = UtilityMethod.removeFirstWord(userInputString);
		}

		return new Constraint(keyword, timeInterval);
	}

	/**
	 * get updated keys and values
	 * 
	 * @param userInputStirng
	 * @return
	 * @throws CommandFailedException
	 */
	public HashMap<String, Object> getUpdatedTaskMap(String userInputString)
			throws CommandFailedException {

		TimeInterval timeInterval = this.pickTimeInterval(userInputString);
		ArrayList<String> tag = this.pickTag(userInputString);
		String description = this.pickDescription(userInputString);
		int priority = this.pickPriority(userInputString);

		HashMap<String, Object> updateAttributes = new HashMap<String, Object>();

		if (this.isTimeChanged) {
			System.err.println("getUpdatedTaskMap: time updated to "
					+ timeInterval);
			updateAttributes.put("time_interval", timeInterval);
		}

		if (this.isTagChanged) {
			System.err.println("getUpdatedTaskMap: tag updated to " + tag);
			updateAttributes.put("tag", tag);
		}

		if (this.isDescriptionChanged) {
			System.err.println("getUpdatedTaskMap: description updated to "
					+ description);
			updateAttributes.put("description", description);
		}

		if (this.isPriorityChanged) {
			System.err.println("getUpdatedTaskMap: priority updated to "
					+ priority);
			updateAttributes.put("priority", priority);
		}

		return updateAttributes;
	}

	

	public static boolean updateModal(String propFilePath) {
		
		try {
			String[] a = new String[2];
			a[0] = "-prop";
			a[1] = propFilePath;
			edu.stanford.nlp.ie.crf.CRFClassifier.main(a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * turn the xml to the required training format
	 * 
	 * @param xmlString
	 * @return
	 */
	public static ArrayList<Pair<String, String>> interpretXML(String xmlString) {
		xmlString = xmlString.replaceAll("<", " <");
		xmlString = xmlString.replaceAll(">", "> ");

		String[] wordArray = xmlString.split(" ");
		ArrayList<Pair<String, String>> wordPairs = new ArrayList<Pair<String, String>>();

		String currentKey = Constant.XML_TAG_DEFAULT;

		for (String currentWord : wordArray) {
			if (currentWord.contains("</") && currentWord.contains(">")
					&& currentWord.indexOf(">") > currentWord.indexOf("</")) {
				currentKey = Constant.XML_TAG_DEFAULT;

			} else if (currentWord.contains("<") && currentWord.contains(">")
					&& currentWord.indexOf(">") > currentWord.indexOf("<")) {
				if (!currentKey.equals(Constant.XML_TAG_DEFAULT)) {
					System.err
							.println("interpretXML: currentKey != defaultKey ----- one word can only have a single tag");
					return null;
				}
				currentKey = currentWord.substring(
						currentWord.indexOf("<") + 1, currentWord.indexOf(">"))
						.trim();
			} else if (!currentWord.equals("")) {
				wordPairs.add(new Pair<String, String>(currentWord.trim(),
						currentKey.trim()));
			}
		}

		return wordPairs;
	}

	/**
	 * translate a mixed list to a map containing pure list
	 * 
	 * @param wordPairs
	 * @return
	 */
	public static HashMap<String, ArrayList<Pair<String, String>>> demux(ArrayList<Pair<String, String>> wordPairs) {
		HashMap<String, ArrayList<Pair<String, String>>> listMap = new HashMap<String, ArrayList<Pair<String, String>>>();
		ArrayList<Pair<String, String>> timeList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> tagList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> descriptionList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> priorityList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> indexList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> commandList = new ArrayList<Pair<String, String>>();

		isTimeModelUpdate = false;
		isTagModelUpdate = false;
		isDescriptionModelUpdate = false;
		isIndexModelUpdate = false;
		isPriorityModelUpdate = false;
		isCommandModelUpdate = false;
		
		for (Pair<String, String> p : wordPairs) {
			System.err.println("demux: " + p.tail);
			switch (p.tail) {
			case Constant.XML_TAG_TIME:
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

		Pair<String, String> trivialPair = new Pair<String, String>(
				".", "O");
		timeList.add(trivialPair);
		descriptionList.add(trivialPair);
		priorityList.add(trivialPair);
		indexList.add(trivialPair);
		tagList.add(trivialPair);
		commandList.add(trivialPair);
		
		listMap.put(Constant.XML_TAG_TIME, timeList);
		listMap.put(Constant.XML_TAG_DESCRIPTION, descriptionList);
		listMap.put(Constant.XML_TAG_PRIORITY, priorityList);
		listMap.put(Constant.XML_TAG_INDEX, indexList);
		listMap.put(Constant.XML_TAG_TAG, tagList);
		listMap.put(Constant.XML_TAG_COMMAND, commandList);
		return listMap;
	}

	/**
	 * update the training model with the given xmlString
	 * 
	 * @param xmlString
	 * @throws IOException
	 */
	public static void updateTsvFile(String xmlString) throws IOException {
		System.err.println("INPUT - updateTsvFile: " + xmlString);
		HashMap<String, ArrayList<Pair<String, String>>> listMap = demux(interpretXML(xmlString));
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
			System.err.println("time list is not empty");
			updateTsvFile(timeList, Constant.FILE_PATH_TIME_PICKER_USER);
		}

		if (isDescriptionModelUpdate) {
			System.err.println("description List is not empty");
			updateTsvFile(descriptionList,
					Constant.FILE_PATH_DESCRIPTION_PICKER_USER);
		}

		if (isTagModelUpdate) {
			System.err.println("tag List is not empty");
			updateTsvFile(tagList, Constant.FILE_PATH_TAG_PICKER_USER);
		}

		if (isIndexModelUpdate) {
			System.err.println("index List is not empty");
			updateTsvFile(indexList, Constant.FILE_PATH_INDEX_PICKER_USER);
		}

		if (isPriorityModelUpdate) {
			System.err.println("priorityList is not empty");
			updateTsvFile(priorityList, Constant.FILE_PATH_PRIORITY_PICKER_USER);
		}
		
		if (isCommandModelUpdate) {
			System.err.println("commandList is not empty");
			updateTsvFile(commandList, Constant.FILE_PATH_COMMAND_PICKER_USER);
		}
	}

	/**
	 * update the specific file
	 * 
	 * @param list
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static void updateTsvFile(ArrayList<Pair<String, String>> list,
			String filePath) throws IOException {

		FileWriter fw = new FileWriter(new File(filePath), true);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.newLine();
		for (Pair<String, String> p : list) {
			String thisLine = p.head + "\t" + p.tail;
			bw.newLine();
			bw.write(thisLine);
		}
		bw.newLine();
		
		System.err.println("Finish writting!");

		bw.flush();
		bw.close();
		fw.close();
	}

	/**
	 * return the key-value map parsed from the xmlString
	 * 
	 * @param xmlString
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> parseToMap(String xmlString) {
		assert (xmlString != null);
		assert (xmlString.length() > 5);
		if (xmlString == null || xmlString.length() <= 5) {
			System.err.println("Invalid input");
			return new HashMap<String, ArrayList<String>>();
		}

		System.err.println("INPUT XML STRING - parseToMap: " + xmlString);
		HashMap<String, ArrayList<String>> taskMap = new HashMap<String, ArrayList<String>>();
		taskMap.put("COMMAND", new ArrayList<String>());
		// get rid of the first and last character
		xmlString = xmlString.substring(1, xmlString.length() - 2);
		String[] xmlSegments = xmlString.split("</");
		// string format: ADD>Add, ADD> <DESCRIPTION> ... , DESCRIPTION>
		// <DATE>...

		int NumberOfSegments = xmlSegments.length - 1;

		for (int i = 0; i < NumberOfSegments; i++) {
			String segment = xmlSegments[i];
			String nextSegment = xmlSegments[i + 1];
			String key = nextSegment.substring(0, nextSegment.indexOf(">"));
			String temp = segment.replaceFirst(
					segment.substring(0, segment.indexOf(">") + 1), "");

			String value = null;
			if (temp.indexOf(">") != -1) {
				value = temp.replaceFirst(
						temp.substring(0, temp.indexOf(">") + 1), "");
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
	 * get a TimeInterval object from a list of date strings
	 * 
	 * @param userInputStrings
	 * @return
	 * @throws CommandFailedException
	 */
	public TimeInterval parseTimeInterval(ArrayList<String> userInputStrings)
			throws CommandFailedException {
		ArrayList<Date> dates = parseTimeToDate(userInputStrings);
		assert (dates != null);
		TimeInterval interval = new TimeInterval();

		if (dates.size() == 1) {
			// Calendar c1 = UtilityMethod.dateToCalendar(dates.get(0));
			Calendar c2 = UtilityMethod.dateToCalendar(dates.get(0));
			// c1.set(Calendar.HOUR_OF_DAY, 0);
			// c1.set(Calendar.MINUTE, 1);
			c2.set(Calendar.HOUR_OF_DAY, 23);
			c2.set(Calendar.MINUTE, 59);
			interval = new TimeInterval(Constant.DEADLINE_START_DATE,
					c2.getTime());
		} else if (dates.size() == 2) {
			Date d0 = dates.get(0);
			Date d1 = dates.get(1);
			interval = new TimeInterval(
					UtilityMethod.selectEarlierDate(d0, d1),
					UtilityMethod.selectLaterDate(d0, d1));
		} else if (dates.size() == 3) {
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
			throw new CommandFailedException(
					"parseTimeInterval: more than three date recieved");
		}

		return interval;
	}

	/**
	 * translate a list of date string to a list of date objects
	 * 
	 * @param userInputStrings
	 * @return
	 */
	public ArrayList<Date> parseTimeToDate(ArrayList<String> userInputStrings) {

		ArrayList<Date> results = new ArrayList<Date>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String stringForToday = format.format(Calendar.getInstance().getTime());

		for (String text : userInputStrings) {
			System.err.println("INPUT TIME STRING - parseTimeToDate: " + text);
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
				Date d = parseStringToDate(interpretedTimeString);
				Calendar c = UtilityMethod.dateToCalendar(d);
				System.err.println("PARSED DATE - parseTimeToDate: "
						+ c.get(Calendar.SECOND));
				if (d != null) {
					if (c.get(Calendar.SECOND) == Constant.CALENDAR_WEEK_IN_SECOND) {
						System.err.println("parsing weeks");
						Date d1 = c.getTime();
						c.add(Calendar.WEEK_OF_YEAR, 1);
						c.add(Calendar.DATE, -1);
						c.set(Calendar.HOUR_OF_DAY, 23);
						c.set(Calendar.MINUTE, 59);
						Date d2 = c.getTime();
						results.add(d1);
						results.add(d2);
					} else {
						results.add(d);
					}
				}
			}
		}
		return results;
	}

	/**
	 * parse a time string
	 * 
	 * @param timeString
	 * @return
	 */
	private Date parseStringToDate(String timeString) {
		// the four possible format:
		// 2014-10-15T14:00
		// 2014-10-24-WXX-5
		// 2014-10-24
		// 2014-02
		// 2014

		System.err.println("INPUT TIME STRING - parseStringToDate: "
				+ timeString);

		Date date = null;
		try {
			if (timeString.length() == 4) {
				date = new SimpleDateFormat("yyyy", Locale.ENGLISH)
						.parse(timeString);
			} else if (timeString.length() == 7) {
				date = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
						.parse(timeString);
			} else if (timeString.length() == 10) {
				date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
						.parse(timeString);
			} else if (timeString.length() == 11) {
				timeString = timeString.replaceFirst("W", "");
				date = new SimpleDateFormat("yyyy-MM-ww", Locale.ENGLISH)
						.parse(timeString);
				Calendar c = UtilityMethod.dateToCalendar(date);
				c.set(Calendar.SECOND, Constant.CALENDAR_WEEK_IN_SECOND);
				date = c.getTime();
				System.err.println("Parsing week: " + date);
			} else if (timeString.length() == 16) {
				if (timeString.charAt(10) == 'T') {
					timeString = timeString.replace("T", " ");
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm",
							Locale.ENGLISH).parse(timeString);
				} else {
					timeString = timeString.substring(0, 10);
					date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
							.parse(timeString);
				}
			} else {
				Constant.logger.log(Level.INFO, "unrecognized format: "
						+ timeString);
				// will return null
			}
		} catch (ParseException e) {
			// will return null
			Constant.logger.log(Level.INFO,
					"parseStringToDate gets expection, returns null");
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * parse tags
	 * 
	 * @param tagMines
	 * @return
	 */
	private ArrayList<String> parseTag(ArrayList<String> tagMines) {

		ArrayList<String> results = new ArrayList<String>();

		for (String tagMine : tagMines) {
			if (tagMine.indexOf(" ") == -1) {
				results.add(tagMine);
			} else {
				String parsedTagString = classifierTag.classifyToString(tagMine,
						"inlineXML", false);
				HashMap<String, ArrayList<String>> tagMap = parseToMap(parsedTagString);
				ArrayList<String> tagList = tagMap.get("TAG");
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
	 * parse priorities
	 * 
	 * @param priorityMines
	 * @return
	 */
	private int parsePriority(String priorityMines) {

		String parsedPriorityString = classifierPriority.classifyToString(
				priorityMines, "inlineXML", false);
		HashMap<String, ArrayList<String>> cmdMap = parseToMap(parsedPriorityString);
		int result = Constant.PRIORITY_INVALID;
		for (String command : cmdMap.keySet()) {
			if (!command.equals("COMMAND")) {
				result = Parser.parsePriority(command);
			}
		}

		if (result == Constant.PRIORITY_INVALID) {
			return Constant.PRIORITY_DEFAULT;
		} else {
			return result;
		}

	}

	/**
	 * parse commands
	 * 
	 * @param commands
	 * @return
	 * @throws CommandFailedException
	 */
	private COMMAND_TYPE parseCommand(ArrayList<String> commands)
			throws CommandFailedException {

		ArrayList<String> results = new ArrayList<String>();

		for (String cmd : commands) {
			String parsedTagString = classifierCommand.classifyToString(cmd,
					"inlineXML", false);
			HashMap<String, ArrayList<String>> cmdMap = parseToMap(parsedTagString);
			for (String command : cmdMap.keySet()) {
				if (!command.equals("COMMAND")) {
					results.add(command);
				}
			}
		}

		if (results.size() >= 1) {
			System.err.println("PARSED CMD - parseCommand: "
					+ results.get(0).toLowerCase());
			return Parser.determineCommandType(results.get(0).toLowerCase());
		} else {
			return COMMAND_TYPE.ADD;
		}
	}

	private static void copyUserNlpFiles() throws IOException {
		System.err.println("initializeUserTrainingModel");
		File rootDirectory = new File(Constant.FILE_PATH_ROOT);
		if (!rootDirectory.exists()) {
			rootDirectory.mkdir();
		}

		File nlpRootDirectory = new File(Constant.FILE_PATH_NLP_ROOT);
		if (!nlpRootDirectory.exists()) {
			nlpRootDirectory.mkdir();
		}

		UtilityMethod.copyFile(Constant.FILE_PATH_TIME_PICKER_SOURCE,
				Constant.FILE_PATH_TIME_PICKER_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_TAG_PICKER_SOURCE,
				Constant.FILE_PATH_TAG_PICKER_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_DESCRIPTION_PICKER_SOURCE,
				Constant.FILE_PATH_DESCRIPTION_PICKER_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_INDEX_PICKER_SOURCE,
				Constant.FILE_PATH_INDEX_PICKER_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_PRIORITY_PICKER_SOURCE,
				Constant.FILE_PATH_PRIORITY_PICKER_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_COMMAND_PICKER_SOURCE,
				Constant.FILE_PATH_COMMAND_PICKER_USER);

		
		// copy prop files

		UtilityMethod.copyFile(Constant.FILE_PATH_TIME_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_TIME_PICKER_PROP_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_TAG_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_TAG_PICKER_PROP_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_DESCRIPTION_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_DESCRIPTION_PICKER_PROP_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_INDEX_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_INDEX_PICKER_PROP_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_PRIORITY_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_PRIORITY_PICKER_PROP_USER);

		UtilityMethod.copyFile(Constant.FILE_PATH_COMMAND_PICKER_PROP_SOURCE,
				Constant.FILE_PATH_COMMAND_PICKER_PROP_USER);
	}
	
	
	/**
	 * update the training modal
	 */
	public static void updateModal() {
		updateModal(Constant.FILE_PATH_TIME_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_TAG_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_DESCRIPTION_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_PRIORITY_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_COMMAND_PICKER_PROP_USER);
		updateModal(Constant.FILE_PATH_INDEX_PICKER_PROP_USER);
	}

}
