package com.mygdx.drop.game.dynamicentities;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.SimpleContactEventFilter;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.listeners.ContactEventListener;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;


public class Arrow extends BoxEntity implements Drawable {
	private static boolean instantiated = false;
	public final AtlasRegion texture;
	public final float damage;
	protected Arrow(World world, float x_mt, float y_mt, Vector2 directionVector) { 
		super(world, Drop.tlToMt(3), Drop.tlToMt(1), 
				((Supplier<BodyDef>) (() -> {
					BodyDef body = new BodyDef();
					body.type = BodyType.DynamicBody;
					body.position.set(x_mt, y_mt + Drop.tlToMt(1) / 2);
					body.fixedRotation = false;
					return body;
				})).get(), 
				((Supplier<FixtureDef>) (() -> {
					FixtureDef fixture = new FixtureDef();
					fixture.density = 1f;
					fixture.filter.categoryBits = Constants.Category.PROJECTILE.value;
					fixture.filter.maskBits = (short) ~Constants.Category.PROJECTILE.value;
					return fixture;
				})).get()
		);
		
		if (!instantiated) {
			initializeClassListeners(world);
		}
		
		this.texture = Assets.Textures.Arrow_arrow.get();
		self.setTransform(getPosition(), directionVector.angleRad());
		self.applyLinearImpulse(directionVector.nor().scl(30), self.getWorldCenter(), false);
		this.damage = 5;
	}
	
	@Override
	public boolean update(Viewport viewport) {
		self.setTransform(getPosition(), self.getLinearVelocity().angleRad());
		return super.update(viewport); 
	}

	@Override
	public void draw(Viewport viewport) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(texture, coords.x, coords.y, 0, 0, getWidth(), getHeight(), 1, 1, self.getAngle() * MathUtils.radiansToDegrees);
	}
	
	private static void initializeClassListeners(World world) {
		assert !instantiated;
		Arrow.instantiated = true;
		
		world.addListener(new SimpleContactEventFilter<Arrow>(Arrow.class) {
			@Override
			public boolean beginContact(ContactEvent event, Participants participants) {
				participants.objectA.dispose();
				return false; 
			}
		});
	}
	
	/**
	 * @see Entity.EntityDefinition
	 */
	public static class Definition extends Entity.EntityDefinition<Arrow> {
		public Vector2 directionVector;

		public Definition(float x_mt, float y_mt, Vector2 directionVector) {
			super(x_mt, y_mt);
			this.directionVector = directionVector;
		}

		@Override
		protected Arrow createEntity(World world) { return new Arrow(world, x, y, directionVector); }

	}
}
