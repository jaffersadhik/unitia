package unitiasmpp.util;

public class UtilityCommon {
	  public String convertLongToString(long diff) {
		  StringBuilder strDate = new StringBuilder();
		  long diffSeconds = 0;
		  long diffMinutes = 0;
		  long diffHours = 0;
		  long diffDays = 0;
		  diffDays = diff / (24 * 60 * 60 * 1000);
		  diff = diff - (diffDays * 24 * 60 * 60 * 1000);
		  diffHours = diff / (60 * 60 * 1000);
		  diff = diff - (diffHours * 60 * 60 * 1000);
		  diffMinutes = diff / (60 * 1000);		       
		  diff = diff - (diffMinutes * 60 * 1000);
		  diffSeconds = diff / 1000;		        	
		  diff = diff - (diffSeconds * 1000);
		  strDate.append(diffDays).append(" days ").append(diffHours).append(" hours ")
		  .append(diffMinutes).append(" mins ").append(diffSeconds).append(" secs ")
		  .append(diff).append(" millis");
		  return strDate.toString();  
	  }
}
