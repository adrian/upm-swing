package com._17od.upm.invest;

import java.security.*;
 
public class ListAlgorithms
{
    
    public static void main(String[] args)
    {
          Security.addProvider(new com.sun.crypto.provider.SunJCE());
        
        System.out.println("Providers -");
        
        Provider[] providers = java.security.Security.getProviders();
        for (int i = 0; i < providers.length; i++)
        {
            System.out.println("    " + providers[i].getName());
            java.util.Enumeration en = providers[i].propertyNames();
            while (en.hasMoreElements())
            {
                String alg = (String)en.nextElement();
                if (alg.toLowerCase().matches(".*pbe.*"))
                    System.out.println("        Alg = " + alg);
            }
        }
        
    }
    
}
