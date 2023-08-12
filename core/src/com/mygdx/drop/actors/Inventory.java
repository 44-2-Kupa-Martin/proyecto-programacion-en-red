package com.mygdx.drop.actors;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Assets.FontId;
import com.mygdx.drop.Assets.SkinId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.game.DebugItem;
import com.mygdx.drop.game.GoofyItem;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.Player;

public class Inventory extends Stack {
	private final Drop game;
	private Table hotbar;
	private Table inventory;
	
	public Inventory(Player player) {
		assert Drop.game != null : "Inventory created before game instance!";
		this.game = Drop.game;
		int slotSize = 50;
		setDebug(Constants.DEBUG);
		setVisible(true);
		
		hotbar = new Table();
		hotbar.setVisible(true);
		
		for (ObservableReference<Item> itemReference : player.hotbar) 
			hotbar.add(new Slot(slotSize, itemReference)).width(slotSize).height(slotSize);
		
		
		
		this.inventory = new Table();
		inventory.setDebug(Constants.DEBUG);
		inventory.setVisible(false);
		Table itemsTable = new Table(game.assets.get(SkinId.Global_default));
		itemsTable.setDebug(Constants.DEBUG);
		
		// The hotbar and inventory's first row share the same item references
		for (ObservableReference<Item> itemReference : player.hotbar) 
			itemsTable.add(new Slot(slotSize, itemReference)).width(slotSize).height(slotSize);
		itemsTable.row();
		// i starts at 1 because we created the hotbar row in the previous loop
		// TODO: remove hardcoded values
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 9; j++) 
				itemsTable.add(new Slot(slotSize, player.inventory.get(i * 9 + j))).width(slotSize).height(slotSize);
			itemsTable.row();
		}
		inventory.add(itemsTable);
		Table armorTable = new Table();
		armorTable.setDebug(Constants.DEBUG);

		for (int i = 0; i < 4; i++) {
			// Armor slot
			armorTable.add(new Slot(slotSize, player.armor.get(i))).width(slotSize).height(slotSize);
			// Accessory slot
			armorTable.add(new Slot(slotSize, player.accessory.get(i))).width(slotSize).height(slotSize);
			armorTable.row();
		}			
		inventory.add(armorTable);
		add(inventory);
		add(hotbar);
	}
	
	@Override
	public void act(float delta) { 
		super.act(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			inventory.setVisible(!inventory.isVisible());
			hotbar.setVisible(!hotbar.isVisible());
		}
	}
	
}
