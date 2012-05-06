package io.leon.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FileUtilsTest {

    public void getDirectoryNameOfPath() {
        Assert.assertEquals(FileUtils.getDirectoryNameOfPath("/a/b/c/file.txt"), "/a/b/c/");
        Assert.assertEquals(FileUtils.getDirectoryNameOfPath("/a/b/c/"), "/a/b/c/");
        Assert.assertEquals(FileUtils.getDirectoryNameOfPath("/a/b/c"), "/a/b/");
    }
}
