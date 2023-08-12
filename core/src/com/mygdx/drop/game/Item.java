package com.mygdx.drop.game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.Drop;

public class Item {
	public final AtlasRegion texture;
	protected final Drop game;
	
	public Item(AtlasRegion texture) {
		assert Drop.game != null : "Entity created before game instance!";
		this.game = Drop.game;
		
		this.texture = texture;
	}
	
	
}
