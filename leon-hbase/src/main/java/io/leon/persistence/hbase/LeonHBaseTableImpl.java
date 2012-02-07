package io.leon.persistence.hbase;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LeonHBaseTableImpl implements LeonHBaseTable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String tableName;

    private final HTableInterface table;

    @Inject
    private HBaseAdmin admin;

    @Inject
    private Gson gson;

    public LeonHBaseTableImpl(String tableName, HTableInterface table) {
        this.tableName = tableName;
        this.table = table;
    }

    @Override
    public HTableInterface getHTableInterface() {
        return table;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void delete() {
        try {
            logger.info("Deleting table [{}].", tableName);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            table.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flushCommits() {
        try {
            table.flushCommits();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
