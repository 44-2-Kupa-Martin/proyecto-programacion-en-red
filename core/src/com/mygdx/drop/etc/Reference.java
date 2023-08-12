package com.mygdx.drop.etc;
/**
 * Holds a reference to an object.
 *
 * @param <T> The type of the object
 */
public class Reference<T> {
	private T object;
	
	public Reference(T object) { this.object = object; }
	
	public T get() { return object; };
	public void set(T object) { this.object = object; }
}
