package com.mygdx.drop.game;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Constants.LayerId;

public class RainbowTile extends Tile {

	public RainbowTile(int x, int y) {
		super(Constants.LayerId.WORLD, Tile.TileId.RAINBOW, x, y);
		Fixture fixture = self.getFixtureList().get(0);
		fixture.setDensity(1);
		fixture.setFriction(1);
		fixture.setRestitution(0);
	}
	
}
