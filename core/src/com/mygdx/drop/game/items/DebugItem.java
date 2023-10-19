package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class DebugItem implements Item {
	public final AtlasRegion texture;
	public DebugItem() {
		this.texture = Assets.Textures.DebugBox_bucket.get(); 
	}

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
}
