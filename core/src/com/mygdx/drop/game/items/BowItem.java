package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.Inventory;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Arrow;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.dynamicentities.TestEnemy;

public class BowItem implements Item {
	public final float useTime;
	private long lastUsedTime;

	public BowItem() {	
		assert Drop.game != null;
		this.useTime = 0.5f;
		this.lastUsedTime = 0;
	}

	@Override
	public int getTextureId() { return Assets.Textures.BowItem_bow.getId(); }

	@Override
	public boolean leftUse(Player player, float x, float y) {
		World world = player.world;
		if (TimeUtils.timeSinceMillis(lastUsedTime) / 1000 < useTime)
			return false;
		lastUsedTime = TimeUtils.millis();
		assert !Constants.MULTITHREADED;
		if (player.items.hasItem(ArrowItem.class)) {
			Vector2 arrowDirection = player.getRelativeCoordinates(new Vector2(x, y));
			world.createEntity(new Arrow.Definition(player.getX(), player.getY(), arrowDirection));
			player.items.consumeItem(ArrowItem.class);
		}
		return false; 
	}

	@Override
	public float getLeftUseTime() { return useTime; }
	
	@Override
	public float getRightUseTime() { return 0; }
}
