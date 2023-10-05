package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.drop.etc.EventEmitter;

public interface Item<OwnerType> {
	public OwnerType getOwner();
	public void setOwner(OwnerType owner);
	
	public TextureRegion getTexture();
	
	/**
	 * Uses the item
	 * @return {@code true} if the item should be consumed, {@code false} otherwise
	 */
	public boolean use();
	
	/**
	 * 
	 * @return {@literal true} item uses can be buffered, {@literal false} if they must be timed
	 */
	public boolean isBufferable();
	
}
