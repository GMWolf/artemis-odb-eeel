package net.fbridault.eeel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.artemis.Aspect;
import com.artemis.BaseSystem;

import net.fbridault.eeel.util.Pair;

public class EEELEventSystem extends BaseSystem {


    private final Map<Class<?>, List<Pair<EventListener, Aspect>>> listenerMap = new HashMap<>();

    @Override
    protected void initialize() {
    }

    public <T> void registerEvent(EventListener<T> listener, Class<?> eventType,  Aspect.Builder aspect) {

        System.out.println(eventType);

        if (!listenerMap.containsKey(eventType)) {
            listenerMap.put(eventType, new ArrayList<>());
        }

         listenerMap.get(eventType).add(new Pair<>(listener, aspect.build(world)));
    }

    public void dispatchEvent(Object event, int entity) {

        List<Pair<EventListener, Aspect>> listeners = listenerMap.get(event.getClass());
        if (listeners != null) {

            for(Pair<EventListener, Aspect> listenerPair : listeners) {
                if (listenerPair.getValue().isInterested(world.getEntity(entity))) {

                    listenerPair.getKey().process(event, entity);

                }
            }

        }

    }

    @Override
    protected void processSystem() {

    }
}
