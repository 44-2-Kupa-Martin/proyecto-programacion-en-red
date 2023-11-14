package com.mygdx.drop.game.dynamicentities;

import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Assets.Textures;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.SimpleContactEventFilter;
import com.mygdx.drop.etc.events.ClassifiedContactEvent;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.listeners.ContactEventListener;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.PlayerManager.FrameComponent;

public class TestEnemy extends BoxEntity implements Drawable {
	private static boolean instantiated = false;
	public final float damage;
	private final Textures texture;
	private final float maxHealth;
	private float health;
	private float invincibilityTimer;
	private @Null Player trackedPlayer;
	private final Fixture playerSensor;

	protected TestEnemy(World world, float x_mt, float y_mt) {
		super(world, Drop.tlToMt(2), Drop.tlToMt(3), ((Supplier<BodyDef>) (() -> {
			BodyDef body = new BodyDef();
			body.position.set(x_mt, Drop.tlToMt(3) / 2 + y_mt);
			body.type = BodyType.DynamicBody;
			body.fixedRotation = true;
			return body;
		})).get(), ((Supplier<FixtureDef>) (() -> {
			FixtureDef fixture = new FixtureDef();
			fixture.density = 1;
			fixture.filter.categoryBits = Constants.Category.PLAYER_COLLIDABLE.value;
			return fixture;
		})).get());

		FixtureDef sensor = new FixtureDef();
		sensor.isSensor = true;
		sensor.filter.maskBits = Constants.Category.PLAYER.value;
		sensor.filter.categoryBits = Constants.Category.SENSOR.value;
		CircleShape trackingRange = new CircleShape();
		trackingRange.setRadius(5f);
		sensor.shape = trackingRange;
		
		if (!instantiated)
			initializeClassListeners(world);
		
		this.playerSensor = self.createFixture(sensor);
		trackingRange.dispose();
		this.trackedPlayer = null;
		this.damage = 15;
		this.maxHealth = 15;
		this.health = maxHealth;
		this.texture = Assets.Textures.GoofyItem_goofy;
		this.invincibilityTimer = 0.25f;
	}

	@Override
	public boolean update() {
		boolean toBeDisposed = super.update();
		assert !Constants.MULTITHREADED;
		if (trackedPlayer != null && !trackedPlayer.isDisposed() && !trackedPlayer.getStats().isDead()) 
			self.setLinearVelocity(trackedPlayer.getPosition().sub(getPosition()).nor().scl(1.5f));			
		
		if (this.invincibilityTimer > 0)
			invincibilityTimer -= Gdx.graphics.getDeltaTime();

		if (this.health <= 0)
			toBeDisposed = true;
		return toBeDisposed;
	}

	@Override
	public FrameComponent getFrameComponent() { 
		Vector2 coords = getDrawingCoordinates();
		return new FrameComponent(texture.getId(), coords.x, coords.y, getWidth(), getHeight());
	}

	public final void applyDamage(float lostHp) {
		assert lostHp >= 0;
		if (invincibilityTimer > 0)
			return;
		invincibilityTimer = 1;
		this.health -= lostHp;
	}

	private static final void initializeClassListeners(World world) {
		assert !instantiated : "TestEnemy.initializeClassListeners called after first instantiation";
		TestEnemy.instantiated = true;
		world.addListener(new SimpleContactEventFilter<TestEnemy>(TestEnemy.class) {
			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<TestEnemy, Entity> classifiedEvent) {
				EventManager.fire(classifiedEvent);
				return event.isHandled();
			}

			@Override
			public boolean endContact(ContactEvent event, ClassifiedContactEvent<TestEnemy, Entity> classifiedEvent) {				
				EventManager.fire(classifiedEvent);
				return event.isHandled();
			}

		});

		world.addListener(new ContactEventFilter<TestEnemy, Arrow>(TestEnemy.class, Arrow.class) {
			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<TestEnemy, Arrow> classfiedEvent) {
				classfiedEvent.self.applyDamage(classfiedEvent.other.damage);
				if (classfiedEvent.self.health <= 0) 
					classfiedEvent.other.firedBy.addPoints(100);;
				
				return event.isHandled();
			}
		});
		
		world.addListener(new ContactEventFilter<TestEnemy, Player>(TestEnemy.class, Player.class) {
			@Override
			public boolean beginContact(ContactEvent event, ClassifiedContactEvent<TestEnemy, Player> participants) {
				if ((participants.selfFixture == participants.self.playerSensor) && (participants.self.trackedPlayer == null)) {
					participants.self.trackedPlayer = participants.other;
				}
				return false;
			}
			
			@Override
			public boolean endContact(ContactEvent event, ClassifiedContactEvent<TestEnemy, Player> participants) { 
				if ((participants.selfFixture == participants.self.playerSensor) && (participants.self.trackedPlayer == participants.other)) {
					participants.self.trackedPlayer = null;
				}
				return false;
			}
		});
	}

	/**
	 * @see Entity.EntityDefinition
	 */
	public static class Definition extends Entity.EntityDefinition<TestEnemy> {

		public Definition(float x_mt, float y_mt) {
			super(x_mt, y_mt);
		}

		@Override
		protected TestEnemy createEntity(World world) { return new TestEnemy(world, x, y); }

	}

}
