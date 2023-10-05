package com.mygdx.drop.game.dynamicentities;

public interface Mob {
	public float getMaxHealth();
	public void setMaxHealth(float health);
	public float getHealth();
	public void setHealth(float health);
	public int getDefense();
	public void setDefense(int defense);
	public int getDamage();
	public void setDamage(int damage);
	
	public void applyDamage(float lostHp);
	public void applyHealing(float recoveredHp);
}
