package net.fbridault.eeel;

import com.artemis.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventTest {
    private ComponentMapper<ComponentA> componentAMapper;
    private ComponentMapper<ComponentB> componentBMapper;

    int testValue = 0;
    EEELEventSystem eeelEventSystem;

    public class TestSystem extends BaseSystem{
        EEELEventSystem eventSystem;

        @Override
        protected void initialize() {
            eventSystem.registerEvent(this::onEvent, SimpleEvent.class, Aspect.all(ComponentA.class, ComponentB.class));
        }

        @Override
        protected void processSystem() {
        }


        void onEvent(SimpleEvent e, int entity) {
            testValue = e.i;
        }
    }

    @Test
    public void TestSimpleEvent() {

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EEELPlugin())
                .with(new TestSystem())
                .build();

        World world = new World(config);
        world.inject(this);

        testValue = 50;

        int e = world.create();

        eeelEventSystem.dispatchEvent(new SimpleEvent(10), e);

        assertEquals(50, testValue);

        componentBMapper.create(e);

        eeelEventSystem.dispatchEvent(new SimpleEvent(5), e);

        assertEquals(50, testValue);

        componentAMapper.create(e);

        eeelEventSystem.dispatchEvent(new SimpleEvent(70), e);

        assertEquals(70, testValue);
    }


    class SimpleEvent {
        int i;

        public SimpleEvent(int i) {
         this.i = i;
        }
    }


}
