package com.mygdx.drop.etc.events;

import com.mygdx.drop.etc.ObservableReference;

/**
 * Event for changes in a property. The event is {@link PropertyChangeEvent#handle() handled} if any listener considers their operations successful and wishes to mark the event as such, note it is not required to
 *
 * @param <PropertyType> The type of the property that has changed.
 */
public class PropertyChangeEvent<PropertyType> extends Event<ObservableReference<PropertyType>> {
	public final PropertyType oldValue;
	public final PropertyType newValue;
	public PropertyChangeEvent(ObservableReference<PropertyType> target, PropertyType oldValue, PropertyType newValue) {
		super(target);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public PropertyType getOldValue() { return oldValue; }
	public PropertyType getNewValue() { return newValue; }
}
