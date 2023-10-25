package com.mygdx.drop.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Assets.Textures;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.listeners.EventListener;
import com.mygdx.drop.etc.events.listeners.PropertyChangeEventListener;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.PlayerManager;
import com.mygdx.drop.game.dynamicentities.Player;

/**
 * An slot that displays an {@link Item} in the UI
 */
public class Slot extends Container<Image> {
	public final static TextureRegionDrawable background;
	public final static TextureRegionDrawable selectedBackground;
	private final TextureRegionDrawable transparentPlaceholder;
	private final String playerName;
	/** The item to display */
	private final int itemIndex;
	private final PlayerManager playerManager;
	private int previousTextureId;
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
	public Slot(int size, int itemIndex, PlayerManager playerManager, String playerName) {
		this.itemIndex = itemIndex;
		this.playerManager = playerManager;
		this.playerName = playerName;
		setBackground(background);
		
		// The placeholder background depends on the size, so it cannot be initialized statically
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,0));
        pixmap.fill();
        this.transparentPlaceholder = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        
        
        setActor(new Image());
        Item referencedItem = playerManager.getItem(playerName, itemIndex);
        this.previousTextureId = referencedItem == null ? -1 : referencedItem.getTextureId();
        getActor().setDrawable(referencedItem == null ? transparentPlaceholder : new TextureRegionDrawable((AtlasRegion) Assets.getById(referencedItem.getTextureId()).get()));
        
        addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) { 
        		playerManager.swapItem(playerName, Player.PlayerInventory.CURSOR_ITEM, itemIndex);
        	}
        });
	}
	
	@Override
	public void act(float delta) {
		setBackground(background);
		super.act(delta); 
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Item item = playerManager.getItem(playerName, itemIndex);
		int textureId = item == null ? -1 : item.getTextureId();
		TextureRegionDrawable drawable = textureId == -1 ? transparentPlaceholder : new TextureRegionDrawable((AtlasRegion) Assets.getById(textureId).get());
		getActor().setDrawable(drawable);

		super.draw(batch, parentAlpha);
	}
}
