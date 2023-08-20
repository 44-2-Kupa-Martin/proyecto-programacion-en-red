package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface Item {
	public TextureRegion getTexture();
	
	/**
	 * Uses the item
	 * @return {@code true} if the item should be consumed, {@code false} otherwise
	 */
	public boolean use();
	
	
}
