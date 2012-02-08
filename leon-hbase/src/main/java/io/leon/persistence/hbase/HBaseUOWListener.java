package io.leon.persistence.hbase;

import io.leon.unitofwork.UOWListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HBaseUOWListener implements UOWListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Queue<LeonHBaseTable> usedTables = new ConcurrentLinkedQueue<LeonHBaseTable>();

    @Override
    public void begin(Object o) {
    }

    @Override
    public void commit(Object o) {
        logger.info("Closing all tables used in this thread.");
        for (LeonHBaseTable leonHBaseTable : usedTables) {
            logger.info("Closing [{}].", leonHBaseTable);
            leonHBaseTable.close();
        }
    }

    public void addLeonHBaseTableUsage(LeonHBaseTable leonHBaseTable) {
        logger.info("Adding [{}] to the list of tables used in this thread [{}]", leonHBaseTable, Thread.currentThread());
        usedTables.add(leonHBaseTable);
    }
}
