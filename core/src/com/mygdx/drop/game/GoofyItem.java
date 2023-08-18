package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;

public class GoofyItem implements Item {
	public final AtlasRegion texture;
	public GoofyItem() { 
		this.texture = Drop.game.assets.get(TextureId.GoofyItem_goofy);
	 }

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public boolean use() { return false; }

}
