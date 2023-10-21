package com.mygdx.drop;

import java.io.Serializable;

public class TestObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6731391076035434317L;
	public int num1;
	public int num2;
	public TestObject(int num1, int num2) {
		this.num1 = num1;
		this.num2 = num2;
	}
	
}
