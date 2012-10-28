package com._17od.upm.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileMonitorTest extends TestCase {

    private File fileToMonitor;

    public void setUp() throws IOException {
        fileToMonitor = File.createTempFile("test", "");
    }

    public void tearDown() {
        fileToMonitor.delete();
    }

    public void testFileDidntChange() throws IOException {
        FileMonitor fileMonitor = new FileMonitor(fileToMonitor, null);
        assertFalse(fileMonitor.fileChanged());
    }

    public void testFileChanged() throws IOException {
        FileMonitor fileMonitor = new FileMonitor(fileToMonitor, null);
        fileToMonitor.setLastModified(fileToMonitor.lastModified() + 1000);
        assertTrue(fileMonitor.fileChanged());
    }

}
