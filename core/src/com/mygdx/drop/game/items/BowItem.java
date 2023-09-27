package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Arrow;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.dynamicentities.TestEnemy;

public class BowItem implements Item {
	public final AtlasRegion texture;
	public final float useTime;
	private final Drop game;
	private long lastUsedTime;
	private final World world;
	private Player player;
	public BowItem(World world, Player player) {
		this.game = Drop.game;
		assert game != null; 
		this.world = world;
		this.player = player;
		this.texture = game.assets.get(TextureId.BowItem_bow);
		this.useTime = 0.5f;
		this.lastUsedTime = 0;
	}

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public boolean use() {
		if (TimeUtils.timeSinceMillis(lastUsedTime)/1000 < useTime) 
			return false;
		lastUsedTime = TimeUtils.millis();
		assert !Constants.MULTITHREADED;
		Vector2 arrowDirection = world.getLastClickPosition().sub(player.getPosition()).nor();
		world.createEntity(new Arrow.Definition(player.getX(), player.getY(), arrowDirection));
		return false;
	}
}
