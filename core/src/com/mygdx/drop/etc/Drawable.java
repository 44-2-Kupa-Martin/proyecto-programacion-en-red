package com.mygdx.drop.etc;

import com.mygdx.drop.game.PlayerManager.FrameComponent;

public interface Drawable {
	/**
	 * 
	 * @return The information needed to draw the object to the screen
	 */
	public FrameComponent getFrameComponent();

}
