package com.mygdx.drop.etc.events.listeners;

import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.PropertyChangeEvent;

public class PropertyChangeEventListener<PropertyType> implements EventListener {
	private final Class<PropertyType> propertyType;
	
	public PropertyChangeEventListener(Class<PropertyType> propertyType) {
		this.propertyType = propertyType;
	}
	
	@Override
	public boolean handle(Event event) {
		if (!(event instanceof PropertyChangeEvent<?>)) 
			return false;
		
		PropertyChangeEvent<?> temp = (PropertyChangeEvent<?>)event;
		assert temp.newValue == null || propertyType.isInstance(temp.newValue);
		
		@SuppressWarnings("unchecked")
		PropertyChangeEvent<PropertyType> propertyChangeEvent = (PropertyChangeEvent<PropertyType>)temp;
		
		return onChange(propertyChangeEvent.getTarget(), propertyChangeEvent.getOldValue(), propertyChangeEvent.getNewValue());
	}
	
	public boolean onChange(Object target, PropertyType oldValue, PropertyType newValue) { return false; }

}
