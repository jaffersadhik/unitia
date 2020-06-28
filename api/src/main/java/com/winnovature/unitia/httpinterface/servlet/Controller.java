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
import com.winnovature.unitia.httpinterface.processor.DNReceiverProcessor;
import com.winnovature.unitia.util.Refresh;
import com.winnovature.unitia.util.Route;
import com.winnovature.unitia.util.redis.RedisQueuePool;
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
		Account.getInstance();
		
		RedisQueuePool.getInstance().reload();
		
		Refresh.getInsatnce().reload();
		
		com.winnovature.unitia.util.account.Refresh.getInsatnce().reload();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		String URIString = request.getRequestURI().trim().toLowerCase();
		
		if(URIString.startsWith("/api/dngen")){
			
			new DNGenProcess().doProcess(request, response);
			
		}else if(URIString.startsWith("/api/dnreceiver")){
			
			new DNReceiverProcessor().doProcess(request, response);
			
		}else if(URIString.startsWith("/api/route")){

			String key=request.getParameter("key");
			String routeclass=request.getParameter("routeclass");

	        PrintWriter out = response.getWriter();
	        
	        if(routeclass==null){
	        	
		        out.print(Route.getInstance().getRouteGroup(key));

	        }else{
	        out.print(Route.getInstance().getRouteGroup(key, routeclass));
	        }
	        out.flush();
	        out.close();

		}else if(URIString.startsWith("/api/allroute")){

		
	        PrintWriter out = response.getWriter();
	        out.print(Route.getInstance().getRoute());
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

}
