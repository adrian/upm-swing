package com._17od.upm.gui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestHelperClass {

	
	
	@Test
	public void testMakeValidHttp(){
		
		HelperClass h= new HelperClass();
		System.out.println(h.makeValidHttp("google.com"));
		assertTrue(("http://www.google.com").equals(h.makeValidHttp("google.com")));
		assertTrue(("http://www.google.com").equals(h.makeValidHttp("www.google.com")));
		assertTrue(("http://www.google.com").equals(h.makeValidHttp("http://www.google.com")));
		
	}
	
}
