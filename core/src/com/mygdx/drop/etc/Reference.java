package com.mygdx.drop.etc;
/**
 * Holds a reference to an object. Allows objects to pass a field "by reference"
 *
 * @param <T> The type of the object being referenced
 */
public class Reference<T> {
	private T object;
	
	public Reference(T object) { this.object = object; }
	
	public T get() { return object; };
	public void set(T object) { this.object = object; }
}
