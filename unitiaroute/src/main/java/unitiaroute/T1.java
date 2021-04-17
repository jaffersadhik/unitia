package unitiaroute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class T1 {

public  boolean isMatch(String template,String fullmsg){
		
		String temp[]=StringUtils.split(template);
		String msg[]=StringUtils.split(fullmsg);
		
		boolean result=isMatch(temp, msg);
		if(result){
			
			return true;
		}
		return isMatch(reverse(temp),reverse( msg));
}

private String [] reverse(String [] param){
	
	String  [] result=new String[param.length];
	int j=0;
	for(int i=param.length-1;i>=0;i--){
		
		result[j++]=param[i];
		
		
	}
	
	
	return result;
}
private boolean isMatch(String temp[],String msg[]){
	
	

	int msgpointer=0;

	for(int i=0;i<temp.length;i++){
		
		if(msgpointer>=msg.length){
			
			return  false;
		}
		String m=msg[msgpointer];
		String t=temp[i];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			int lastTempPointer=getLastTempPointer(temp,i);

			int maxVarCharCount=getMaxVarcharCount(lastTempPointer,temp);
			int upcomingmsgpointer=getUpcomingMsgPointer(lastTempPointer,temp,msg,msgpointer);
			String varchar=getVarcharCharacter(msg,msgpointer,upcomingmsgpointer,lastTempPointer,temp);
			int varcharcount=varchar.length();

			String tempvarchar=getTempVarchar(i,lastTempPointer,temp);
	
			//System.out.println(" i "+ i);
			//System.out.println(" tempvarchar "+ tempvarchar);

			if((i+1)<temp.length){
				//System.out.println(" next temp "+ temp[i+1]);

			}else{
				
				//System.out.println(" next temp "+ "");

			}
			
			if((upcomingmsgpointer)<msg.length){
				//System.out.println(" upcoming msg "+ msg[upcomingmsgpointer]);

			}else{
				
				//System.out.println(" upcoming msg "+ "");

			}
			
			if((msgpointer+1)<msg.length){
				//System.out.println(" next msg "+ msg[msgpointer+1]);

			}else{
				
				//System.out.println(" next msg "+ "");

			}
			//System.out.println(" lastTempPointer "+ lastTempPointer);
			//System.out.println(" maxVarCharCount "+ maxVarCharCount);
			//System.out.println(" upcomingmsgpointer "+ upcomingmsgpointer);
			//System.out.println(" varcharcount "+ varcharcount);
			//System.out.println(" varchar "+ varchar);
			//System.out.println(" msgpointer "+ msgpointer);
			//System.out.println(" msg.length "+ msg.length);
			//System.out.println(" temp.length "+ temp.length);


			String	prefixingcharacter=getPrefixingCharacter(tempvarchar);

			
								//System.out.println(" prefixingcharacter "+ prefixingcharacter);

				
				if(prefixingcharacter!=null&&prefixingcharacter.trim().length()>0&&!varchar.startsWith(prefixingcharacter)){
					
					return false;
				}
				
				String	suffixingcharacter=getSufffixingCharacter(tempvarchar);
				
				//System.out.println(" suffixingcharacter "+ suffixingcharacter);

				

				
				if(suffixingcharacter!=null&&suffixingcharacter.trim().length()>0&&!varchar.endsWith(suffixingcharacter)){
					return false;
				}
				

				List<String> intersectlist =getIntserSectList(tempvarchar);
				
				if(intersectlist!=null){
					//System.out.println("intersectlist : "+intersectlist);
					
				
					for(int j=0;j<intersectlist.size();j++){
						
						String s=intersectlist.get(j);

						if(s!=null&&s.trim().length()>0){
						if(!(varchar.indexOf(s.trim())>-1)){

							//System.out.println(s);
							return false;
						}else{
							
							maxVarCharCount=maxVarCharCount+s.length();

						}
						}
						
					}
				}
				
				i=lastTempPointer;

			
	
	
			if(maxVarCharCount>varcharcount){
				msgpointer=upcomingmsgpointer;
			}else{
				return false;
			}

		}else if(! m.equalsIgnoreCase(t)){
			
			//System.out.println("temp : "+t);
			//System.out.println("msg : "+m);

			return false;
		}else{
			msgpointer++;
		}

		
	
	} 
/*
*/
	if(msg.length==temp.length){
		
		return true;
		
	}else if(msg.length>temp.length){
		
		
		String t=temp[temp.length-1];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
		
			if(msgpointer==msg.length-1){
				
				return true;

			
			
			}else{
				
				return false;
			}
		}else{
	
		if(msgpointer==msg.length){
	
			return true;

		
		
		}else{
			
			return false;
		}
		
		}
	}else{
		
		return true;
	}
	

}
private List<String> getIntserSectList(String tempvarchar) {
	
	List<String> result=new ArrayList<String>();

	StringBuffer sb=new StringBuffer();
	char c[]=tempvarchar.toCharArray();

	for(int i=0;i<=c.length;i++){
	
		if(i<c.length){
		if(c[i]=='{'){
			if((i+6)<c.length){
				
				if(c[i+1]=='#'&&c[i+2]=='v'&&c[i+3]=='a'&&c[i+4]=='r'&&c[i+5]=='#'&&c[i+6]=='}'){
				
					i=i+6;
					if(sb.toString().trim().length()>0){
					result.add(sb.toString());
					sb=new StringBuffer();
					}
				}else{
					
					sb.append(c[i]);

				}
			}
		}else{
		
			sb.append(c[i]);

		}
		}else{
			if(sb.toString().length()>0){
				result.add(sb.toString());
				sb=new StringBuffer();
				}	
		}
		
	}
	
	return result;
}

private String getTempVarchar(int min,int lastTempPointer, String[] temp) {

	StringBuffer result=new StringBuffer();
	
	for(int i=min;i<=lastTempPointer;i++){
		String t=temp[i];

		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			result.append(t).append(" ");
		}else{
			break;
		}
	}

	return result.toString().trim();

	

}

private String getSufffixingCharacter(String t) {
	String temp[]=t.split(" ");
	
	return temp[temp.length-1].substring(temp[temp.length-1].lastIndexOf("{#var#}")+7);
}

private String getPrefixingCharacter(String t) {
	
	int i=t.indexOf("{#var#}");
	if(i>0){
	
		return t.substring(0,i);
	}
	return "";
}

private String getVarcharCharacter(String[] msg, int msgpointer, int upcomingmsgpointer,int temppointer,String [] temp) {
	
	StringBuffer sb=new StringBuffer();
	if(msgpointer==upcomingmsgpointer){
		sb.append(msg[msgpointer]);
	}else{
		int end=upcomingmsgpointer-1;
		//System.out.println("temp[temppointer] : "+temp[temppointer]);
		//System.out.println("msg[upcomingmsgpointer] : "+msg[upcomingmsgpointer]);

		if(temppointer==temp.length-1){

			if(temp[temppointer].equals(msg[upcomingmsgpointer])){
				
				end=upcomingmsgpointer;
			}
			
		}
		
	for(int i=msgpointer;i<=end;i++){
		
		sb.append(msg[i]).append(" ");
		
	}
	}
	return sb.toString().trim();
}

private int getUpcomingMsgPointer(int lastTempPointer, String[] temp, String[] msg, int msgpointer) {
	
	int temppointer=lastTempPointer;
	
	
	temppointer++;
	
	if(temppointer==temp.length){
		
		return msg.length-1;
	}
	
	if(temppointer<temp.length){
		
		String t=temp[temppointer];
		
		for(int i=msgpointer;i<msg.length;i++){
			
			String m=msg[i];
			
			if(m.equalsIgnoreCase(t)){
				
				return i;
			}
		}
	}
		
		return msg.length-1;
	
	
}

private int getMaxVarcharCount(int lastTempPointer, String[] temp) {

	int pointer=lastTempPointer;
	int count=getCount(temp[pointer]);
	while(pointer>-1){
		
		pointer--;
		
		if(pointer==-1){
			
			return (count*30)+1;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count=count+getCount(t);
		}else{
			return (count*30)+1;
		}
	}

	return (count*30)+1;

	
}

private int getCount(String keyword) {
	String var="{#var#}";
	int count=0;
	while(keyword!=null&&keyword.length()>0){
		
		int index=keyword.indexOf(var);
		if(index > -1){
		keyword=keyword.substring(index+7);
		count++;
		}else{
			
			break;
		}
	}
	
	return count;
}

private int getLastTempPointer(String[] temp, int i) {

	int pointer=i;
	int count=0;
	while(pointer<temp.length){
		
		pointer++;
		
		if(pointer==temp.length){
			
			return i+count;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count++;
		}else{
			return count+i;
		}
	}

	return count+i;
}


}
