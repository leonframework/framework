package io.leon.unitofwork;

import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UOWManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Injector injector;

    private List<Binding<UOWListener>> listener;

    private ThreadLocal<Object> threadLocalContext = new ThreadLocal<Object>();

    private ThreadLocal<Map<Class<? extends UOWListener>, UOWListener>> threadLocalListener =
            new ThreadLocal<Map<Class<? extends UOWListener>, UOWListener>>() {
        @Override
        protected Map<Class<? extends UOWListener>, UOWListener> initialValue() {
            return Maps.newHashMap();
        }
    };

    @Inject
    public UOWManager(Injector injector) {
        this.injector = injector;
        listener = injector.findBindingsByType(new TypeLiteral<UOWListener>() {});
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
            UOWListener instance = injector.getInstance(listenerBinding.getKey());
            // check for duplicates
            if (threadLocalListener.get().containsKey(instance.getClass())) {
                throw new IllegalStateException(
                        "The class [" + instance.getClass().getName() + "] is already in use as a UOWListener.");
            }
            instance.begin(threadLocalContext.get());
            threadLocalListener.get().put(instance.getClass(), instance);
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

    public <T extends UOWListener> T getThreadLocalListenerByType(Class<T> listenerType) {
        if (!threadHasActiveUnitOfWork()) {
            throw new NoActiveUnitOfWorkException();
        }
        return listenerType.cast(threadLocalListener.get().get(listenerType));
    }

}
