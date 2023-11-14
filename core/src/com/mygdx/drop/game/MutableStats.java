package com.mygdx.drop.game;

public final class MutableStats extends Stats {
	
	public MutableStats(float maxHealth, float health, float invincibilityDuration, int points) {
		super(maxHealth,health,0,0,invincibilityDuration, points);
	}
	
	public MutableStats(float maxHealth, float health, int defense, int contactDamage, float invincibilityDuration, int points) { 
		super(maxHealth, health, defense, contactDamage, invincibilityDuration, points);
	 }
	
	public MutableStats(Stats other) {
		super(other);
	}

	public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }

	public void setHealth(float health) { this.health = health; }

	public void setDefense(int defense) { this.defense = defense; }

	public void setContactDamage(int contactDamage) { this.contactDamage = contactDamage; }

	public void setInvincibilityDuration(float invincibilityDuration) { this.invincibilityDuration = invincibilityDuration; }
	
	public void setPoints(int points) {this.points = points;}
}
