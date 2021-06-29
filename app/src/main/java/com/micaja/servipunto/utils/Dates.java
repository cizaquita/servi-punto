/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/

package com.micaja.servipunto.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.net.ParseException;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Dates {
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm:ss";
	public static final String DD_MM_YYYY_HH_MM = "dd-MM-yyyy, hh:mm";
	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";
	public static final String HH_MM = "HH:MM";

	/**
	 * Returns system date required format. format argument is specifies format
	 * of date.
	 * 
	 * <p>
	 * This method always returns system date.
	 * 
	 * @param format
	 *            the system date with specified format like MM/dd/yyyy or
	 *            dd:MM:yyyy
	 */

	public static String getSysDate(final String format) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Log.v("varahalaabu===========", formatter.format(date).toString());
		return formatter.format(date).toString();
	}

	public static long getSysDateinMilliSeconds() {
		Date date = new Date();

		return date.getTime();
	}

	/**
	 * Returns system date required format and locale. format argument is
	 * specifies format of date. locale argument is specifies country date
	 * format
	 * 
	 * <p>
	 * This method always returns system date.
	 * 
	 * @param format
	 *            the system date with locale & specified format like MM/dd/yyyy
	 *            or dd:MM:yyyy
	 */

	public static String getSysDateWithLocale(String format, Locale locale) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format, locale);

		return formatter.format(date).toString();
	}

	/**
	 * Returns system time required format. format argument is specifies format
	 * of time.
	 * 
	 * <p>
	 * This method always returns system time.
	 * 
	 * @param format
	 *            the system time with specified format hh:mm:ss a
	 */

	public static String getSysTime(final String format) {
		DateFormat dateFormat = new SimpleDateFormat(format); // "hh:mm a"
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Returns No.of days between two dates firstDate argument is specifies
	 * first date. secondDate argument is specifies second date.
	 * 
	 * <p>
	 * This method always returns no.of days between two dates.
	 * 
	 * @param firstDate
	 *            date any format
	 * @param secondDate
	 *            date any format
	 */
	public static int getDifferenceInDays(String firstDate, String secondDate) {
		DateFormat df = new SimpleDateFormat(YYYY_MM_DD);
		Date startDate, endDate;
		long days = 0;
		try {
			startDate = df.parse(firstDate);
			endDate = df.parse(secondDate);
			days = (endDate.getTime() - startDate.getTime())
					/ (1000 * 60 * 60 * 24);

		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return (int) days;
	}

	public static Boolean featureDateValidation(String dateFromPicker) {
		boolean result = false;

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);

			String currentDateandTime = sdf.format(new Date());

			Date date1 = null;
			Date date2 = null;

			date1 = sdf.parse(currentDateandTime);
			date2 = sdf.parse(dateFromPicker);

			if (date1.after(date2)) {
				result = true;
			}

			if (date1.before(date2)) {
				result = false;
			}

			if (date1.equals(date2)) {
				result = true;
			}
			return result;
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (java.text.ParseException e1) {
		}
		return result;
	}

	public static String convertDate(String inputFormat, String outputFormat,
			String dateStr) {
		try {
			SimpleDateFormat input = new SimpleDateFormat(inputFormat);
			SimpleDateFormat required = new SimpleDateFormat(outputFormat);

			return required.format(input.parse(dateStr));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String convertMilliSecondsToDate(long milliSeconds,
			String format) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(milliSeconds);

			SimpleDateFormat df = new SimpleDateFormat(format);

			return df.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String changeFormate(String inputdate) {

		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {
			}
		}
		DateFormat dfff = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
		return dfff.format(startDate);
	}

	public static String serverdateformate(String inputdate) {

		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {

			}
		}
		DateFormat dfff = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		return dfff.format(startDate);
	}

	public static Date getdate(String inputdate) {

		try {
			//Log.v("Date ", "varahalababu" + inputdate);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.parse(inputdate);
		} catch (java.text.ParseException e) {
			Log.v("exception", "Babu");
			return new Date();
		}

	}

	public static String formateyyyyMMdd(String inputdate) {

		Date startDate = null;
		try {
			DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
			startDate = dff.parse(inputdate);
		} catch (java.text.ParseException e1) {
		}
		DateFormat dfff = new SimpleDateFormat("yyyy-MM-dd");
		return dfff.format(startDate);
	}

	public static String c_date() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(new Date());
	}

	/*
	// Se comenta este método ya que el formato de la fecha retornado es erróneo
	public static Date serverdateformateDateObj(String inputdate) {
		Log.e("inputdate -->", inputdate);
		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {
			}
		}
		DateFormat dfff = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
		try {
			startDate = dfff.parse(dfff.format(startDate));
		} catch (Exception e) {
			Date date = new Date();
			startDate = date;
		}
		Log.e("final date -->", startDate.toString());
		return startDate;
	}
	*/

	public static Date serverdateformateDateObj(String inputdate) {
		//Log.e("inputdate -->", inputdate);
		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			//Log.e("ParseException -->", e.getMessage());
			startDate = new Date();
		}
		//Log.e("final date -->", startDate.toString());
		return startDate;
	}

	public static Date serverdateformateDateObj1(String inputdate) {

		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				inputdate = "2014-05-20 14:12:30";
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {
			}
		}
		return startDate;
	}

	public static String getStringDate(Date date) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
		return dateFormat.format(date);

	}

	public static String currentdate() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
		return dateFormat.format(new Date());
	}

	public static String datetostring(String inputdate) {

		Date startDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {
			}
		}
		DateFormat dfff = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
		return dfff.format(startDate);
	}

	public static String current_date() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}

	public static String current_dateddmmyyy() {

		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		Log.v("date", "babu" + dateFormat.format(new Date()));
		return dateFormat.format(new Date());
	}

	public static String getCurrentTime(String date) {
		Date startDate = null;
		String time = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
			startDate = dateFormat.parse(date);
			SimpleDateFormat localDateFormat = new SimpleDateFormat("HHmmss");
			time = localDateFormat.format(startDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getDateFromeString(String vale) {
		Date startDate = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 2015-05-25
		try {
			startDate = dateFormat.parse(vale);
			int day = startDate.getDate();
			int mon = startDate.getMonth() + 1;
			String m_value = "" + mon;
			if (m_value.length() == 1) {
				m_value = "0" + m_value;
			}
			String year = "" + startDate.getYear();
			String value = year.substring(1, 3);
			return "" + day + m_value + value;
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String printdate(String inputdate) {

		Date startDate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			startDate = df.parse(inputdate);
		} catch (java.text.ParseException e) {
			try {
				DateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				startDate = dff.parse(inputdate);
			} catch (java.text.ParseException e1) {
			}
		}
		return df.format(startDate);
	}

	public static String getTimeFormate(String myTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
		Date date = null;
		try {
			date = sdf.parse(myTime);
			Log.v("===", "varahlababu" + myTime);
			return new SimpleDateFormat("hh:mm:ss").format(date);
		} catch (java.text.ParseException e) {
			return myTime;
		}
	}

	public static String getMyCashBoxFecha(String vale) {
		Date startDate = null;
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");// 2015-05-25
		try {
			startDate = dateFormat.parse(vale);
			return new SimpleDateFormat("dd-MM-yyyy").format(startDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return vale;
		}
	}
	
	public static String getMyCashBoxFechaforbar(String vale) {
		Date startDate = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 2015-05-25
		try {
			startDate = dateFormat.parse(vale);
			return new SimpleDateFormat("dd-MM-yyyy").format(startDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return vale;
		}
	}
	public static String currentdate2() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(new Date());
	}
	
	public static String currentdate2forbarcodecollection() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(new Date());
	}
	
	
	public static String get_2bpunthodate(String input_date) throws java.text.ParseException{
		String input = input_date;
		String output;
		try {
			DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date = inputFormatter.parse(input);
			DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
			output = outputFormatter.format(date);
		} catch (Exception e) {
			DateFormat inputFormatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			Date date = inputFormatter.parse(input);
			DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
			output = outputFormatter.format(date);
		}
		return output;
	}
	
	public static String get_2bpunthohovers(String input_date) throws java.text.ParseException{
		String input = input_date;
		String output;
		try {
			DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date = inputFormatter.parse(input);
			DateFormat outputFormatter = new SimpleDateFormat("HH:mm:ss");
			output = outputFormatter.format(date);
		} catch (Exception e) {
			DateFormat inputFormatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			Date date = inputFormatter.parse(input);
			DateFormat outputFormatter = new SimpleDateFormat("HH:mm:ss");
			output = outputFormatter.format(date);
		}
		return output;
	}

	// Method for formatting the date to dd-mm-aaaa hh:mm am|pm
	public static String formatDateToDD_MM_YYYY_HH_MM(String date){
		String[] dateArray = date.split(" ");
		String partialDate = dateArray[0];
		String partialHour = dateArray[1];
		String[] newDateArray = partialDate.split("-"); // aaaa-mm-ddd
		String day = newDateArray[2];
		String month = newDateArray[1];
		String year = newDateArray[0];
		String[] hourArray = partialHour.split(":"); // hh:mm:ss
		int hour = Integer.parseInt(hourArray[0]);
		String mins = hourArray[1];
		//String segs = hourArray[2];
		String timeSystem = "am"; // default
		if(hour == 0){
			hour = 12;
			timeSystem = "am";
		} else if(hour <= 23 && hour >= 12){
			if(hour != 12){
				hour -= 12;
			}
			timeSystem = "pm";
		}
		String formattedHour = (hour < 10) ? "0" + hour : "" + hour;
		//String formattedDate = day + "-" + month + "-" + year + " " + formattedHour + ":" + mins + ":" + segs + " " + timeSystem;
		String formattedDate = day + "-" + month + "-" + year + " " + formattedHour + ":" + mins + timeSystem;
		return formattedDate;
	}

	// Method for formatting the date to dd-mm-aaaa hh:mm:ss am|pm
	public static String formatDateToDD_MM_YYYY_HH_MM_SS(String date){
		String[] dateArray = date.split(" ");
		String partialDate = dateArray[0];
		String partialHour = dateArray[1];
		String[] newDateArray = partialDate.split("-"); // aaaa-mm-ddd
		String day = newDateArray[2];
		String month = newDateArray[1];
		String year = newDateArray[0];
		String[] hourArray = partialHour.split(":"); // hh:mm:ss
		int hour = Integer.parseInt(hourArray[0]);
		String mins = hourArray[1];
		String segs = hourArray[2];
		String timeSystem = "am"; // default
		if(hour == 0){
			hour = 12;
			timeSystem = "am";
		} else if(hour <= 23 && hour >= 12){
			if(hour != 12){
				hour -= 12;
			}
			timeSystem = "pm";
		}
		String formattedHour = (hour < 10) ? "0" + hour : "" + hour;
		String formattedDate = day + "-" + month + "-" + year + " " + formattedHour + ":" + mins + ":" + segs + timeSystem;
		return formattedDate;
	}

	// Method for formatting the date to dd-mm-aaaa without hour
	public static String formatDateToDD_MM_YYYY(String date) {
		String[] newDateArray = date.split("-"); // aaaa-mm-dd
		String formattedDate = "";
		if(newDateArray != null && newDateArray.length == 3){
			String day = newDateArray[2];
			String month = newDateArray[1];
			String year = newDateArray[0];
			if(day.contains(" ")){ // Es porque la fecha vino con hh:mm
				day = day.split(" ")[0];
			}
			formattedDate = day + "-" + month + "-" + year;
		}
		return formattedDate;
	}

	// Method for formatting a Date object to dd-mm-aaaa hh:mm am|pm
	public static String getDate_DD_MM_YYYY_HH_MM(Date date) {
		//Log.e("date antes -->", date.toString());
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa"); // HH para que retorne hora militar
		String formatedDate = dateFormat.format(date);
		if(formatedDate.toLowerCase().contains("am")){
			formatedDate = formatedDate.replace("AM", "am");
		}
		if(formatedDate.toLowerCase().contains("pm")){
			formatedDate = formatedDate.replace("PM", "pm");
		}
		return formatedDate;
	}

	public static String getDate_DD_MM_YYYY_HH_MM(Date date,int delta ) {
		//Log.e("date antes -->", date.toString());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR,delta);
		date = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa"); // HH para que retorne hora militar
		String formatedDate = dateFormat.format(date);
		if(formatedDate.toLowerCase().contains("am")){
			formatedDate = formatedDate.replace("AM", "am");
		}
		if(formatedDate.toLowerCase().contains("pm")){
			formatedDate = formatedDate.replace("PM", "pm");
		}
		return formatedDate;
	}

	public static String addOneDayToTwoDate(String date) {
		String dt = date; // Start date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(dt));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.add(Calendar.DATE, 1); // number of days to add
		return sdf.format(c.getTime());
	}

}
