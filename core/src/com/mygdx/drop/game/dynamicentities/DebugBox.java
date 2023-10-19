package com.mygdx.drop.game.dynamicentities;

import java.util.function.Supplier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.listeners.ClickEventListener;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;

public class DebugBox extends BoxEntity implements Drawable {
	private final AtlasRegion texture;

	/**
	 * Creates a DebugBox.
	 * @param x_mt Measured in meters
	 * @param y_mt Measured in meters
	 * @param width_mt Measured in meters
	 * @param height_mt Measured in meters
	 */
	protected DebugBox(World world, float x_mt, float y_mt, float width_mt, float height_mt) {
		super(world, width_mt, height_mt, 
		((Supplier<BodyDef>) (() -> {
			BodyDef body = new BodyDef();
			body.type = BodyType.DynamicBody;
			body.position.set(x_mt, y_mt + height_mt / 2);
			return body;
		})).get(), 
		((Supplier<FixtureDef>) (() -> {
			FixtureDef fixture = new FixtureDef();
			fixture.density = 0.5f;
			fixture.filter.categoryBits = (short) (Constants.Category.PLAYER_COLLIDABLE.value);
			return fixture;
		})).get());

		this.texture = Assets.Textures.DebugBox_bucket.get();
		
		addListener(new ClickEventListener(Input.Buttons.RIGHT) {
			@Override
			public void clicked(InputEvent event, float x, float y) { 
				System.out.println("clicked debugbox");
			}
		});
	}

	public void clicked() { System.out.println("clicked debug box"); };

	@Override
	public void draw(Viewport viewport) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(texture, coords.x, coords.y, 0, 0, getWidth(), getHeight(), 1, 1, self.getAngle() * MathUtils.radiansToDegrees);
	}

	/**
	 * @see Entity.EntityDefinition
	 */
	public static class Definition extends Entity.EntityDefinition<DebugBox> {
		public float width, height;

		/**
		 * @see DebugBox#DebugBox(World, float, float, float, float)
		 */
		public Definition(float x_mt, float y_mt, float width_mt, float height_mt) {
			super(x_mt, y_mt);
			this.width = width_mt;
			this.height = height_mt;
		}

		@Override
		protected DebugBox createEntity(World world) { return new DebugBox(world, x, y, width, height); }

	}

}
