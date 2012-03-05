package io.leon.unitofwork;

import com.google.common.collect.Maps;
import com.google.inject.*;
import io.leon.guice.GuiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UOWManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Injector injector;

    private List<Binding<UOWListener>> listener;

    private ThreadLocal<Object> threadLocalContext = new ThreadLocal<Object>();

    private ThreadLocal<Map<Key<? extends UOWListener>, UOWListener>> threadLocalListener =
            new ThreadLocal<Map<Key<? extends UOWListener>, UOWListener>>() {
        @Override
        protected Map<Key<? extends UOWListener>, UOWListener> initialValue() {
            return Maps.newHashMap();
        }
    };

    @Inject
    public UOWManager(Injector injector) {
        this.injector = injector;
        listener = GuiceUtils.getByType(injector, UOWListener.class);
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
        if (threadLocalContext.get() != null) {
            throw new IllegalStateException("The unit of work was already started for this thread.");
        }

        logger.debug("Starting new unit of work for thread [{}] with context [{}].", Thread.currentThread(), context);

        // Use a dummy value in case that the user provided a null value
        context = context != null ? context : new Object();
        threadLocalContext.set(context);

        // Notify all listener and add them to the thread local listener list
        for (Binding<UOWListener> listenerBinding : listener) {
            Key<UOWListener> key = listenerBinding.getKey();
            UOWListener instance = injector.getInstance(key);

            // check for duplicates
            if (threadLocalListener.get().containsKey(key)) {
                throw new IllegalStateException("The key " + key + " is used twice. Should not happen, potential bug.");
            }
            instance.begin(threadLocalContext.get());
            threadLocalListener.get().put(key, instance);
        }
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

        // Notify all listener and clean up
        for (UOWListener l : threadLocalListener.get().values()) {
            l.commit(threadLocalContext.get());
        }
        threadLocalListener.remove();
        threadLocalContext.remove();
    }

    /**
     * Returns {@code true} if a unit of work was started for this thread.
     *
     * @return {@code true} if a unit of work was started for this thread
     */
    public boolean threadHasActiveUnitOfWork() {
        return threadLocalContext.get() != null;
    }

    /**
     * Returns the listener instance that is used in the current unit of work.
     *
     * @param key The {@code Key} used for the Guice binding.
     * @return The listener instance
     */
    public <T extends UOWListener> UOWListener getThreadLocalListenerByKey(Key<T> key) {
        if (!threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }
        return threadLocalListener.get().get(key);
    }

}
