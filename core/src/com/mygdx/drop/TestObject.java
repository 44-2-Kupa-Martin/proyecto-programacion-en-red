package com.mygdx.drop;

import java.io.Serializable;

public class TestObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6731391076035434317L;
	public final int x;
	public final int y;
	public final int assetId;
	public TestObject(int x, int y, int assetId) {
		this.x = x;
		this.y = y;
		this.assetId = assetId;
	}

	
	
}
