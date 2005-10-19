/*
 * Created on Oct 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com._17od.upm.invest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author smitha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestRandomAccessFile {

	public static void main(String[] args) throws IOException {
		File databaseFile = new File(System.getProperty("user.dir") + "/pd");
		byte[] salt = "abc".getBytes();
		RandomAccessFile raf = new RandomAccessFile(databaseFile, "rw");
		raf.write(salt);
		raf.close();
	}
	
}
