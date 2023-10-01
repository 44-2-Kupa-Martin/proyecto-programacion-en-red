package com.mygdx.drop.game.tiles;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.game.Tile;
import com.mygdx.drop.game.World;

public class RainbowTile extends Tile {
	public RainbowTile(World world, int x, int y) {
		super(world, Constants.LayerId.WORLD, TileId.RAINBOW_TILE, x, y, RainbowTile.class);
		Fixture fixture = self.getFixtureList().get(0);
		fixture.setDensity(1);
		fixture.setFriction(1);
		fixture.setRestitution(0);
	}

	/**
	 * See {@link Tile.TileDefinition}
	 */
	public static class Definition extends Tile.TileDefinition<RainbowTile> {
		public Definition(int x, int y) { super(x, y); }

		@Override
		protected RainbowTile createEntity(World world) { return new RainbowTile(world, (int)x, (int)y); }
	}
}
