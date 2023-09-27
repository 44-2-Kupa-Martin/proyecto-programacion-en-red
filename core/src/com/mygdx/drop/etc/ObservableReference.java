package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.handlers.EventHandler;
/**
 * Holds a reference to an object and notifies its listeners when it changes.
 *
 * @param <T> The type of the object.
 * @see Reference
 */
public class ObservableReference<T> extends Reference<T> implements EventListener {
	public Array<EventHandler> handlers = new Array<>();
	public ObservableReference(T object) { super(object); }

	@Override
	public void set(T object) {
		if (get() == object)
			return;
		T oldValue = get();
		super.set(object);
		PropertyChangeEvent<T> changeEvent = new PropertyChangeEvent<T>(this, oldValue, get());
		fire(changeEvent);
	}

	@Override
	public void addHandler(EventHandler handler) { handlers.add(handler); }

	@Override
	public boolean removeHandler(EventHandler handler) { return handlers.removeValue(handler, false); }

	@Override
	public boolean fire(Event event) {
		for (EventHandler eventHandler : handlers) 
			eventHandler.handle(event);
		return event.isCancelled(); 
	}
}
