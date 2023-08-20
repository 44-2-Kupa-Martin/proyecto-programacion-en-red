package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.handlers.EventHandler;
/**
 * Holds a reference to an object and notifies its listeners when it changes.
 *
 * @param <T> The type of the object.
 * @see Reference
 */
public class ObservableReference<T> extends Reference<T> {
	public Array<EventHandler<PropertyChangeEvent<T>>> handlers = new Array<>();
	public ObservableReference(T object) { super(object); }

	@Override
	public void set(T object) {
		if (get() == object)
			return;
		T oldValue = get();
		super.set(object);
		PropertyChangeEvent<T> changeEvent = new PropertyChangeEvent<T>(this, oldValue, get());
		asPropertyChangeEventListener().fire(changeEvent);
	}

	/**
	 * Allows for registering property change event handlers
	 */
	public EventListener<PropertyChangeEvent<T>> asPropertyChangeEventListener() {
		return new EventListener<PropertyChangeEvent<T>>() {

			@Override
			public void addHandler(EventHandler<PropertyChangeEvent<T>> handler) { handlers.add(handler); }

			@Override
			public boolean removeHandler(EventHandler<PropertyChangeEvent<T>> handler) { return handlers.removeValue(handler, false); }

			@Override
			public boolean fire(PropertyChangeEvent<T> event) {
				event.setTarget(ObservableReference.this);
				for (EventHandler<PropertyChangeEvent<T>> eventHandler : handlers) 
					eventHandler.handle(event);
				return event.isCancelled(); 
			}
		};
	}
}
