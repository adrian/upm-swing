package com._17od.upm.invest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class TestPref {

    /**
     * @param args
     * @throws BackingStoreException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, BackingStoreException {
        // TODO Auto-generated method stub
        Preferences p = Preferences.userNodeForPackage(TestPref.class);
        p.put("DBToOpenOnStartup", "c:/temp/f");
        p.exportNode(new FileOutputStream(System.getProperty("user.dir") + "/pref"));
        System.out.println(p.getClass());
    }

}
