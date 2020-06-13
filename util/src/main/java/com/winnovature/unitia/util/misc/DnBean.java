package com.winnovature.unitia.util.misc;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class DnBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(DnBean.class);
	private static Log logerror = LogFactory.getLog("NGDNERRORLOGGER");
	
	private transient PropertiesConfiguration prop = null;

	private String id="";//refered by HttpPoller
	private String dr = "";
	private String smscid = "";
	private String statuscd = "";
	private String msgid = "";
	private String ackid = "";
	private String pid = "";
	private String aid = "";
	private String custref = "";
	private String rts = "";
	private String dlrtype = "";
	private String mobile = "";
	private String lastutime = "";
	private String expiry = "";
	
	// New Parameters
	private String msg = "";
	private String senderid = "";
	private String carrierid = "0";
	private String circleid = "0";
	private String interr = "";
	private String intstat = "";
	
	private String drsubmitdate = "";
	private String drdonedate = "";
	private String drerr = "";
	private String drstat = "";
	private String drid = "";
	private String statusflag = "";
	
	private String errcode = "";
	private String errdes = "";
	
	private String dr16 = "";
	private String dr32 = "";
	
	private String routeid = "";
	
	private boolean isDNRetry=false;
	
	private String protocol="";
	
	private String cimdtime="";
	
	private String row="";
	
	private long customerdonedt=0L;
	
	private String msgType="PM";
	
	private String esm="4";
	
	private int dnpostretryattempt=1;
	
	private String systemid="";
	
	private String splitseq="";
	
	private Timestamp dnsts=null;
	
	private Timestamp dncrts=null;

	private String dnretrymode="";
	
	private String err="";
	
	private boolean issuccessMasked=false;

	private String mismask=null;
	
	private String param1=null;
	
	private String param2=null;
	
	private String param3=null;
	
	private String param4=null;
	
	private String param5=null;
	
	private String premsgid=null;
	
	private String isFromSingleDn=null;
	
	private String qs=null;
	
	public boolean isIssuccessMasked() {
		return issuccessMasked;
	}



	public void setIssuccessMasked(boolean issuccessMasked) {
		this.issuccessMasked = issuccessMasked;
	}



	public DnBean()
	{		
	}
	
	

	public String getDr16()
	{
		return dr16;
	}



	public void setDr16(DnBean bean,String dr16) throws Exception
	{}



	public void setDr32(String dr32) throws ParseException
	{}



	public String getDr32()
	{
		return dr32;
	}


	public String getDrid()
	{
		return drid;
	}


	public void setDrid(String drid)
	{
		this.drid = drid;
	}



	public String getErrcode()
	{
		return errcode;
	}



	public void setErrcode(String errcode)
	{
		this.errcode = errcode;
	}



	public String getErrdes()
	{
		return errdes;
	}



	public void setErrdes(String errdes)
	{
		this.errdes = errdes;
	}



	public String getStatusflag()
	{
		return statusflag;
	}

	public void setStatusflag(String statusflag)
	{
		if(statusflag == null)	
			this.statusflag = "FAILURE";
		else
			this.statusflag  = statusflag;
	}

	public String getDrsubmitdate()
	{
		return drsubmitdate;
	}
	public void setDrsubmitdate(String drsubmitdate, String dtformat)
	{
		this.drsubmitdate = drsubmitdate;
	}
	public String getDrdonedate()
	{
		return drdonedate;
	}
	public void setDrdonedate(String drdonedate,String dtformat) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(dtformat);
		this.drdonedate = drdonedate;		
		this.setCustomerdonedt(sdf.parse(drdonedate).getTime());
	}
	public String getDrerr()
	{
		return drerr;
	}
	public void setDrerr(String drerr)
	{
		this.drerr = drerr;
	}
	public String getDrstat()
	{
		return drstat;
	}
	public void setDrstat(String drstat)
	{
		this.drstat = drstat;
	}
	
	public String getDr()
	{
		return dr;
	}
	public void setDr(String dr,boolean needToSetDR) throws Exception
	{
		
	}
	
	
	
	
	
	
	
	public String getSmscid()
	{
		return smscid;
	}
	public void setSmscid(String smscid)
	{
		this.smscid = smscid;
	}
	public String getStatuscd()
	{
		return statuscd;
	}
	public void setStatuscd(String statuscd)
	{
		this.statuscd = statuscd;
	}
	public String getMsgid()
	{
		return msgid;
	}
	public void setMsgid(String msgid)
	{
		this.msgid = msgid;
	}
	public String getAckid()
	{
		return ackid;
	}
	public void setAckid(String ackid)
	{
		this.ackid = ackid;
	}
	public String getPid()
	{
		return pid;
	}
	public void setPid(String pid)
	{
		this.pid = pid;
	}
	public String getAid()
	{
		return aid;
	}
	public void setAid(String aid)
	{
		this.aid = aid;
	}
	public String getCustref()
	{
		return custref;
	}
	public void setCustref(String custref)
	{
		this.custref = custref;
	}
	public String getRts()
	{
		return rts;
	}
	public void setRts(String rts)
	{
		this.rts = rts;
	}
	public String getDlrtype()
	{
		return dlrtype;
	}
	public void setDlrtype(String dlrtype)
	{
		this.dlrtype = dlrtype;
	}
	public String getMobile()
	{
		if(mobile.trim().length() == 0)
			mobile="0";
		return mobile;
	}
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}
	public String getLastutime()
	{
		return lastutime;
	}
	public void setLastutime(String lastutime)
	{
		this.lastutime = lastutime;
	}
	public String getExpiry()
	{
		return expiry;
	}
	public void setExpiry(String expiry)
	{
		this.expiry = expiry;
	}
	public String getMsg()
	{
		return msg;
	}
	public void setMsg(String msg)
	{
		
	}
	public String getSenderid()
	{
		return senderid;
	}
	public void setSenderid(String senderid)
	{
		this.senderid = senderid;
	}
	public String getCarrierid()
	{
		return carrierid;
	}
	public void setCarrierid(String carrierid)
	{
		if(carrierid!=null && carrierid.trim().length()!=0)
			this.carrierid = carrierid;
	}
	public String getCircleid()
	{
		
		return circleid;
	}
	public void setCircleid(String circleid)
	{
		if(circleid!=null && circleid.trim().length()!=0)
			this.circleid = circleid;
	}
	public String getInterr()
	{
		return interr;
	}
	public void setInterr(String interr)
	{
		this.interr = interr;
	}
	public String getIntstat()
	{
		return intstat;
	}
	public void setIntstat(String intstat)
	{
		this.intstat = intstat;
	}
	
	
	
	public Map disect(String dr) throws Exception
	{		
		
		
		//Sample DR
		//1
		//String dr="id:0787121638 sub:000 dlvrd:000 submit date:1210311252 done date:1210311252 stat:DELIVRD err:000 text:Rs. 5000 is Credited";

		//2
		//String dr="id:0336641967 submit date:1210311252 done date:1210311252 stat:DELIVRD err:0";
		
		//3
		//String dr="id:arsmpp-1342166236631-45-0203 sub:1 dlvrd:0 submit date:120713132848 done date:120713132848 stat:FAILED:Mob. Abst.|nt ext|out ser err:100 text:";
	
		//4
		
		//id:918879021808984630351444422643 sub:001 dlvrd:001 submit date:2013-02-17 00:04:29 done date:2013-02-17 00:04:32 stat:DELIVRD err:000 text:Bsmart SMSC

		
		/*
		 * 
		 * DELIVEY RECEPT	SMSCID
			id:3681053784 sub:000 dlvrd:000 submit date:1302200000 done date:1302200000 stat:DELIVRD err:000 text:9391608421: Remember	BPL-MUM
			id:01085081361298653000111 sub:000 dlvrd:000 submit date:130220000053 done date:130220000057 stat:DELIVRD err:000 Text:9865050090: DOLLAR:	BSNL
			id:1305032053831018099 sub:001 dlvrd:001 submit date:1302020538 done date:1302020538 stat:DELIVRD err:000 text:	TANLA
			id:918879021808984630351444422643 sub:001 dlvrd:001 submit date:2013-02-17 00:04:29 done date:2013-02-17 00:04:32 stat:DELIVRD err:000 text:Bsmart SMSC	VODA


		 */

		HashMap map = new HashMap(5);
		dr=dr.toUpperCase()+" ";
        String id=null;
        String submitDate=null;
        String doneDate =null;
        String stat=null;
        String err=null;

        if(dr.indexOf("ID:")!=-1)
                        id=dr.substring(dr.indexOf("ID:")+3, dr.indexOf(" ",dr.indexOf("ID:")+3));
        if(dr.indexOf("SUBMIT DATE:")!=-1)
                        submitDate = dr.substring(dr.indexOf("SUBMIT DATE:") + 12, dr.indexOf("DONE DATE:")).trim();
        if(dr.indexOf("DONE DATE:")!=-1)
                        doneDate = dr.substring(dr.indexOf("DONE DATE:") + 10, dr.indexOf("STAT:")).trim();
        if(dr.indexOf("STAT:")!=-1)
                        stat = dr.substring(dr.indexOf("STAT:") + 5, dr.indexOf(" ",dr.indexOf("STAT:") + 5)).trim();
        if(dr.indexOf("ERR:")!=-1)
                        err = dr.substring(dr.indexOf("ERR:") + 4, dr.indexOf(" ",dr.indexOf("ERR:") + 4)).trim();


        
        if(stat.indexOf(":")!=-1)
            stat=stat.substring(0,stat.indexOf(":"));

        
    	map.put("id", id);
		map.put("submitdate", submitDate);
		map.put("donedate", doneDate);
		map.put("stat", stat);
		map.put("err", err);
		log.debug("dr = "+dr);
		log.debug("dr map = "+map);
	
		return map;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("dr=").append(dr).append("\n");
		sb.append("smscid=").append(smscid).append("\n");
		sb.append("statuscd=").append(statuscd).append("\n");
		sb.append("msgid=").append(msgid).append("\n");
		sb.append("ackid=").append(ackid).append("\n");
		sb.append("pid=").append(pid).append("\n");
		sb.append("aid=").append(aid).append("\n");
		sb.append("rts=").append(rts).append("\n");
		sb.append("dlrtype=").append(dlrtype).append("\n");
		sb.append("mobile=").append(mobile).append("\n");
		sb.append("lastutime=").append(lastutime).append("\n");
		sb.append("senderid=").append(senderid).append("\n");
		sb.append("carrierid=").append(carrierid).append("\n");
		sb.append("circleid=").append(circleid).append("\n");
		sb.append("interr=").append(interr).append("\n");
		sb.append("intstat=").append(intstat).append("\n");
		sb.append("drsubmitdate=").append(drsubmitdate).append("\n");
		sb.append("drdonedate=").append(drdonedate).append("\n");
		sb.append("drerr=").append(drerr).append("\n");
		sb.append("drstat=").append(drstat).append("\n");
		sb.append("statusflag=").append(statusflag).append("\n");
		sb.append("errcode=").append(errcode).append("\n");
		sb.append("errdes=").append(errdes).append("\n");
		sb.append("dr16=").append(dr16).append("\n");
		sb.append("dr32=").append(dr32).append("\n");
		sb.append("\n");
		return sb.toString();
	}



	public String getRouteid()
	{
		return routeid;
	}



	public void setRouteid(String routeid)
	{
		if(routeid != null)
		{
			routeid = routeid.trim();
			if(routeid.length()==0)
				routeid = null;
		}
		
		this.routeid = routeid;
	}



	public boolean isDNRetry() {
		return isDNRetry;
	}



	public void setDNRetry(boolean isDNRetry) {
		this.isDNRetry = isDNRetry;
	}



	public String getProtocol()
	{
		return protocol;
	}



	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}



	public String getCimdtime()
	{
		return cimdtime;
	}



	public void setCimdtime(String cimdtime)
	{
		this.cimdtime = cimdtime;
	}



	public String getRow()
	{
		return row;
	}



	public void setRow(String row)
	{
		this.row = row;
	}



	public long getCustomerdonedt()
	{
		return customerdonedt;
	}



	public void setCustomerdonedt(long customerdonedt)
	{
		this.customerdonedt = customerdonedt;
	}



	public String getMsgType() {
		return msgType;
	}



	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}



	public String getEsm() {
		return esm;
	}



	public void setEsm(String esm) {
		this.esm = esm;
	}



	public int getDnpostretryattempt() {
		return dnpostretryattempt;
	}



	public void setDnpostretryattempt(int dnpostretryattempt) {
		this.dnpostretryattempt = dnpostretryattempt;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getSystemid() {
		return systemid;
	}



	public void setSystemid(String systemid) {
		this.systemid = systemid;
	}



	public String getSplitseq() {
		return splitseq;
	}



	public void setSplitseq(String splitseq) {
		this.splitseq = splitseq;
	}



	public Timestamp getDnsts() {
		return dnsts;
	}



	public void setDnsts(Timestamp dnsts) {
		this.dnsts = dnsts;
	}



	public Timestamp getDncrts() {
		return dncrts;
	}



	public void setDncrts(Timestamp dncrts) {
		this.dncrts = dncrts;
	}



	public String getDnretrymode()
	{
		return dnretrymode;
	}



	public void setDnretrymode(String dnretrymode)
	{
		this.dnretrymode = dnretrymode;
	}



	public String getErr()
	{
		return err;
	}



	public void setErr(String err)
	{
		this.err = err;
	}


	public String getMismask() {
		return mismask;
	}



	public void setMismask(String mismask) {
		this.mismask = mismask;
	}



	public String getParam1() {
		return param1;
	}



	public void setParam1(String param1) {
		this.param1 = param1;
	}



	public String getParam2() {
		return param2;
	}



	public void setParam2(String param2) {
		this.param2 = param2;
	}



	public String getParam3() {
		return param3;
	}



	public void setParam3(String param3) {
		this.param3 = param3;
	}



	public String getParam4() {
		return param4;
	}



	public void setParam4(String param4) {
		this.param4 = param4;
	}



	public String getParam5() {
		return param5;
	}



	public void setParam5(String param5) {
		this.param5 = param5;
	}



	public String getPremsgid() {
		return premsgid;
	}



	public void setPremsgid(String premsgid) {
		this.premsgid = premsgid;
	}



	public String getIsFromSingleDn() {
		return isFromSingleDn;
	}



	public void setIsFromSingleDn(String isFromSingleDn) {
		this.isFromSingleDn = isFromSingleDn;
	}



	public String getQs() {
		return qs;
	}



	public void setQs(String qs) {
		this.qs = qs;
	}



	
	
	
	
}