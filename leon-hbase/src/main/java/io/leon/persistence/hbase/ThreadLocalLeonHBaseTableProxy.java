package io.leon.persistence.hbase;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.leon.persistence.hbase.unitofwork.HBaseUOWManager;
import io.leon.persistence.hbase.unitofwork.exceptions.NoActiveUnitOfWorkException;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ThreadLocalLeonHBaseTableProxy implements InvocationHandler {

    public static LeonHBaseTable createProxy(Injector injector, HTablePool pool, String tableName) {
        return (LeonHBaseTable) Proxy.newProxyInstance(
                ThreadLocalLeonHBaseTableProxy.class.getClassLoader(),
                new Class[]{LeonHBaseTable.class},
                new ThreadLocalLeonHBaseTableProxy(injector, pool, tableName));
    }

    private final HTablePool pool;

    private final String tableName;

    private Injector injector;

    @Inject
    private HBaseUOWManager uowManager;

    private final ThreadLocal<LeonHBaseTable> threadLocal = new ThreadLocal<LeonHBaseTable>() {
        @Override
        protected LeonHBaseTable initialValue() {
            return createNewInstance();
        }

        @Override
        public void remove() {
            get().close();
            super.remove();
        }
    };

    public ThreadLocalLeonHBaseTableProxy(Injector injector, HTablePool pool, String tableName) {
        this.pool = pool;
        this.tableName = tableName;
        this.injector = injector;
        injector.injectMembers(this);
    }

    private LeonHBaseTable createNewInstance() {
        HTableInterface tableInterface = pool.getTable(tableName);
        LeonHBaseTableImpl leonHBaseTable = new LeonHBaseTableImpl(tableName, tableInterface);
        injector.injectMembers(leonHBaseTable);
        uowManager.getHBaseUOWTableCoordinator().addLeonHBaseTableUsage(leonHBaseTable);
        return leonHBaseTable;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Make sure that we are inside of a unit of work
        if (!uowManager.threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }

        // Delegate call
        return method.invoke(threadLocal.get(), args);
    }

}
