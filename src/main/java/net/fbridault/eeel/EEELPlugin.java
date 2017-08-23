package net.fbridault.eeel;

import com.artemis.*;

/**
 * Created by felix on 21/08/2017.
 */
public class EEELPlugin implements ArtemisPlugin {

    public void setup(WorldConfigurationBuilder worldConfigurationBuilder) {
        worldConfigurationBuilder.with(new EEELSystem());
        worldConfigurationBuilder.with(new EEELAnnotationSystem());
    }

}
