package io.leon.persistence.hbase;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LeonHBaseTableProvider implements Provider<LeonHBaseTable> {

    private final String tableName;

    private final String[] columnFamilies;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Injector injector;

    @Inject
    private Configuration configuration;

    @Inject
    private HBaseAdmin hBaseAdmin;

    @Inject
    private HTablePool hTablePool;

    public LeonHBaseTableProvider(String tableName, String... columnFamilies) {
        this.tableName = tableName;
        this.columnFamilies = columnFamilies;
    }

    private void createTableIfNecessary() {
        try {
            boolean exists = hBaseAdmin.tableExists(tableName);
            if (exists) {
                logger.debug("HBase table [{}] exists. Checking column families {}.", tableName, columnFamilies);
                checkColumnFamiliesForExistingTable(tableName, columnFamilies);
            } else {
                logger.info("Creating HBase table [{}] with column families {}.", tableName, columnFamilies);
                HTableDescriptor d = new HTableDescriptor(tableName);
                List<HColumnDescriptor> cd = Lists.transform(
                        Arrays.asList(columnFamilies),
                        new Function<String, HColumnDescriptor>() {
                            @Override
                            public HColumnDescriptor apply(@Nullable String input) {
                                return new HColumnDescriptor(input);
                            }
                        });
                for (HColumnDescriptor i : cd) {
                    d.addFamily(i);
                }
                hBaseAdmin.createTable(d);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkColumnFamiliesForExistingTable(String tableName, String[] columnFamilies) {
        HTableDescriptor descriptor;
        try {
            descriptor = hBaseAdmin.getTableDescriptor(tableName.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Convert Set<byte[]> to List<String> for convenience
        List<String> currentCf = Lists.transform(
                Lists.newLinkedList(descriptor.getFamiliesKeys()),
                new Function<byte[], String>() {
                    @Override
                    public String apply(@Nullable byte[] input) {
                        return new String(input);
                    }
                });

        // Disable/enable table if required and add missing column families
        try {
            if (!currentCf.containsAll(Arrays.asList(columnFamilies))) {
                hBaseAdmin.disableTable(tableName);
                for (String requiredCf : columnFamilies) {
                    if (!currentCf.contains(requiredCf)) {
                        logger.info("Adding column family [{}] to table [{}].", requiredCf, tableName);
                        hBaseAdmin.addColumn(tableName, new HColumnDescriptor(requiredCf));
                    }
                }
                hBaseAdmin.enableTable(tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LeonHBaseTable get() {
        createTableIfNecessary();
        return ThreadLocalLeonHBaseTableProxy.createProxy(injector, hTablePool, tableName);
    }
}
