package com.mygdx.drop.actors;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

public class HUD extends Table {
	
	private Label labelHP;
	private Player player;
	
	public HUD(Player player, World world) {
		
		
		assert Drop.game != null : "Inventory created before game instance!";
		
		labelHP = new Label("HP:" + player.getStats().getHealth(), Assets.Skins.Glassy_glassy.get());
		
		this.player = player;
		
		//Health
		
		
		
		setFillParent(true);
		add(new Inventory(player)).top().right();

		
//		healthTable.top().left();
//		healthTable.padLeft(10f).padBottom(10f);
		add(labelHP).top().left();
		
		
		align(Align.topLeft);
		setFillParent(true);
//
		
		
		setDebug(Constants.DEBUG);
		

	}
	
	public void act (float delta) {
		
		super.act(delta);
		
		labelHP.setText("HP:" + player.getStats().getHealth());
		
	}
	
}
