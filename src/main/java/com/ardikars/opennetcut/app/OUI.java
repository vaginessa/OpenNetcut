/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	if (res == null) return "";
        String[] vendorName = res.split("#");
	if (vendorName == null) return "";
        return (vendorName[vendorName.length-1] == null) ? "" : vendorName[vendorName.length-1].trim();
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
