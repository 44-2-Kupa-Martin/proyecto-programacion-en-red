package com.mygdx.drop;

import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.Player;

public class PhonyItem implements Item {
	public final Category category;
	public final int textureId;
	

	public PhonyItem(Category category, int textureId) {
		super();
		this.category = category;
		this.textureId = textureId;
	}

	@Override
	public Category getCategory() { return category; }

	@Override
	public int getTextureId() { return textureId; }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
	
	@Override
	public boolean leftUse(Player player, float x, float y) { throw new UnsupportedOperationException("Not yet implemented"); }
	
	@Override
	public boolean rightUse(Player player, float x, float y) { throw new UnsupportedOperationException("Not yet implemented"); }

}
