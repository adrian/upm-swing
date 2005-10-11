/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.crypto;

import com._17od.upm.crypto.EncryptionService;

import junit.framework.TestCase;


public class TestEncryptionService extends TestCase {

	private char[] password = {
			't', 'e', 's', 't', ' ', 'p', 'a', 's', 's',
			'w', 'o', 'r', 'd'};
	EncryptionService encryptionService;

	
	protected void setUp() throws Exception {
		super.setUp();
		encryptionService = EncryptionService.getInstance();
		encryptionService.init(password);
	}

	
	public void testEncryptDecryptString() throws Exception {
		byte[] cleartext = "samplestring".getBytes();
		byte[] cipherText = encryptionService.encrypt(cleartext);
		byte[] cleartext2 = encryptionService.decrypt(cipherText);
		assertEquals("Decrypted text is different to original cleartext", new String(cleartext), new String(cleartext2));
	}
	
}
