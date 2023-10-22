package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class DebugItem implements Item {

	@Override
	public int getTextureId() { return Assets.Textures.DebugBox_bucket.getId(); }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
}
