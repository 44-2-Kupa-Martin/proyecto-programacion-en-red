package com.mygdx.drop.game.items;

import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class ArrowItem implements Item {

	@Override
	public int getTextureId() { return Assets.Textures.Arrow_arrow.getId(); }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
	
	@Override
	public boolean consume() { return true; }

	@Override
	public Category getCategory() { return Item.Category.CONSUMABLE; }
}
