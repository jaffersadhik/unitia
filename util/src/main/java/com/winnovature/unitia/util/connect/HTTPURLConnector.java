package com.winnovature.unitia.util.connect;
/**
 * 	@(#)HTTPURLConnector.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.constants.ComponentType;
import com.winnovature.unitia.util.constants.ConfigParamKeys;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;

public class HTTPURLConnector {
	/*
	 * NGLogger instance.
	 */
	Log logger = LogFactory.getLog(this.getClass());
	/*
	 * class name for logging
	 */
	private static final String	className	=	"[HTTPURLConnector] ";
	
	String response	=	null;
	String errorResponse	=	null;
	HashMap	responseMapWithStatus	=	null;
	//URLTieoutTherad urlTimeoutThread	=null;
	
	/* This method connects client URL using HTTP GET method
	 * @param String URL
	 * @return String application response.
	 */
	public String connectGetMethod(String url)throws Exception{
		
		if(logger.isDebugEnabled())
		logger.debug(className + "[connectGetmethod()]");
		String response	=	"";
		HttpClient client = new HttpClient();		
		GetMethod get = new GetMethod(url);	
		 
		
		get.setFollowRedirects(true);
		try{
			
			
			int httpConnectionTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_CONNECTION_TIMEOUT));
			int httpResponseTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_RESPONSE_TIMEOUT));
			client.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			client.setTimeout(httpResponseTimeout);
			client.setConnectionTimeout(httpConnectionTimeout);
			//client.getHttpConnectionManager().getParams().setConnectionTimeout(httpConnectionTimeout);

			int iGetResultCode = client.executeMethod(get);
			//client.setTimeout(httpConnectionTimeout);			
			logger.info(className + "[connectGetmethod()] Status = " + iGetResultCode);

			if(iGetResultCode == 200){

				response = get.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectGetmethod()] Response from EC - " + response);
			}
			else{
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectGetmethod()] Response from EC - " + get.getResponseBodyAsString());
				//throw new Exception(className + "[connectGetmethod() ()] error: Status = " +iGetResultCode);
			}
			
		}
		catch(Exception e){
			//SNMP TRAP Implementation
			throw e;
		}finally
		{
			get.releaseConnection();
		}
		return response;
	}	
	
	/* This method connects client URL using HTTP GET method
	 * @param String URL
	 * @return String application response.
	 */
	public String connectKannel(String url)throws Exception{
		
		if(logger.isDebugEnabled())
		logger.debug(className + "[connectKannel()]");
		String response	=	"";
		HttpClient client = new HttpClient();		
		GetMethod get = new GetMethod(url);
		//get.setFollowRedirects(true);
		try{
			
			
			int httpConnectionTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_CONNECTION_TIMEOUT));
			int httpResponseTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_RESPONSE_TIMEOUT));
			client.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
						
			client.setTimeout(httpResponseTimeout);
			client.setConnectionTimeout(httpConnectionTimeout);
			
			int iGetResultCode = client.executeMethod(get);

			logger.info(className + "[connectKannel()] Status = " + iGetResultCode);

			if(iGetResultCode == 200 || iGetResultCode == 202){

				response = get.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectKannel()] Response from EC - " + response);
			}
			else{				
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectKannel()] Response from EC - " + get.getResponseBodyAsString());
				//throw new Exception(className + "[connectGetmethod() ()] error: Status = " +iGetResultCode);
			}
			
		}
		catch(Exception e){

			throw e;
		}finally
		{
			get.releaseConnection();
		}
		return response;
	}		
	/* This method connects client URL using HTTP POST method
	 * @param String URL
	 * @param HashMap parameters key , value
	 * @return String application response.
	 */
	public String connectPostMethod(String url, HashMap params)throws Exception{
		String response	=	"";
		if(logger.isDebugEnabled())
		logger.debug(className + "[connectPostmethod()]");
		HttpClient client = new HttpClient();		
		PostMethod post = new PostMethod(url);
		
		if(params!=null || !params.isEmpty()){
			
			for(Iterator keys=params.keySet().iterator();keys.hasNext();){
				String param	=	(String)keys.next();
				post.addParameter(param, (String)params.get(param));
			}
		}
		//post.setFollowRedirects(true);
		try{
			int iPostResultCode = client.executeMethod(post);

			logger.info(className + "[connectPostmethod()] Status = " + iPostResultCode);

			if(iPostResultCode == 200){

				response = post.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod()] Response from EC - " + response);
			}
			else{				
				
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod()] Response from EC - " + post.getResponseBodyAsString());
				throw new Exception(className + "[connectPostmethod ()] error: Status = " +iPostResultCode);
			}
			
		}
		catch(Exception e){
			throw e;
		}finally
		{
			post.releaseConnection();
		}
		return response;
	}
	/* This method connects client URL using HTTP POST method
	 * @param String URL
	 * @param String data
	 * @return String application response.
	 */
	public String connectPostMethod(String url, String data)throws Exception{
		String response	=	"";
		if(logger.isDebugEnabled())
		logger.debug(className + "[connectPostmethod(String url, String data)]");
		HttpClient client = new HttpClient();
		
		PostMethod post = new PostMethod(url);
	
		try{
			
			
			
			int httpConnectionTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_RESPONSE_TIMEOUT));
			int httpResponseTimeout=Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.HTTP_CONNECTION_TIMEOUT));
			client.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			//client.getHttpConnectionManager().getParams().setConnectionTimeout(httpConnectionTimeout);
			
			client.setTimeout(httpResponseTimeout);
			client.setConnectionTimeout(httpConnectionTimeout);
			
			post.setRequestEntity(new StringRequestEntity(data, null, null));
			
			int iPostResultCode = client.executeMethod(post);
			
			logger.info(className + "[connectPostmethod(String url, String data)] Status = " + iPostResultCode);

			if(iPostResultCode == 200){

				response = post.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod(String url, String data)] Response from EC - " + response);
			}
			else{				
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod(String url, String data)] Response from EC - " + post.getResponseBodyAsString());
				throw new Exception(className + "[connectPostmethod (String url, String data)] error: Status = " +iPostResultCode);
			}
			
		}
		catch(Exception e){
			logger.error(className , e);
			throw e;
		}finally
		{
			post.releaseConnection();
		}
		return response;
	}
	/* This method connects client URL using HTTP GET method
	 * @param String URL
	 * @param int timeOut in seconds
	 * @return String application response.
	 */
	public String connectGetMethod(String url, int timeOut)throws Exception{
		response	=	null;
		errorResponse	=	null;
		
		URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url);
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
	
	/* This method connects client URL using HTTP POST method
	 * @param String URL
	 * @param String stats json output
	 * @return int PostResultCode http status code.
	 */
	/* This method connects client URL using HTTP POST method
	 * @param String URL
	 * @param HashMap parameters key , value
	 * @param int timeOut in seconds
	 * @return String application response.
	 */
		public String connectPostMethod(String url, HashMap params, int timeOut)throws Exception{
			
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url, params);
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
		/* This method connects client URL using HTTP POST method
		 * @param String URL
		 * @param String data
		 * @param int timeOut in seconds
		 * @return String application response.
		 */
		public String connectPostMethod(String url, String data, int timeOut)throws Exception{
			
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url, data);
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
		/* This method connects client URL using HTTP GET method
		 * @param String URL
		 * @return HashMap application response and status code.
		 */
		public HashMap connectGetMethodWithStatus(String url)throws Exception{
			HashMap responseMap	=	new HashMap();
			
			if(logger.isDebugEnabled())
			logger.debug(className + "[connectGetmethod()]");
			String response	=	"";
			HttpClient client = new HttpClient();		
			GetMethod get = new GetMethod(url);
			//get.setFollowRedirects(true);
			try{
				int iGetResultCode = client.executeMethod(get);

				logger.info(className + "[connectGetmethod()] Status = " + iGetResultCode);
				
				response = get.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectGetmethod()] Response from EC - " + response);			
				responseMap.put("status", String.valueOf(iGetResultCode));
				responseMap.put("response", response);
				
			}
			catch(Exception e){
				throw e;
			}finally
			{
				get.releaseConnection();
			}
			return responseMap;
		}	
		
		/* This method connects client URL using HTTP POST method
		 * @param String URL
		 * @param HashMap parameters key , value
		 * @return HashMap application response and status code.
		 */
		public HashMap connectPostMethodWithStatus(String url, HashMap params)throws Exception{
			HashMap responseMap	=	new HashMap();
			String response	=	"";
			if(logger.isDebugEnabled())
			logger.debug(className + "[connectPostmethod()]");
			HttpClient client = new HttpClient();		
			PostMethod post = new PostMethod(url);
			
			if(params!=null || !params.isEmpty()){
				
				for(Iterator keys=params.keySet().iterator();keys.hasNext();){
					String param	=	(String)keys.next();
					post.addParameter(param, (String)params.get(param));
				}
			}
			
			try{
				int iPostResultCode = client.executeMethod(post);

				logger.info(className + "[connectPostmethod()] Status = " + iPostResultCode);

				response = post.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod()] Response from EC - " + response);
				responseMap.put("status", String.valueOf(iPostResultCode));
				responseMap.put("response", response);
				
			}
			catch(Exception e){
				throw e;
			}finally
			{
				post.releaseConnection();
			}
			return responseMap;
		}
		/* This method connects client URL using HTTP POST method
		 * @param String URL
		 * @param String data
		 * @return HashMap application response and status code.
		 */
		public HashMap connectPostMethodWithStatus(String url, String data)throws Exception{
			HashMap responseMap	=	new HashMap();
			String response	=	"";
			if(logger.isDebugEnabled())
			logger.debug(className + "[connectPostmethod(String url, String data)]");
			HttpClient client = new HttpClient();
			
			PostMethod post = new PostMethod(url);
			
			post.setRequestEntity(new StringRequestEntity(data, null, null));		
			
			
			try{
				int iPostResultCode = client.executeMethod(post);
				
				logger.info(className + "[connectPostmethod(String url, String data)] Status = " + iPostResultCode);
				response = post.getResponseBodyAsString();
				if(response!=null)
					response	=	response.trim();
				if(logger.isDebugEnabled())
				logger.debug(className + "[connectPostmethod(String url, String data)] Response from EC - " + response);
				responseMap.put("status", String.valueOf(iPostResultCode));
				responseMap.put("response", response);
				
			}
			catch(Exception e){
				logger.error(className , e);
				throw e;
			}finally
			{
				post.releaseConnection();
			}
			return responseMap;
		}
		/* This method connects client URL using HTTP GET method
		 * @param String URL
		 * @param int timeOut in seconds
		 * @return HashMap application response and status code.
		 */
		public HashMap connectGetMethodWithStatus(String url, int timeOut)throws Exception{
			
			response	=	null;
			errorResponse	=	null;
			
			URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url, true);
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
			return responseMapWithStatus;
		}
		/* This method connects client URL using HTTP POST method
		 * @param String URL
		 * @param HashMap parameters key , value
		 * @param int timeOut in seconds
		 * @return HashMap application response and status code.
		 */
		public HashMap connectPostMethodWithStatus(String url, HashMap params, int timeOut)throws Exception{

			
				
				response	=	null;
				errorResponse	=	null;
				
				URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url, params, true);
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
				return responseMapWithStatus;
			}
			/* This method connects client URL using HTTP POST method
			 * @param String URL
			 * @param String data
			 * @param int timeOut in seconds
			 * @return HashMap application response and status code.
			 */
			public HashMap connectPostMethodWithStatus(String url, String data, int timeOut)throws Exception{
				
				response	=	null;
				errorResponse	=	null;
				
				URLConnectorTherad urlConnectorTherad	=	new URLConnectorTherad(url, data, true);
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
				return responseMapWithStatus;	
			}
			
				
		class URLConnectorTherad extends Thread
		{
			boolean postXml	=	false;
			boolean postMap	=	false;
			boolean postXmlwithStatus	=	false;
			boolean postMapwithStatus	=	false;
			boolean qswithstatus	=	false;
			
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
			
			private URLConnectorTherad(String url, HashMap dataMap, boolean status){
				this.url	=	url;
				this.dataMap	=	dataMap;
				postMapwithStatus	=	status;
			}
			
			private URLConnectorTherad(String url, String data, boolean status){
				this.url	=	url;
				this.data	=	data;
				postXmlwithStatus	=	status;
				
			}
			
			private URLConnectorTherad(String url, boolean status){
				this.url	=	url;	
				qswithstatus	=	status;
			}
			
			private URLConnectorTherad(String url, HashMap dataMap){
				this.url	=	url;
				this.dataMap	=	dataMap;
				postMap	=	true;
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
						
						else if(postXmlwithStatus)
							responseMapWithStatus	=	connectPostMethodWithStatus(url, data);
						
						else if(postMapwithStatus)
							responseMapWithStatus	=	connectPostMethodWithStatus(url, dataMap);
						
						else if(qswithstatus)
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