package com.mygdx.drop.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.Player;

/**
 * The UI for the {@link Player}'s inventory
 */
public class Inventory extends Stack {
	private final Drop game;
	private Table hotbar;
	private Table inventory;
	private Player player;
	
	public Inventory(Player player) {
		assert Drop.game != null : "Inventory created before game instance!";
		this.game = Drop.game;
		this.player = player;
		int slotSize = 50;
		setDebug(Constants.DEBUG);
		setVisible(true);
		
		hotbar = new Table();
		hotbar.setVisible(true);
		
		for (ObservableReference<Item> itemReference : player.items.hotbar) 
			hotbar.add(new Slot<>(slotSize, itemReference, Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
		
		this.inventory = new Table();
		inventory.setDebug(Constants.DEBUG);
		inventory.setVisible(false);
		Table itemsTable = new Table(Assets.Skins.Global_default.get());
		itemsTable.setDebug(Constants.DEBUG);
		
		// The hotbar and inventory's first row share the same item references
		for (ObservableReference<Item> itemReference : player.items.hotbar) 
			itemsTable.add(new Slot<>(slotSize, itemReference, Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
		itemsTable.row();
		// i starts at 1 because we created the hotbar row in the previous loop
		// TODO: remove hardcoded values
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 9; j++) 
				itemsTable.add(new Slot<>(slotSize, player.items.inventory.get(i*9 + j), Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
			itemsTable.row();
		}
		inventory.add(itemsTable);
		Table armorTable = new Table();
		armorTable.setDebug(Constants.DEBUG);
		for (int i = 0; i < 4; i++) {
			// Armor slot
			armorTable.add(new Slot<>(slotSize, player.items.armor.get(i), EquippableItem.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
			// Accessory slot
			armorTable.add(new Slot<>(slotSize, player.items.accessory.get(i), EquippableItem.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
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
		Slot selectedSlot = (Slot)hotbar.getChild(player.items.getSelectedSlot());
		selectedSlot.setBackground(Slot.selectedBackground);
	}
	
}
