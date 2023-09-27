package com.mygdx.drop.etc.events;

/**
 * Event for changes in a property
 *
 * @param <PropertyType> The type of the property that has changed
 */
public class PropertyChangeEvent<PropertyType> extends Event {
	public final Object target;
	public final PropertyType oldValue;
	public final PropertyType newValue;
	public PropertyChangeEvent(Object target, PropertyType oldValue, PropertyType newValue) { 
		assert target != null;
		this.target = target;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public Object getTarget() { return target; }
	public PropertyType getOldValue() { return oldValue; }
	public PropertyType getNewValue() { return newValue; }
}
