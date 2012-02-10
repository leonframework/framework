package io.leon.persistence.hbase.tests;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.leon.persistence.hbase.HBaseBinder;
import io.leon.persistence.hbase.LeonHBaseFeatureModule;
import io.leon.persistence.hbase.LeonHBaseTable;
import io.leon.unitofwork.UOWModule;
import io.leon.unitofwork.UOWManager;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class LeonHBaseTableManagementTest extends AbstractLeonHBaseTest {

    @Test
    public void testAutomaticTableCreationAndDeletionApi() throws IOException {
        final String personTableName = getRandomTableName("person");

        // Create a module for testing
        Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new LeonHBaseFeatureModule());
                install(new UOWModule());
                new HBaseBinder(binder()).addTable(personTableName, "data", "cf1");
            }
        });
        Assert.assertFalse(
                "Table should not be created before the LeonHBaseTable instance is requested.",
                getAdmin(i).tableExists(personTableName));

        // Force table creation
        LeonHBaseTable personTable = getTable(i, personTableName);
        Assert.assertTrue(
                "Table should be created when the LeonHBaseTable istance is requested.",
                getAdmin(i).tableExists(personTableName));

        // Delete table
        UOWManager manager = i.getInstance(UOWManager.class);
        manager.begin(this);
        personTable.delete();
        manager.commit();
        Assert.assertFalse(
                "Table should have been deleted.", getAdmin(i).tableExists(personTableName));
    }

    @Test
    public void testColumnFamiliesManagement() throws IOException {
        final String personTableName = getRandomTableName("person");

        // Create a module for testing
        Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new LeonHBaseFeatureModule());
                new HBaseBinder(binder()).addTable(personTableName, "cf1", "cf2");
            }
        });

        // Force table creation
        getTable(i, personTableName);

        // Test column families
        HTableDescriptor desc = getAdmin(i).getTableDescriptor(personTableName.getBytes());
        Set<byte[]> fKeys = desc.getFamiliesKeys();
        Assert.assertTrue("Person table should have column family cf1", fKeys.contains("cf1".getBytes()));
        Assert.assertTrue("Person table should have column family cf2", fKeys.contains("cf2".getBytes()));
        Assert.assertFalse("Person table should not yet have column family cf3", fKeys.contains("cf3".getBytes()));

        // Add a 3rd column familiy
        i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new LeonHBaseFeatureModule());
                new HBaseBinder(binder()).addTable(personTableName, "cf1", "cf2", "cf3");
            }
        });

        // Force table creation
        getTable(i, personTableName);

        // Test column families
        desc = getAdmin(i).getTableDescriptor(personTableName.getBytes());
        fKeys = desc.getFamiliesKeys();
        Assert.assertTrue("Person table should have column family cf1", fKeys.contains("cf1".getBytes()));
        Assert.assertTrue("Person table should have column family cf2", fKeys.contains("cf2".getBytes()));
        Assert.assertTrue("Person table should have column family cf3", fKeys.contains("cf3".getBytes()));

        // Clean up
        deleteTable(i, personTableName);
    }

}
