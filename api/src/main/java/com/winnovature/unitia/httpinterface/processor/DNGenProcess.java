package com.winnovature.unitia.httpinterface.processor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DNGenProcess {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        PrintWriter out = response.getWriter();
        String dlrurl = request.getParameter("dlr-url");
     /*   if (Queue.getInstance().getQ().size() < 50000)
        {
            Queue.getInstance().add(dlrurl);
        }
        else
        {
            try
            {
                new Utility().handoverToDN(dlrurl);
            }
            catch (Exception e)
            {
                throw new ServletException();
            }
        }
       */ out.print("Sent.");
        out.flush();
        out.close();

	
	}
}
