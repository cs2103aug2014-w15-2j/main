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
	private AbstractSequenceClassifier<CoreLabel> classifierOverall;
	private AbstractSequenceClassifier<CoreLabel> classifierTag;
	private AbstractSequenceClassifier<CoreLabel> classifierCommand;
	private AbstractSequenceClassifier<CoreLabel> classifierTime;
	private AbstractSequenceClassifier<CoreLabel> classifierTimePicker;
	private AbstractSequenceClassifier<CoreLabel> classifierCommandPicker;
	
	public NERParser () {
		super();
		classifierOverall = CRFClassifier.getClassifierNoExceptions("src/NLPTraining/overall-ner-model.ser.gz");
		classifierTag = CRFClassifier.getClassifierNoExceptions("NLPTraining/tag-ner-model.ser.gz");
		classifierCommand = CRFClassifier.getClassifierNoExceptions("NLPTraining/command-ner-model.ser.gz");
		classifierTime = CRFClassifier.getClassifierNoExceptions("src/NLPTraining/time-ner-model.ser.gz");
		classifierTimePicker = CRFClassifier.getClassifierNoExceptions("src/NLPTraining/time-picker-ner-model.ser.gz");
		classifierCommandPicker = CRFClassifier.getClassifierNoExceptions("src/NLPTraining/command-picker-ner-model.ser.gz");
	}
	
	public Task parseTask (String userInput) throws CommandFailedException {
		String xmlString = parseToXML(userInput);
		
		
		HashMap<String, ArrayList<String>> xmlMap = parseToMap(xmlString);
		for (String key : xmlMap.keySet()) {
			if (key.equals("COMMAND")) {
				System.out.println("COMMAND: " + parseCommand(xmlMap.get(key)));
			} else if (key.equals("TAG")) {
				System.out.println("TAG: " + parseTag(xmlMap.get(key)));
			} else if (key.equals("DATE")) {
				System.out.println("Time Interval: " + parseTimeInterval(xmlMap.get(key)));
			} else {
				System.out.println(key + ": " + xmlMap.get(key));	
			}
		}
		
		return null;
	}
	
	
	public String pasreTimeToXML (String content) {
		return classifierTime.classifyToString(content, "inlineXML", false);
	}
	
	
	
	/**
	 * Crucial part of NLP, parse the content to XML with 
	 * @param content
	 * @return
	 */
	public String parseToXML (String content) {
		//parse the content to XML format, no reservation for spaces
		return classifierOverall.classifyToString(content, "inlineXML", false);
	}
	
	
	/**
	 * pick out the date fragments from an unparsed input string and translate to TimeInterval
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException 
	 */
	public TimeInterval pickDate(String userInputString) throws CommandFailedException {
		String xmlStr = classifierTimePicker.classifyToString(userInputString, "inlineXML", false);
		System.err.println("XML STRING - pickDate: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser.parseToMap(xmlStr);
		ArrayList<String> resultList =  result.get("DATE");
		if (resultList == null) {
			throw new CommandFailedException("Unparseble Date");
		} else {
			return parseTimeInterval(result.get("DATE"));
		}
	}
	
	
	/**
	 * pick out the cmd fragments and tranlsate to the enum
	 * @param userInputString
	 * @return
	 * @throws CommandFailedException
	 */
	public COMMAND_TYPE pickCommand (String userInputString) throws CommandFailedException {
		String xmlStr = classifierCommandPicker.classifyToString(userInputString, "inlineXML", false);
		System.err.println("XML STRING - pickCommand: " + xmlStr);
		HashMap<String, ArrayList<String>> result = NERParser.parseToMap(xmlStr);
		ArrayList<String> resultList =  result.get("COMMAND");
		if (resultList == null || resultList.size() == 0) {
			return COMMAND_TYPE.ADD;
		} else {
			return this.parseCommand(result.get("COMMAND"));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * return the key-value map parsed from the xmlString
	 * @param xmlString
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> parseToMap(String xmlString) {
		assert(xmlString != null);
		assert(xmlString.length() > 5);
		
		System.err.println("INPUT XML STRING - parseToMap: " + xmlString);
		HashMap<String, ArrayList<String>> taskMap = new HashMap<String, ArrayList<String>>();
		taskMap.put("COMMAND", new ArrayList<String>());
		//get rid of the first and last character
		xmlString = xmlString.substring(1, xmlString.length()-2);
		String[] xmlSegments = xmlString.split("</");
		//string format: ADD>Add, ADD> <DESCRIPTION> ... , DESCRIPTION> <DATE>...
		
		
		int NumberOfSegments = xmlSegments.length-1;
		
		for (int i = 0; i < NumberOfSegments; i++) {
			String segment = xmlSegments[i];
			String nextSegment = xmlSegments[i+1];
			String key = nextSegment.substring(0, nextSegment.indexOf(">"));
			String temp = segment.replaceFirst(segment.substring(0, segment.indexOf(">") + 1), "");
			
			String value = null;
			if (temp.indexOf(">") != -1) {
				value = temp.replaceFirst(temp.substring(0, temp.indexOf(">") + 1), "");
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
	
	
	public TimeInterval parseTimeInterval (ArrayList<String> userInputStrings) throws CommandFailedException {
		ArrayList<Date> dates = parseTimeToDate(userInputStrings);
		assert(dates != null);
		TimeInterval interval = new TimeInterval();
		
		if (dates.size() == 1) {
			Calendar c1 = UtilityMethod.dateToCalendar(dates.get(0));
			Calendar c2 = UtilityMethod.dateToCalendar(dates.get(0));
			c1.set(Calendar.HOUR_OF_DAY, 0);
			c1.set(Calendar.MINUTE, 1);
			c2.set(Calendar.HOUR_OF_DAY, 23);
			c2.set(Calendar.MINUTE, 59);
			interval = new TimeInterval(c1.getTime(), c2.getTime());
		} else if (dates.size() == 2) {
			Date d0 = dates.get(0);
			Date d1 = dates.get(1);
			interval = new TimeInterval(UtilityMethod.selectEarlierDate(d0, d1), UtilityMethod.selectLaterDate(d0, d1));
		} else if (dates.size() == 3) {
			Calendar c1 = UtilityMethod.dateToCalendar(dates.get(0));
			Calendar c2 = UtilityMethod.dateToCalendar(dates.get(1));
			Calendar c3 = UtilityMethod.dateToCalendar(dates.get(2));
			
			int differentIndex = UtilityMethod.selectDifferentDate(c1, c2, c3);
			
			switch (differentIndex) {
				case 1:
					c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
					c3.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
					Date startDate1 = UtilityMethod.selectEarlierDate(c2.getTime(), c3.getTime());
					Date endDate1 = UtilityMethod.selectLaterDate(c2.getTime(), c3.getTime());
					interval = new TimeInterval(startDate1, endDate1);
					break;
					
				case 2:
					c1.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
					c3.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
					Date startDate2 = UtilityMethod.selectEarlierDate(c1.getTime(), c3.getTime());
					Date endDate2 = UtilityMethod.selectLaterDate(c1.getTime(), c3.getTime());
					interval = new TimeInterval(startDate2, endDate2);
					break;
					
				case 3:
					c2.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), c3.get(Calendar.DAY_OF_MONTH));
					c1.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), c3.get(Calendar.DAY_OF_MONTH));
					Date startDate3 = UtilityMethod.selectEarlierDate(c2.getTime(), c1.getTime());
					Date endDate3 = UtilityMethod.selectLaterDate(c2.getTime(), c1.getTime());
					interval = new TimeInterval(startDate3, endDate3);
					break;
					
				default:
					break;
			}
		} else {
			throw new CommandFailedException("parseTimeInterval: more than three date recieved");
		}
		
		return interval;
	}
	
	public ArrayList<Date> parseTimeToDate (ArrayList<String> userInputStrings) {
	    Properties props = new Properties();
	    props.put("sutime.binders","0");
	    props.put("sutime.rules", "src/NLPTraining/defs.sutime.txt, src/NLPTraining/english.holidays.sutime.txt, src/NLPTraining/english.sutime.txt");
	    AnnotationPipeline pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new TokenizerAnnotator(false));
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	    
	    ArrayList<Date> results = new ArrayList<Date>();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    String stringForToday = format.format(Calendar.getInstance().getTime());
	    
	    for (String text : userInputStrings) {
	    	System.err.println("INPUT TIME STRING - parseTimeToDate: " + text);
	    	Annotation annotation = new Annotation(text);
	    	annotation.set(CoreAnnotations.DocDateAnnotation.class, stringForToday);
	    	pipeline.annotate(annotation);
	    	List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		    for (CoreMap cm : timexAnnsAll) {
		    	String interpretedTimeString = cm.get(TimeExpression.Annotation.class).getTemporal().toString();
		    	Date d = parseStringToDate(interpretedTimeString);
		   	  	if (d!= null) {
		   	  		results.add(d);
		   		}
		   	}
	    }
	    return results;
	}
	
	private Date parseStringToDate (String timeString) {
		//the four possible format:
		//2014-10-15T14:00
		//2014-10-24-WXX-5
		//2014-10-24
		//2014-02
		//2014
		
		System.err.println("INPUT TIME STRING - parseStringToDate: " + timeString);
		
		Date date = null;
		try {
			if (timeString.length() == 4) {
				date = new SimpleDateFormat("yyyy", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 7) {
				date = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 10) {
				date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 16) {
				if (timeString.charAt(10) == 'T') {
					timeString = timeString.replace("T", " ");
					date = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH).parse(timeString);
				} else {
					timeString = timeString.substring(0, 10);
					date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(timeString);
				}
			} else {
				Constant.logger.log(Level.INFO, "unrecognized format: " + timeString);
				//will return null
			}
		} catch (ParseException e) {
			//will return null
			Constant.logger.log(Level.INFO, "parseStringToDate gets expection, returns null");
			e.printStackTrace();
		}
		return date;
	}
	

	
	private ArrayList<String> parseTag(ArrayList<String> tagMines) {
		
		ArrayList<String> results = new ArrayList<String>();
		
		for (String tagMine : tagMines) {
			String parsedTagString = classifierTag.classifyToString(tagMine, "inlineXML", false);
			HashMap<String, ArrayList<String>> tagMap = parseToMap(parsedTagString);
			ArrayList<String> tagList = tagMap.get("TAG");
			if (tagList != null) {
				for (String tag: tagList) {
					results.add(tag);
				}
			}
		}
		
		return results;
	}
	
	private COMMAND_TYPE parseCommand(ArrayList<String> commands) throws CommandFailedException {
		
		ArrayList<String> results = new ArrayList<String>();
		
		for (String cmd : commands) {
			String parsedTagString = classifierCommand.classifyToString(cmd, "inlineXML", false);
			HashMap<String, ArrayList<String>> cmdMap = parseToMap(parsedTagString);
			for (String command: cmdMap.keySet()) {
				if (!command.equals("COMMAND")) {
					results.add(command);
				}
			}
		}
		
		if (results.size() >= 1) {
			System.err.println("PARSED CMD - parseCommand: " + results.get(0).toLowerCase());
			return Parser.determineCommandType(results.get(0).toLowerCase());
		} else {
			return COMMAND_TYPE.ADD;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
