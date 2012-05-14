package io.leon.utils;

import java.io.*;
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
        if (!file.exists()) {
            return lines;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading lines of file '" + file.getName() + "'", e);
        } finally {
            close(reader);
        }
        return lines;
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            } else {
                throw new RuntimeException("Can not call closeable.close() since the passed Closeable is null.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteIfExists(String fileName) {
        return deleteIfExists(new File(fileName));
    }

    public static boolean deleteIfExists(File file) {
        return file.exists() && file.delete();
    }
}
