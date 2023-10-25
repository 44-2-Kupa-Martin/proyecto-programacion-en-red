package com.mygdx.drop.game.protocol;

import java.io.Serializable;

public class SessionRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5534485228962428071L;
	public final String playerName;

	public SessionRequest(String playerName) {
		super();
		this.playerName = playerName;
	}
}
