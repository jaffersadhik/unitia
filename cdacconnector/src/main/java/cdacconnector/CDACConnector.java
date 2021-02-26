package cdacconnector;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;

public class CDACConnector {

	
	public String getErrorMessage(){
		
		return MessageStatus.MAX_KANNEL_RETRY_EXCEEDED+" : Max retry Exceed";
	}
	
	
	public String sendUnicodeSMS(Map<String,Object> msgmap){
		
		HttpPost post=null;
		String finalmessage = "";

		String message=Convertor.getMessage(msgmap.get(MapKeys.FULLMSG).toString());


		String ip=msgmap.get(MapKeys.KANNEL_IP).toString();
		String port=msgmap.get(MapKeys.KANNEL_PORT).toString();

		String username=msgmap.get(MapKeys.CDAC_USERNAME).toString();
		String password=msgmap.get(MapKeys.CDAC_PASSWORD).toString();

		String senderId=msgmap.get(MapKeys.SENDERID).toString();		
		String mobileNumber=msgmap.get(MapKeys.MOBILE).toString();
		String secureKey=msgmap.get(MapKeys.CDAC_KEY).toString();
		String templateid=msgmap.get(MapKeys.TEMPLATEID).toString();
		
		for(int i = 0 ; i< message.length();i++){
		char ch = message.charAt(i);
		int j = (int) ch;
		String sss = "&#"+j+";";
		finalmessage = finalmessage+sss;
		}
		String responseString = "";
		
		boolean isRetry=true;
		int attempt=0;
		while(isRetry){
			isRetry=false;
			attempt++;

		SSLSocketFactory sf=null;
		SSLContext context=null;
		String encryptedPassword=null;
		try{
		//context=SSLContext.getInstance("TLSv1.1"); // Use this line for Java version 6
		context=SSLContext.getInstance("TLSv1.2"); // Use this line for Java version 7 and above
		context.init(null, null, null);
		sf=new SSLSocketFactory(context, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
		Scheme scheme=new Scheme("https",443,sf);
		HttpClient client=new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(scheme);
		 post=new HttpPost("https://"+msgmap.get(MapKeys.KANNEL_IP)+"/esms/sendsmsrequestDLT");


		String genratedhashKey = hashGenerator(username,senderId, finalmessage, secureKey);
		List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("bulkmobno", mobileNumber));
		nameValuePairs.add(new BasicNameValuePair("senderid", senderId));
		nameValuePairs.add(new BasicNameValuePair("content", finalmessage));
		nameValuePairs.add(new BasicNameValuePair("smsservicetype", "unicodemsg"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("key", genratedhashKey));
		nameValuePairs.add(new BasicNameValuePair("templateid", templateid));
		
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response=client.execute(post);
		BufferedReader bf=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line="";
		while((line=bf.readLine())!=null){
		responseString = responseString+line;
		}
		} catch (NoSuchAlgorithmException e) {
		} catch (KeyManagementException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			
			if(attempt<5){
				isRetry=true;
			}
			responseString=getErrorMessage();
			sleep();
		}catch(Exception e){
			
			if(attempt<5){
				isRetry=true;
			}

			responseString=getErrorMessage();
			sleep();

		}
		finally {
			try{
				post.releaseConnection();

				
				}catch(Exception e1){
					try{
						
						post.abort();

						
					}catch(Exception e3){
						
					}
				}
				}
		}
		return responseString;

	}
	
	

	private String getQuery(Map<String,Object> msgmap,String serviceType) throws UnsupportedEncodingException{
		
		StringBuffer sb=new StringBuffer();

		String message=msgmap.get(MapKeys.FULLMSG).toString().trim();

		String genratedhashKey = hashGenerator(msgmap.get(MapKeys.CDAC_USERNAME).toString().trim(), msgmap.get(MapKeys.SENDERID).toString().trim(), message, msgmap.get(MapKeys.CDAC_KEY).toString().trim());

		
		sb.append("mobileno").append("=").append(URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString().trim(),"UTF-8")).append("&");
		sb.append("senderid").append("=").append(URLEncoder.encode(msgmap.get(MapKeys.SENDERID).toString().trim(),"UTF-8")).append("&");
		sb.append("content").append("=").append(URLEncoder.encode(message,"UTF-8")).append("&");
		sb.append("smsservicetype").append(URLEncoder.encode(serviceType,"UTF-8")).append("").append("&");
		sb.append("username").append("=").append(URLEncoder.encode(msgmap.get(MapKeys.CDAC_USERNAME).toString().trim(),"UTF-8")).append("&");
		sb.append("password").append("=").append(URLEncoder.encode(msgmap.get(MapKeys.CDAC_PASSWORD).toString().trim(),"UTF-8")).append("&");
		sb.append("key").append("=").append(URLEncoder.encode(genratedhashKey,"UTF-8")).append("&");
		sb.append("templateid").append("=").append(URLEncoder.encode(msgmap.get(MapKeys.TEMPLATEID).toString().trim(),"UTF-8")).append("&");

		return sb.toString();
	}
	
	public String sendSingleSMS(Map<String,Object> msgmap){
		
		HttpsURLConnection con=null;
		String responseString = "";

		boolean isRetry=true;
		int attempt=0;
		while(isRetry){
		try{
		String httpsURL = "https://www.abcd.com/auth/login/";

		String query = getQuery(msgmap,"singlemsg");
		
		msgmap.put("cdac_query", query);

		URL myurl = new URL("https://"+msgmap.get(MapKeys.KANNEL_IP)+"/esms/sendsmsrequestDLT");
		 con = (HttpsURLConnection)myurl.openConnection();
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-length", String.valueOf(query.length())); 
		con.setRequestProperty("Content-Type","application/x-www- form-urlencoded"); 
		con.setRequestProperty("User-Agent", "Unitia 1.0"); 
		con.setDoOutput(true); 
		con.setDoInput(true); 

		DataOutputStream output = new DataOutputStream(con.getOutputStream());  


		output.writeBytes(query);

		output.close();

	
		responseString= con .getResponseMessage(); 	
		}catch(Exception e){
			

			
			if(attempt<5){
				isRetry=true;
			}

			responseString=getErrorMessage();
			sleep();

		
		}finally{
			
			try{
				con.disconnect();

			}catch(Exception e){
				
			}
		}
		
		}
		return responseString;
}

	private void sleep() {
		
		try{
			Thread.sleep(1000L);
		}catch(Exception e){
			
		}
	}

	
	/*
	public String sendSingleSMS(Map<String,Object> msgmap){
		
		HttpPost post=null;
		String message=msgmap.get(MapKeys.FULLMSG).toString();
		String responseString = "";
		boolean isRetry=true;
		int attempt=0;
		while(isRetry){
			isRetry=false;
			attempt++;
		SSLSocketFactory sf=null;
		SSLContext context=null;
		try {
		//context=SSLContext.getInstance("TLSv1.1"); // Use this line for Java version 6
		context=SSLContext.getInstance("TLSv1.2"); // Use this line for Java version 7 and above
		context.init(null, null, null);
		sf=new SSLSocketFactory(context, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
		Scheme scheme=new Scheme("https",443,sf);
		HttpClient client=new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(scheme);
		//

		 post=new HttpPost("https://"+msgmap.get(MapKeys.KANNEL_IP)+"/esms/sendsmsrequestDLT");
		String genratedhashKey = hashGenerator(msgmap.get(MapKeys.CDAC_USERNAME).toString().trim(), msgmap.get(MapKeys.SENDERID).toString().trim(), message, msgmap.get(MapKeys.CDAC_KEY).toString().trim());
		List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("mobileno", msgmap.get(MapKeys.MOBILE).toString()));
		nameValuePairs.add(new BasicNameValuePair("senderid",  msgmap.get(MapKeys.SENDERID).toString()));
		nameValuePairs.add(new BasicNameValuePair("content", message));
		nameValuePairs.add(new BasicNameValuePair("smsservicetype", "singlemsg"));
		nameValuePairs.add(new BasicNameValuePair("username", msgmap.get(MapKeys.CDAC_USERNAME).toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("password", msgmap.get(MapKeys.CDAC_PASSWORD).toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("key", genratedhashKey));
		nameValuePairs.add(new BasicNameValuePair("templateid", msgmap.get(MapKeys.TEMPLATEID).toString()));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response=client.execute(post);
		BufferedReader bf=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line="";
		while((line=bf.readLine())!=null){
		responseString = responseString+line;
		}
		//xmsgmap.put("",responseString);
		} catch (NoSuchAlgorithmException e) {
		} catch (KeyManagementException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		
			if(attempt<5){
				isRetry=true;
			}

			responseString=getErrorMessage();
			sleep();
		}catch(Exception e){
			
			if(attempt<5){
				isRetry=true;
			}

			responseString=getErrorMessage();
			sleep();

		}	finally {
			try{
				post.releaseConnection();

				
				}catch(Exception e1){
					try{
						
						post.abort();

						
					}catch(Exception e3){
						
					}
				}
				}

		
		}
		return responseString;
		}

	private void sleep() {
		
		try{
			Thread.sleep(1000L);
		}catch(Exception e){
			
		}
	}
*/
	protected String hashGenerator(String userName, String senderId, String content, String secureKey) {
		// TODO Auto-generated method stub
		StringBuffer finalString=new StringBuffer();
		finalString.append(userName.trim()).append(senderId.trim()).append(content.trim()).append(secureKey.trim());
		// logger.info("Parameters for SHA-512 : "+finalString);
		String hashGen=finalString.toString();
		StringBuffer sb=new StringBuffer();
		MessageDigest md;
		try {
		md = MessageDigest.getInstance("SHA-512");
		md.update(hashGen.getBytes());
		byte byteData[] = md.digest();
		//convert the byte to hex format method 1
		sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
		sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return sb.toString();
		}

	
}
