package com.mygdx.drop;

import java.io.ByteArrayInputStream;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.game.ServerThread;
import com.mygdx.drop.game.protocol.DiscoverWorld;

public class MultiplayerScreen implements Screen {
	
	private final Drop game;
	private final Stage stage;
	private final OrthographicCamera camera;
	private Skin skin;
	private final Viewport viewport;
	private Label ipLabel, portLabel;
	private TextButton backButton, connectButton, hostButton;
	private TextField ipField, portField;
	private UDPThread udpThread;
	ArrayList<WorldDiscovery> servers = new ArrayList<WorldDiscovery>();
	private String backTexts[] = {"Back to Main Menu", "Back"};
	private Table serverTable;
	
	
	public MultiplayerScreen(Drop game){
		
		UDPThread udpThread = null;
		try {
			udpThread = new UDPThread(InetAddress.getByName("192.168.1.255"), 5669, this::recievedPacket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
			udpThread.socket.send(UDPThread.serializeObjectToPacket(udpThread.socket.getRemoteSocketAddress(), new DiscoverWorld()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		skin = Assets.Skins.Glassy_glassy.get();
		
		ipLabel = new Label("Server IP:", skin);
		ipLabel.setVisible(false);
		
		portLabel = new Label("Server Port:", skin);
		portLabel.setVisible(false);
		
		
		connectButton = new TextButton("Connect to a server", skin, "small");
		connectButton.setTransform(true);
		

		
		hostButton = new TextButton("Host a server", skin, "small");
		hostButton.setTransform(true);
		
		backButton = new TextButton(backTexts[0], skin, "small");
		backButton.setTransform(true);
		
		ipField = new TextField("", skin);
		portField = new TextField("",skin);
		
		Table addressTable = new Table();
		serverTable = new Table();
		Table buttonsTable = new Table();
		
		addressTable.setVisible(false);
		serverTable.setVisible(false);
		
		serverTable.setFillParent(true);
		serverTable.center();
		
		connectButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
					ipLabel.setVisible(true);
					portLabel.setVisible(true);
					hostButton.setVisible(false);
					addressTable.setVisible(true);
					serverTable.setVisible(true);
					
					backButton.setText(backTexts[1]);
					
					buttonsTable.center().bottom();
					
					
					
				}
			});
		
		hostButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				 game.serverThread = new ServerThread(Constants.WORLD_WIDTH_tl, Constants.WORLD_HEIGHT_tl, new Vector2(0, -10), 1/60f);
			     game.serverThread.start();
		         Client client = null;
		        
				 try {
					client = new Client("fullaccess", InetAddress.getByName("127.0.0.1"));
				 } catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
			      while (client.notConnected);
		        	game.setScreen(new GameScreen(game, "fullaccess", client));
				  dispose();
					
					
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
				    ipLabel.setVisible(false);
				    portLabel.setVisible(false);
				    hostButton.setVisible(true);
				    addressTable.setVisible(false);
				    serverTable.setVisible(false);
				    
				    backButton.setText(backTexts[0]);
				    buttonsTable.center();
				}

				
				
					}
				});
				
		ipField.setTextFieldListener(new TextField.TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				try {
						int ipAdress;
		                ipAdress = Integer.parseInt(textField.getText());
		                game.ipAdress = ipAdress;
				} catch (NumberFormatException e) {
		                    //TODO This is bad, cry about it.
		                }
		            }
		        });
				
		portField.setTextFieldListener(new TextField.TextFieldListener() {
		    @Override
			public void keyTyped(TextField textField, char key) {
					try {
		                	int portAdress;
		                	portAdress = Integer.parseInt(textField.getText());
		                    game.portAdress = portAdress;
		            } catch (NumberFormatException e) {
		                    //TODO This is bad, cry about it.
		                }
		            }
		        });
		
		addressTable.setFillParent(true);
		addressTable.center().top();
				
		addressTable.add(ipLabel);
		addressTable.add(ipField).row();
				
		addressTable.add(portLabel);
		addressTable.add(portField).row();
				
				
		buttonsTable.setFillParent(true);
				
		buttonsTable.add(connectButton).row();
		buttonsTable.add(hostButton).row();
		buttonsTable.add(backButton);
		
		serverTable.debug();
		
		serverTable.setFillParent(true);
		serverTable.center();
		

				
		stage.addActor(addressTable);
		stage.addActor(buttonsTable);
		stage.addActor(serverTable);
		
	}


	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
				
	}

	@Override
	public void render(float delta) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(delta);
			stage.draw();
			
			for (int i = 0; i < servers.size(); i++) {
				
				serverTable.clear();
				
			    WorldDiscovery server = servers.get(i);

			    Label worldLabel = new Label(server.worldName, skin);
			    TextButton joinButton = new TextButton("Join", skin, "small");

			    serverTable.add(worldLabel).left();
			    serverTable.add(joinButton).right().row();
			}
			
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
			
		}
	
	
	private void recievedPacket(DatagramPacket packet) {
		
		ObjectInputStream input = null;
		Serializable object = UDPThread.deserializeObjectFromPacket(packet);
		
		if (object == null) {
			System.out.println("Couldnt deserialize");
		}else if(object instanceof WorldDiscovery) {
			WorldDiscovery update = (WorldDiscovery) object;
			
			servers.add(update);
			
			System.out.println("hola");
		}
		
	}
	
	
	
	
	}
