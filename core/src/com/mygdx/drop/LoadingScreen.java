package com.mygdx.drop;

import java.net.InetAddress;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen implements Screen{
	
	private Label loadingLabel;
	private String playerName;
	private final Drop game;
	private InetAddress serverIP;
	private final Viewport viewport;
	private final OrthographicCamera camera;
	private final Stage stage;
	private final Client client;
	
	public LoadingScreen(WorldDiscovery server, String playerName, Drop game) {
		this(server.IP, playerName, game);
		
	}
	
	public LoadingScreen(InetAddress serverIP, String playerName, Drop game) {
		
		this.serverIP = serverIP;
		this.playerName = playerName;
		this.game = game;
		this.camera = new OrthographicCamera();
		this.viewport = new ScreenViewport(camera);
		this.stage = new Stage(viewport);
		camera.setToOrtho(false, 800, 480);
		
		loadingLabel = new Label("", Assets.Skins.Glassy_glassy.get());
		
		Table loadingTable = new Table();
		
		loadingTable.add(loadingLabel).center();
	      
		client = new Client(playerName, serverIP);
		
	}

	@Override
	public void show() {
		
	      
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
		if (client.connected) {
	    	  game.setScreen(new GameScreen(game, playerName, client));
		  dispose();
	      } 
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	

}
