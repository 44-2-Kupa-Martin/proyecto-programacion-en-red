package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class ArrowItem implements Item {

	@Override
	public TextureRegion getTexture() { return Assets.Textures.Arrow_arrow.get(); }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
	
	@Override
	public boolean consume() { return true; }
}
