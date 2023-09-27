package com.mygdx.drop.game.dynamicentities;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets.SoundId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.handlers.ContactEventHandler;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.items.BowItem;
import com.mygdx.drop.game.items.DebugItem;
import com.mygdx.drop.game.Entity.EntityDefinition;
import com.mygdx.drop.game.Item;

public class Player extends BoxEntity implements Drawable {
	private static boolean instantiated = false;
	private State previousState;
	private State currentState;
	private float animationTimer;
	private float invincibilityTimer;
	private EnumMap<State, Animation<TextureRegion>> animations;
	private ObservableReference<Item>[] heldItems;
	private ObservableReference<Item> itemOnHand;
	public List<ObservableReference<Item>> hotbar;
	public List<ObservableReference<Item>> inventory;
	public List<ObservableReference<Item>> armor;
	public List<ObservableReference<Item>> accessory;
	private float maxHealth;
	public float health;
	// TODO REMOVE
	private TestEnemy enemy;

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
		FixtureDef sensor = new FixtureDef();
		sensor.isSensor = true;
		sensor.filter.maskBits = Constants.Category.ITEM.value;
		sensor.filter.categoryBits = Constants.Category.SENSOR.value;
		CircleShape pickupRange = new CircleShape();
		pickupRange.setRadius(1f);
		sensor.shape = pickupRange;
		self.createFixture(sensor);
		pickupRange.dispose();
		
		if (!instantiated) {
			instantiated = true;
			world.addHandler(new ContactEventHandler() {
				@Override
				public boolean beginContact(World world, Contact contact) {
					//TODO: prevent item duplication when two pickups occur at the same time
					Object ownerFixtureA = contact.getFixtureA().getBody().getUserData();
					Object ownerFixtureB = contact.getFixtureB().getBody().getUserData();
					boolean playerOwnsFixtureA = ownerFixtureA instanceof Player;
					boolean playerOwnsFixtureB = ownerFixtureB instanceof Player;
					boolean playerInvolved = playerOwnsFixtureA || playerOwnsFixtureB;
					if (!playerInvolved) 
						return false;
					boolean itemOwnsFixtureA = ownerFixtureA instanceof DroppedItem;
					boolean itemOwnsFixtureB = ownerFixtureB instanceof DroppedItem;
					boolean itemInvolved = itemOwnsFixtureA || itemOwnsFixtureB;
					if (!itemInvolved)
						return false;
					
					Player player = (Player)(playerOwnsFixtureA ? ownerFixtureA : ownerFixtureB);
					DroppedItem item = (DroppedItem)(itemOwnsFixtureA ? ownerFixtureA : ownerFixtureB);
					Fixture playerRadar = playerOwnsFixtureA ? contact.getFixtureA() : contact.getFixtureB();
					Fixture itemFixture = itemOwnsFixtureA ? contact.getFixtureA() : contact.getFixtureB();
					
					player.inventory.get(6).set(item.droppedItem);
					item.dispose();
					return true; 
				}
				
			});
			world.addHandler(new ContactEventHandler() {
				@Override
				public boolean beginContact(World world, Contact contact) {
			
					Object ownerFixtureA = contact.getFixtureA().getBody().getUserData();
					Object ownerFixtureB = contact.getFixtureB().getBody().getUserData();
					boolean playerOwnsFixtureA = ownerFixtureA instanceof Player;
					boolean playerOwnsFixtureB = ownerFixtureB instanceof Player;
					boolean playerInvolved = playerOwnsFixtureA || playerOwnsFixtureB;
					if (!playerInvolved) 
						return false;
					boolean enemyOwnsFixtureA = ownerFixtureA instanceof TestEnemy;
					boolean enemyOwnsFixtureB = ownerFixtureB instanceof TestEnemy;
					boolean enemyInvolved = enemyOwnsFixtureA || enemyOwnsFixtureB;
					if (!enemyInvolved)
						return false;
					
					Player player = (Player)(playerOwnsFixtureA ? ownerFixtureA : ownerFixtureB);
					TestEnemy enemy = (TestEnemy)(enemyOwnsFixtureA ? ownerFixtureA : ownerFixtureB);
					
					Player.this.enemy = enemy;
					return true; 
				}
			
				@Override
				public boolean endContact(World world, Contact contact) { 
					Object ownerFixtureA = contact.getFixtureA().getBody().getUserData();
					Object ownerFixtureB = contact.getFixtureB().getBody().getUserData();
					boolean playerOwnsFixtureA = ownerFixtureA instanceof Player;
					boolean playerOwnsFixtureB = ownerFixtureB instanceof Player;
					boolean playerInvolved = playerOwnsFixtureA || playerOwnsFixtureB;
					if (!playerInvolved) 
						return false;
					boolean enemyOwnsFixtureA = ownerFixtureA instanceof TestEnemy;
					boolean enemyOwnsFixtureB = ownerFixtureB instanceof TestEnemy;
					boolean enemyInvolved = enemyOwnsFixtureA || enemyOwnsFixtureB;
					if (!enemyInvolved)
						return false;
					
					Player.this.enemy = null;
					return true; 
					
				}
			});
			
		}
		
		this.previousState = State.IDLE;
		this.currentState = State.IDLE;
		this.animationTimer = 0;
		this.invincibilityTimer = 0;
		this.animations = initAnimationsMap();
		this.maxHealth = 100;
		this.health = maxHealth;
		
		@SuppressWarnings("unchecked")
		ObservableReference<Item>[] heldItems = new ObservableReference[9 * 4 /* hotbar + inventory */ 
		                                         + 4 /* armor */ 
		                                         + 4 /* accesory */];
		this.heldItems = heldItems;
		
		for (int i = 0; i < heldItems.length; i++) 
			heldItems[i] = new ObservableReference<Item>((Item)null);
		
		
		this.hotbar = Arrays.asList(heldItems).subList(0, 9);
		this.inventory = Arrays.asList(heldItems).subList(0, 9*4);
		this.armor = Arrays.asList(heldItems).subList(9*4, 9*4 + 4);
		this.accessory = Arrays.asList(heldItems).subList(9*4 + 4, 9*4 + 4 + 4);
		this.itemOnHand = hotbar.get(0);
		hotbar.get(0).set(new BowItem(world, this));
		hotbar.get(1).set(new DebugItem());
		hotbar.get(2).set(new DebugItem());
	}

	@Override
	public final boolean update(Viewport viewport) {
		super.update(viewport);
		if (this.invincibilityTimer > 0) 
			invincibilityTimer -= Gdx.graphics.getDeltaTime();
		
		if (enemy != null) 
			applyDamage(enemy.damage);
		
		if (this.health <= 0) 
			dispose();
		
		previousState = currentState;
		currentState = State.IDLE;
		
		// TODO change this to a click listener
		if (Gdx.input.isButtonJustPressed(Buttons.LEFT) && itemOnHand.get() != null) {
			itemOnHand.get().use();
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			self.applyLinearImpulse(new Vector2(-1, 0), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			self.applyLinearImpulse(new Vector2(1, 0), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			self.applyLinearImpulse(new Vector2(0, 1), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			self.applyLinearImpulse(new Vector2(0, -1), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}

		if (currentState != previousState)
			animationTimer = 0;

		return false;
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
		animations.put(State.IDLE, new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.AnimationId.Player_idle), PlayMode.LOOP));
		animations.put(State.WALKING, new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.AnimationId.Player_walk), PlayMode.LOOP));
		return animations;
	}

	/**
	 * The state of the player
	 */
	enum State {
		IDLE, WALKING;
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
		protected Player createEntity(World world) {
			return new Player(world, x, y);
		}

	}

}
