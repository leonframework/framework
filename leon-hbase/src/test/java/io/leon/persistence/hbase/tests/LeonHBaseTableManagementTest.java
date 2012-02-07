package io.leon.persistence.hbase.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.leon.persistence.hbase.LeonHBaseModule;
import io.leon.persistence.hbase.LeonHBaseTable;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class LeonHBaseTableManagementTest extends AbstractLeonHBaseTest {

    @Test
    public void testAutomaticTableCreationAndDeletionApi() throws IOException {
        // Delete person table in case it already exists
        Injector i = Guice.createInjector(new LeonHBaseModule());
        deleteTable(i, "person");

        // Create a fresh module for testing
        i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                addTable("person", "data", "cf1");
            }
        });
        Assert.assertFalse(
                "Table should not be created before the LeonHBaseTable instance is requested.",
                getAdmin(i).tableExists("person"));

        // Force table creation
        LeonHBaseTable personTable = getTable(i, "person");
        Assert.assertTrue(
                "Table should be created when the LeonHBaseTable istance is requested.",
                getAdmin(i).tableExists("person"));

        // Delete table
        personTable.delete();
        Assert.assertFalse(
                "Table should have been deleted.", getAdmin(i).tableExists("person"));
    }

    @Test
    public void testColumnFamiliesManagement() throws IOException {
        // Delete person table in case it already exists
        Injector i = Guice.createInjector(new LeonHBaseModule());
        deleteTable(i, "person");

        // Create a fresh module for testing
        i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                addTable("person", "cf1", "cf2");
            }
        });

        // Force table creation
        getTable(i, "person");

        // Test column families
        HTableDescriptor desc = getAdmin(i).getTableDescriptor("person".getBytes());
        Set<byte[]> fKeys = desc.getFamiliesKeys();
        Assert.assertTrue("Person table should have column family cf1", fKeys.contains("cf1".getBytes()));
        Assert.assertTrue("Person table should have column family cf2", fKeys.contains("cf2".getBytes()));
        Assert.assertFalse("Person table should not yet have column family cf3", fKeys.contains("cf3".getBytes()));

        // Add a 3rd column familiy
        i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                addTable("person", "cf1", "cf2", "cf3");
            }
        });

        // Force table creation
        getTable(i, "person");

        // Test column families
        desc = getAdmin(i).getTableDescriptor("person".getBytes());
        fKeys = desc.getFamiliesKeys();
        Assert.assertTrue("Person table should have column family cf1", fKeys.contains("cf1".getBytes()));
        Assert.assertTrue("Person table should have column family cf2", fKeys.contains("cf2".getBytes()));
        Assert.assertTrue("Person table should have column family cf3", fKeys.contains("cf3".getBytes()));

        // Clean up
        deleteTable(i, "person");
    }

}
