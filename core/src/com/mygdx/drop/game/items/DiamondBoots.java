package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.MutableStats;

public class DiamondBoots implements EquippableItem {

	@Override
	public TextureRegion getTexture() { return Assets.Textures.DiamondSet_boots.get(); }

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
