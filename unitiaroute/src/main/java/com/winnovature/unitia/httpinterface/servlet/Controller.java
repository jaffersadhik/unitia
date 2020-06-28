package com.winnovature.unitia.httpinterface.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
     
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException 
	{
		
		new T().start();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		String URIString = request.getRequestURI().trim().toLowerCase();
		
		if(URIString.startsWith("/api/route")){
			
	        PrintWriter out = response.getWriter();

	        Map<String,String> msgmap=getMap(request);
	        
	        SMSProcessor processor=new SMSProcessor(msgmap);
	        
	        processor.doCountryCodeCheck();
	        processor.doNumberingPlan();
	        processor.doBlackListMobileNumber();
	        processor.doBlackListSMSPattern();
	        processor.doBlackListSenderid();
	        processor.doFilteringSMSPatternCheck();
	        processor.doAllowedSMSPatternCheck();
	        processor.doSenderCheck();
	        processor.doRouteGroupAvailable();
	        processor.doSMSCIDAvailable();
	        processor.doKannelAvailable();
	        
	        out.print(processor.toString());
	        out.flush();
	        out.close();
			
		}else{
			
			new ServletException("Unknown Context Path");
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}


	private Map<String, String> getMap(HttpServletRequest request) {
		
        final StringTokenizer st = new StringTokenizer(request.getQueryString(), "&");
        final HashMap reqParam = new HashMap();
        while (st.hasMoreTokens())
        {
            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
            String key = "";
            String value = "";
            if (st2.hasMoreTokens())
            {
                key = st2.nextToken();
                if (st2.hasMoreTokens())
                {
                    value = st2.nextToken();
                }
            }
            reqParam.put(key, value);
        }
        return reqParam;
    }
}
