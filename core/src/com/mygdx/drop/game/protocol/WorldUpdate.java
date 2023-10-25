package com.mygdx.drop.game.protocol;

import java.io.Serializable;

import com.mygdx.drop.game.PlayerManager.FrameComponent;
import com.mygdx.drop.game.PlayerManager.ItemData;
import com.mygdx.drop.game.Stats;

public class WorldUpdate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2630020167561646607L;
	public final FrameComponent[] frameData;
	public final ItemData[] itemData;
	public final int lastSelectedSlot;
	public final float playerX;
	public final float playerY;
	public final Stats playerStats;
	public WorldUpdate(FrameComponent[] frameData, ItemData[] itemData, int lastSelectedSlot, float playerX, float playerY, Stats playerStats) {
		super();
		this.frameData = frameData;
		this.itemData = itemData;
		this.lastSelectedSlot = lastSelectedSlot;
		this.playerX = playerX;
		this.playerY = playerY;
		this.playerStats = playerStats;
	}
}
