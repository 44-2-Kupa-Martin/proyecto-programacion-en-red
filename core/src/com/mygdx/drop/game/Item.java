package com.mygdx.drop.game;

import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.game.dynamicentities.Player;

//TODO make items immutable and create a static object in some common registry
public interface Item {

	public int getTextureId();

	/**
	 * 
	 * @param player The player that used the item
	 * @param x The x coordinate of the input measured in meters
	 * @param y The y coordinate of the input measured in meters
	 * @return {@code true} if the item should be consumed, {@code false} otherwise
	 */
	public default boolean leftUse(Player player, float x, float y) { return false; }

	/**
	 * 
	 * @param player The player that used the item
	 * @param x The x coordinate of the input measured in meters
	 * @param y The y coordinate of the input measured in meters
	 * @return {@code true} if the item should be consumed, {@code false} otherwise
	 */
	public default boolean rightUse(Player player, float x, float y) { return false; }

	/**
	 * 
	 * @return {@code true} if the item should be removed from the inventory, {@code false} otherwise
	 */
	public default boolean consume() { return true; }
	
	/**
	 * @return The minimum time between calls to {@link #leftUse(InputEvent, Inventory) leftUse()} for it to perform an action
	 */
	public float getLeftUseTime();
	
	/**
	 * @return The minimum time between calls to {@link #rightUse(InputEvent, Inventory) rightUse()} for it to perform an action
	 */
	public float getRightUseTime();
}
