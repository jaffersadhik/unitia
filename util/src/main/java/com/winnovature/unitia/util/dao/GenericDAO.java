package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class GenericDAO
{
    
    
    Log                     log  = LogFactory.getLog(this.getClass());
    
    public Map getTemplateMasterMapping() throws Exception
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map records = new HashMap();
        
        try
        {
            
            // String sql = "select * from template_master where approved_yn=1";
            String sql = "select * from template_master where approved_yn=1 order by priority_order ASC";
            
            con = CoreDBConnection.getInstance().getConnection();
            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next())
            {
                String aid = rs.getString("aid");
                String id = rs.getString("id");
                String template = rs.getString("template");
                
                List list = (ArrayList) records.get(aid);
                if (list == null)
                    list = new ArrayList();
                
                Map templatemap = new HashMap();
                templatemap.put("TEMPLATEID", id);
                templatemap.put("TEMPLATE", template);
                list.add(templatemap);
                records.put(aid, list);
            }
            
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
        	Close.close(rs);
        	Close.close(ps);
        	Close.close(con);

        }
        return records;
        
    }
    
    public Map getRoutingTemplate() throws Exception
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map records = new HashMap();
        
        try
        {
            
            // String sql = "select * from template_master where approved_yn=1";
            String sql = "select * from routing_template order by priority_order ASC";
            
            con =CoreDBConnection.getInstance().getConnection();
            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next())
            {
                String aid = rs.getString("aid");
                String id = rs.getString("id");
                String template = rs.getString("template");
                
                List list = (ArrayList) records.get(aid);
                if (list == null)
                    list = new ArrayList();
                
                Map templatemap = new HashMap();
                templatemap.put("TEMPLATEID", id);
                templatemap.put("TEMPLATE", template);
                list.add(templatemap);
                records.put(aid, list);
            }
            
        }
        catch (Exception e)
        {
            records = null;
            throw e;
        }
        finally
        {
            Close.close(rs);
            Close.close(ps);
            Close.close(con);
   
        }
        return records;
        
    }
    
    public Map<String, Object> getEncryptEnabledTemplates() throws Exception
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Map<String, String>> templateIdMap = new HashMap<String, Map<String, String>>();
        Map<String, List<Map<String, String>>> aidMap = new HashMap<String, List<Map<String, String>>>();
        
        try
        {
            
            // String sql = "select * from template_master where approved_yn=1
            // and encrypt_yn='1'";
            String sql = "select * from template_master where approved_yn=1 and encrypt_yn='1' order by priority_order ASC";
            
            con = CoreDBConnection.getInstance().getConnection();
            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next())
            {
                String aid = rs.getString("aid");
                String id = rs.getString("id");
                
                String template = rs.getString("template");
                
                List<Map<String, String>> list = (ArrayList<Map<String, String>>) aidMap.get(aid);
                if (list == null)
                    list = new ArrayList<Map<String, String>>();
                
                Map<String, String> templatemap = new HashMap<String, String>();
                templatemap.put("AID", aid);
                templatemap.put("TEMPLATE", template);
                templateIdMap.put(id, templatemap);
                
                Map<String, String> templatedetails = new HashMap<String, String>();
                templatemap.put("ID", id);
                templatemap.put("TEMPLATE", template);
                list.add(templatemap);
                aidMap.put(aid, list);
            }
            
            result.put("aidwisemap", aidMap);
            result.put("idwisemap", templateIdMap);
            
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
           Close.close(rs);
           Close.close(ps);
           Close.close(con);

        }
        return result;
        
    }
     
    
    
	
}
