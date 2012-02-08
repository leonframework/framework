package io.leon.persistence.hbase;

import com.google.inject.AbstractModule;
import io.leon.guice.GuiceUtils;
import io.leon.unitofwork.UOWListener;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;

public class LeonHBaseFeatureModule extends AbstractModule {

    private final Configuration configuration;

    public LeonHBaseFeatureModule() {
        this(HBaseConfiguration.create());
    }

    public LeonHBaseFeatureModule(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        try {
            bind(Configuration.class).toInstance(configuration);
            bind(HBaseAdmin.class).toInstance(new HBaseAdmin(configuration));
            bind(HTablePool.class).toInstance(new HTablePool(configuration, Integer.MAX_VALUE));
            GuiceUtils.bindClassWithName(binder(), UOWListener.class, HBaseUOWListener.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
