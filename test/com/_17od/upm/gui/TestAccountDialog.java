/*
 * Universal Password Manager
 * Copyright (C) 2005-2013 Adrian Smith
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
package com._17od.upm.gui;


import junit.framework.TestCase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public  class TestAccountDialog extends TestCase {

//Reflection Strings for Private Method Name
 String InclUpperCaseMethod = "InclUpperCase";
 String InclLowerCaseMethod = "InclLowerCase";
 String InclNumberMethod = "InclNumber";
 String InclEscapeMethod = "InclEscape";
 String GeneratePasswordMethod = "GeneratePassword";

 public void testInclUpperCaseTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

   //Using reflection to test private method
   Class myTarget = AccountDialog.class;
   Class params[] = new Class[1];
   params[0] = boolean.class;

   Method method = myTarget.getDeclaredMethod(InclUpperCaseMethod, String.class);
   method.setAccessible(true);

   String Password = "Klfjjru!234";
   Boolean result = (Boolean)method.invoke(method, Password);

   assertTrue(result);

 }

 public void testInclUpperCaseFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

    //Using reflection to test private method
    Class myTarget = AccountDialog.class;
    Class params[] = new Class[1];
    params[0] = boolean.class;

    Method method = myTarget.getDeclaredMethod(InclUpperCaseMethod, String.class);
    method.setAccessible(true);

    String Password = "kllfmjiugiwug@#$%%^k856";
    Boolean result = (Boolean)method.invoke(method, Password);

    assertFalse(result);

 }

public void testInclLowerCaseTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

   //Using reflection to test private method
   Class myTarget = AccountDialog.class;
   Class params[] = new Class[1];
   params[0] = boolean.class;

   Method method = myTarget.getDeclaredMethod(InclLowerCaseMethod, String.class);
   method.setAccessible(true);

   String Password = "hghtkghjKLLP123%^&";
   Boolean result = (Boolean)method.invoke(method, Password);

   assertTrue(result);

 }


public void testInclLowerCaseFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

   //Using reflection to test private method
   Class myTarget = AccountDialog.class;
   Class params[] = new Class[1];
   params[0] = boolean.class;

   Method method = myTarget.getDeclaredMethod(InclLowerCaseMethod, String.class);
   method.setAccessible(true);

   String Password = "KGHJLMBCX78990^&**";
   Boolean result = (Boolean)method.invoke(method, Password);

   assertFalse(result);

 }

 public void testInclNumberTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

    //Using reflection to test private method
    Class myTarget = AccountDialog.class;
    Class params[] = new Class[1];
    params[0] = boolean.class;

    Method method = myTarget.getDeclaredMethod(InclNumberMethod, String.class);
    method.setAccessible(true);

    String Password = "Kghkfrjgir*&^$%4523";
    Boolean result = (Boolean)method.invoke(method, Password);

    assertTrue(result);

 }

 public void testInclNumberFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclNumberMethod, String.class);
     method.setAccessible(true);

     String Password = "KJHHGhnnfhhshd&*()hghg";
     Boolean result = (Boolean)method.invoke(method, Password);

     assertFalse(result);

 }

 public void testInclEscapeTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclEscapeMethod, String.class);
     method.setAccessible(true);

     String Password = "Jajjggj*hgjf12364";
     Boolean result = (Boolean)method.invoke(method, Password);

     assertTrue(result);

 }

public void testInclEscapeFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclEscapeMethod, String.class);
     method.setAccessible(true);

     String Password = "JKhghgloeor159645";
     Boolean result = (Boolean)method.invoke(method, Password);

     assertFalse(result);

 }

public void testGeneratePassword() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = String.class;



     Method method = myTarget.getDeclaredMethod(GeneratePasswordMethod, int.class, boolean.class);
     method.setAccessible(true);

     int Length= 10;
     int Length2 = 7;
     String result = (String)method.invoke(method, Length, true);
     String result2 = (String)method.invoke(method, Length, false);
     String result3 = (String)method.invoke(method, Length2, true);
     String result4 = (String)method.invoke(method, Length2, false);

     assertEquals(10,result.length(), 0.1);
     assertEquals(10,result2.length(), 0.1);
     assertNotSame(12,result.length());
	 assertNotSame(12,result2.length());
	 assertNotSame(9,result.length());
	 assertNotSame(9,result2.length());

	 assertEquals(7,result3.length(), 0.1);
	 assertEquals(7,result4.length(), 0.1);
	 assertNotSame(12,result3.length());
	 assertNotSame(12,result4.length());
	 assertNotSame(9,result3.length());
	 assertNotSame(9,result4.length());


 }
}
