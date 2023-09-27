package com.mygdx.drop.actors;


import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

public class HUD extends Table {
	

	
	public HUD(Player player, World world) {
		
		
		assert Drop.game != null : "Inventory created before game instance!";
		
		//Health
		
		Table healthTable = new Table();
		
		setFillParent(true);
		healthTable.top().left();
		healthTable.padLeft(10f).padBottom(10f);
		
//		align(Align.topLeft);
//		setFillParent(true);
//
//		add(new Inventory(player)).top().left();
//		row();
		
		setDebug(Constants.DEBUG);
		

	}
	
}
