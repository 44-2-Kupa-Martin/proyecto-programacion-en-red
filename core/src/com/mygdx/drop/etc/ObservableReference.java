package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.PropertyChangeEvent;
import com.mygdx.drop.etc.events.listeners.EventListener;
/**
 * Holds a reference to an object and notifies its listeners when it changes.
 *
 * @param <T> The type of the object.
 * @see Reference
 */
public class ObservableReference<T> extends Reference<T> implements EventCapable {
	private Array<EventListener> listeners = new Array<>();
	public ObservableReference(T object) { super(object); }

	@Override
	public void set(T object) {
		if (get() == object)
			return;
		T oldValue = get();
		super.set(object);
		PropertyChangeEvent<T> changeEvent = new PropertyChangeEvent<T>(this, oldValue, get());
		EventManager.fire(changeEvent);
	}

	@Override
	public void addListener(EventListener listener) { listeners.add(listener); }

	@Override
	public boolean removeListener(EventListener listener) { return listeners.removeValue(listener, false); }

	@Override
	public Array<EventListener> getListeners() { return listeners; }
}
