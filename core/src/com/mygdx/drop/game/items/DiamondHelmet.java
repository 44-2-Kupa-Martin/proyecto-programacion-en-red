package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.MutableStats;

//TODO find a better way to classify and distinguish items
public class DiamondHelmet implements EquippableItem {
	private final Drop game;
	private final AtlasRegion texture;
	
	public DiamondHelmet() {
		assert Drop.game != null;
		this.game = Drop.game;
		this.texture = Assets.Textures.DiamondSet_helmet.get();
	}

	@Override
	public TextureRegion getTexture() { return texture; }

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
