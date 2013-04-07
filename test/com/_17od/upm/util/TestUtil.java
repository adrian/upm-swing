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
package com._17od.upm.util;

import junit.framework.TestCase;


public class TestUtil extends TestCase {

    public void testLpad1() {
        assertEquals(Util.lpad(1, 1, '0'), "1");
    }

    public void testLpad2() {
        assertEquals(Util.lpad(1, 2, '0'), "01");
    }

    public void testLpad3() {
        assertEquals(Util.lpad(34, 2, '0'), "34");
    }

    public void testLpad4() {
        assertEquals(Util.lpad(34, 6, 'd'), "dddd34");
    }

}
