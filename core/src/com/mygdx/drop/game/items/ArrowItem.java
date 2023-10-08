package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class ArrowItem implements Item {

	@Override
	public TextureRegion getTexture() { return Drop.game.assets.get(TextureId.Arrow_arrow); }

	@Override
	public float getLeftUseTime() { return 0; }

	@Override
	public float getRightUseTime() { return 0; }
	
	@Override
	public boolean consume() { return true; }
}
