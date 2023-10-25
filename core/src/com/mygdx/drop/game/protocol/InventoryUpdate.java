package com.mygdx.drop.game.protocol;

import java.io.Serializable;

public class InventoryUpdate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8654586382456003567L;
	public final String playerName;
	public final Type type;
	public final int swapIndex1;
	public final int swapIndex2;
	
	public InventoryUpdate(String playerName, Type type, int swapIndex1, int swapIndex2) {
		this.playerName = playerName;
		this.type = type;
		this.swapIndex1 = swapIndex1;
		this.swapIndex2 = swapIndex2;
	}

	public enum Type {
		ITEM_SWAP;
	}
}
