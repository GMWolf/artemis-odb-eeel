[![Build Status](https://travis-ci.org/GMWolf/artemis-odb-eeel.svg?branch=master)](https://travis-ci.org/GMWolf/artemis-odb-eeel)
# artemis-odb-eeel
Easy Entity Event Listening

EEEL is a drop in plugin for [Artemis-odb](https://github.com/junkdog/artemis-odb)

EEEL provided an easy annotation based interface to subscribe to Entity events.

## Susbscribing to Aspects

The first step to using EEEL is to add the plugin to your world:
```java
WorldConfiguration config = new WorldConfigurationBuilder()
  .with(new EEELPlugin())
  ...
  .build();
world = new World(config);
```

Any system can then subscribe to entities.


Any system can subscribe to Aspects by using the `@Inserted` and `@Removed` annotations, along side the `@All`, `@One` and `@Exclude` annotations.
```java
class MySystem extends BaseSystem {
  
  @Inserted
  @All(Position.class)
  public void handlePositionEntityInsert(int entityId) {
    //do Something
  }
  
  @Inserted
  @All({life.class, damage.class})
  @Exclude(invurnerable.class)
  public void handleDamage(int entityId) {
    //do Something
  }
  
  @Removed
  @All(Position.class)
  public void handleRemoved(int entityId) {
    //do Something
  }
  
  @Inserted
  @Removed
  @All(TestComponent.class)
  public void addedOrRemoved(int entityId) {
    //do something
  }
 
  
  ...
}
```

As you can see, EEEL removes the need for event listeners, and thus makes complex systems much easier to manage.

## Annotations in other objects
You can register any other objects to listen to events using the EEELSystem.register function:
```java
world.getSystem(EEELSystem.class).register(MyListener);
```

## Using method registration
Its possible you may not want to use Annotations due to the cost associated with invoking methods.
This is why EEEL also provides a way to directly register methods (or lambdas) to be called.
This is also useful if you need to register eventListeners on the fly.

This is does easily with the `EEELSystem.inserted` and `EEELSystem.removed` methods.

```java
class MySystem extends BaseSystem {
    
    protected void initialize() {
        EEELSystem.inserted(Aspect.all(health.class, attack.class), this::handleAttack);
        EEELSystem.removed(Aspect.all(health.class), this::handleDead);
    }
    
    public handleAttack(int entity) {
        //do something
    }
    
    public handleDead(int entity) {
        //do something
    }
    
    ...
}
```

This example shows the use of Method references, but lambdas can be used in the same way:
```java
EEELSystem.inserted(Aspect.all(Position.class), entity -> {
   //do something
});
```

## Getting started

### Maven
```xml
<dependency>
	<groupId>net.fbridault.eeel</groupId>
	<artifactId>artemis-odb-eeel</artifactId>
	<version>1.1</version>
</dependency>
```

### Gradle
```gradle
dependencies { compile "net.fbridault.eeel:artemis-odb-eeel:1.1" }
```
