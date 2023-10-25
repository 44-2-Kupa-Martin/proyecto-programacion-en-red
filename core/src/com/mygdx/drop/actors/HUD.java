package com.mygdx.drop.actors;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.drop.Assets;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.PlayerManager;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;

public class HUD extends Table {
	
	private Label labelHP;
	private PlayerManager playerManager;
	private String playerName;
	
	public HUD(String playerName, PlayerManager playerManager) {
		
		
		assert Drop.game != null : "Inventory created before game instance!";
		this.playerManager = playerManager;
		this.playerName = playerName;
		labelHP = new Label("HP:" + playerManager.getStats(playerName).getHealth(), Assets.Skins.Glassy_glassy.get());		
		//Health
		
		
		
		setFillParent(true);
		add(new Inventory(playerManager, playerName)).top().right();

		
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
		
		labelHP.setText("HP:" + playerManager.getStats(playerName).getHealth());
		
	}
	
}
