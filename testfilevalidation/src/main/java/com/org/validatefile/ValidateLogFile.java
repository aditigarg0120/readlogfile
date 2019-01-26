package com.org.validatefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aditi Garg
 *
 *         Version 0.1
 */
public class ValidateLogFile {

	/*
	 * Main method for below functionality 1. load property file and its values
	 * 2. loads the log file and scan it line by line 3. reads entry and exit
	 * timestamp 4. display the difference between entry and exit
	 */
	public static void main(String[] args) {

		long timeInMilliSecondsEntered = 0L;
		long timeInMilliSecondsExited = 0L;
		Properties prop = new Properties();
		Scanner scanner = null;
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// loading log file generated
			File file = new File(prop.getProperty(Constants.FILEPATH));

			// scanning the log file
			scanner = new Scanner(file);

			// scanning log file line by line
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String patternEntered = Constants.ENTRYPATTERN;
				String patternExited = Constants.EXITPATTERN;

				// Analysing entry pattern and exit pattern
				if (patternMatcher(line, patternEntered)) {
					timeInMilliSecondsEntered = getTimestamp(line);
				} else if (patternMatcher(line, patternExited)) {
					timeInMilliSecondsExited = getTimestamp(line);
				}

				// Calculating and display the time difference between entry
				// time and exit time
				if (timeInMilliSecondsEntered != 0L && timeInMilliSecondsExited != 0L) {
					long timeInMilliSecondsDiff = timeInMilliSecondsExited - timeInMilliSecondsEntered;
					Date date2 = new Date(timeInMilliSecondsDiff);

					SimpleDateFormat sdf2 = new SimpleDateFormat(Constants.TIMEFORMAT);
					sdf2.setTimeZone(TimeZone.getTimeZone(prop.getProperty(Constants.TIMEZONE)));
					System.out.println("Timestamp difference in HH:mm:ss is " + sdf2.format(date2));

					// once time difference is retrieved then set the entry time
					// and exit time to zero
					timeInMilliSecondsExited = 0L;
					timeInMilliSecondsEntered = 0L;
				}
			}
		} catch (IOException e) {
			System.out.println("Error in loading file.. Error Message: " + e);
		} finally {
			scanner.close();
		}
	}

	/*
	 * Method to match string with pattern input- String, String output- boolean
	 */
	private static boolean patternMatcher(String line, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	/*
	 * Method to return the timestamp present in the string line and converting
	 * it to milliseconds input- String output- long
	 */
	private static long getTimestamp(String line) {
		long timeInMilliSeconds = 0L;
		Pattern timeRegex = Pattern.compile("\\d*(\\.)\\d*");
		Matcher matcherTime = timeRegex.matcher(line);
		if (matcherTime.find()) {
			String text = matcherTime.group();
			double value = Double.parseDouble(text);
			timeInMilliSeconds = (long) Math.floor(value * 60 * 1000);
		}

		return timeInMilliSeconds;

	}

}
