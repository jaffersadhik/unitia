package com.winnovature.unitia.util.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureCode {

	public static final String EMS="EMS";
	
	public static final String EMC="EMC";
	
	public static final String EFS="EFS";
	
	public static final String EFC="EFC";
	
	public static final String UMS="UMS";
	
	public static final String UMC="UMC";
	
	public static final String UFS="UFS";
	
	public static final String UFC="UFC";

	public static final String BMS="BMS";
	
	public static final String BMC="BMS";

	public static final String PEMS="PEMS";
	
	public static final String PEMC="PEMC";
	
	public static final String PUMS="PUMS";
	
	public static final String PUMC="PUMC";
	
	static Set<String> dnretryFeaturecd=new HashSet<String>();

	static Set<String> hexaFeaturecd=new HashSet<String>();

	static Set<String> udhFeaturecd=new HashSet<String>();

	static Set<String> unicodeconcateFeaturecd=new HashSet<String>();
	
	static Map<String,String> kannelurlsuffix=new HashMap<String,String>();


	static{
		
		kannelurlsuffix.put("EMS", "&coding=0");
		kannelurlsuffix.put("EFS", "&coding=0&msgclass=0&alt-dcs=0");
		kannelurlsuffix.put("UMS", "&coding=2&alt-dcs=1");
		kannelurlsuffix.put("UFS", "&coding=2&alt-dcs=1&msgclass=0");
		kannelurlsuffix.put("BMS", "&coding=1&alt-dcs=1");
		kannelurlsuffix.put("PEMS", "&coding=0&alt-dcs=0");
		kannelurlsuffix.put("PUMS", "&coding=2&alt-dcs=1");
		kannelurlsuffix.put("EMC", "&coding=0");
		kannelurlsuffix.put("EFC", "&coding=0&msgclass=0&alt-dcs=0");
		kannelurlsuffix.put("UMC", "&coding=2&alt-dcs=1");
		kannelurlsuffix.put("UFC",  "&coding=2&alt-dcs=1&msgclass=0");
		kannelurlsuffix.put("BMC", "&coding=1&alt-dcs=1");
		kannelurlsuffix.put("PEMC", "&coding=0&alt-dcs=0");
		kannelurlsuffix.put("PUMC", "&coding=2&alt-dcs=1");
	}
	
	static{
		
		unicodeconcateFeaturecd.add(UMC);
		unicodeconcateFeaturecd.add(UFC);
	}
	static{
		udhFeaturecd.add(EMC);
		udhFeaturecd.add(EFC);
		udhFeaturecd.add(UMC);
		udhFeaturecd.add(UFC);
	}
	
	static{
		
		hexaFeaturecd.add(UMS);
		hexaFeaturecd.add(UMC);
		hexaFeaturecd.add(UFS);
		hexaFeaturecd.add(UFC);
		hexaFeaturecd.add(BMS);
		hexaFeaturecd.add(BMC);
		hexaFeaturecd.add(PUMS);
		hexaFeaturecd.add(PUMC);

	}
	
	static{
		
		dnretryFeaturecd.add(EMS);
		dnretryFeaturecd.add(EFS);
		dnretryFeaturecd.add(UMS);
		dnretryFeaturecd.add(UFS);
	}
	public static boolean isUDHMessage(String featurecd){
		
		return udhFeaturecd.contains(featurecd);
	}
	
	public static boolean isUnicodeConcate(String featurecd){
		
		return unicodeconcateFeaturecd.contains(featurecd);
	}

	public static boolean isHexa(String featurecd) {
		
		return hexaFeaturecd.contains(featurecd);
	}

	public static String getKannelUrlSuffix(String featurecd) {

		return kannelurlsuffix.get(featurecd);
	
	}

	public static boolean isDNRetry(String featurecd) {
		
		return dnretryFeaturecd.contains(featurecd);
	}
	
}
