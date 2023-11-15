package com.mygdx.drop.game.protocol;

import java.io.Serializable;

public class TerminateSession implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9186264418300607768L;
	
	public final String playerName;
	
		public TerminateSession (String playerName) {
			this.playerName = playerName;
		}

}
