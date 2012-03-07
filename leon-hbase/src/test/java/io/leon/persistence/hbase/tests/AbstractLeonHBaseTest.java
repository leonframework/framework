package io.leon.persistence.hbase.tests;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.leon.persistence.hbase.LeonHBaseTable;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

public class AbstractLeonHBaseTest {

    protected HBaseAdmin getAdmin(Injector injector) {
        return injector.getInstance(HBaseAdmin.class);
    }

    protected LeonHBaseTable getTable(Injector injector, String tableName) {
        return injector.getInstance(Key.get(LeonHBaseTable.class, Names.named(tableName)));
    }

    protected void deleteTable(Injector injector, String tableName) {
        try {
            if (getAdmin(injector).tableExists(tableName)) {
                getAdmin(injector).disableTable(tableName);
                getAdmin(injector).deleteTable(tableName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createTestTableName(String name) {
        String cn = getClass().getName().replace('.', '_');
        return "LeonUnitTest_" + name + "_" + cn;
    }


}
