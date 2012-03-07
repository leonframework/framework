package io.leon.persistence.hbase.unitofwork;

import io.leon.persistence.hbase.LeonHBaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HBaseUOWTableCoordinator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Queue<LeonHBaseTable> usedTables = new ConcurrentLinkedQueue<LeonHBaseTable>();

    public void begin(Object o) {
    }

    public void commit(Object o) {
        logger.debug("Closing all tables used in this thread.");
        for (LeonHBaseTable leonHBaseTable : usedTables) {
            logger.debug("Closing [{}].", leonHBaseTable);
            leonHBaseTable.close();
        }
    }

    public void addLeonHBaseTableUsage(LeonHBaseTable leonHBaseTable) {
        logger.debug("Adding [{}] to the list of tables used in this thread [{}]", leonHBaseTable, Thread.currentThread());
        usedTables.add(leonHBaseTable);
    }
}
