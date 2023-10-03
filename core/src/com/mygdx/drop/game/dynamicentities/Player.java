package com.mygdx.drop.game.dynamicentities;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets.SoundId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.SimpleContactEventFilter;
import com.mygdx.drop.etc.events.CanPickupEvent;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.FreeSlotEvent;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.handlers.CanPickupEventHandler;
import com.mygdx.drop.etc.events.handlers.ClickEventHandler;
import com.mygdx.drop.etc.events.handlers.FreeSlotEventHandler;
import com.mygdx.drop.etc.events.handlers.InputEventHandler;
import com.mygdx.drop.etc.events.handlers.PropertyChangeEventHandler;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.items.BowItem;
import com.mygdx.drop.game.items.DebugItem;
import com.mygdx.drop.game.Item;

public class Player extends BoxEntity implements Drawable {
	private static boolean instantiated = false;

	private State previousState;
	private State currentState;
	private float animationTimer;
	private float invincibilityTimer;
	private EnumMap<State, Animation<TextureRegion>> animations;
	public final Inventory items;
	private float maxHealth;
	private float health;
	private int groundContacts;
	private final Map<Integer, Runnable> keybinds;

	/**
	 * @param x Measured in meters
	 * @param y Measured in meters
	 */
	protected Player(World world, float x, float y) {
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
			fixture.filter.maskBits = Constants.Category.PLAYER_COLLIDABLE.value;
			return fixture;
		})).get());

		if (!instantiated)
			initializeClassListeners(world);

		FixtureDef sensor = new FixtureDef();
		sensor.isSensor = true;
		sensor.filter.maskBits = Constants.Category.ITEM.value;
		sensor.filter.categoryBits = Constants.Category.SENSOR.value;
		CircleShape pickupRange = new CircleShape();
		pickupRange.setRadius(1.5f);
		sensor.shape = pickupRange;
		self.createFixture(sensor);
		pickupRange.dispose();

		this.previousState = State.IDLE;
		this.currentState = State.IDLE;
		this.animationTimer = 0;
		this.invincibilityTimer = 0;
		this.animations = initAnimationsMap();
		this.maxHealth = 100;
		this.health = maxHealth;
		this.items = new Inventory();
		for (ObservableReference<Item> itemReference : items.inventory) {
			itemReference.set(new DebugItem());
		}
		items.hotbar.get(0).set(new BowItem(world, this));
		this.groundContacts = 0;
		this.keybinds = new HashMap<>();
		keybinds.put(Input.Keys.W, this::jump);
		keybinds.put(Input.Keys.A, this::moveLeft);
		keybinds.put(Input.Keys.D, this::moveRight);
	}

	public float getHealth() { return health; }

	@Override
	public final boolean update(Viewport viewport) {
		boolean toBeDisposed = super.update(viewport);
		if (this.invincibilityTimer > 0)
			invincibilityTimer -= Gdx.graphics.getDeltaTime();

		previousState = currentState;
		currentState = groundContacts > 0 ? State.IDLE : State.AIRBORNE;

		for (Runnable task : tasks)
			task.run();

		if (this.health <= 0)
			return true;

		// TODO change this to a click listener
		
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			dropItem();
		}
		
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Item item = items.getItemOnHand();
			if (item != null)
				item.use();
		}
		
		for (int key : new int[]{Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5, Keys.NUM_6, Keys.NUM_7, Keys.NUM_8, Keys.NUM_9}) {
			if (Gdx.input.isKeyJustPressed(key)) {
				items.changeItemOnHand(key - Keys.NUM_1);
			}
		}

		if (currentState != previousState)
			animationTimer = 0;

		return toBeDisposed;
	}

	@Override
	public final void draw(Viewport viewport) {
		animationTimer += Gdx.graphics.getDeltaTime();
		Vector2 coords = getDrawingCoordinates();
		Animation<TextureRegion> currentAnimation = animations.get(currentState);
		TextureRegion frame = currentAnimation.getKeyFrame(animationTimer);
		game.batch.draw(frame, coords.x, coords.y, getWidth(), getHeight());
	}

	public final void applyDamage(float lostHp) {
		assert lostHp >= 0;
		if (invincibilityTimer > 0)
			return;
		invincibilityTimer = 1;
		game.assets.get(SoundId.Player_hurt).play(game.masterVolume);
		this.health -= lostHp;
	}

	private final EnumMap<State, Animation<TextureRegion>> initAnimationsMap() {
		EnumMap<State, Animation<TextureRegion>> animations = new EnumMap<>(State.class);
		animations.put(State.IDLE,
				new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.AnimationId.Player_idle), PlayMode.LOOP));
		animations.put(State.WALKING,
				new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.AnimationId.Player_idle), PlayMode.LOOP));
		animations.put(State.AIRBORNE,
				new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.AnimationId.Player_walk), PlayMode.LOOP));
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

		world.addListener(new ClickEventHandler(Input.Buttons.RIGHT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (event.getTarget() != null)
					return;
				if (event.player.items.getCursorItem() != null)
					event.player.dropItem();
			}

		});

		world.addListener(new InputEventHandler() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Runnable task = event.player.keybinds.get(keycode);
				if (task != null)
					event.player.addTask(task);
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
			public boolean beginContact(ContactEvent event, Participants participants) {
				boolean entityBelongsToTheWorldCategory = (participants.fixtureB.getFilterData().categoryBits
						& Constants.Category.WORLD.value) != 0;
				if (entityBelongsToTheWorldCategory)
					participants.objectA.groundContacts++;
				return event.isHandled();
			}

			@Override
			public boolean endContact(ContactEvent event, ContactEventFilter<Player, Entity>.Participants participants) {
				boolean entityBelongsToTheWorldCategory = (participants.fixtureB.getFilterData().categoryBits
						& Constants.Category.WORLD.value) != 0;
				if (entityBelongsToTheWorldCategory)
					participants.objectA.groundContacts--;
				return event.isHandled();
			}

		});
		/**
		 * NOTE: only a single instance of these handlers will exist, NEVER keep collision specific state.
		 * If said state is needed, make a map associating it with the contact object
		 */
		world.addListener(new ContactEventFilter<Player, DroppedItem>(Player.class, DroppedItem.class) {
			class State {
				CanPickupEventHandler onPickupDelayEnd;
				FreeSlotEventHandler onFreePlayerSlot;

			}

			HashMap<Integer, State> collisionState = new HashMap<>();

			@Override
			public boolean beginContact(ContactEvent event, Participants participants) {
				Player player = participants.objectA;
				DroppedItem droppedItem = participants.objectB;
				State state = new State();
				/**
				 * because free slot events are fired by a propertychange event listener, and because said listener
				 * is registered first when the free slot event ends the change event continues and the slots hear
				 * the first change event last
				 */
				state.onFreePlayerSlot = new FreeSlotEventHandler() {
					public boolean onFreeSlot(FreeSlotEvent event) {
						event.putItemIntoSlot(droppedItem.item);
						droppedItem.dispose();
						player.removeListener(this);
						return true;
					};

				};

				state.onPickupDelayEnd = new CanPickupEventHandler() {
					@Override
					public boolean onCanPickup(CanPickupEvent event) {
						boolean pickedUp = player.items.pickupItem(event.droppedItem.item);
						if (pickedUp) {
							event.stop();
							event.droppedItem.dispose();
						} else {
							player.addListener(state.onFreePlayerSlot);
						}
						droppedItem.removeListener(this);
						return event.isHandled();
					}

				};
				// TODO find an actual key
				collisionState.put(droppedItem.hashCode(), state);
				if (droppedItem.canPickUp()) {
					boolean pickedUp = player.items.pickupItem(droppedItem.item);
					if (pickedUp) {
						droppedItem.dispose();
					} else {
						player.addListener(state.onFreePlayerSlot);
					}
					return event.isHandled();
				}
				droppedItem.addListener(state.onPickupDelayEnd);
				return false;
			}

			@Override
			public boolean endContact(ContactEvent event, Participants participants) {
				Player player = participants.objectA;
				DroppedItem droppedItem = participants.objectB;
				State state = collisionState.get(droppedItem.hashCode());
				player.removeListener(state.onFreePlayerSlot);
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
			public boolean beginContact(ContactEvent event, Participants participants) {
				State state = new State();
				state.task = () -> {
					participants.objectA.applyDamage(participants.objectB.damage);
				};
				participants.objectA.addTask(state.task);
				collisionState.put(Objects.hash(participants.objectA, participants.objectB), state);
				return event.isHandled();
			}

			@Override
			public boolean endContact(ContactEvent event, Participants participants) {
				State state = collisionState.get(Objects.hash(participants.objectA, participants.objectB));
				if (state != null) {
					participants.objectA.removeTask(state.task);
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

	public class Inventory {
		public static final int HOTBAR_SLOTS = 9;
		public static final int INVENTORY_SLOTS = HOTBAR_SLOTS + 9 * 3;
		public static final int ACCESSORY_SLOTS = 4;
		public static final int ARMOR_SLOTS = 4;

		private static final int HOTBAR_START = 0;
		private static final int HOTBAR_END = HOTBAR_START + HOTBAR_SLOTS;
		private static final int INVENTORY_START = 0;
		private static final int INVENTORY_END = INVENTORY_START + INVENTORY_SLOTS;
		private static final int ACCESSORY_START = INVENTORY_END;
		private static final int ACCESSORY_END = ACCESSORY_START + ACCESSORY_SLOTS;
		private static final int ARMOR_START = ACCESSORY_END;
		private static final int ARMOR_END = ARMOR_START + ARMOR_SLOTS;
		private static final int CURSOR_ITEM = ARMOR_END;
		/** The hotbar counts as part of the inventory, hence it is not included in the calculation */
		public static final int N_ITEMS = INVENTORY_SLOTS + ACCESSORY_SLOTS + ARMOR_SLOTS + 1;

		public final List<ObservableReference<Item>> hotbar;
		public final List<ObservableReference<Item>> inventory;
		public final List<ObservableReference<Item>> armor;
		public final List<ObservableReference<Item>> accessory;

		private final ObservableReference<Item>[] heldItems;
		private ObservableReference<Item> itemOnHand;
		private int selectedSlot;

		public Inventory() {
			@SuppressWarnings("unchecked")
			ObservableReference<Item>[] heldItems = new ObservableReference[N_ITEMS];
			this.heldItems = heldItems;

			for (int i = 0; i < heldItems.length; i++)
				heldItems[i] = new ObservableReference<Item>((Item) null);

			this.hotbar = Arrays.asList(heldItems).subList(HOTBAR_START, HOTBAR_END);
			this.inventory = Arrays.asList(heldItems).subList(INVENTORY_START, INVENTORY_END);
			this.armor = Arrays.asList(heldItems).subList(ARMOR_START, ARMOR_END);
			this.accessory = Arrays.asList(heldItems).subList(ACCESSORY_START, ACCESSORY_END);
			this.itemOnHand = hotbar.get(0);
			this.selectedSlot = 0;

			for (int i = 0; i < inventory.size(); i++) {
				final int finalI = i;
				ObservableReference<Item> itemReference = inventory.get(i);
				itemReference.addListener(new PropertyChangeEventHandler<Item>(Item.class) {
					@Override
					public boolean onChange(Object target, Item oldValue, Item newValue) {
						if (newValue == null) {
							FreeSlotEvent event = new FreeSlotEvent(Player.this, finalI);
							Player.this.fire(event);
							return event.isHandled();
						}
						return false;
					}

				});
			}
		}

		public final ObservableReference<Item> getItemReference(int index) { return heldItems[index]; }

		public final Item getCursorItem() { return heldItems[CURSOR_ITEM].get(); }

		public final void setCursorItem(Item newItem) { heldItems[CURSOR_ITEM].set(newItem); }

		public final ObservableReference<Item> getCursorItemReference() { return heldItems[CURSOR_ITEM]; }

		public final int getSelectedSlot() { return selectedSlot; }

		// TODO Should this affect the cursor item?
		public final void changeItemOnHand(int index) {
			this.itemOnHand = hotbar.get(index);
			this.selectedSlot = index;
		}

		public final Item getItemOnHand() {
			return getCursorItemReference().get() == null ? itemOnHand.get() : getCursorItemReference().get();
		}

		public final ObservableReference<Item> getItemOnHandReference() {
			return getCursorItemReference().get() == null ? itemOnHand : getCursorItemReference();
		}

		/**
		 * Finds the first free slot in the inventory
		 * 
		 * @return the index of the free slot or {@code -1} if there isn't
		 */
		public final int findFreeInventorySlot() {
			for (int i = 0; i < inventory.size(); i++) {
				if (inventory.get(i).get() == null)
					return i;
			}
			return -1;
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
		/**
		 * See {@link Player#Player(World, float, float)}
		 */
		public Definition(float x, float y) { super(x, y); }

		@Override
		protected Player createEntity(World world) { return new Player(world, x, y); }

	}

}
