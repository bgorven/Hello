package org.bven.hello;

import java.io.*;
import java.nio.file.*;

public class Native {

    static {
        try {
            String bits = System.getProperty("sun.arch.data.model");
            Path tmp = Files.createTempDirectory("lib");
            tmp.toFile().deleteOnExit();
            String fileName = "64".equals(bits) ? "hello-x64.dll" : "hello-x86.dll";
            Path file = tmp.resolve(fileName);
            try (InputStream in = Native.class.getResourceAsStream("/linux-x64/libcpp.so");
                    OutputStream out = new FileOutputStream(file.toFile())) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }
            }
            System.load(file.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new Error("Failed to extract and load library.", e);
        }
    }

    private native static String getGreeting();

    private native static void sendGreeting(String message);

    public static void main(String[] args) {
        System.out.println(getGreeting());
        sendGreeting("world!");
    }
}
