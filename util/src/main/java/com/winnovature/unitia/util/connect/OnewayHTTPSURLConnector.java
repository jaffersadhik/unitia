package com.winnovature.unitia.util.connect;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OnewayHTTPSURLConnector {
	
	Log logger = LogFactory.getLog(this.getClass());
	
	private static final String	className	=	"[OnewayHTTPSURLConnector] ";
	
	private String response	=	null;
	private String errorResponse	=	null;
	HashMap	responseMapWithStatus	=	null;
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @return String application response.
	 */
	public String connectGetMethod(String urlString)throws Exception{
		int port=0;		
		GetMethod httpget =	null;
		String response	=	"";
		logger.info(className + urlString);
		try {
			URL url =new URL(urlString);
			
			port=(url.getPort()<0)?443:url.getPort();
			
			ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
		
			Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

			Protocol.registerProtocol( "https", myHTTPS );

			HttpClient httpclient = new HttpClient();

			httpget = new GetMethod(urlString);
			
			httpget.setFollowRedirects(true);
			
			httpclient.executeMethod(httpget);
			
			if(logger.isDebugEnabled())
			logger.info(className+ "[connectGetmethod] Status Line:  " +httpget.getStatusLine());
			
			response =	 httpget.getResponseBodyAsString();			
			
			}catch(Exception e){
				
				logger.error(className, e);
				
				throw e;
			} finally {
				httpget.releaseConnection();
			}
			return response;		
	}
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @param String data
	 * @return String application response.
	 */
	
	public String connectPostMethod(String urlString, String data)throws Exception{
		int port=0;		
		PostMethod httpPost =	null;
		String response	=	"";
		
		if(logger.isDebugEnabled())
		logger.info(className + "connectPostMethod(String urlString, String data)" + urlString);
		try {
			URL url =new URL(urlString);
			
			port=(url.getPort()<0)?443:url.getPort();
			
			ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
		
			Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

			Protocol.registerProtocol( "https", myHTTPS );

			HttpClient httpclient = new HttpClient();

			httpPost = new PostMethod(urlString);
			
			httpPost.setRequestEntity(new StringRequestEntity(data, null, null));
			
			httpclient.executeMethod(httpPost);
			if(logger.isDebugEnabled())
			logger.info(className+ "[connectPostMethod(String urlString, String data)] Status Line:  " +httpPost.getStatusLine());
			
			response =	 httpPost.getResponseBodyAsString();			
			
			}catch(Exception e){
				
				logger.error(className + "[connectPostMethod(String urlString, String data)]", e);
				
				throw e;
			} finally {
				httpPost.releaseConnection();
			}
			return response;		
	}
	
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @param HashMap params
	 * @return String application response.
	 */
	public String connectPostMethod(String urlString, HashMap params)throws Exception{
		int port=0;		
		PostMethod httpPost =	null;
		String response	=	"";
		if(logger.isDebugEnabled())
		logger.info(className + "connectPostMethod(String urlString, String data)" + urlString);
		try {
			URL url =new URL(urlString);
			
			port=(url.getPort()<0)?443:url.getPort();
			
			ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
		
			
			Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

			Protocol.registerProtocol( "https", myHTTPS );
			
			HttpClient httpclient = new HttpClient();

			httpPost = new PostMethod(urlString);
			
			if(params!=null || !params.isEmpty()){
				
				for(Iterator keys=params.keySet().iterator();keys.hasNext();){
					String param	=	(String)keys.next();
					httpPost.addParameter(param, (String)params.get(param));
				}
			}
			httpclient.executeMethod(httpPost);
			if(logger.isDebugEnabled())
			logger.info(className+ "[connectPostMethod(String urlString, String data)] Status Line:  " +httpPost.getStatusLine());
			
			response =	 httpPost.getResponseBodyAsString();			
			
			}catch(Exception e){
				
				logger.error(className + "[connectPostMethod(String urlString, String data)]", e);
				
				throw e;
			} finally {
				try{
				httpPost.releaseConnection();

				
				}catch(Exception e1){
					try{
						
						httpPost.abort();

						
					}catch(Exception e3){
						
					}
				}
				}
			return response;		
	}
	
	/* This method connects client URL using HTTP GET method (uses socket)
	 * @param String URL
	 * @return String application response.
	 */
	public String postSocketRequest(String urlString) throws Exception
	{	
		int port	=	0;
		SSLSocket socket = null;
		SSLSocketFactory factory = null;
		BufferedWriter bWriter = null;
		BufferedReader bReader = null;
		Provider a_provider = null;
		String inputLine = "";
		String message = "";
		String inputTxt = "";
		boolean flag = false;
		URL url =new URL(urlString);
		
		port=(url.getPort()<0)?443:url.getPort();
		//load SSL Provider
		try
		{
			Class provider = Class.forName("com.sun.net.ssl.internal.ssl.Provider");
			a_provider = (Provider) provider.newInstance();
			Security.addProvider(a_provider);
		}
		catch (Exception ex)
		{
			throw ex;
		}
		try
		{
			KeyManagerFactory kmf;
			KeyStore ks;
			KeyManager[] km = null;
			TrustManager[] myTM = new TrustManager[] { new WinTrustManager()};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, myTM, null);
			factory = ctx.getSocketFactory();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			//create SSL socket
			socket = (SSLSocket) factory.createSocket(url.getHost(), port);
			socket.startHandshake();
			//create IO Streams on the socket
			bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String stringCharset = "Content-Type: text/xml\r\n";

			String str1 = "GET " + url.getFile() + " HTTP/1.0\r\n"+
						  "Host: " + url.getHost() + "\r\n"+ stringCharset +
						  "User-Agent: java1.2.2\r\n"+
						  "Accept: text/html, image/gif, *; q=.2, */*; q=.2\r\n";
			
			String header = "";
			header = str1 + "Connection: Close\r\n";
			if(logger.isDebugEnabled())
			logger.debug(className  + "[postSocketRequest] header" + header);
			//send header first
			bWriter.write(header.toCharArray());
			bWriter.flush();
			long bCount = message.length();
			if (bCount > Integer.MAX_VALUE)
			{
				throw new Exception("The byte count in XML File is greater than Integer.MAX_VALUE");
			}
			
			//send message length and the message
			String str2 = "";
			str2 = "Content-length: " + 0 + "\r\n\r\n";
			bWriter.write( str2.toCharArray() );
			bWriter.write( "" );
			bWriter.flush();

			String str3 = "\r\n";   // HTTP 1.1 syntax - end of header fields
			bWriter.write( str3.toCharArray() );
			bWriter.flush();


			bReader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
			String getIn="";
			
			if(logger.isDebugEnabled())
			logger.debug(className + "[postSocketRequest] Response is ....");
			
			while ((getIn = bReader.readLine()) != null)
			{
				if(logger.isDebugEnabled())
				logger.debug(getIn);
				inputLine += getIn;
				if (getIn.trim().length() == 0 && !flag)
				{
					flag = true;
					if (inputLine.indexOf("Content-Type:") != -1)
					{
						String txt = inputLine.substring((inputLine.indexOf("Content-Type:") + 14));						
					}
					if (inputLine.indexOf("200 OK") == -1)
						throw new Exception(
							className + "[postSocketRequest] Received Error HTTP Response from the URL" + inputLine);
					inputLine = "";
				}
			}
			
			if(logger.isDebugEnabled())
			logger.debug(className + "[postSocketRequest] Response - " + inputLine);
			
			if (inputLine.trim().length() > 0)
				inputTxt = inputLine;
			/*else
			{
				throw new Exception("[postSocketRequest ()]  No Response from client");
			}*/
			getIn = null;
		}
		catch (Exception e)
		{
			logger.error(e);
			
			if (inputTxt == null || inputTxt.trim().length() == 0)
			{
				logger.error(className + "[postSocketRequest ()]  : No response from client" );
				throw new Exception(className + "[postSocketRequest()]  No Response from client");
			}
			else
			{
				if (!flag)
				{
					logger.error(className + "postSocketRequest()]  : Error connecting to URL "+e );
					throw new Exception(className + "[postSocketRequest()]  : Error connecting to URL " + e);
				}
			}
		}
		finally
		{
			try
			{
				if (bWriter != null)
					bWriter.close();
				if (bReader != null)
					bReader.close();
				if (socket != null)
					socket.close();
				bWriter = null;
				bReader = null;
				socket = null;
			}
			catch (Exception e)
			{
				logger.error(className + "[postSocketRequest()]  : Error "+e );
				
				throw new Exception(className + "[postSocketRequest()]  : Error " + e);
			}
		}
		return inputTxt;
	}
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @param int Time out
	 * @return String application response.
	 */
	public String connectGetMethod(String urlString , int timeOut)throws Exception{
		response	=	null;
		errorResponse	=	null;
		
		URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString);
		try{
			urlConnectorTherad.start();
			urlConnectorTherad.join(timeOut*1000);
			
			if(errorResponse!=null)
				throw new Exception(errorResponse);
			
			if(response ==null){
				urlConnectorTherad.done();				
			}
			
		}catch(Exception e){
			throw e;
		}
		return response;
	}
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @param String data
	 * @param int Time out
	 * @return String application response.
	 */
	public String connectPostMethod(String urlString, String data, int timeOut)throws Exception{
		response	=	null;
		errorResponse	=	null;
		
		URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, data);
		try{
			urlConnectorTherad.start();
			urlConnectorTherad.join(timeOut*1000);
			
			if(errorResponse!=null)
				throw new Exception(errorResponse);
			
			if(response ==null){
				urlConnectorTherad.done();				
			}
			
		}catch(Exception e){
			throw e;
		}
		return response;	
	}
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
	 * @param HashMap params
	 * @param int Time out
	 * @return String application response.
	 */
	public String connectPostMethod(String urlString, HashMap params, int timeOut)throws Exception{
		response	=	null;
		errorResponse	=	null;
		
		URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, params);
		try{
			urlConnectorTherad.start();
			urlConnectorTherad.join(timeOut*1000);
			
			if(errorResponse!=null)
				throw new Exception(errorResponse);
			
			if(response ==null){
				urlConnectorTherad.done();				
			}
			
		}catch(Exception e){
			throw e;
		}
		return response;
	}
	/* This method connects client URL using HTTP GET method (uses socket)
	 * @param String URL
	 * @param int Time out
	 * @return String application response.
	 */
	public String postSocketRequest(String urlString, int timeOut) throws Exception{
		response	=	null;
		errorResponse	=	null;
		
		URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, true);
		try{
			urlConnectorTherad.start();
			urlConnectorTherad.join(timeOut*1000);
			
			if(errorResponse!=null)
				throw new Exception(errorResponse);
			
			if(response ==null){
				urlConnectorTherad.done();				
			}
			
		}catch(Exception e){
			throw e;
		}
		return response;
	}
	/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
	 * @param String URL
		* @return HashMap application response and status.
		 */
		public HashMap connectGetMethodWithStatus(String urlString)throws Exception{
			HashMap responseMap	=	new HashMap();
			int port=0;		
			GetMethod httpget =	null;
			String response	=	"";
			
			if(logger.isDebugEnabled())
			logger.info(className + urlString);
			try {
				URL url =new URL(urlString);
				
				port=(url.getPort()<0)?443:url.getPort();
				
				ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
			
				Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

				Protocol.registerProtocol( "https", myHTTPS );

				HttpClient httpclient = new HttpClient();

				httpget = new GetMethod(urlString);
				
				int statusCode	=	httpclient.executeMethod(httpget);
				
				if(logger.isDebugEnabled())
				logger.info(className+ "[connectGetmethod] Status Line:  " +httpget.getStatusLine());
				
				response =	 httpget.getResponseBodyAsString();			
				responseMap.put("status", String.valueOf(statusCode));
				responseMap.put("response", response);
				}catch(Exception e){
					
					logger.error(className, e);
					
					throw e;
				} finally {
					httpget.releaseConnection();
				}
				return responseMap;		
		}
		/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
		 * @param String URL
		 * @param String data
		* @return HashMap application response and status.
		 */
		public HashMap connectPostMethodWithStatus(String urlString, String data)throws Exception{
			HashMap responseMap	=	new HashMap();
			int port=0;		
			PostMethod httpPost =	null;
			String response	=	"";
			if(logger.isDebugEnabled())
			logger.info(className + "connectPostMethod(String urlString, String data)" + urlString);
			try {
				URL url =new URL(urlString);
				
				port=(url.getPort()<0)?443:url.getPort();
				
				ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
			
				Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

				Protocol.registerProtocol( "https", myHTTPS );

				HttpClient httpclient = new HttpClient();

				httpPost = new PostMethod(urlString);
				
				httpPost.setRequestEntity(new StringRequestEntity(data, null, null));
				
				int statusCode	=	httpclient.executeMethod(httpPost);
				
				if(logger.isDebugEnabled())
				logger.info(className+ "[connectPostMethod(String urlString, String data)] Status Line:  " +httpPost.getStatusLine());
				
				response =	 httpPost.getResponseBodyAsString();			
				responseMap.put("status", String.valueOf(statusCode));
				responseMap.put("response", response);
				}catch(Exception e){
					
					logger.error(className + "[connectPostMethod(String urlString, String data)]", e);
					
					throw e;
				} finally {
					httpPost.releaseConnection();
				}
				return responseMap;		
		}
		/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
		 * @param String URL
		 * @param HashMap params
		* @return HashMap application response and status.
		 */
		public HashMap connectPostMethodWithStatus(String urlString, HashMap params)throws Exception{
			HashMap responseMap	=	new HashMap();
			int port=0;		
			PostMethod httpPost =	null;
			String response	=	"";
			
			if(logger.isDebugEnabled())
			logger.info(className + "connectPostMethod(String urlString, String data)" + urlString);
			try {
				URL url =new URL(urlString);
				
				port=(url.getPort()<0)?443:url.getPort();
				
				ProtocolSocketFactory	sslSocketFactory	=	new SSLProtocolSocketFactory(); 
			
				Protocol myHTTPS = new Protocol( "https", sslSocketFactory , port );

				Protocol.registerProtocol( "https", myHTTPS );

				HttpClient httpclient = new HttpClient();

				httpPost = new PostMethod(urlString);
				
				if(params!=null || !params.isEmpty()){
					
					for(Iterator keys=params.keySet().iterator();keys.hasNext();){
						String param	=	(String)keys.next();
						httpPost.addParameter(param, (String)params.get(param));
					}
				}
				
				int statusCode	=	httpclient.executeMethod(httpPost);
				
				if(logger.isDebugEnabled())
				logger.info(className+ "[connectPostMethod(String urlString, String data)] Status Line:  " +httpPost.getStatusLine());
				
				response =	 httpPost.getResponseBodyAsString();			
				responseMap.put("status", String.valueOf(statusCode));
				responseMap.put("response", response);
				}catch(Exception e){
					
					logger.error(className + "[connectPostMethod(String urlString, String data)]", e);
					
					throw e;
				} finally {
					httpPost.releaseConnection();
				}
				return responseMap;		
		}
		
		/* This method connects client URL using HTTP GET method (uses socket)
		 * @param String URL
		* @return HashMap application response and status.
		 */
		public HashMap postSocketRequestWithStatus(String urlString) throws Exception
		{	HashMap	responseMap	=	new HashMap();
			int port	=	0;
			SSLSocket socket = null;
			SSLSocketFactory factory = null;
			BufferedWriter bWriter = null;
			BufferedReader bReader = null;
			Provider a_provider = null;
			String inputLine = "";
			String message = "";
			String inputTxt = "";
			boolean flag = false;
			URL url =new URL(urlString);
			
			port=(url.getPort()<0)?443:url.getPort();
			//load SSL Provider
			try
			{
				Class provider = Class.forName("com.sun.net.ssl.internal.ssl.Provider");
				a_provider = (Provider) provider.newInstance();
				Security.addProvider(a_provider);
			}
			catch (Exception ex)
			{
				throw ex;
			}
			try
			{
				KeyManagerFactory kmf;
				KeyStore ks;
				KeyManager[] km = null;
				TrustManager[] myTM = new TrustManager[] { new WinTrustManager()};
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, myTM, null);
				factory = ctx.getSocketFactory();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				//create SSL socket
				socket = (SSLSocket) factory.createSocket(url.getHost(), port);
				socket.startHandshake();
				
				//create IO Streams on the socket
				bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String stringCharset = "Content-Type: text/xml\r\n";

				String str1 = "GET " + url.getFile() + " HTTP/1.0\r\n"+
							  "Host: " + url.getHost() + "\r\n"+ stringCharset +
							  "User-Agent: java1.2.2\r\n"+
							  "Accept: text/html, image/gif, *; q=.2, */*; q=.2\r\n";
				
				String header = "";
				header = str1 + "Connection: Close\r\n";
				if(logger.isDebugEnabled())
				logger.debug(className  + "[postSocketRequest] header" + header);
				//send header first
				bWriter.write(header.toCharArray());
				bWriter.flush();
				long bCount = message.length();
				if (bCount > Integer.MAX_VALUE)
				{
					throw new Exception("The byte count in XML File is greater than Integer.MAX_VALUE");
				}
				
				//send message length and the message
				String str2 = "";
				str2 = "Content-length: " + 0 + "\r\n\r\n";
				bWriter.write( str2.toCharArray() );
				bWriter.write( "" );
				bWriter.flush();

				String str3 = "\r\n";   // HTTP 1.1 syntax - end of header fields
				bWriter.write( str3.toCharArray() );
				bWriter.flush();


				bReader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
				String getIn="";
				
				if(logger.isDebugEnabled())
				logger.debug(className + "[postSocketRequest] Response is ....");
				
				while ((getIn = bReader.readLine()) != null)
				{
					if(logger.isDebugEnabled())
					logger.debug(getIn);
					inputLine += getIn;
					if (getIn.trim().length() == 0 && !flag)
					{
						flag = true;
						if (inputLine.indexOf("Content-Type:") != -1)
						{
							String txt = inputLine.substring((inputLine.indexOf("Content-Type:") + 14));						
						}
						if (inputLine.indexOf("200 OK") == -1)
							responseMap.put("status", String.valueOf(200));
						inputLine = "";
					}
				}
				
				if(logger.isDebugEnabled())
				logger.debug(className + "[postSocketRequest] Response - " + inputLine);
				
				if (inputLine.trim().length() > 0)
					inputTxt = inputLine;
				responseMap.put("response", inputTxt);
				getIn = null;
			}
			catch (Exception e)
			{
				logger.error(e);
				
				if (inputTxt == null || inputTxt.trim().length() == 0)
				{
					logger.error(className + "[postSocketRequest ()]  : No response from client" );
					throw new Exception(className + "[postSocketRequest()]  No Response from client");
				}
				else
				{
					if (!flag)
					{
						logger.error(className + "postSocketRequest()]  : Error connecting to URL "+e );
						throw new Exception(className + "[postSocketRequest()]  : Error connecting to URL " + e);
					}
				}
			}
			finally
			{
				try
				{
					if (bWriter != null)
						bWriter.close();
					if (bReader != null)
						bReader.close();
					if (socket != null)
						socket.close();
					bWriter = null;
					bReader = null;
					socket = null;
				}
				catch (Exception e)
				{
					logger.error(className + "[postSocketRequest()]  : Error "+e );
					
					throw new Exception(className + "[postSocketRequest()]  : Error " + e);
				}
			}
			return responseMap;
		}
		/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
		 * @param String URL
		 * @param int Time out
		* @return HashMap application response and status.
		 */
		public HashMap connectGetMethodWithStatus(String urlString , int timeOut)throws Exception{
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, 0);
			try{
				urlConnectorTherad.start();
				urlConnectorTherad.join(timeOut*1000);
								
				if(response ==null){
					urlConnectorTherad.done();				
				}
				
			}catch(Exception e){
				throw e;
			}
			return responseMapWithStatus;
		}
		/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
		 * @param String URL
		 * @param String data
		 * @param int Time out
		* @return HashMap application response and status.
		 */
		public HashMap connectPostMethodWithStatus(String urlString, String data, int timeOut)throws Exception{
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, data, true);
			try{
				urlConnectorTherad.start();
				urlConnectorTherad.join(timeOut*1000);
				
				
				if(response ==null){
					urlConnectorTherad.done();				
				}
				
			}catch(Exception e){
				throw e;
			}
			return responseMapWithStatus;	
		}
		/* This method connects client URL using HTTP GET method (Uses Apache httpClient)
		 * @param String URL
		 * @param HashMap params
		 * @param int Time out
		* @return HashMap application response and status.
		 */
		public HashMap connectPostMethodWithStatus(String urlString, HashMap params, int timeOut)throws Exception{
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, params, true);
			try{
				urlConnectorTherad.start();
				urlConnectorTherad.join(timeOut*1000);
				
				
				if(response ==null){
					urlConnectorTherad.done();				
				}
				
			}catch(Exception e){
				throw e;
			}
			return responseMapWithStatus;
		}
		/* This method connects client URL using HTTP GET method (uses socket)
		 * @param String URL
		 * @param int Time out
		* @return HashMap application response and status.
		 */
		public HashMap postSocketRequestWithStatus(String urlString, int timeOut) throws Exception{
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(urlString, true, true);
			try{
				urlConnectorTherad.start();
				urlConnectorTherad.join(timeOut*1000);
				
								
				if(response ==null){
					urlConnectorTherad.done();				
				}
				
			}catch(Exception e){
				throw e;
			}
			return responseMapWithStatus;
		}
	class WinTrustManager implements X509TrustManager{
		
		X509TrustManager sunX509TrustManager;
		WinTrustManager() throws Exception
		{
		}
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
		{
		}
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}
		public void checkClientTrusted(java.security.cert.X509Certificate a[], String str)
		{
		}
	} 
	
	class URLConnectorTherad extends Thread
	{
		boolean postXml	=	false;
		boolean postMap	=	false;
		boolean socketReq	=	false;
		boolean postXmlwithStatus	=	false;
		boolean postMapwithStatus	=	false;
		boolean socketReqwithStatus	=	false;
		boolean qswithStatus	=	false;
		
		String url	=	null;
		String data	=	null;
		HashMap	dataMap	=	null;			
		private boolean done = false;

		private URLConnectorTherad(String url, String data){
			this.url	=	url;
			this.data	=	data;
			postXml	=	true;
			
		}
		
		private URLConnectorTherad(String url){
			this.url	=	url;	
			
		}
		private URLConnectorTherad(String url, boolean socketReq){
			this.url	=	url;
			socketReq	=	true;
			
		}
		
		private URLConnectorTherad(String url, HashMap dataMap){
			this.url	=	url;
			this.dataMap	=	dataMap;
			postMap	=	true;
		}
		//
		private URLConnectorTherad(String url, String data, boolean status){
			this.url	=	url;
			this.data	=	data;
			postXmlwithStatus	=	status;
			
		}
		
		private URLConnectorTherad(String url, int status){
			this.url	=	url;
			qswithStatus	=	true;
			
		}
		private URLConnectorTherad(String url, boolean socketReq, boolean status){
			this.url	=	url;
			
			socketReqwithStatus	=	status;
			
		}
		
		private URLConnectorTherad(String url, HashMap dataMap, boolean status){
			this.url	=	url;
			this.dataMap	=	dataMap;
			postMapwithStatus	=	status;
		}
		public void done(){
			done	=	true;				
		}

		public void run()
		{
			while(!done){
				try	{
					if(postXml)
						response	=	connectPostMethod(url, data);
											
					else if(postMap)
						response	=	connectPostMethod(url, dataMap);
					
					else if(socketReq)
						response 	=	postSocketRequest(url);
					else if(postXmlwithStatus)
						responseMapWithStatus	=	connectPostMethodWithStatus(url, data);	
					else if(postMapwithStatus)
						responseMapWithStatus	=	connectPostMethodWithStatus(url, dataMap);
					else if(socketReqwithStatus)
						responseMapWithStatus	=	postSocketRequestWithStatus(url);
					else if(qswithStatus)
						responseMapWithStatus	=	connectGetMethodWithStatus(url);
					else
						response	=	connectGetMethod(url);
					
					done=true;
					}catch (Exception e){
						errorResponse	=	"Error while accessing the url";
						logger.error(className + "URLTieoutTherad", e);
						done=true;
					}				
			}
		}	
	}
}