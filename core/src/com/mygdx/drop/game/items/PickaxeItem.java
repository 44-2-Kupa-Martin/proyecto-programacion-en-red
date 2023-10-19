package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Entity;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.Tile;
import com.mygdx.drop.game.dynamicentities.Player;

public class PickaxeItem implements Item {
	public final float useTime;
	private long lastUsedTime;
	
	public PickaxeItem() {
		this.useTime = 1/3f;
	}

	@Override
	public TextureRegion getTexture() { return Assets.Textures.GoofyItem_goofy.get(); }

	@Override
	public float getLeftUseTime() { return useTime; }
	
	@Override
	public boolean leftUse(Player player, float x, float y) {
		if (TimeUtils.timeSinceMillis(lastUsedTime) / 1000 < useTime)
			return false;
		
		this.lastUsedTime = TimeUtils.millis();
		
		System.out.println("used pickaxe");
		
		if (!player.canReach(x, y)) 
			return false;
		
		System.out.println("clicked in range");
		
		Entity hit = player.world.hit(x, y);
		if (hit == null || !(hit instanceof Tile)) 
			return false;
		
		System.out.println("hited a tile");
		
		Tile tile = (Tile)hit;
		tile.fracture();
		System.out.println("called fracture");
		return false;
	}

	@Override
	public float getRightUseTime() { return 0; }

}
