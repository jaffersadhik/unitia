package com.winnovature.unitia.httpinterface.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.http.HttpRequestProcessor;
import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;

public class SMSReceiverProcessor {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		Map<String,String> msgmap=new HashMap<String,String>();
		Map<String,String> logmap=new HashMap<String,String>();
		logmap.put(MapKeys.LOGTIME,new WinDate().getLogDate());
		Bean.setDefaultValues(msgmap);
        PrintWriter out = response.getWriter();
		String respForClient	=	"";
		HttpRequestProcessor processor = new HttpRequestProcessor();
		try
		{
			respForClient = processor.processRequest(request,msgmap,logmap);
			out.print(respForClient);
			logmap.put("respForClient", respForClient);
		}
		catch (Exception e)
		{
			logmap.put("servlet error",ErrorMessage.getMessage(e));
			respForClient=ToJsonString.toString(logmap);
			out.print(respForClient);

		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.flush();
					out.close();
				}
			}
			catch (Exception ie)
			{
			}
		}

		logmap.putAll(msgmap);
		new Log().log(logmap);

	}
}
