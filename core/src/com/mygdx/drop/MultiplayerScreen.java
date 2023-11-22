package com.mygdx.drop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.game.ServerThread;
import com.mygdx.drop.game.protocol.DiscoverWorld;

public class MultiplayerScreen implements Screen {
	
	private final Drop game;
	private final Stage stage;
	private final OrthographicCamera camera;
	private final Viewport viewport;
	private Skin skin;
	private Label ipLabel, portLabel;
	private TextButton backButton, hostButton, connectButton, saveButton;
	private TextField nameField;
	private UDPThread udpThread;
	ArrayList<WorldDiscovery> servers = new ArrayList<WorldDiscovery>();
	private String backTexts[] = {"Back to Main Menu", "Back"};
	private Table serverTable;
	private String playerName;
	private long startTime = TimeUtils.millis();
	private boolean filledTable = false;
	
	
	public MultiplayerScreen(Drop game){
		
		
		
		UDPThread udpThread = null;
		udpThread = new UDPThread( this::recievedPacket, ()->{});
		
		
		this.game = game;
		this.camera = new OrthographicCamera();
		this.viewport = new ScreenViewport(camera);
		this.stage = new Stage(viewport);
		camera.setToOrtho(false, 800, 480);
		this.udpThread = udpThread; 
		udpThread.start();
		
		try {
			udpThread.socket.setBroadcast(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("probando");
			udpThread.socket.send(UDPThread.serializeObjectToPacket(InetAddress.getByName("255.255.255.255"),5669, new DiscoverWorld()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		skin = Assets.Skins.Glassy_glassy.get();
	
		
		connectButton = new TextButton ("Connect to a server", skin, "small");
		connectButton.setVisible(false);
		
		saveButton = new TextButton("Save name", skin, "small");
		
		hostButton = new TextButton("Host a server", skin, "small");
		hostButton.setVisible(false);
		
		backButton = new TextButton(backTexts[0], skin, "small");
		
		nameField = new TextField("",skin);
		
		serverTable = new Table();
		Table buttonsTable = new Table();
		Table nameTable = new Table();
		
		serverTable.setVisible(false);
		
		
		serverTable.setFillParent(true);
		serverTable.center();
		
		saveButton.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y) {
				

				nameTable.clear();
				
				hostButton.setVisible(true);
				connectButton.setVisible(true);
				
				playerName = nameField.getText();
				
			}
			
		});
		
		connectButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
					
					hostButton.setVisible(false);
					serverTable.setVisible(true);
					connectButton.setVisible(false);
					
					backButton.setText(backTexts[1]);
					
					buttonsTable.center().bottom();
					
					
				}
			});
		
		hostButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				game.serverThread = new ServerThread(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10), 1/60f, playerName);
			    game.serverThread.start();
	 
				try {
					
					game.setScreen(new LoadingScreen(InetAddress.getByName("127.0.0.1"), playerName, game));
					dispose();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					
					
				}
			});
				
		// Add a click listener to the "Back" button
		backButton.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) { 
				
				
				if (backButton.getText().toString().equals(backTexts[0])) {
				    game.setScreen(new MainMenuScreen(game)); 
				    dispose();
				} else if (backButton.getText().toString().equals(backTexts[1])) {
				    
				    hostButton.setVisible(true);
				    connectButton.setVisible(true);
				    serverTable.setVisible(false);
				    
				    backButton.setText(backTexts[0]);
				    buttonsTable.center();
				}

				
				
					}
				});
				
				
		
		nameTable.setFillParent(true);
		nameTable.center();
		
		nameTable.add(nameField);
		nameTable.add(saveButton);		
				
		buttonsTable.setFillParent(true);
				
		buttonsTable.add(connectButton).row();
		buttonsTable.add(hostButton).row();
		buttonsTable.add(backButton);
		
		
		serverTable.center();
		

		stage.addActor(nameTable);
		stage.addActor(buttonsTable);
		stage.addActor(serverTable);
		
			
		
		Gdx.input.setInputProcessor(stage);
		
	}


	@Override
	public void show() {
		
				
	}

	@Override
	public void render(float delta) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			
			if(TimeUtils.timeSinceMillis(startTime) / 1000f > 5 && !filledTable) {
				
				for (int i = 0; i < servers.size(); i++) {
					
				    WorldDiscovery server = servers.get(i);

				    Label worldLabel = new Label(server.worldName, skin);
				    TextButton joinButton = new TextButton("Join", skin, "small");
				    
				    joinButton.setTouchable(null);
			

				    serverTable.add(worldLabel).left();
				    serverTable.add(joinButton).right().row();
				    
				    
				    joinButton.addListener(new ClickListener() {
				    	
				    	@Override
						public void clicked(InputEvent event, float x, float y) {
							
				    			
				    			game.setScreen(new LoadingScreen(server, playerName, game));
								dispose();
							}
						});
				}
				filledTable = true;
			}
			
			
			
			stage.act();
			stage.draw();
			
		}
	
	@Override
	public void resize(int width, int height) {
			
		}
	
	@Override
	public void pause() {
			
		}
	
	@Override
	public void resume() {
			
		}
	
	@Override
	public void hide() {
			
		}
	
	@Override
	public void dispose() {
			
			stage.dispose();
			udpThread.interrupt();
			
		}
	
	
	
	private void recievedPacket(DatagramPacket packet) {
		
		ObjectInputStream input = null;
		Serializable object = UDPThread.deserializeObjectFromPacket(packet);
		
		if (object == null) {
			System.out.println("Couldnt deserialize");
		}else if(object instanceof WorldDiscovery) {
			WorldDiscovery update = (WorldDiscovery) object;
			
			servers.add(update);
			
			
		} else {
			
		}
		
	}
	
	
	
	
	}
