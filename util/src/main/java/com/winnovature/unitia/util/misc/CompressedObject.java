package com.winnovature.unitia.util.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.zip.InflaterInputStream;

public class CompressedObject implements Serializable{

	byte[] databytes=null;

	public void setDatabytes(byte[] databytes) {
		this.databytes = databytes;
	}
	
	public Object getObject() throws IOException, ClassNotFoundException {
	    ObjectInputStream ois = new ObjectInputStream(new InflaterInputStream(new ByteArrayInputStream(databytes)));
	    return ois.readObject();
	}
}
