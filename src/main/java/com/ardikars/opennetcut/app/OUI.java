
package com.ardikars.opennetcut.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class OUI implements Runnable {

    private LoggerHandler logHandler;
    
    public OUI(LoggerHandler logHandler) {
        this.logHandler = logHandler;
    }
    
    
    public static String searchVendor(String MacAddr) {
        MacAddr = MacAddr.trim().substring(0, 8).toUpperCase();
        final String vendorId = MacAddr;
        String res = null;    
        try (Stream<String> lines = Files.lines(new File("oui.txt").toPath(), Charset.defaultCharset())) {
            res = lines.filter(l -> l.startsWith(vendorId)).findFirst().orElse(null);
        } catch (IOException ex) {
            return res;
        }
        String[] vendorName = res.split("#");
        return vendorName[vendorName.length-1].trim();
    }
    
    @Override
    public void run() {
        logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Download OUI.");
        URL website = null;
        try {
            website = new URL("https://code.wireshark.org/review/gitweb?p=wireshark.git;a=blob_plain;f=manuf;hb=HEAD");
        } catch (MalformedURLException ex) {
            logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + ex.getMessage());
        }
        try (InputStream in = website.openStream()) {
            Files.copy(in, Paths.get("oui.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + ex.getMessage());
        }
        logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: UOI Update finished.");
    }
    
}
