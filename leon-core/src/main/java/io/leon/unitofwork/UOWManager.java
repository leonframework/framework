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
        listener = GuiceUtils.getAllBindingsForType(injector, UOWListener.class);
    }

    public void begin(Object context) {
        // Check if the UOW was not started yet
        if (threadLocalContext.get() != null) {
            throw new IllegalStateException("The unit of work was already started for this thread.");
        }

        logger.info("Starting new unit of work for thread [{}] with context [{}].", Thread.currentThread(), context);

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

    public void commit() {
        // Check if the UOW was started
        if (threadLocalContext.get() == null) {
            throw new NoActiveUnitOfWorkException();
        }

        logger.info("Commiting unit of work for thread [{}].", Thread.currentThread());

        // Notify all listener and clean up
        for (UOWListener l : threadLocalListener.get().values()) {
            l.commit(threadLocalContext.get());
        }
        threadLocalListener.remove();
        threadLocalContext.remove();
    }

    public boolean threadHasActiveUnitOfWork() {
        return threadLocalContext.get() != null;
    }

    public <T extends UOWListener> UOWListener getThreadLocalListenerByKey(Key<T> key) {
        if (!threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }
        return threadLocalListener.get().get(key);
    }

}
