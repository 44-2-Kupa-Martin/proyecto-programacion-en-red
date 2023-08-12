package com.mygdx.drop.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Player;
import com.mygdx.drop.game.World;

public class HUD extends Table {
	private final Drop game;
	
	public HUD(Player player, World world) {
		assert Drop.game != null : "Inventory created before game instance!";
		this.game = Drop.game;
		setDebug(Constants.DEBUG);
		align(Align.topLeft);
		setFillParent(true);

		add(new Inventory(player)).fill()

			.top().left().expand();
		add(new Inventory(player)).fill().top().left().expand();
		row();
		add(new Inventory(player)).fill().top().left().expand();
		add(new Inventory(player)).fill().top().left().expand();
		row();
	}
	
}
