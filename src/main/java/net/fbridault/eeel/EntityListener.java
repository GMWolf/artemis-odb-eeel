package net.fbridault.eeel;

import com.artemis.utils.IntBag;

public interface EntityListener {
    void process(int entityId);

    EntityListener NONE = entityId -> {};

    default void process(IntBag entities) {
        for(int i = 0; i < entities.size(); i++) {
            process(entities.get(i));
        }
    }
}
