package io.leon.persistence.hbase.tests;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.leon.persistence.hbase.HBaseBinder;
import io.leon.persistence.hbase.LeonHBaseModule;
import io.leon.persistence.hbase.unitofwork.exceptions.NoActiveUnitOfWorkException;
import io.leon.persistence.hbase.unitofwork.HBaseUOWManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LeonHBaseTableThreadTest extends AbstractLeonHBaseTest {

    public void testThreadSeperation() {
        final String personTableName = getRandomTableName("person");

        // Create a module for testing
        final Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new LeonHBaseModule());
                new HBaseBinder(binder()).addTable(personTableName, "cf1", "cf2");
            }
        });
        final HBaseUOWManager manager = i.getInstance(HBaseUOWManager.class);

        // Test behaviour in one thread
        manager.begin(this);
        HTableInterface person1 = getTable(i, personTableName).getHTableInterface();
        HTableInterface person2 = getTable(i, personTableName).getHTableInterface();
        Assert.assertSame(person1, person2, "A thread should only have one HTable instance.");

        manager.commit();

        // Test behaviour with two threads
        final Thread[] threads = new Thread[2];
        final HTableInterface[] instances = new HTableInterface[2];
        threads[0] = new Thread() {
            @Override
            public void run() {
                manager.begin(this);

                // Remember the instance used in this thread
                instances[0] = getTable(i, personTableName).getHTableInterface();
                // Wait for the other thread
                try {
                    while (instances[1] == null) {
                        System.out.println("LeonHBaseTableThreadTest.testThreadSeperation(): Waiting for other thread to finish.");
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                manager.commit();
            }
        };
        threads[1] = new Thread() {
            @Override
            public void run() {
                manager.begin(this);

                // Remember the instance used in this thread
                instances[1] = getTable(i, personTableName).getHTableInterface();
                // Wait for the other thread
                try {
                    while (instances[0] == null) {
                        System.out.println("LeonHBaseTableThreadTest.testThreadSeperation(): Waiting for other thread to finish.");
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                manager.commit();
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
        Assert.assertNotSame(instances[0], instances[1], "Two threads should have different HTable instances.");
    }

    @Test(expectedExceptions = NoActiveUnitOfWorkException.class)
    public void hbaseSupportCanNotBeUsedWithoutActiveUnitOfWork() throws InterruptedException {
        final String personTableName = getRandomTableName("person");

        Injector i = null;
        try {
            // Create a module for testing
            i = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    install(new LeonHBaseModule());
                    new HBaseBinder(binder()).addTable(personTableName, "cf1", "cf2");
                }
            });
            getTable(i, personTableName).getTableName();
        } finally {
            deleteTable(i, personTableName);
        }
    }

    public void hbaseSupportCanNotBeUsedAfterActiveUnitOfWorkCommit() throws InterruptedException {
        final String personTableName = getRandomTableName("person");

        // Create a module for testing
        final Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new LeonHBaseModule());
                new HBaseBinder(binder()).addTable(personTableName, "cf1", "cf2");
            }
        });
        HBaseUOWManager uowManager = i.getInstance(HBaseUOWManager.class);
        uowManager.begin(this);
        getTable(i, personTableName).getTableName();
        uowManager.commit();

        boolean gotException = false;
        try {
            getTable(i, personTableName).getTableName();
        } catch (NoActiveUnitOfWorkException e) {
            gotException = true;
        }
        Assert.assertTrue(gotException, "Usage without UOW must throw an NoActiveUnitOfWorkException.");
    }


}
