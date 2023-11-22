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
	private Label labelPoints;
	private PlayerManager playerManager;
	private String playerName;
	
	public HUD(String playerName, PlayerManager playerManager) {
		
		
		assert Drop.game != null : "Inventory created before game instance!";
		this.playerManager = playerManager;
		this.playerName = playerName;
		labelHP = new Label("HP:" + playerManager.getStats(playerName).getHealth(), Assets.Skins.Glassy_glassy.get());		
		labelPoints = new Label("Points: " + playerManager.getStats(playerName).getPoints(), Assets.Skins.Glassy_glassy.get());		
		
		
		
		setFillParent(true);
		add(new Inventory(playerManager, playerName)).top().right();

		
		add(labelHP).top().left();
		add(labelPoints).top().left();
		
		
		align(Align.topLeft);
		setFillParent(true);
		
		
		setDebug(Constants.DEBUG);
		

	}
	
	public void act (float delta) {
		
		super.act(delta);
		
		labelHP.setText("HP:" + playerManager.getStats(playerName).getHealth());
		labelPoints.setText("Points: " + playerManager.getStats(playerName).getPoints());
		
	}
	
}
