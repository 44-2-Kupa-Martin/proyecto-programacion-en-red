package com.mygdx.drop.etc.events;

/**
 * Event for changes in a property
 *
 * @param <PropertyType> The type of the property that has changed
 */
public class PropertyChangeEvent<PropertyType> extends Event<Object> {
	public PropertyType oldValue;
	public PropertyType newValue;
	public PropertyChangeEvent(Object target, PropertyType oldValue, PropertyType newValue) { 
		super(target);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
