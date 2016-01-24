package org.bven.hello;

import java.io.*;
import java.nio.file.*;
import org.hyperic.jni.*;

public class Native {

    static {
        try (ArchLoader loader = new ArchLoader(Native.class)) {
            System.load(loader.getLibFile());
        }
	}

    private native static String getGreeting();

    private native static void sendGreeting(String message);

    public static void main(String[] args) {
        System.out.println(getGreeting());
        sendGreeting("world!");
    }
}