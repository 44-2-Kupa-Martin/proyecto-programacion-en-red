package com.mygdx.drop;
/**
 * An abstract Debug class that implements safeguards that are common to all Debug classes. A Debug class contains attributes of a class that are used only on 
 * debug builds. A class may have a nested Debug class holding said attributes; all nested Debug classes must inherit from this class.
 */
public abstract class Debug {
	@SuppressWarnings("unused")
	protected Debug() {
		assert Constants.DEBUG : "Debug object created in non-debug build!";
		if (!Constants.DEBUG) {
			throw new RuntimeException("Debug object created in non-debug build!");
		}
		assert !isConstructed() : "Debug object is constructed multiple times";
		setConstructed(true);
	}
	
	// All debug classes are singletons, so they all must implement a static flag indicating whether an instance was created
	protected abstract boolean isConstructed();
	protected abstract void setConstructed(boolean value);
}
