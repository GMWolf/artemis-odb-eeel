package net.fbridault.eeel;

import com.artemis.*;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Inserted;
import net.fbridault.eeel.annotation.Removed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by felix on 21/08/2017.
 */
@RunWith(Parameterized.class)
public class ListenerTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection listeners() {
        return Arrays.asList(new Object[][] {
                {
                    "Annotations",
                        new AnnotationTestSystem()
                },
                {
                    "System Registration",
                        new SystemRegistrationTestSystem()
                },
                {
                    "Object Registration",
                        new TestObjectRegistrationSystem()
                }
        });
    }

    private ComponentMapper<ComponentA> componentAMapper;
    private ComponentMapper<ComponentB> componentBMapper;
    private ComponentMapper<Confirmation> confirmationMapper;
    World world;

    private BaseSystem testSystem;

    public ListenerTest(String paramName, BaseSystem listenerSystem) {
        testSystem = listenerSystem;
    }

    @Before
    public void setup() {
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EEELPlugin())
                .with(testSystem)
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
    public void removedTest() {
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

    @Test
    public void multiEventTest() {
        int e1 = world.create();
        componentAMapper.create(e1);
        int e2 = world.create();
        componentAMapper.create(e2);
        int e3 = world.create();
        componentAMapper.create(e3);

        assertFalse(confirmationMapper.has(e1));
        assertFalse(confirmationMapper.has(e2));
        assertFalse(confirmationMapper.has(e3));

        world.process();

        assertTrue(confirmationMapper.has(e1));
        assertTrue(confirmationMapper.has(e2));
        assertTrue(confirmationMapper.has(e3));
    }

    static class AnnotationTestSystem extends BaseSystem{

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

    static class SystemRegistrationTestSystem extends BaseSystem {

        EEELSystem EEELSystem;
        private ComponentMapper<Confirmation> confirmationMapper;

        @Override
        protected void initialize() {
            EEELSystem.inserted(Aspect.all(ComponentA.class), this::AInserted);
            EEELSystem.inserted(Aspect.all(ComponentA.class, ComponentB.class), this::ABInserted);
            EEELSystem.removed(Aspect.all(ComponentA.class, ComponentB.class), this::ABRemoved);
        }

        public void AInserted(int entityId) {
            confirmationMapper.set(entityId, true);
        }


        public void ABInserted(int entityId) {

            confirmationMapper.set(entityId, true);
        }

        public void ABRemoved(int entityId) {
            confirmationMapper.remove(entityId);
        }


        @Override
        protected void processSystem() {
        }
    }

    static class TestObjectRegistrationSystem extends BaseSystem {

        EEELSystem eeelSystem;
        @Override
        protected void initialize() {
            TestObject test = new TestObject();
            world.inject(test);
            eeelSystem.register(test);
        }

        @Override
        protected void processSystem() {

        }
    }

    static class TestObject {
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

    }


}
