package net.fbridault.eeel;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.EntitySubscription;
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
        registerEventListeners();
        setEnabled(false);
    }


    @Override
    protected void processSystem() {
    }

    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            Inserted inserted = method.getAnnotation(Inserted.class);
            Removed removed = method.getAnnotation(Removed.class);

            if (inserted == null && removed == null) {
                continue;
            }

            checkParameters(method);

            EntitySubscription subscription = methodGetSubscription(method);

            EntitySubscription.SubscriptionListener listener =
                    getEntitySubscriptionListener(object, method, inserted != null, removed != null);

            subscription.addSubscriptionListener(listener);

        }
    }

    private void registerEventListeners() {
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

    private EntitySubscription methodGetSubscription(Method m) {
        Aspect.Builder builder = methodGetAspect(m);
        return world.getAspectSubscriptionManager().get(builder);
    }

    private EntitySubscription.SubscriptionListener getEntitySubscriptionListener(final Object listener, final Method method, boolean insert, boolean remove) {

        EntityListener invoker = getInvoker(listener, method);

        EntityListener insertedListener = insert ? invoker : EntityListener.none;
        EntityListener removedListener = remove ? invoker : EntityListener.none;


        return new EntitySubscription.SubscriptionListener() {
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

    private EntityListener getInvoker(final Object listener, final  Method method) {
        return entityId -> {
            try {
                method.invoke(listener, entityId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }


    private interface EntityListener {
        void process(int entityId);

        EntityListener none = entityId -> {};

        default void process(IntBag entities) {
            for(int i = 0; i < entities.size(); i++) {
                process(entities.get(i));
            }
        }
    }



}
