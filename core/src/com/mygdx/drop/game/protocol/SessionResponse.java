package com.mygdx.drop.game.protocol;

import java.io.Serializable;

import com.mygdx.drop.game.Stats;

public class SessionResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4958865548803095600L;
	public final boolean accepted;
	public final int worldWidth_tl, worldHeight_tl;
	public final Stats playerStats;

	public SessionResponse(boolean accepted, int worldWidth_tl, int worldHeight_tl, Stats playerStats) {
		super();
		this.accepted = accepted;
		this.worldWidth_tl = worldWidth_tl;
		this.worldHeight_tl = worldHeight_tl;
		this.playerStats = playerStats;
	}
	
}
