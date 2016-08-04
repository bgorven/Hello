package org.bven.hello;

import java.io.*;
import java.nio.file.*;
import org.bven.jni.*;

public class Native {

    static {
        ArchLoader.load(Native.class);
	}

    private native static String getGreeting();

    private native static void sendGreeting(String message);

    public static void main(String[] args) {
        System.out.println(getGreeting());
        sendGreeting("world!");
    }
}