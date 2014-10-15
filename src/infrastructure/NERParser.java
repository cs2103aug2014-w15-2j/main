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



public abstract class NERParser {
	static AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions("NLPTraining/ner-model.ser.gz");
	
	public static Task parseTask (String userInput) {
		String xmlString = parseToXML(userInput);
		HashMap<String, String> xmlMap = parseToMap(xmlString);
		
		
		
		return null;
	}
	
	
	
	
	
	/**
	 * Crucial part of NLP, parse the content to XML with 
	 * @param content
	 * @return
	 */
	private static String parseToXML (String content) {
		//parse the content to XML format, no reservation for spaces
		return classifier.classifyToString(content, "inlineXML", false);
	}
	
	
	/**
	 * return the key-value map parsed from the xmlString
	 * @param xmlString
	 * @return
	 */
	private static HashMap<String, String> parseToMap(String xmlString) {
		assert(xmlString != null);
		assert(xmlString.length() > 5);
		
		//get rid of the first and last character
		xmlString = xmlString.substring(1, xmlString.length()-2);
		String[] xmlSegments = xmlString.split("> <");
		HashMap<String, String> results = new HashMap<String, String>();
		
		for (String segment : xmlSegments) {
			//segment format should be like ADD>Add</ADD
			
			String key = segment.substring(0, segment.indexOf('>'));
			//key format should be like ADD
			
			String value = segment.replace(key + ">", "").replace("</" + key, "");
			//value format should be like Add
			
			results.put(key, value);
		}
		return results;
	}
	
	
	public static TimeInterval parseTimeInterval (ArrayList<String> userInputStrings) throws CommandFailedException {
		ArrayList<Date> dates = parseTimeToDate(userInputStrings);
		assert(dates != null);
		TimeInterval interval = new TimeInterval();
		
		if (dates.size() == 1) {
			interval = new TimeInterval(null, dates.get(0));
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
	
	private static ArrayList<Date> parseTimeToDate (ArrayList<String> userInputStrings) {
	    Properties props = new Properties();
	    props.put("sutime.binders","0");
	    props.put("sutime.rules", "src/NLPTraining/defs.sutime.txt, src/NLPTraining/english.holidays.sutime.txt, src/NLPTraining/english.sutime.txt");
	    AnnotationPipeline pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	    pipeline.addAnnotator(new TokenizerAnnotator(false));
	    
	    ArrayList<Date> results = new ArrayList<Date>();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    String stringForToday = format.format(Calendar.getInstance().getTime());
	    
	    for (String text : userInputStrings) {
	      Annotation annotation = new Annotation(text);
	      annotation.set(CoreAnnotations.DocDateAnnotation.class, stringForToday);
	      pipeline.annotate(annotation);
	      List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
	      for (CoreMap cm : timexAnnsAll) {
	    	  String interpretedTimeString = cm.get(TimeExpression.Annotation.class).getTemporal().toString();
	    	  if (parseStringToDate(interpretedTimeString) != null) {
	    		  results.add(parseStringToDate(interpretedTimeString));
	    	  }
	      }
	    }
	    return results;
	}
	
	private static Date parseStringToDate (String timeString) {
		//the four possible format:
		//2014-10-15T14:00
		//2014-10-24-WXX-5
		//2014-10-24
		//2014-02
		//2014
		Date date = null;
		try {
			if (timeString.length() == 4) {
				date = new SimpleDateFormat("yyyy", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 7) {
				date = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 10) {
				date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(timeString);
			} else if (timeString.length() == 15) {
				if (timeString.charAt(10) == 'T') {
					timeString.replace('T', ' ');
					date = new SimpleDateFormat("yyyy-MM-dd hh:00", Locale.ENGLISH).parse(timeString);
				} else {
					timeString = timeString.substring(0, 10);
					date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(timeString);
				}
				date = new SimpleDateFormat("yyyy", Locale.ENGLISH).parse(timeString);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
