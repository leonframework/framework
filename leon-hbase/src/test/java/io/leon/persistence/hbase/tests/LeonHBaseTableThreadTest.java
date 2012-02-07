package io.leon.persistence.hbase.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.leon.persistence.hbase.LeonHBaseModule;
import io.leon.unitofwork.NoActiveUnitOfWorkException;
import io.leon.unitofwork.UOWManager;
import io.leon.unitofwork.UOWModule;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.junit.Assert;
import org.junit.Test;

public class LeonHBaseTableThreadTest extends AbstractLeonHBaseTest {

    @Test
    public void testThreadSeperation() {
        // Create a module for testing
        final Injector i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                install(new UOWModule());
                addTable("person", "cf1", "cf2");
            }
        });
        final UOWManager manager = i.getInstance(UOWManager.class);

        // Test behaviour in one thread
        manager.begin(this);
        HTableInterface person1 = getTable(i, "person").getHTableInterface();
        HTableInterface person2 = getTable(i, "person").getHTableInterface();
        Assert.assertSame("A thread should only have one HTable instance.", person1, person2);
        manager.commit();

        // Test behaviour with two threads
        final Thread[] threads = new Thread[2];
        final HTableInterface[] instances = new HTableInterface[2];
        threads[0] = new Thread() {
            @Override
            public void run() {
                manager.begin(this);

                // Remember the instance used in this thread
                instances[0] = getTable(i, "person").getHTableInterface();
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
                instances[1] = getTable(i, "person").getHTableInterface();
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
        Assert.assertNotSame("Two threads should have different HTable instances.", instances[0], instances[1]);
    }

    @Test(expected = NoActiveUnitOfWorkException.class)
    public void hbaseSupportCanNotBeUsedWithoutActiveUnitOfWork() throws InterruptedException {
        // Create a module for testing
        final Injector i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                addTable("person", "cf1", "cf2");
            }
        });
        getTable(i, "person").getTableName();
    }

    @Test
    public void hbaseSupportCanNotBeUsedAfterActiveUnitOfWorkCommit() throws InterruptedException {
        // Create a module for testing
        final Injector i = Guice.createInjector(new LeonHBaseModule() {
            @Override
            protected void configure() {
                super.configure();
                install(new UOWModule());
                addTable("person", "cf1", "cf2");
            }
        });
        UOWManager uowManager = i.getInstance(UOWManager.class);
        uowManager.begin(this);
        getTable(i, "person").getTableName();
        uowManager.commit();

        boolean gotException = false;
        try {
            getTable(i, "person").getTableName();
        } catch (NoActiveUnitOfWorkException e) {
            gotException = true;
        }
        Assert.assertTrue("Usage without UOW must throw an NoActiveUnitOfWorkException.", gotException);
    }


}
