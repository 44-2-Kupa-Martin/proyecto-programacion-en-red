package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Item;

public class GoofyItem<OwnerType> implements Item<OwnerType> {
	private OwnerType owner;
	public final AtlasRegion texture;
	public GoofyItem(OwnerType owner) {
		this.owner = owner;
		this.texture = Drop.game.assets.get(TextureId.GoofyItem_goofy);
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
