package net.fbridault.eeel;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.EntitySubscription;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;

/**
 * Created by felix on 21/08/2017.
 */
public class EEELSystem extends BaseSystem {


    EEELAnnotationSystem annotationSystem;


    @Override
    protected void initialize() {
        registerSystems();
        setEnabled(false);
    }

    @Override
    protected void processSystem() {
    }

    public void inserted(Aspect.Builder aspect, EntityListener listener) {
        registerEntityListener(aspect, getSubscriptionListener(listener, EntityListener.NONE));
    }

    public void removed(Aspect.Builder aspect, EntityListener listener) {
        registerEntityListener(aspect, getSubscriptionListener(EntityListener.NONE, listener));
    }


    private void registerSystems() {
        annotationSystem.registerSystems();
    }

    void registerEntityListener(Aspect.Builder aspect, SubscriptionListener listener) {
        getSubscription(aspect).addSubscriptionListener(listener);
    }

    private EntitySubscription getSubscription(Aspect.Builder aspect) {
        return world.getAspectSubscriptionManager().get(aspect);
    }

    SubscriptionListener getSubscriptionListener(final EntityListener insertedListener, final EntityListener removedListener) {
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






}
