package com.mygdx.drop.etc.events.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.game.World;

/**
 * Unpacks {@link ContactEvent}s
 */
public class ContactEventListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ContactEvent)) 
			return false;
		ContactEvent contactEvent = (ContactEvent)event;
		switch (contactEvent.eventType) {
			case preSolve:
				return preSolve(contactEvent);
				
			case postSolve:
				return postSolve(contactEvent);
				
			case beginContact:
				return beginContact(contactEvent);

			case endContact:
				return endContact(contactEvent);
			
		}
		throw new RuntimeException("Unreachable");
	}
	
	public boolean preSolve(ContactEvent event) { return false; }
	public boolean postSolve(ContactEvent event) { return false; }
	public boolean beginContact(ContactEvent event) { return false; }
	public boolean endContact(ContactEvent event) { return false; }
}
