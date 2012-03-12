package io.leon.persistence.hbase;

import com.google.inject.Scopes;
import io.leon.LeonModule;
import io.leon.persistence.hbase.unitofwork.HBaseUOWManager;
import io.leon.persistence.hbase.unitofwork.HBaseUOWTableCoordinator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;

public class LeonHBaseModule extends LeonModule {

    private final Configuration configuration;

    public LeonHBaseModule() {
        this(HBaseConfiguration.create());
    }

    public LeonHBaseModule(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void config() {
        try {
            bind(HBaseUOWManager.class).in(Scopes.SINGLETON);
            bind(HBaseUOWTableCoordinator.class);

            bind(Configuration.class).toInstance(configuration);
            bind(HBaseAdmin.class).toInstance(new HBaseAdmin(configuration));
            bind(HTablePool.class).toInstance(new HTablePool(configuration, Integer.MAX_VALUE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
