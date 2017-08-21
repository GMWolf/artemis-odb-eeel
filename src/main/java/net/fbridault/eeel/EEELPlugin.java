package net.fbridault.eeel;

import com.artemis.*;
import com.artemis.utils.IntBag;
import net.fbridault.eeel.annotation.*;

import java.lang.invoke.LambdaMetafactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by felix on 21/08/2017.
 */
public class EEELPlugin implements ArtemisPlugin {


    public void setup(WorldConfigurationBuilder worldConfigurationBuilder) {
        worldConfigurationBuilder.with(new EEELSystem());
    }

    private class EEELSystem extends BaseSystem {



        @Override
        protected void initialize() {
            registerEventListeners();
            setEnabled(false);
        }


        @Override
        protected void processSystem() {
        }


        private void registerEventListeners() {
            for(BaseSystem system : world.getSystems()) {
                for(Method method : system.getClass().getDeclaredMethods()) {
                    Inserted inserted = method.getAnnotation(Inserted.class);
                    Removed removed = method.getAnnotation(Removed.class);

                    if (inserted == null && removed == null) {
                        continue;
                    }

                    checkParameters(method);

                    EntitySubscription subscription = methodGetSubscription(method);

                    EntitySubscription.SubscriptionListener listener =
                            getEntitySubscriptionListener(system, method, inserted != null, removed != null);

                    subscription.addSubscriptionListener(listener);

                }
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

        private EntitySubscription methodGetSubscription(Method m) {
            Aspect.Builder builder = methodGetAspect(m);
            return world.getAspectSubscriptionManager().get(builder);
        }

        private EntitySubscription.SubscriptionListener getEntitySubscriptionListener(final BaseSystem system, final Method method, final boolean insert, final boolean remove) {
            if (insert) {
                if (remove) {
                    return new EntitySubscription.SubscriptionListener() {

                        public void inserted(IntBag intBag) {
                            try {
                                for (int i = 0;  i < intBag.size(); i++) {
                                    method.invoke(system, intBag.get(i));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        }

                        public void removed(IntBag intBag) {
                            try {
                                for (int i = 0;  i < intBag.size(); i++) {
                                    method.invoke(system, intBag.get(i));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                } else {
                    return new EntitySubscription.SubscriptionListener() {

                        public void inserted(IntBag intBag) {
                            try {
                                for (int i = 0;  i < intBag.size(); i++) {
                                    method.invoke(system, intBag.get(i));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        }

                        public void removed(IntBag intBag) {
                        }
                    };
                }
            } else {
                if (remove) {
                    return new EntitySubscription.SubscriptionListener() {

                        public void inserted(IntBag intBag) {
                        }


                        public void removed(IntBag intBag) {
                            try {
                                for (int i = 0;  i < intBag.size(); i++) {
                                    method.invoke(system, intBag.get(i));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                } else {
                    throw new IllegalStateException("Cannot have no cases!");
                }
            }
        }



    }
}
