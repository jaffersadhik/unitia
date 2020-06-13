package com.winnovature.unitia.util.misc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class WinDate {

	private SimpleDateFormat sdfforlog=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	String dateBeforeString = "2020-01-01";

	private String getWinDayId() {
		
		String dateAfterString = getDate();
			
		//Parsing the date
		LocalDate dateBefore = LocalDate.parse(dateBeforeString);
		LocalDate dateAfter = LocalDate.parse(dateAfterString);
			
		//calculating number of days in between
		long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		
		long days=(noOfDaysBetween%9999);
		return ("0000" +( ""+days )).substring((""+days).length());
	}
	
	public String getLogDate() {
		
		return sdfforlog.format(new Date());
	}
	public String getDate() {
		
		return sdf.format(new Date());
	}
	
		
	private String getQuarterOfDate() {
		
		int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		int minute=Calendar.getInstance().get(Calendar.MINUTE);
		
		int quarter=getQuarter(minute);
		
		String quarterstr=""+((hour*4)+quarter);
		
		return ("00" +quarterstr).substring((quarterstr).length());

	}

	private int getQuarter(int minute) {

		if(minute<16) {
			
			return 1;
			
		}else if(minute<31){
			
			return 2;

		}else if(minute<46){
			
			return 3;

		}else {
			
			return 4;
		}
		}

	public String getTime() {
		
		return getWinDayId()+getQuarterOfDate();
	}
	
	public String getLogTime() {
		return getWinDayId()+getQuarterOfDate();
	}
	public static void main(String args[]){
		
		System.out.print(new WinDate().getTime());
	}

	public String getHour() {
		
		return ""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
	}

	public String getMinute() {
		return ""+Calendar.getInstance().get(Calendar.MINUTE);
	}

	public String getSecond() {
		return ""+Calendar.getInstance().get(Calendar.SECOND);

	}
}
