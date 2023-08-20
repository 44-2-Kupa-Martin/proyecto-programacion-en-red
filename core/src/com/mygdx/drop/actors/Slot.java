package com.mygdx.drop.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.handlers.EventHandler;
import com.mygdx.drop.game.Item;

/**
 * An slot that displays an {@link Item} in the UI
 */
public class Slot extends Container<Image> {
	private static TextureRegionDrawable background;
	private TextureRegionDrawable transparentPlaceholder;
	/** The item to display */
	private final ObservableReference<Item> itemReference;
	
	static {
		// The background is common to all instances
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();
		background = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.dispose();
	}
	
	/**
	 * A {@link Container} that displays an {@link Item} and updates automatically if the items is dropped or moved
	 * @param size The size of the actor in pixels
	 * @param reference A reference to an Item
	 */
	public Slot(int size, ObservableReference<Item> reference) {
		assert reference != null : "Cannot take a null reference";
		this.itemReference = reference;
		setBackground(background);
		
		itemReference.asPropertyChangeEventListener().addHandler(new EventHandler<PropertyChangeEvent<Item>>() {
			@Override
			public boolean handle(PropertyChangeEvent<Item> changeEvent) {
				Item newItem = changeEvent.newValue;
				TextureRegionDrawable drawable = newItem == null ? transparentPlaceholder : new TextureRegionDrawable(newItem.getTexture());
				getActor().setDrawable(drawable);
				return true;
			} 
		});
		
		// The placeholder background depends on the size, so it cannot be initialized statically
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,0));
        pixmap.fill();
        this.transparentPlaceholder = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        
        
        setActor(new Image());
        Item referencedItem = itemReference.get();
        getActor().setDrawable(referencedItem == null ? transparentPlaceholder : new TextureRegionDrawable(referencedItem.getTexture()));
        
        addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) { 
        		boolean slotHasItem = itemReference.get() != null;
        		boolean cursorHasItem = Drop.game.heldItem != null;
        		if (!slotHasItem && !cursorHasItem)
        			return;
        		
        		if (!slotHasItem && cursorHasItem) {        			
        			itemReference.set(Drop.game.heldItem);
        			Drop.game.heldItem = null;
        		}
        		
        		if (slotHasItem && !cursorHasItem) {
					Drop.game.heldItem = itemReference.get();
					itemReference.set(null);
				}
        		if (slotHasItem && cursorHasItem) {
					Item temp = itemReference.get();
					itemReference.set(Drop.game.heldItem);
					Drop.game.heldItem = temp;
				}
        	}
        });
	}
}
