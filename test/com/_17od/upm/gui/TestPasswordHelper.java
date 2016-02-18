package com._17od.upm.gui;

import org.junit.Assert;
import org.junit.Test;


public class TestPasswordHelper
{
    @Test
    public void testPasswordHints()
    {
        PasswordHelper testing = new PasswordHelper();
        Assert.assertFalse(testing.getCalledState());
    }

}
