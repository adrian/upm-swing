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

    @Test
    public void testPasswordHint()
    {
        PasswordHelper testing = new PasswordHelper();
        Assert.assertEquals("Password should:\n- Be between 8-12 characters length\n- Have at least one special character" +
                "\n- Have at least one uppercase letter\n- Not be a dictionary word\n- Not be a recent password\n",testing.passwordHint());
    }

}
