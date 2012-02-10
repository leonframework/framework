package io.leon.persistence.hbase;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class HBaseBinder {

    private final Binder binder;

    public HBaseBinder(Binder binder) {
        this.binder = binder;
    }

    public void addTable(String tableName, String... columnFamilies) {
        binder.bind(LeonHBaseTable.class)
                .annotatedWith(Names.named(tableName))
                .toProvider(new LeonHBaseTableProvider(tableName, columnFamilies)).in(Scopes.SINGLETON);
    }

}
