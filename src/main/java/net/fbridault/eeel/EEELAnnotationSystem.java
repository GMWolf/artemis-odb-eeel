package net.fbridault.eeel;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.EntitySubscription;
import net.fbridault.eeel.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EEELAnnotationSystem extends BaseSystem {


    EEELSystem eeel;

    @Override
    protected void initialize() {

    }

    void registerSystems() {
        for(BaseSystem system : world.getSystems()) {
            register(system);
        }
    }

    void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            Inserted inserted = method.getAnnotation(Inserted.class);
            Removed removed = method.getAnnotation(Removed.class);

            if (inserted == null && removed == null) {
                continue;
            }

            checkParameters(method);

            boolean onInsert = inserted != null;
            boolean onRemove = removed != null;

            EntitySubscription.SubscriptionListener listener = getInvokeSubscriptionListener(object, method, onInsert, onRemove);

            eeel.registerEntityListener(methodGetAspect(method), listener);

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

    private EntitySubscription.SubscriptionListener getInvokeSubscriptionListener(Object listener, Method method, boolean insert, boolean remove) {

        EntityListener invoker = getInvoker(listener, method);

        EntityListener insertedListener = insert ? invoker : EntityListener.NONE;
        EntityListener removedListener = remove ? invoker : EntityListener.NONE;

        return eeel.getSubscriptionListener(insertedListener, removedListener);
    }

    private EntityListener getInvoker(final Object listener, final Method method) {
        return entityId -> {
            try {
                method.invoke(listener, entityId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    protected void processSystem() {

    }
}
