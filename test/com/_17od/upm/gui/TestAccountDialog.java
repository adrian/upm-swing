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

   Method method = myTarget.getDeclaredMethod(InclUpperCaseMethod, new Class[] {String.class});
   method.setAccessible(true);

   String Password = "Klfjjru!234";
   Boolean result = (Boolean)method.invoke(method, new String[] {Password});

   assertTrue(result.booleanValue());

 }

 public void testInclUpperCaseFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

    //Using reflection to test private method
    Class myTarget = AccountDialog.class;
    Class params[] = new Class[1];
    params[0] = boolean.class;

    Method method = myTarget.getDeclaredMethod(InclUpperCaseMethod, new Class[] {String.class});
    method.setAccessible(true);

    String Password = "kllfmjiugiwug@#$%%^k856";
    Boolean result = (Boolean)method.invoke(method, new String[] {Password});

    assertFalse(result.booleanValue());

 }

public void testInclLowerCaseTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

   //Using reflection to test private method
   Class myTarget = AccountDialog.class;
   Class params[] = new Class[1];
   params[0] = boolean.class;

   Method method = myTarget.getDeclaredMethod(InclLowerCaseMethod, new Class[] {String.class});
   method.setAccessible(true);

   String Password = "hghtkghjKLLP123%^&";
   Boolean result = (Boolean)method.invoke(method, new String[] {Password});

   assertTrue(result.booleanValue());

 }


public void testInclLowerCaseFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

   //Using reflection to test private method
   Class myTarget = AccountDialog.class;
   Class params[] = new Class[1];
   params[0] = boolean.class;

   Method method = myTarget.getDeclaredMethod(InclLowerCaseMethod, new Class[] {String.class});
   method.setAccessible(true);

   String Password = "KGHJLMBCX78990^&**";
   Boolean result = (Boolean)method.invoke(method, new String[] {Password});

   assertFalse(result.booleanValue());

 }

 public void testInclNumberTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

    //Using reflection to test private method
    Class myTarget = AccountDialog.class;
    Class params[] = new Class[1];
    params[0] = boolean.class;

    Method method = myTarget.getDeclaredMethod(InclNumberMethod, new Class[] {String.class});
    method.setAccessible(true);

    String Password = "Kghkfrjgir*&^$%4523";
    Boolean result = (Boolean)method.invoke(method, new String[] {Password});

    assertTrue(result.booleanValue());

 }

 public void testInclNumberFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclNumberMethod, new Class[] {String.class});
     method.setAccessible(true);

     String Password = "KJHHGhnnfhhshd&*()hghg";
     Boolean result = (Boolean)method.invoke(method, new String[] {Password});

     assertFalse(result.booleanValue());

 }

 public void testInclEscapeTrue() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclEscapeMethod, new Class[] {String.class});
     method.setAccessible(true);

     String Password = "Jajjggj*hgjf12364";
     Boolean result = (Boolean)method.invoke(method, new String[] {Password});

     assertTrue(result.booleanValue());

 }

public void testInclEscapeFalse() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = boolean.class;

     Method method = myTarget.getDeclaredMethod(InclEscapeMethod, new Class[] {String.class});
     method.setAccessible(true);

     String Password = "JKhghgloeor159645";
     Boolean result = (Boolean)method.invoke(method, new String[] {Password});

     assertFalse(result.booleanValue());

 }

public void testGeneratePassword() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

     //Using reflection to test private method
     Class myTarget = AccountDialog.class;
     Class params[] = new Class[1];
     params[0] = String.class;



     Method method = myTarget.getDeclaredMethod(GeneratePasswordMethod, new Class[] {int.class, boolean.class});
     method.setAccessible(true);

     Integer Length = new Integer(10);
     Integer Length2 = new Integer(7);
     String result = (String)method.invoke(method, new Object[] {Length, Boolean.TRUE});
     String result2 = (String)method.invoke(method, new Object[] {Length, Boolean.FALSE});
     String result3 = (String)method.invoke(method, new Object[] {Length2, Boolean.TRUE});
     String result4 = (String)method.invoke(method, new Object[] {Length2, Boolean.FALSE});

     assertEquals(10,result.length(), 0.1);
     assertEquals(10,result2.length(), 0.1);
     assertNotSame(new Integer(12), new Integer(result.length()));
     assertNotSame(new Integer(12), new Integer(result2.length()));
     assertNotSame(new Integer(9), new Integer(result.length()));
     assertNotSame(new Integer(9), new Integer(result2.length()));

     assertEquals(7,result3.length(), 0.1);
     assertEquals(7,result4.length(), 0.1);
     assertNotSame(new Integer(12), new Integer(result3.length()));
     assertNotSame(new Integer(12), new Integer(result4.length()));
     assertNotSame(new Integer(9), new Integer(result3.length()));
     assertNotSame(new Integer(9), new Integer(result4.length()));

 }
}
