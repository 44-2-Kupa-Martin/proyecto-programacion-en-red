package com.mygdx.drop.game;

public interface EquippableItem extends Item {
	
	public abstract void equip(MutableStats stats);
	
	public abstract void unequip(MutableStats stats);

}
