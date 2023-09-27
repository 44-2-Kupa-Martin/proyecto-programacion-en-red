package com.mygdx.drop.game.dynamicentities;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.BoxEntity;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.World;

public class TestEnemy extends BoxEntity {
	public final float damage;
	private final AtlasRegion texture;
	//TODO REMOVE
	private final Player player;
	protected TestEnemy(World world, float x_mt, float y_mt, Player player /*TODO: REMOVE!!*/) {
		super(world, Drop.tlToMt(2), Drop.tlToMt(3),
				((Supplier<BodyDef>) (() -> {
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
		
		this.player = player;
		this.damage = 5;
		this.texture = game.assets.get(TextureId.GoofyItem_goofy);
	}
	
	@Override
	public boolean update(Viewport viewport) { 
		super.update(viewport);
		assert !Constants.MULTITHREADED;
		self.setLinearVelocity(player.getPosition().sub(getPosition()).nor().scl(1.5f));
		return false;
	}
	
	public void draw(Viewport viewport) {
		Vector2 coords = getDrawingCoordinates();
		game.batch.draw(texture, coords.x, coords.y, getWidth(), getHeight());
	}
	
	/**
	 * @see Entity.EntityDefinition
	 */
	public static class Definition extends Entity.EntityDefinition<TestEnemy> {
		// TODO REMOVE
		public Player player;

		public Definition(float x_mt, float y_mt, Player player) {
			super(x_mt, y_mt);
			this.player = player;
		}

		@Override
		protected TestEnemy createEntity(World world) { return new TestEnemy(world, x, y, player); }

	}

}
