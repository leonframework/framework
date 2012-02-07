package io.leon.persistence.hbase;

import org.apache.hadoop.hbase.client.HTableInterface;

public interface LeonHBaseTable {

    void delete();

    void close();

    String getTableName();

    HTableInterface getHTableInterface();

    void flushCommits();

}
