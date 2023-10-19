package com.mygdx.drop;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.listeners.EventListener;

/**
 * An event manager. Events are dispatched on demand, that is when {@link #fire()} is called.
 */
public class EventManager {
	/** a queue for event chaining */
	private static final Queue<Event<?>> eventQueue = new Queue<>();
	/** these will always be notified first, regardless of the event's target */
	private static final Array<EventListener> globalListeners = new Array<>();
	
	/** A safeguard to prevent event chaining from iterating over events twice */
	private static boolean firing;
	
	public void addGlobalListener(EventListener listener) { globalListeners.add(listener); }
	
	public boolean removeGlobalListener(EventListener listener) { return globalListeners.removeValue(listener, false); }
	
	/**
	 * Fires an event on the target
	 * 
	 * @param event The event to be fired
	 */
	public static void fire(Event<?> event) {
		eventQueue.addLast(event);
		if (firing) 
			return;
		
		firing = true;
		while (eventQueue.size != 0) {
			Event<?> queuedEvent = eventQueue.removeFirst();
			for (int i = 0; i < globalListeners.size; i++) {
				EventListener eventListener = globalListeners.get(i);
				eventListener.handle(queuedEvent);
				if (queuedEvent.isStopped()) 
					break;
				
			}
			
			if (queuedEvent.target == null || queuedEvent.isStopped()) 
				continue;
			
			Array<EventListener> listeners = queuedEvent.target.getListeners();
			for (EventListener eventListener : listeners) {
				eventListener.handle(queuedEvent);
				if (queuedEvent.isStopped()) 
					break;
			}
		}
		firing = false;
	}

}
