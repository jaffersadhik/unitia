package com.winnovature.unitia.util.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.params.HttpMethodParams;
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

public class HttpsPost {


public String sendDN(String urlString, HashMap params){
String responseString = "";
SSLSocketFactory sf=null;
SSLContext context=null;
String encryptedPassword=null;
HttpPost post=null;
try {
//context=SSLContext.getInstance("TLSv1.1"); // Use this line for Java version 6
context=SSLContext.getInstance("TLSv1.2"); // Use this line for Java version 7 and above
context.init(null, null, null);
sf=new SSLSocketFactory(context, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
Scheme scheme=new Scheme("https",443,sf);
HttpClient client=new DefaultHttpClient();
client.getConnectionManager().getSchemeRegistry().register(scheme);


client.getParams().setParameter(
	    HttpMethodParams.USER_AGENT,
	    "Unitia SMS gateway"
	);

post=new HttpPost(urlString);
List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);

if(params!=null || !params.isEmpty()){
	
	for(Iterator keys=params.keySet().iterator();keys.hasNext();){
		String param	=	(String)keys.next();
		nameValuePairs.add(new BasicNameValuePair(param, (String)params.get(param)));

	}
}
post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
HttpResponse response=client.execute(post);
BufferedReader bf=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
String line="";
while((line=bf.readLine())!=null){
responseString = responseString+line;
}
System.out.println(responseString);
} catch (NoSuchAlgorithmException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (KeyManagementException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (UnsupportedEncodingException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (ClientProtocolException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}finally {
	try{
	post.releaseConnection();

	
	}catch(Exception e1){
		try{
			
			post.abort();

			
		}catch(Exception e3){
			
		}
	}
	}
return responseString;
}


}
