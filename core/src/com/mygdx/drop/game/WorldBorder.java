package com.mygdx.drop.game;

import java.util.function.Supplier;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;

class WorldBorder extends BoxEntity {
	/**
	 * Creates a wall that prevents the player from going out of bounds
	 * @param world
	 * @param wallCardinality 
	 */
	protected WorldBorder(World world, Cardinality wallCardinality) { 
		super(world, world.worldWidth_mt * 3, world.worldHeight_mt * 3, 
			((Supplier<BodyDef>)(() -> {
				assert wallCardinality != null : "The cardinality cannot be null";
				BodyDef wallDefinition = new BodyDef();
				wallDefinition.type = BodyType.StaticBody;
				switch (wallCardinality) {
					case NORTH:
						wallDefinition.position.set(0, 2 * world.worldHeight_mt);						
						return wallDefinition;
					case SOUTH:
						wallDefinition.position.set(0, -2 * world.worldHeight_mt);						
						return wallDefinition;
					case EAST:
						wallDefinition.position.set(2 * world.worldWidth_mt, 0);
						return wallDefinition;
					case WEST:
						wallDefinition.position.set(-2 * world.worldWidth_mt, 0);
						return wallDefinition;
					default:
						throw new RuntimeException("Unreachable");
				}
			})).get(), 
			((Supplier<FixtureDef>)(()->{
				FixtureDef wallFixture = new FixtureDef();
				wallFixture.density = Float.POSITIVE_INFINITY;
				wallFixture.friction = 0;
				wallFixture.restitution = 0;
				wallFixture.filter.maskBits = Constants.Category.PLAYER.value; // Only allow collisions with the player category
				wallFixture.filter.categoryBits = Constants.Category.PLAYER_COLLIDABLE.value; // The walls belong to the player collidable category
				return wallFixture;
			})).get());
	}

	@Override
	public boolean update(Viewport viewport) { return false; }
	
	public enum Cardinality {
		NORTH,
		SOUTH,
		EAST,
		WEST;
	}
}
