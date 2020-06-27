package com.winnovature.unitia.httpinterface.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.httpinterface.processor.DNGenProcess;
import com.winnovature.unitia.util.redis.RedisQueuePool;
import com.winnovature.unitia.util.routing.Refresh;
import com.winnovature.unitia.util.routing.Route;
import com.winnovature.unitia.util.test.Account;

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
		
		RedisQueuePool.getInstance().reload();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		String URIString = request.getRequestURI().trim().toLowerCase();
		
		if(URIString.startsWith("/api/dngen")){
			
			new DNGenProcess().doProcess(request, response);
			
		}else{
			
			new ServletException("Unknown Context Path");
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}

}
