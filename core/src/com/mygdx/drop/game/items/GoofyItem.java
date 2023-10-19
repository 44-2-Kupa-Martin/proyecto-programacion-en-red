package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class GoofyItem implements Item {
	public final AtlasRegion texture;
	public GoofyItem() {
		this.texture = Assets.Textures.GoofyItem_goofy.get();
	 }

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }

}
