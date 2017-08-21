package net.fbridault.eeel.annotation;

import com.artemis.Component;

import java.lang.annotation.*;

/**
 * Created by felix on 21/08/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Exclude {
    Class<? extends Component>[] value();

}
