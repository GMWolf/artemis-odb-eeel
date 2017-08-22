package net.fbridault.eeel;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.EntitySubscription;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;
import net.fbridault.eeel.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by felix on 21/08/2017.
 */
public class EEELSystem extends BaseSystem {


    @Override
    protected void initialize() {
        registerSystems();
        setEnabled(false);
    }


    @Override
    protected void processSystem() {
    }

    public void inserted(Aspect.Builder aspect, EntityListener listener) {
        registerListener(aspect, getSubscriptionListener(listener, EntityListener.NONE));
    }

    public void removed(Aspect.Builder aspect, EntityListener listener) {
        registerListener(aspect, getSubscriptionListener(EntityListener.NONE, listener));
    }

    private void registerListener(Aspect.Builder aspect, SubscriptionListener listener) {
        getSubscription(aspect).addSubscriptionListener(listener);
    }

    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            Inserted inserted = method.getAnnotation(Inserted.class);
            Removed removed = method.getAnnotation(Removed.class);

            if (inserted == null && removed == null) {
                continue;
            }

            checkParameters(method);

            boolean onInsert = inserted != null;
            boolean onRemove = removed != null;

            SubscriptionListener listener = getInvokeSubscriptionListener(object, method, onInsert, onRemove);

            registerListener(methodGetAspect(method), listener);

        }
    }

    private void registerSystems() {
        for (BaseSystem system : world.getSystems()) {
            register(system);
        }
    }

    private void checkParameters(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length != 1 || !parameters[0].equals(int.class)) {
            throw new IllegalStateException("EEEL Methods must have a single int entityId parameter.");
        }
    }

    private Aspect.Builder methodGetAspect(Method m) {
        All all = m.getAnnotation(All.class);
        One one = m.getAnnotation(One.class);
        Exclude exclude = m.getAnnotation(Exclude.class);

        Aspect.Builder builder = Aspect.all();

        if (all != null) {
            builder.all(all.value());
        }
        if (one != null) {
            builder.one(one.value());
        }
        if (exclude != null) {
            builder.exclude(exclude.value());
        }

        return builder;
    }

    private EntitySubscription getSubscription(Aspect.Builder aspect) {
        return world.getAspectSubscriptionManager().get(aspect);
    }

    private SubscriptionListener getSubscriptionListener(final EntityListener insertedListener, final EntityListener removedListener) {
        if (insertedListener == null || removedListener == null) {
            throw new IllegalArgumentException("Listeners cannot be null. Use EntityListener.NONE instead");
        }


        return new SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                insertedListener.process(entities);
            }
            @Override
            public void removed(IntBag entities) {
                removedListener.process(entities);
            }
        };
    }

    private SubscriptionListener getInvokeSubscriptionListener(Object listener, Method method, boolean insert, boolean remove) {

        EntityListener invoker = getInvoker(listener, method);

        EntityListener insertedListener = insert ? invoker : EntityListener.NONE;
        EntityListener removedListener = remove ? invoker : EntityListener.NONE;

        return getSubscriptionListener(insertedListener, removedListener);
    }

    private EntityListener getInvoker(final Object listener, final  Method method) {
        return entityId -> {
            try {
                method.invoke(listener, entityId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }


    public interface EntityListener {
        void process(int entityId);

        EntityListener NONE = entityId -> {};

        default void process(IntBag entities) {
            for(int i = 0; i < entities.size(); i++) {
                process(entities.get(i));
            }
        }
    }

}
