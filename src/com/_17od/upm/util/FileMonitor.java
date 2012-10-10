package com._17od.upm.util;

import java.io.File;

/**
 * Monitor a file and call a callback when the file changes.
 */
public class FileMonitor implements Runnable {

    private FileChangedCallback fileChangedCallback;
    private File fileToMonitor;
    private long previousLastModified;
    private boolean paused;

    public FileMonitor(File fileToMonitor, FileChangedCallback fileChangedCallback) {
        this.fileChangedCallback = fileChangedCallback;
        this.fileToMonitor = fileToMonitor;
        this.previousLastModified = fileToMonitor.lastModified();
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (!paused && fileChanged()) {
                    fileChangedCallback.fileChanged(fileToMonitor);
                    paused = true;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void start() {
        this.previousLastModified = fileToMonitor.lastModified();
        paused = false;
    }

    public synchronized void pause() {
        paused = true;
    }

    public boolean fileChanged() {
        boolean fileModified = false;
        long currentLastModified = fileToMonitor.lastModified();
        if (previousLastModified != currentLastModified) {
            fileModified = true;
        }
        this.previousLastModified = currentLastModified;
        return fileModified;
    }

}
