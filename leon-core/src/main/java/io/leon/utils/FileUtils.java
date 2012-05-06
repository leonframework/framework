package io.leon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {

    public static String getDirectoryNameOfPath(String path) {
        if (path.endsWith("/")) {
            // Already a directory path
            return path;
        }
        int lastSlash = path.lastIndexOf('/') + 1;
        return path.substring(0, lastSlash);
    }

    public static List<String> readLines(File file) {
        List<String> lines = new LinkedList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading lines of file '" + file.getName() + "'", e);
        }
        return lines;
    }
}
