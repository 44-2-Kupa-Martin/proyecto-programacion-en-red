package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;

public class DebugItem extends Item {

	public DebugItem() { super(Drop.game.assets.get(TextureId.DebugBox_bucket));
	 }

}
