package com.mygdx.drop.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.game.EquippableItem;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.PlayerManager;
import com.mygdx.drop.game.dynamicentities.Player;

/**
 * The UI for the {@link Player}'s inventory
 */
public class Inventory extends Stack {
	private final Drop game;
	private final String playerName;
	private final PlayerManager playerManager;
	private Table hotbar;
	private Table inventory;
	public Inventory(PlayerManager playerManager, String playerName) {
		assert Drop.game != null : "Inventory created before game instance!";
		this.game = Drop.game;
		this.playerName = playerName;
		this.playerManager = playerManager;
		int slotSize = 50;
		setDebug(Constants.DEBUG);
		setVisible(true);
		
		hotbar = new Table();
		hotbar.setVisible(true);
		
		for (int i = Player.PlayerInventory.HOTBAR_START; i < Player.PlayerInventory.HOTBAR_END; i++) 
			hotbar.add(new Slot(slotSize, i, playerManager, playerName)).width(slotSize).height(slotSize);
			
		
		
		this.inventory = new Table();
		inventory.setDebug(Constants.DEBUG);
		inventory.setVisible(false);
		Table itemsTable = new Table(Assets.Skins.Global_default.get());
		itemsTable.setDebug(Constants.DEBUG);
		
		// The hotbar and inventory's first row share the same item references
		for (int i = Player.PlayerInventory.HOTBAR_START; i < Player.PlayerInventory.HOTBAR_END; i++) 
			itemsTable.add(new Slot(slotSize, i, playerManager, playerName)).width(slotSize).height(slotSize);			
		
		itemsTable.row();
		// i starts at 1 because we created the hotbar row in the previous loop
		// TODO: remove hardcoded values
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 9; j++) 
				itemsTable.add(new Slot(slotSize, Player.PlayerInventory.INVENTORY_START + i*9 + j, playerManager, playerName)).width(slotSize).height(slotSize);
			itemsTable.row();
		}
		inventory.add(itemsTable);
		Table armorTable = new Table();
		armorTable.setDebug(Constants.DEBUG);
		for (int i = 0; i < 4; i++) {
			// Armor slot
			armorTable.add(new Slot(slotSize, Player.PlayerInventory.ARMOR_START+i, playerManager, playerName)).width(slotSize).height(slotSize);
			// Accessory slot
			armorTable.add(new Slot(slotSize, Player.PlayerInventory.ACCESSORY_START+i, playerManager, playerName)).width(slotSize).height(slotSize);
			armorTable.row();
		}			
		inventory.add(armorTable);
		add(inventory);
		add(hotbar);
		
		
	}
	
	@Override
	public void act(float delta) { 
		super.act(delta);
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			inventory.setVisible(!inventory.isVisible());
			hotbar.setVisible(!hotbar.isVisible());
		}
		Slot selectedSlot = (Slot)hotbar.getChild(playerManager.getSelectedSlot(playerName));
		selectedSlot.setBackground(Slot.selectedBackground);

		
	}
	
}
