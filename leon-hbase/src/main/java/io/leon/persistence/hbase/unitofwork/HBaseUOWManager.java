package io.leon.persistence.hbase.unitofwork;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.leon.persistence.hbase.unitofwork.exceptions.NoActiveUnitOfWorkException;
import io.leon.persistence.hbase.unitofwork.exceptions.UnitOfWorkAlreadyActiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseUOWManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<HBaseUOWTableCoordinator> tableCoordinatorProvider;

    private ThreadLocal<Object> threadLocalContext = new ThreadLocal<Object>();

    private ThreadLocal<HBaseUOWTableCoordinator> threadLocalTableCoordinator =
            new ThreadLocal<HBaseUOWTableCoordinator>() {
                @Override
                protected HBaseUOWTableCoordinator initialValue() {
                    return tableCoordinatorProvider.get();
                }
            };

    @Inject
    public HBaseUOWManager(Provider<HBaseUOWTableCoordinator> tableCoordinatorProvider) {
        this.tableCoordinatorProvider = tableCoordinatorProvider;
    }

    /**
     * Start a unit of work (UOW) for this thread. If a UOW was already started for this thread,
     * an {@code IllegalStateException} will be thrown.
     *
     * @param context The object that should be associated with this UOW.
     *                Typical types are HttpServletRequest, ActionEvent, etc.
     *                Can be null.
     *
     * @throws IllegalStateException In case that a UOW was already started for this thread
     */
    public void begin(Object context) {
        // Check if the UOW was not started yet
        if (threadHasActiveUnitOfWork()) {
            throw new UnitOfWorkAlreadyActiveException();
        }

        logger.debug("Starting new unit of work for thread [{}] with context [{}].", Thread.currentThread(), context);

        if (context != null) {
            threadLocalContext.set(context);
        } else {
            threadLocalContext.set(System.currentTimeMillis());
        }
        threadLocalTableCoordinator.get().begin(threadLocalContext.get());
    }

    /**
     * Commit the unit of work (UOW) associated with this thread.
     *
     * @throws NoActiveUnitOfWorkException If no UOW was started for this thread
     */
    public void commit() {
        // Check if the UOW was started
        if (!threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }

        logger.debug("Commiting unit of work for thread [{}].", Thread.currentThread());

        threadLocalTableCoordinator.get().commit(threadLocalContext.get());
        threadLocalTableCoordinator.remove();
        threadLocalContext.remove();
    }

    /**
     * @return {@code true} if a unit of work was started for this thread
     */
    public boolean threadHasActiveUnitOfWork() {
        return threadLocalContext.get() != null;
    }

    /**
     * @return the {@code HBaseUOWTableCoordinator} associated with the current thread
     */
    public HBaseUOWTableCoordinator getHBaseUOWTableCoordinator() {
        // Check if the UOW was started
        if (!threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }

        return threadLocalTableCoordinator.get();
    }

}
