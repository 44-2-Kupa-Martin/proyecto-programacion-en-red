package com.mygdx.drop.etc.events.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.game.World;

/**
 * Unpacks {@link ContactEvent}s
 */
public class ContactEventHandler implements EventHandler<ContactEvent> {

	@Override
	public boolean handle(ContactEvent event) { 
		switch (event.eventType) {
			case preSolve:
				return preSolve(event.getTarget(), event.getContact(), event.getManifold());
				
			case postSolve:
				return postSolve(event.getTarget(), event.getContact(), event.getContactImpulse());
				
			case beginContact:
				return beginContact(event.getTarget(), event.getContact());

			case endContact:
				return endContact(event.getTarget(), event.getContact());
			
			default:
				throw new RuntimeException("Unreachable");
		}
	}
	
	public boolean preSolve(World world, Contact contact, Manifold oldManifold) { return false; }
	public boolean postSolve(World world, Contact contact, ContactImpulse impulse) { return false; }
	public boolean beginContact(World world, Contact contact) { return false; }
	public boolean endContact(World world, Contact contact) { return false; }
}
