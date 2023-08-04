package com.mygdx.drop.game;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Constants.LayerId;

public class RainbowTile extends Tile {
	static {
		
	}

	public RainbowTile(World world, int x, int y) {
		super(world, Constants.LayerId.WORLD, TileId.RAINBOW_TILE, x, y, RainbowTile.class);
		Fixture fixture = self.getFixtureList().get(0);
		fixture.setDensity(1);
		fixture.setFriction(1);
		fixture.setRestitution(0);
	}

	@Override
	public void dispose() {}

	/**
	 * See {@link Tile.TileDefinition}
	 */
	public static class Definition extends Tile.TileDefinition<RainbowTile> {
		public Definition(int x, int y) { super(x, y); }

		@Override
		protected RainbowTile createTile(World world) { return new RainbowTile(world, x, y); }

	}

}
