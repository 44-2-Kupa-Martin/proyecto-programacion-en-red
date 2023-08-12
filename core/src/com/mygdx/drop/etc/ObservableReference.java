package com.mygdx.drop.etc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
/**
 * Holds a reference to an object and notifies its listeners when it changes.
 *
 * @param <T> The type of the object.
 */
public class ObservableReference<T> extends Reference<T> {
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	public ObservableReference(T object) { super(object); }

	@Override
	public void set(T object) {
		T oldValue = get();
		super.set(object); 
		changeSupport.firePropertyChange("change", oldValue, get());
	}
	
	public final void addListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}
	
	public final void removeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
}
