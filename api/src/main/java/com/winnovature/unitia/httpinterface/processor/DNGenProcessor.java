package com.winnovature.unitia.httpinterface.processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.dngen.ErrorCodeType;


public class DNGenProcessor
{
    
    
    
    public DNGenProcessor()
    {
    }
    
    public void handoverToDN(String DLRURL)
    {
        URL url=null;
		try {
			url = new URL(DLRURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        final Map<String,String> reqmap = getRequestParam(url.getQuery());
        final Map drmap = getDR( reqmap.get("username"),  reqmap.get("dnmsg"));
        final String a = URLEncoder.encode((String) drmap.get("dlr"));
        final String err = (String) drmap.get("err");
        DLRURL = DLRURL.replace("%a", a);
        if (err.equalsIgnoreCase("000"))
        {
            DLRURL = DLRURL.replace("%d", "1");
        }
        else
        {
            DLRURL = DLRURL.replace("%d", "2");
        }
        DLRURL = DLRURL.replace("%o", "appssystemid");
        DLRURL = DLRURL.replace("%i", "apps");
        try {
			url = new URL(DLRURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        deliverThroughURL(url);
    }
    
    private void deliverThroughURL(final URL url)
    {
        HttpURLConnection httpConnection = null;
        final int responseCode = 0;
        String urlResponse = "";
        BufferedReader reader = null;
        final OutputStream outStream = null;
        try
        {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setRequestMethod("GET");
            reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            int val = 0;
            while ((val = reader.read()) > -1)
            {
                urlResponse = String.valueOf(urlResponse) + (char) val;
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (Exception ex)
            {}
        }
        try
        {
            reader.close();
        }
        catch (Exception ex2)
        {}
    }
    
    private Map getDR(final String username, final String msg)
    {
        final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmm");
        final HashMap<String,String> drmap = new HashMap<String,String>();
        final String date = df.format(new Date());
        String dnMsg = "id:1";
        dnMsg = String.valueOf(dnMsg) + " sub:001";
        dnMsg = String.valueOf(dnMsg) + " dlvrd:001";
        dnMsg = String.valueOf(dnMsg) + " submit date:" + date;
        dnMsg = String.valueOf(dnMsg) + " done date:" + date;
        if ( ErrorCodeType.getInstance().isDnRetry(username))
        {
            dnMsg = String.valueOf(dnMsg) + " stat:UNDELIV";

            drmap.put("err", "003");

        }
        else
        {
        	 dnMsg = String.valueOf(dnMsg) + " stat:DELIVRD";
             drmap.put("err", "000");

        }
        dnMsg = String.valueOf(dnMsg) + " err:" + drmap.get("err") + " ";
        dnMsg = String.valueOf(dnMsg) + " Text:" + msg;
        drmap.put("dlr", dnMsg);
        return drmap;
    }
    
    public Map getRequestParam(final String queryString)
    {
        final StringTokenizer st = new StringTokenizer(queryString, "&");
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
