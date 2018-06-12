package net.fbridault.eeel;

import com.artemis.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventTest {

    EEELEventSystem eventSystem;

    private ComponentMapper<ComponentA> componentAMapper;
    private ComponentMapper<ComponentB> componentBMapper;

    int testValue = 0;
    @Test
    public void TestSimpleEvent() {

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EEELPlugin())
                .build();

        World world = new World(config);
        world.inject(this);


        eventSystem.registerEvent(this::HandleSimpleEvent, SimpleEvent.class, Aspect.all(ComponentA.class));

        testValue = 50;

        int e = world.create();

        eventSystem.dispatchEvent(new SimpleEvent(10), e);

        assertEquals(50, testValue);

        componentBMapper.create(e);

        eventSystem.dispatchEvent(new SimpleEvent(5), e);

        assertEquals(50, testValue);

        componentAMapper.create(e);
        eventSystem.dispatchEvent(new SimpleEvent(70), e);

        assertEquals(70, testValue);

    }


    public void HandleSimpleEvent(SimpleEvent event, int e) {
        testValue = event.i;
    }


    class SimpleEvent {
        int i;

        public SimpleEvent(int i) {
         this.i = i;
        }
    }


}
