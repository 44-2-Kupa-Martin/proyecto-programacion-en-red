package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class DebugItem<OwnerType> implements Item<OwnerType> {
	public final AtlasRegion texture;
	private OwnerType owner;
	public DebugItem(OwnerType owner) {
		this.owner = owner;
		this.texture = Drop.game.assets.get(TextureId.DebugBox_bucket); 
	}

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public boolean use() { return false; }

	@Override
	public boolean isBufferable() { return true; }

	@Override
	public OwnerType getOwner() { return owner; }
	
	@Override
	public void setOwner(OwnerType owner) { this.owner = owner; }

}
