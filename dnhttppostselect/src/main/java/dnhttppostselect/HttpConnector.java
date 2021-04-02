package dnhttppostselect;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.winnovature.unitia.util.misc.ErrorMessage;

public class HttpConnector {

	
	
	
	public String send(String url,String gsonstring){
		
		HttpPost post=null;
		String responseString = "";
		try {
		DefaultHttpClient client = new DefaultHttpClient();

		int timeout =60; // seconds
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(
		  httpParams, timeout * 1000); // http.connection.timeout
		HttpConnectionParams.setSoTimeout(
		  httpParams, timeout * 1000); // http.socket.timeout
		
		//

		 post=new HttpPost(url);

		 StringEntity postingString = new StringEntity(gsonstring);
		 post.setEntity(postingString);
		 post.setHeader("Content-type", "application/json");
		 HttpResponse response=client.execute(post);
		BufferedReader bf=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line="";
		while((line=bf.readLine())!=null){
		responseString = responseString+line;
		}
		//xmsgmap.put("",responseString);
		}catch(Exception e){
			
		
			responseString=ErrorMessage.getMessage(e);
			

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

		
		return responseString;
		}
}
