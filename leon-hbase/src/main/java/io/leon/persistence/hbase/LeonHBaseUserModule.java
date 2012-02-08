package io.leon.persistence.hbase;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public abstract class LeonHBaseUserModule extends AbstractModule {

    public void addTable(String tableName, String... columnFamilies) {
        bind(LeonHBaseTable.class)
                .annotatedWith(Names.named(tableName))
                .toProvider(new LeonHBaseTableProvider(tableName, columnFamilies)).in(Scopes.SINGLETON);
    }

}
