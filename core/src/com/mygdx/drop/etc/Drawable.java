package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.viewport.Viewport;

public interface Drawable {
	/**
	 * Draws the object to the screen
	 * 
	 * @param viewport Needed for projecting/unprojecting
	 */
	public void draw(Viewport viewport);

}
