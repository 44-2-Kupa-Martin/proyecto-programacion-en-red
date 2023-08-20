package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class DebugItem implements Item {
	public final AtlasRegion texture;
	public DebugItem() { 
		this.texture = Drop.game.assets.get(TextureId.DebugBox_bucket); 
	}

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public boolean use() { return false; }

}
