package com.mygdx.drop.etc;

public class SimpleContactEventFilter<TypeObjectA> extends ContactEventFilter<TypeObjectA, Object> {

	public SimpleContactEventFilter(Class<TypeObjectA> typeA) { 
		super(typeA, null);
	}

}
