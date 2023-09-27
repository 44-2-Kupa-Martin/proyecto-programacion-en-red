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
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.events.handlers.ContactEventHandler;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;


public class Arrow extends BoxEntity implements Drawable {
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
					//TODO add projectile category
					return fixture;
				})).get()
		);
		
		
		this.texture = game.assets.get(TextureId.Arrow_arrow);
		self.applyLinearImpulse(directionVector.nor().scl(30), self.getWorldCenter(), false);
		this.damage = 5;
	}

	@Override
	public void draw(Viewport viewport) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(texture, coords.x, coords.y, 0, 0, getWidth(), getHeight(), 1, 1, self.getAngle() * MathUtils.radiansToDegrees);
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
