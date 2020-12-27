package com.winnovature.unitia.util.misc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;


import java.util.HashMap;




public class ResultSetToHashMapConverter  {
public Map<String,String> toObject(ResultSet rs) throws Exception {
	Map map = new HashMap();
	ResultSetMetaData meta = rs.getMetaData();
	// Load ResultSet into map by column name
	int numberOfColumns = meta.getColumnCount();
	for (int i = 1; i <= numberOfColumns; ++i) {
		String name = meta.getColumnName(i);
		Object value = rs.getObject(i);
		// place into map
		map.put(name.toLowerCase(), value);
	}
	return map;
}
}