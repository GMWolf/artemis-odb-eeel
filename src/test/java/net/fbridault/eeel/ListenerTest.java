package net.fbridault.eeel;

import com.artemis.*;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Inserted;
import net.fbridault.eeel.annotation.Removed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by felix on 21/08/2017.
 */
public class ListenerTest {

    private ComponentMapper<ComponentA> componentAMapper;
    private ComponentMapper<ComponentB> componentBMapper;
    private ComponentMapper<Confirmation> confirmationMapper;
    World world;

    @Before
    public void setup() {
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EEELPlugin())
                .with(new TestSystem())
                .build();

        world = new World(config);
        world.inject(this);
    }

    @Test
    public void testSubscriptionsA() {
        int e = world.create();
        componentAMapper.create(e);
        world.process();
        assertTrue(confirmationMapper.has(e));
    }

    @Test
    public void testSubscriptionsAB() {
        int eA = world.create();

        int eB = world.create();
        componentBMapper.create(eB);

        int eAB = world.create();
        componentAMapper.create(eAB);
        componentBMapper.create(eAB);

        world.process();

        assertFalse(confirmationMapper.has(eA));
        assertFalse(confirmationMapper.has(eB));
        assertTrue(confirmationMapper.has(eAB));

    }

    @Test
    public void removed() {
        int e = world.create();
        componentAMapper.create(e);
        world.process();
        assertTrue(confirmationMapper.has(e));
        componentBMapper.create(e);
        world.process();
        assertTrue(confirmationMapper.has(e));
        componentAMapper.remove(e);
        world.process();
        assertFalse(confirmationMapper.has(e));
    }

    static class TestSystem extends BaseSystem{

        private ComponentMapper<Confirmation> confirmationMapper;

        @Inserted
        @All(ComponentA.class)
        public void AInserted(int entityId) {
            confirmationMapper.set(entityId, true);
        }

        @Inserted
        @All({ComponentA.class, ComponentB.class})
        public void ABInserted(int entityId) {

            confirmationMapper.set(entityId, true);
        }

        @Removed
        @All({ComponentA.class, ComponentB.class})
        public void ABRemoved(int entityId) {
            confirmationMapper.remove(entityId);
        }

        protected void processSystem() {

        }
    }
}
