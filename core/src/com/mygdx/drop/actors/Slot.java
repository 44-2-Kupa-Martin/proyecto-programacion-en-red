package com.mygdx.drop.actors;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.game.Item;

public class Slot extends Container<Image> implements PropertyChangeListener {
	private static TextureRegionDrawable background;
	private TextureRegionDrawable transparentPlaceholder;
	private final ObservableReference<Item> heldItem;
	
	static {
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();
		background = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.dispose();
	}
	
	public Slot(int size, ObservableReference<Item> reference) {
		assert reference != null : "Cannot take a null reference";
		this.heldItem = reference;
		heldItem.addListener(this);
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,0));
        pixmap.fill();
        this.transparentPlaceholder = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        setBackground(background);
        setActor(new Image());
        refreshImage(heldItem.get());
        
        addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) { 
        		boolean slotHasItem = heldItem != null;
        		boolean cursorHasItem = Drop.game.heldItem != null;
        		if (!slotHasItem && !cursorHasItem)
        			return;
        		
        		if (!slotHasItem && cursorHasItem) {        			
        			heldItem.set(Drop.game.heldItem);
        			Drop.game.heldItem = null;
        		}
        		
        		if (slotHasItem && !cursorHasItem) {
					Drop.game.heldItem = heldItem.get();
					heldItem.set(null);
				}
        		if (slotHasItem && cursorHasItem) {
					Item temp = heldItem.get();
					heldItem.set(Drop.game.heldItem);
					Drop.game.heldItem = temp;
				}
        	}
        });
	}
	
	private final void refreshImage(Item newItem) {
		TextureRegionDrawable drawable = newItem == null ? transparentPlaceholder : new TextureRegionDrawable(newItem.texture);
		getActor().setDrawable(drawable);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshImage((Item) evt.getNewValue());
	}
}
