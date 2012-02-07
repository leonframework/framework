package io.leon.persistence.hbase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class LeonHBaseTableCrudTest {

    private HBaseAdmin getAdmin(Injector injector) {
        return injector.getInstance(HBaseAdmin.class);
    }

    private LeonHBaseTable getTable(Injector injector, String tableName) {
        return injector.getInstance(Key.get(LeonHBaseTable.class, Names.named(tableName)));
    }

    private void deleteTable(Injector injector, String tableName) {
        try {
            if (getAdmin(injector).tableExists(tableName)) {
                getAdmin(injector).disableTable(tableName);
                getAdmin(injector).deleteTable(tableName);
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    @Test
    public void testThreadSeperation() {
        // Create a module for testing
        final Injector i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                addTable("person", "cf1", "cf2");
            }
        });

        // Test behaviour in one thread
        HTableInterface person1 = getTable(i, "person").getHTableInterface();
        HTableInterface person2 = getTable(i, "person").getHTableInterface();
        Assert.assertSame("A thread should only have one HTable instance.", person1, person2);

        // Test behaviour with two threads
        final Thread[] threads = new Thread[2];
        final HTableInterface[] instances = new HTableInterface[2];
        threads[0] = new Thread() {
            @Override
            public void run() {
                // Remember the instance used in this thread
                instances[0] = getTable(i, "person").getHTableInterface();
                // Wait for the other thread
                try {
                    while (instances[1] == null) {
                        System.out.println("LeonHBaseTableCrudTest.testThreadSeperation(): Waiting for other thread to finish.");
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        threads[1] = new Thread() {
            @Override
            public void run() {
                // Remember the instance used in this thread
                instances[1] = getTable(i, "person").getHTableInterface();
                // Wait for the other thread
                try {
                    while (instances[0] == null) {
                        System.out.println("LeonHBaseTableCrudTest.testThreadSeperation(): Waiting for other thread to finish.");
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            threads[0].start();
            threads[1].start();
            threads[0].join();
            threads[1].join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotSame("Two threads should have different HTable instances.", instances[0], instances[1]);
    }



}
