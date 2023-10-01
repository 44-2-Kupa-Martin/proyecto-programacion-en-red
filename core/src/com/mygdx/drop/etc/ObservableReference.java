package com.mygdx.drop.etc;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
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
	private Array<EventHandler> handlers = new Array<>();
	private Queue<Event> eventQueue = new Queue<>();
	private boolean firing = false;
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
	public void fire(Event event) {
		eventQueue.addLast(event);
		if (firing) 
			return;
		
		firing = true;
		while (eventQueue.size != 0) {			
			Event queuedEvent = eventQueue.removeFirst();
			for (int i = 0; i < handlers.size; i++) {
				EventHandler eventHandler = handlers.get(i);
				eventHandler.handle(queuedEvent);
				if (queuedEvent.isStopped()) 
					break;
			}
		}
		firing = false;
	}
}
