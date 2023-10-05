package com.mygdx.drop.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.drop.Assets.SkinId;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.ObservableReference;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.items.ChestplateItem;
import com.mygdx.drop.game.items.EquippableItem;
import com.mygdx.drop.game.items.HelmetItem;
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
		
		for (ObservableReference<Item<Player>> itemReference : player.items.hotbar) 
			hotbar.add(new Slot<Player, Item<Player>>(slotSize, itemReference, (Class<Item<Player>>)(Class<?>)Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
		
		this.inventory = new Table();
		inventory.setDebug(Constants.DEBUG);
		inventory.setVisible(false);
		Table itemsTable = new Table(game.assets.get(SkinId.Global_default));
		itemsTable.setDebug(Constants.DEBUG);
		
		// The hotbar and inventory's first row share the same item references
		for (ObservableReference<Item<Player>> itemReference : player.items.hotbar) 
			itemsTable.add(new Slot<>(slotSize, itemReference, (Class<Item<Player>>)(Class<?>)Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
		itemsTable.row();
		// i starts at 1 because we created the hotbar row in the previous loop
		// TODO: remove hardcoded values
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 9; j++) 
				itemsTable.add(new Slot<>(slotSize, player.items.inventory.get(i*9 + j), (Class<Item<Player>>)(Class<?>)Item.class, player.items.getCursorItemReference())).width(slotSize).height(slotSize);
			itemsTable.row();
		}
		inventory.add(itemsTable);
		Table armorTable = new Table();
		armorTable.setDebug(Constants.DEBUG);
		Class[] armorTypes = new Class[]{HelmetItem.class, ChestplateItem.class, ChestplateItem.class, ChestplateItem.class};
		for (int i = 0; i < 4; i++) {
			// Armor slot
			armorTable.add(new Slot<>(slotSize, player.items.armor.get(i), (Class<EquippableItem<Player>>)armorTypes[i], player.items.getCursorItemReference())).width(slotSize).height(slotSize);
			// Accessory slot
			armorTable.add(new Slot<>(slotSize, player.items.accessory.get(i), (Class<EquippableItem<Player>>)(Class<?>)armorTypes[i], player.items.getCursorItemReference())).width(slotSize).height(slotSize);
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
