package com.mygdx.drop.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.handlers.EventListener;
import com.mygdx.drop.etc.events.handlers.PropertyChangeEventHandler;
import com.mygdx.drop.game.Item;

/**
 * An slot that displays an {@link Item} in the UI
 */
public class Slot<ControlledItemType extends Item> extends Container<Image> {
	public final static TextureRegionDrawable background;
	public final static TextureRegionDrawable selectedBackground;
	private TextureRegionDrawable transparentPlaceholder;
	/** The item to display */
	private final ObservableReference<ControlledItemType> itemReference;
	private final Class<ControlledItemType> controlledItemType;
	private final ObservableReference<Item> cursorItemReference;
	private boolean itemChanged;
	
	static {
		// The background is common to all instances
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();
		background = new TextureRegionDrawable(new Texture(pixmap));
		
		pixmap.setColor(Color.BLUE);
		pixmap.fill();
		selectedBackground = new TextureRegionDrawable(new Texture(pixmap));
		
		pixmap.dispose();
	}
	
	/**
	 * A {@link Container} that displays an {@link Item} and updates automatically if the items is dropped or moved
	 * @param size The size of the actor in pixels
	 * @param controlledItem A reference to an Item
	 */
	public Slot(int size, ObservableReference<ControlledItemType> controlledItem, Class<ControlledItemType> controlledItemType, ObservableReference<Item> cursorItem) {
		assert controlledItem != null : "Cannot take a null reference";
		this.itemReference = controlledItem;
		this.controlledItemType = controlledItemType;
		this.cursorItemReference = cursorItem;
		this.itemChanged = false;
		setBackground(background);
		
		itemReference.addListener(new PropertyChangeEventHandler<Item>(Item.class) {
			@Override
			public boolean onChange(Object target, Item oldValue, Item newValue) {
				Slot.this.itemChanged = true;
				return false;
			}
		});
		
		// The placeholder background depends on the size, so it cannot be initialized statically
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,0));
        pixmap.fill();
        this.transparentPlaceholder = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        
        
        setActor(new Image());
        ControlledItemType referencedItem = itemReference.get();
        getActor().setDrawable(referencedItem == null ? transparentPlaceholder : new TextureRegionDrawable(referencedItem.getTexture()));
        
        addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) { 
        		boolean slotHasItem = itemReference.get() != null;
        		boolean cursorHasItem = cursorItemReference.get() != null;
        		if (!slotHasItem && !cursorHasItem)
        			return;
        		
        		if (!slotHasItem && cursorHasItem) {     
        			boolean canPlace = controlledItemType.isInstance(cursorItemReference.get());
        			if (canPlace) {						
        				itemReference.set((ControlledItemType) cursorItemReference.get());
        				cursorItemReference.set(null);
					}
        		}
        		
        		if (slotHasItem && !cursorHasItem) {
					cursorItemReference.set(itemReference.get());
					itemReference.set(null);
				}
        		if (slotHasItem && cursorHasItem) {
        			boolean canPlace = controlledItemType.isInstance(cursorItemReference.get());
        			if (canPlace) {
        				ControlledItemType temp = itemReference.get();
        				itemReference.set((ControlledItemType) cursorItemReference.get());
        				cursorItemReference.set(temp);						
					}
				}
        	}
        });
	}
	
	@Override
	public void act(float delta) { 
		super.act(delta);
		setBackground(background);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (itemChanged) {
			TextureRegionDrawable drawable = itemReference.get() == null ? transparentPlaceholder : new TextureRegionDrawable(itemReference.get().getTexture());
			getActor().setDrawable(drawable);
		}
		super.draw(batch, parentAlpha);
	}
}
