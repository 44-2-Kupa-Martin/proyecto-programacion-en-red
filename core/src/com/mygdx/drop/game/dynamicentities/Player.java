package com.mygdx.drop.game.dynamicentities;

import java.net.IDN;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.SimpleContactEventFilter;
import com.mygdx.drop.etc.events.CanPickupEvent;
import com.mygdx.drop.etc.events.ClassifiedContactEvent;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.FreeSlotEvent;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.listeners.CanPickupEventListener;
import com.mygdx.drop.etc.events.listeners.ClickEventListener;
import com.mygdx.drop.etc.events.listeners.ContactEventListener;
import com.mygdx.drop.etc.events.listeners.EventListener;
import com.mygdx.drop.etc.events.listeners.FreeSlotEventListener;
import com.mygdx.drop.etc.events.listeners.InputEventListener;
import com.mygdx.drop.etc.events.listeners.PropertyChangeEventListener;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.Inventory;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.PlayerManager.FrameComponent;
import com.mygdx.drop.game.items.ArrowItem;
import com.mygdx.drop.game.items.BowItem;
import com.mygdx.drop.game.items.DebugItem;
import com.mygdx.drop.game.items.DiamondBoots;
import com.mygdx.drop.game.items.DiamondHelmet;
import com.mygdx.drop.game.items.PickaxeItem;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.MutableStats;
import com.mygdx.drop.game.Stats;
import com.mygdx.drop.game.Tile;

public class Player extends BoxEntity implements Drawable {
	private static boolean instantiated = false;

	public final String name;
	private State previousState;
	private State currentState;
	private float animationTimer;
	private float invincibilityTimer;
	/** A map from the player's state to a pair containing the asset id and animation object to draw */
	private EnumMap<State, SimpleImmutableEntry<Integer, Animation<AtlasRegion>>> animations;
	public final PlayerInventory items;
	public final Stats baseStats;
	private MutableStats stats;
	private int groundContacts;
	private long deathTime;
	private final Fixture groundSensor;
	private final Fixture hitRadius;
	private final float respawnTimer;
	private final Map<Integer, Runnable> keybinds;
	private final Vector2 spawnPosition;
	private boolean deathTimeSet;

	/**
	 * @param x Measured in meters
	 * @param y Measured in meters
	 */
	protected Player(World world, String name, float x, float y) {
		super(world, Drop.tlToMt(2), Drop.tlToMt(3), ((Supplier<BodyDef>) (() -> {
			BodyDef body = new BodyDef();
			body.position.set(x, Drop.tlToMt(3) / 2 + y);
			body.type = BodyType.DynamicBody;
			body.fixedRotation = true;
			return body;
		})).get(), ((Supplier<FixtureDef>) (() -> {
			FixtureDef fixture = new FixtureDef();
			fixture.density = 1;
			fixture.filter.categoryBits = Constants.Category.PLAYER.value;
			fixture.filter.maskBits = (short) (Constants.Category.PLAYER_COLLIDABLE.value | Constants.Category.SENSOR.value);
			return fixture;
		})).get());
		this.name = name;
		if (!instantiated)
			initializeClassListeners(world);

		FixtureDef sensor = new FixtureDef();
		sensor.isSensor = true;
		sensor.filter.maskBits = Constants.Category.ITEM.value;
		sensor.filter.categoryBits = Constants.Category.SENSOR.value;
		CircleShape pickupRange = new CircleShape();
		pickupRange.setRadius(1.2f);
		sensor.shape = pickupRange;
		self.createFixture(sensor);
		
		CircleShape hitRange = pickupRange;
		hitRange.setRadius(Drop.tlToMt(3));
		sensor.filter.maskBits = Constants.Category.WORLD.value;
		sensor.shape = hitRange;
		this.hitRadius = self.createFixture(sensor);
		hitRange.dispose();
		
		PolygonShape groundContact = new PolygonShape();
		groundContact.setAsBox(getWidth()/2 -0.02f, 0.04f, new Vector2(0, -getHeight()/2 -0.02f),  0);
		sensor.filter.maskBits = Constants.Category.WORLD.value;
		sensor.shape = groundContact;
		this.groundSensor = self.createFixture(sensor);
		groundContact.dispose();
		
		this.previousState = State.IDLE;
		this.currentState = State.IDLE;
		this.deathTimeSet = false;
		this.deathTime = 0;
		this.animationTimer = 0;
		this.respawnTimer = 10;
		this.invincibilityTimer = 0;
		this.spawnPosition = new Vector2(x, Drop.tlToMt(3) / 2 + y);
		this.animations = initAnimationsMap();
		this.baseStats = new Stats(100, 0, 0, 0.25f/*s*/, 0);
		this.stats = new MutableStats(baseStats);
		this.groundContacts = 0;
		this.items = new PlayerInventory();
		for (ObservableReference<Item> itemReference : items.inventory) {
			itemReference.set(new ArrowItem());
		}
		
		this.addListener(new ContactEventListener() {
			@Override
			public boolean beginContact(ContactEvent event) {
				boolean groundFixtureIsParticipant = event.getContact().getFixtureA().equals(groundSensor) || event.getContact().getFixtureB().equals(groundSensor);
				if (groundFixtureIsParticipant) {
					Player.this.groundContacts++;
				}
				return event.isHandled(); 
			}
			
			@Override
			public boolean endContact(ContactEvent event) {
				boolean groundFixtureIsParticipant = event.getContact().getFixtureA().equals(groundSensor) || event.getContact().getFixtureB().equals(groundSensor);
				if (groundFixtureIsParticipant) {
					Player.this.groundContacts--;;
				}
				return event.isHandled(); 
			}
		});		
		
		items.hotbar.get(0).set(new BowItem());
		items.hotbar.get(1).set(new DiamondHelmet());
		items.hotbar.get(2).set(new DiamondBoots());
		items.hotbar.get(3).set(new PickaxeItem());
		this.keybinds = new HashMap<>();
		keybinds.put(Input.Keys.W, this::jump);
		keybinds.put(Input.Keys.A, this::moveLeft);
		keybinds.put(Input.Keys.D, this::moveRight);
	}

	public final Stats getStats() { return stats; }
	
	public final void applyDamage(float lostHp) {
		assert lostHp >= 0;
		if (invincibilityTimer > 0)
			return;
		invincibilityTimer = 1;
		Assets.Sounds.playerHurt.get().play(game.masterVolume);
		stats.setHealth(stats.getHealth() - (lostHp - stats.getDefense()));
	}

	@Override
	public final boolean update() {
		boolean toBeDisposed = super.update();
		if (stats.isDead()) {
			if (!deathTimeSet) {
				deathTime = TimeUtils.millis();
				deathTimeSet = true;
				return toBeDisposed;
			}
			
			if (TimeUtils.timeSinceMillis(deathTime) / 1000f > respawnTimer) {
				stats.setHealth(baseStats.getMaxHealth());
				self.setTransform(spawnPosition, 0);
				self.setLinearVelocity(0,0);
				self.setAngularVelocity(0);
				tasks.clear();
				stats.setPoints(0);
				deathTimeSet = false;
			}
			return toBeDisposed;
		}
		
		if (this.invincibilityTimer > 0)
			invincibilityTimer -= Gdx.graphics.getDeltaTime();

		previousState = currentState;
		currentState = groundContacts > 0 ? State.IDLE : State.AIRBORNE;
		
		if (items.getItemOnHand() != null) {
			if (world.isButtonPressed(this, Buttons.LEFT)) {
				Vector2 clickPosition = world.getLastClickPosition(this);
				items.getItemOnHand().leftUse(this, clickPosition.x, clickPosition.y);
			} else if (world.isButtonPressed(this, Buttons.RIGHT)) {
				Vector2 clickPosition = world.getLastClickPosition(this);
				items.getItemOnHand().rightUse(this, clickPosition.x, clickPosition.y);
			}
		}
		
		for (Runnable task : tasks)
			task.run();
		
		if (currentState != previousState)
			animationTimer = 0;

		return toBeDisposed;
	}
	
	public final void addPoints(int points) {
		stats.setPoints(stats.getPoints() + points);
	}
	
	@Override
	public FrameComponent getFrameComponent() { 
		if (stats.isDead()) 
			return null;
		
		animationTimer += Gdx.graphics.getDeltaTime();
		Vector2 coords = getDrawingCoordinates();
		SimpleImmutableEntry<Integer, Animation<AtlasRegion>> currentAnimationIdPair = animations.get(currentState);
		int frameIndex = currentAnimationIdPair.getValue().getKeyFrameIndex(animationTimer);
		return new FrameComponent(currentAnimationIdPair.getKey(), coords.x, coords.y, getWidth(), getHeight(), 0, frameIndex); 
	}
	
	public final boolean canReach(float x, float y) {
		return hitRadius.testPoint(x, y);
	}

	private final EnumMap<State, SimpleImmutableEntry<Integer, Animation<AtlasRegion>>> initAnimationsMap() {
		EnumMap<State, SimpleImmutableEntry<Integer, Animation<AtlasRegion>>> animations = new EnumMap<>(State.class);
		animations.put(State.IDLE,
				new SimpleImmutableEntry<>(Assets.Animations.playerIdle.getId(), new Animation<AtlasRegion>(0.05f, Assets.Animations.playerIdle.get(), PlayMode.LOOP)));
		animations.put(State.WALKING,
				new SimpleImmutableEntry<>(Assets.Animations.playerIdle.getId(), new Animation<AtlasRegion>(0.05f, Assets.Animations.playerIdle.get(), PlayMode.LOOP)));
		animations.put(State.AIRBORNE,
				new SimpleImmutableEntry<>(Assets.Animations.playerWalking.getId(), new Animation<AtlasRegion>(0.05f, Assets.Animations.playerWalking.get(), PlayMode.LOOP)));
		return animations;
	}

	private final void dropItem() {
		if (items.getItemOnHand() == null)
			return;
		world.createEntity(new DroppedItem.Definition(getX(), getY(), items.getItemOnHand()));
		items.getItemOnHandReference().set(null);
	}

	private final void moveLeft() {
		self.applyLinearImpulse(new Vector2(-1, 0), self.getWorldCenter(), true);
		if (currentState != State.AIRBORNE)
			currentState = State.WALKING;
	}

	private final void moveRight() {
		self.applyLinearImpulse(new Vector2(1, 0), self.getWorldCenter(), true);
		if (currentState != State.AIRBORNE)
			currentState = State.WALKING;

	}

	private final void jump() {
		if (currentState != State.AIRBORNE) {
			self.applyLinearImpulse(new Vector2(0, 10), self.getWorldCenter(), true);
		}
	}

	private static final void initializeClassListeners(World world) {
		assert !instantiated : "Player.initializeClassListeners called after first instantiation";
		Player.instantiated = true;
		
		world.addListener(new ClickEventListener(Input.Buttons.RIGHT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (event.player.items.getCursorItem() != null)
					event.player.dropItem();
				System.out.println("Player clicked");
			}
		});

		world.addListener(new InputEventListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Runnable task = event.player.keybinds.get(keycode);
				if (task != null)
					event.player.addTask(task);
				for (int key : new int[]{Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5, Keys.NUM_6, Keys.NUM_7, Keys.NUM_8, Keys.NUM_9}) {
					if (keycode == key) {
						event.player.items.changeItemOnHand(key - Keys.NUM_1);
					}
				}
				if (keycode == Keys.Q) {
					event.player.dropItem();
				}
				return true;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				Runnable task = event.player.keybinds.get(keycode);
				if (task != null)
					event.player.removeTask(task);
				return true;
			}
		});
		/**
		 * These handlers are shared across all instances of this class. They fire for all events in the
		 * world and as such are fit for class wide handlers
		 */
		world.addListener(new SimpleContactEventFilter<Player>(Player.class) {
			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<Player, Entity> classifiedEvent) {
				EventManager.fire(classifiedEvent);
				return event.isHandled();
			}

			@Override

			public boolean endContact(ContactEvent event, ClassifiedContactEvent<Player, Entity> classifiedEvent) {
				EventManager.fire(classifiedEvent);
				return event.isHandled();
			}
		});
		/**
		 * NOTE: only a single instance of these handlers will exist, NEVER keep collision specific state.
		 * If said state is needed, make a map associating it with the contact object
		 */
		world.addListener(new ContactEventFilter<Player, DroppedItem>(Player.class, DroppedItem.class) {
			class State {
				CanPickupEventListener onPickupDelayEnd;
				FreeSlotEventListener onFreePlayerSlot;
			}

			HashMap<Integer, State> collisionState = new HashMap<>();

			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<Player, DroppedItem> classifiedEvent) {
				Player player = classifiedEvent.self;
				DroppedItem droppedItem = classifiedEvent.other;
				State state = new State();
				/**
				 * because free slot events are fired by a propertychange event listener, and because said listener
				 * is registered first when the free slot event ends the change event continues and the slots hear
				 * the first change event last. In order to fix this, a flag was added to all fire implementations
				 */
				state.onFreePlayerSlot = new FreeSlotEventListener() {
					public boolean onFreeSlot(FreeSlotEvent event) {
						event.putItemIntoSlot(droppedItem.item);
						droppedItem.dispose();
						player.removeListener(this);
						return true;
					};

				};

				state.onPickupDelayEnd = new CanPickupEventListener() {
					@Override
					public boolean onCanPickup(CanPickupEvent event) {
						boolean pickedUp = player.items.pickupItem(event.target.item);
						if (pickedUp) {
							event.handle();
						} else {
							player.addListener(state.onFreePlayerSlot);
						}
						event.target.removeListener(this);
						return event.isHandled();
					}

				};
				// TODO find an actual key
				collisionState.put(Objects.hash(classifiedEvent.self, classifiedEvent.other), state);
				if (droppedItem.canPickUp()) {
					boolean pickedUp = player.items.pickupItem(droppedItem.item);
					if (pickedUp) {
						droppedItem.dispose();
					} else {
						player.items.addListener(state.onFreePlayerSlot);
					}
					return event.isHandled();
				}
				droppedItem.addListener(state.onPickupDelayEnd);
				return false;
			}

			@Override
			public boolean endContact(ContactEvent event, ClassifiedContactEvent<Player, DroppedItem> classifiedEvent) {
				Player player = classifiedEvent.self;
				DroppedItem droppedItem = classifiedEvent.other;
				State state = collisionState.get(Objects.hash(classifiedEvent.self, classifiedEvent.other));
				player.items.removeListener(state.onFreePlayerSlot);
				droppedItem.removeListener(state.onPickupDelayEnd);
				// TODO find an actual key
				collisionState.remove(droppedItem.hashCode());
				return false;
			}

		});

		world.addListener(new ContactEventFilter<Player, TestEnemy>(Player.class, TestEnemy.class) {
			class State {
				Runnable task;
			}

			HashMap<Integer, State> collisionState = new HashMap<>();
			
			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<Player, TestEnemy> classfiedEvent) {
				if (classfiedEvent.otherFixture == classfiedEvent.other.getFixtures().get(0)) {
					State state = new State();
					state.task = () -> {
						classfiedEvent.self.applyDamage(classfiedEvent.other.damage);
					};
					classfiedEvent.self.addTask(state.task);
					collisionState.put(Objects.hash(classfiedEvent.self, classfiedEvent.other), state);					
				}
				return event.isHandled();
			}

			@Override
			public boolean endContact(ContactEvent event, ClassifiedContactEvent<Player, TestEnemy> classifiedEvent) {
				State state = collisionState.get(Objects.hash(classifiedEvent.self, classifiedEvent.other));
				if (state != null) {
					classifiedEvent.self.removeTask(state.task);
				}
				return event.isHandled();
			}

		});
	}

	/**
	 * The state of the player
	 */
	private enum State {
		IDLE,
		WALKING,
		AIRBORNE;
	}

	public class PlayerInventory extends Inventory {
		public static final int HOTBAR_SLOTS = 9;
		public static final int INVENTORY_SLOTS = HOTBAR_SLOTS + 9 * 3;
		public static final int ACCESSORY_SLOTS = 4;
		public static final int ARMOR_SLOTS = 4;

		public static final int CURSOR_ITEM = 0;
		public static final int HOTBAR_START = 1;
		public static final int HOTBAR_END = HOTBAR_START + HOTBAR_SLOTS;
		public static final int INVENTORY_START = 1;
		public static final int INVENTORY_END = INVENTORY_START + INVENTORY_SLOTS;
		public static final int ACCESSORY_START = INVENTORY_END;
		public static final int ACCESSORY_END = ACCESSORY_START + ACCESSORY_SLOTS;
		public static final int ARMOR_START = ACCESSORY_END;
		public static final int ARMOR_END = ARMOR_START + ARMOR_SLOTS;
		/** The hotbar counts as part of the inventory, hence it is not included in the calculation */
		public static final int N_ITEMS = INVENTORY_SLOTS + ACCESSORY_SLOTS + ARMOR_SLOTS + 1;

		public final List<ObservableReference<Item>> hotbar;
		public final List<ObservableReference<Item>> inventory;
		public final List<ObservableReference<EquippableItem>> armor;
		public final List<ObservableReference<EquippableItem>> accessory;

		private final EventListener equippableListener;
		private ObservableReference<Item> itemOnHand;
		private int selectedSlot;

		public PlayerInventory() {
			super(N_ITEMS);
			for (int i = 0; i < items.length; i++)
				items[i] = new ObservableReference<Item>((Item)null);

			this.hotbar = Arrays.asList(items).subList(HOTBAR_START, HOTBAR_END);
			this.inventory = Arrays.asList(items).subList(INVENTORY_START, INVENTORY_END);
			this.armor = (List<ObservableReference<EquippableItem>>)(List<?>)Arrays.asList(items).subList(ARMOR_START, ARMOR_END);
			this.accessory = (List<ObservableReference<EquippableItem>>)(List<?>)Arrays.asList(items).subList(ACCESSORY_START, ACCESSORY_END);
			this.itemOnHand = hotbar.get(0);
			this.selectedSlot = 0;
			this.equippableListener = new PropertyChangeEventListener<Item>(Item.class) {
				@Override
				public boolean onChange(Object target, Item oldValue, Item newValue) {
					if (oldValue instanceof EquippableItem && oldValue != null) 
						((EquippableItem)oldValue).unequip(Player.this.stats);
					if (newValue instanceof EquippableItem && newValue != null) 
						((EquippableItem)newValue).equip(Player.this.stats);
					return false; 
				}
			};

			for (int i = 0; i < inventory.size(); i++) {
				final int finalI = i;
				ObservableReference<Item> itemReference = inventory.get(i);
				itemReference.addListener(new PropertyChangeEventListener<Item>(Item.class) {
					@Override
					public boolean onChange(Object target, Item oldValue, Item newValue) {
						if (newValue == null) {
							FreeSlotEvent event = new FreeSlotEvent(PlayerInventory.this, finalI);
							EventManager.fire(event);
							return event.isHandled();
						}
						return false;
					}
				});
			}
			
			for (int i = 0; i < armor.size(); i++) 
				armor.get(i).addListener(equippableListener);
			
			itemOnHand.addListener(equippableListener);
		}

//		public final ObservableReference<Item> getItemReference(int index) { return heldItems[index]; }

		public final Item getCursorItem() { return items[CURSOR_ITEM].get(); }

		public final void setCursorItem(Item newItem) { items[CURSOR_ITEM].set(newItem); }

		public final ObservableReference<Item> getCursorItemReference() { return items[CURSOR_ITEM]; }

		public final int getSelectedSlot() { return selectedSlot; }

		// TODO Should this affect the cursor item?
		public final void changeItemOnHand(int index) {
			if (itemOnHand.get() instanceof EquippableItem) {
				((EquippableItem)itemOnHand.get()).unequip(stats);
			}
			this.itemOnHand.removeListener(equippableListener);
			this.itemOnHand = hotbar.get(index);
			this.itemOnHand.addListener(equippableListener);
			if (itemOnHand.get() instanceof EquippableItem) {
				((EquippableItem)itemOnHand.get()).equip(stats);
			}
			this.selectedSlot = index;
		}

		public final Item getItemOnHand() {
			return getCursorItemReference().get() == null ? itemOnHand.get() : getCursorItemReference().get();
		}

		public final ObservableReference<Item> getItemOnHandReference() {
			return getCursorItemReference().get() == null ? itemOnHand : getCursorItemReference();
		}

	
		@Override
		public final int findFreeInventorySlot() {
			for (int i = 0; i < inventory.size(); i++) {
				if (inventory.get(i).get() == null)
					return i;
			}
			return -1;
		}

		@Override
		public ObservableReference<Item> getItemReference(int index) {
			assert index < INVENTORY_END;
			return super.getItemReference(index); 
		}
		
		public final boolean canPickupItem() { return findFreeInventorySlot() != -1; }

		/**
		 * Tries to add an item to the inventory
		 * 
		 * @param item The item to pickup
		 * @return {@code true} if the inventory has picked up the item, {@code false} if it can't
		 */
		public final boolean pickupItem(Item item) {
			int slot = findFreeInventorySlot();
			if (slot == -1)
				return false;
			inventory.get(slot).set(item);
			return true;
		}

	}

	/**
	 * See {@link Entity.EntityDefinition}
	 */
	public static class Definition extends Entity.EntityDefinition<Player> {
		public String name;
		/**
		 * See {@link Player#Player(World, float, float)}
		 */
		public Definition(String name, float x, float y) { 
			super(x, y);
			this.name = name;
		}

		@Override
		protected Player createEntity(World world) { return new Player(world, name, x, y); }

	}

	
}
