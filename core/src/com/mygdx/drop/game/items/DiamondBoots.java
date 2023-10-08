package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.MutableStats;

public class DiamondBoots implements EquippableItem {

	@Override
	public TextureRegion getTexture() { return Drop.game.assets.get(TextureId.DiamondSet_boots); }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }

	@Override
	public void equip(MutableStats stats) {
		stats.setDefense(stats.getDefense() + 5);
	}

	@Override
	public void unequip(MutableStats stats) {
		stats.setDefense(stats.getDefense() - 5);
	}

}
