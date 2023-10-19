package com.mygdx.drop.etc.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.etc.ContactEventFilter;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.game.World;

/**
 * Event for handling collisions between fixtures. The event is {@link #handle() handled} if any listener considers their operations successful and wishes to mark the event as such, note it is not required to
 */
public class ContactEvent<T extends EventCapable> extends Event<T> {
	public final Type eventType;
	public final Contact contact;
	public final @Null Manifold manifold;
	public final @Null ContactImpulse contactImpulse;
	
	public ContactEvent(T target, Contact contact, Type eventType) {
		this(target, contact, eventType, (Manifold)null, (ContactImpulse)null);
	}
	
	public ContactEvent(T target, Contact contact, Type eventType, Manifold manifold) {
		this(target, contact, eventType, manifold, (ContactImpulse)null);
	}
	
	public ContactEvent(T target, Contact contact, Type eventType, ContactImpulse contactImpulse) {
		this(target, contact, eventType, (Manifold)null, contactImpulse);
	}
	
	public ContactEvent(T target, Contact contact, Type eventType, Manifold manifold, ContactImpulse contactImpulse) {
		super(target);
		assert eventType != null;
		this.eventType = eventType;
		assert contact != null;
		this.contact = contact;
		this.manifold = manifold;
		this.contactImpulse = contactImpulse;
	}
	
	public Contact getContact() { return contact; }
	public Manifold getManifold() { return manifold; }
	public ContactImpulse getContactImpulse() { return contactImpulse; }
	
	public enum Type {
		preSolve,
		postSolve,
		beginContact,
		endContact;
	}
}

