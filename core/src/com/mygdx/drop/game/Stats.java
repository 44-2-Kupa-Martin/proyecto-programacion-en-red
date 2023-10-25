package com.mygdx.drop.game;

import java.io.Serializable;

/**
 * An immutable POD for storing stats
 */
public class Stats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7591744591288892868L;
	protected float maxHealth;
	protected float health;
	/** value subtracted from incoming damage */
	protected int defense;
	/** damage inflicted upon colliding entities */
	protected int contactDamage;
	/** how much time (measured in seconds) does it take for the entity to be vulnerable after taking damage */
	protected float invincibilityDuration;

	public Stats(Stats other) {
		this.maxHealth = other.maxHealth;
		this.health = other.health;
		this.defense = other.defense;
		this.contactDamage = other.contactDamage;
		this.invincibilityDuration = other.invincibilityDuration;
	}
	
	public Stats(float maxHealth, int defense, int contactDamage, float invincibilityDuration) {
		this(maxHealth, maxHealth, defense, contactDamage, invincibilityDuration);
	}
	
	public Stats(float maxHealth, float health, int defense, int contactDamage, float invincibilityDuration) {
		this.maxHealth = maxHealth;
		this.health = health;
		this.defense = defense;
		this.contactDamage = contactDamage;
		this.invincibilityDuration = invincibilityDuration;
	}

	public float getMaxHealth() { return maxHealth; }

	public float getHealth() { return health; }

	public int getDefense() { return defense; }

	public int getContactDamage() { return contactDamage; }

	public float getInvincibilityDuration() { return invincibilityDuration; }
	
}
