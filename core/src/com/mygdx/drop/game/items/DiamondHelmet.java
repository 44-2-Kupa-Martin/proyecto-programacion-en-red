package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.Assets.TextureId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.dynamicentities.Mob;

//TODO find a better way to classify and distinguish items
public class DiamondHelmet<OwnerType extends Mob> extends HelmetItem<OwnerType> {
	private final Drop game;
	private final AtlasRegion texture;
	private OwnerType owner;
	
	public DiamondHelmet(OwnerType owner) {
		assert Drop.game != null;
		this.game = Drop.game;
		this.texture = game.assets.get(TextureId.DiamondSet_helmet);
		this.owner = owner;
	}
	
	@Override
	public void equip() {
		owner.setDefense(owner.getDefense() + 5);
	}

	@Override
	public void unequip() {
		owner.setDefense(owner.getDefense() - 5);
	}

	@Override
	public OwnerType getOwner() { return owner; }

	@Override
	public TextureRegion getTexture() { return texture; }

	@Override
	public boolean use() { return false; }

	@Override
	public boolean isBufferable() { return false; }

	@Override
	public void setOwner(OwnerType owner) { this.owner = owner; }

}
