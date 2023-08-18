package com.mygdx.drop.game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.Drop;

public interface Item {
	public TextureRegion getTexture();
	
	/**
	 * 
	 * @return {@code true} if the item should be consumed
	 */
	public boolean use();
	
	
}
