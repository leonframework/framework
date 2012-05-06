package io.leon.utils;

public class FileUtils {

    public static String getDirectoryNameOfPath(String path) {
        if (path.endsWith("/")) {
            // Already a directory path
            return path;
        }
        int lastSlash = path.lastIndexOf('/') + 1;
        return path.substring(0, lastSlash);
    }
}
