package com.mygdx.drop.game;

public final class MutableStats extends Stats {
	
	public MutableStats(float maxHealth, float health, float invincibilityDuration) {
		super(maxHealth,health,0,0,invincibilityDuration);
	}
	
	public MutableStats(float maxHealth, float health, int defense, int contactDamage, float invincibilityDuration) { 
		super(maxHealth, health, defense, contactDamage, invincibilityDuration);
	 }
	
	public MutableStats(Stats other) {
		super(other);
	}

	public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }

	public void setHealth(float health) { this.health = health; }

	public void setDefense(int defense) { this.defense = defense; }

	public void setContactDamage(int contactDamage) { this.contactDamage = contactDamage; }

	public void setInvincibilityDuration(float invincibilityDuration) { this.invincibilityDuration = invincibilityDuration; }
}
