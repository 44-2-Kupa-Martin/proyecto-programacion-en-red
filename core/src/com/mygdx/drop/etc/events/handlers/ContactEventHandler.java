package com.mygdx.drop.etc.events.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.game.World;

/**
 * Unpacks {@link ContactEvent}s
 */
public class ContactEventHandler implements EventHandler {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ContactEvent)) 
			return false;
		ContactEvent contactEvent = (ContactEvent)event;
		switch (contactEvent.eventType) {
			case preSolve:
				return preSolve(contactEvent.getWorld(), contactEvent.getContact(), contactEvent.getManifold());
				
			case postSolve:
				return postSolve(contactEvent.getWorld(), contactEvent.getContact(), contactEvent.getContactImpulse());
				
			case beginContact:
				return beginContact(contactEvent.getWorld(), contactEvent.getContact());

			case endContact:
				return endContact(contactEvent.getWorld(), contactEvent.getContact());
			
		}
		throw new RuntimeException("Unreachable");
	}
	
	public boolean preSolve(World world, Contact contact, Manifold oldManifold) { return false; }
	public boolean postSolve(World world, Contact contact, ContactImpulse impulse) { return false; }
	public boolean beginContact(World world, Contact contact) { return false; }
	public boolean endContact(World world, Contact contact) { return false; }
}
