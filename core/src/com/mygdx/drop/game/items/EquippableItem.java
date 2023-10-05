package com.mygdx.drop.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.Mob;

public abstract class EquippableItem<OwnerType extends Mob> implements Item<OwnerType> {
	
	public abstract void equip();
	
	public abstract void unequip();

}
