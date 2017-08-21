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
