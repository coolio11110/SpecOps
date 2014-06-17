package com.me.SpecOps.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.me.SpecOps.SpecOps;
import com.me.SpecOps.entities.Bullet;
import com.me.SpecOps.entities.Enemy;
import com.me.SpecOps.entities.Player;
import com.me.SpecOps.entities.Player.state;
import com.me.SpecOps.misc.CustInputMultiplexer;

public class Play extends AbstractScreen {
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	public static OrthographicCamera cam;
	private Player player;
	private Enemy enemy;
	private TiledMapTileLayer blocked;
	private TextureAtlas atlas;
	private Array<Sprite> agentSprites, enemySprites;
	private ArrayList<Bullet> bullets;
	private ShapeRenderer sr;
	float time = 0;
	private BitmapFont bf;
	private SpriteBatch sb;
	private TextButton shootButton;
	private Skin skin;
	private Stage stage;
	private Table table;
	TextButtonStyle textButtonStyle;
	private CustInputMultiplexer inputMultiplexer;

	public Play(SpecOps game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		renderer.setView(cam);
		renderer.render();
		
		cam.update();
		
		//draw button
		if(stage != null) {
			stage.act(delta);
			stage.draw();
		}
		
		//if(inputMultiplexer.getProcessors().get(0).touchDown(Gdx.input.getX(), screenY, 0, 0))
		
		//set animation time - 1sec per image
		time+=(delta*4);
		if(time >= 3)
			time = 0;
		
		moveEnemy(enemy);
		
		renderer.getSpriteBatch().begin();
		
		//player turn
		if(player.getState() != state.DEAD) {
			if(player.getMoves() <= 0) {
				//System.out.println("player " + player.getHealth());
				enemy.getOpponentPos(player.getX(), player.getY(), player.getDirection());
				player.setState(state.IDLE);
				Gdx.input.setInputProcessor(enemy);
				enemy.setTurn(true);
				player.setMoves(3);
			}
			
			animateAgent(time,player,agentSprites);
		}
		
		//enemy turn
		if(enemy.getState() != state.DEAD) {
			if(enemy.getMoves() <= 0) {
				//System.out.println("enemy " + enemy.getHealth());
				player.getOpponentPos(enemy.getX(), enemy.getY(), enemy.getDirection());
				enemy.setState(state.IDLE);
				enemy.setTurn(false);
				Gdx.input.setInputProcessor(inputMultiplexer);
				enemy.setMoves(3);
			}

			animateAgent(time,enemy,enemySprites);
		}
		
		//check player locations - test
		bf.setColor(Color.RED);
		bf.setScale(1);
		bf.draw(renderer.getSpriteBatch(),player.getsX() + " , " + player.getsY() + " -- " + player.getX() + " , " + player.getY(),20,70);
		//bf.draw(renderer.getSpriteBatch(),player.getX() + " , " + player.getsY(),1,1);
		
		sr.setProjectionMatrix(cam.combined);
		
		try {
		animateBullets(sr);
		} catch(IndexOutOfBoundsException e) {}
		
		renderer.getSpriteBatch().end();
		
		if(player.getState() == state.DEAD)
			game.setScreen(new GameOver(game,enemy.getId()));
		else if (enemy.getState() == state.DEAD)
			game.setScreen(new GameOver(game, player.getId()));
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportHeight = width;
		cam.viewportWidth = height;
		cam.update();
	}

	@Override
	public void show() {
		cam = new OrthographicCamera();
		cam.setToOrtho(false);
		cam.position.set(0,0,0);
		//for 512px -- 8.0f
		//cam.zoom = 0.3f;
		
		inputMultiplexer = new CustInputMultiplexer(Gdx.input.getInputProcessor());
		
		map = new TmxMapLoader().load("data/16pxdata/batMap16px.tmx");
		blocked = (TiledMapTileLayer) map.getLayers().get(1);
		renderer = new OrthogonalTiledMapRenderer(map);

		atlas = new TextureAtlas("data/16pxdata/specOps16px.txt");
		enemySprites = atlas.createSprites("enemy_16px");
		agentSprites = atlas.createSprites("agent_16px");
		
		//orient the camera to the center of the screen
		if(Gdx.app.getType().equals(ApplicationType.Android)) {
			//for 512px -- width - /2.0f, height - /5.0f
			cam.zoom = 0.2f;
			cam.position.set(Gdx.graphics.getWidth()/8.0f,Gdx.graphics.getHeight()/10.0f,0);

			stage = new Stage();
			skin = new Skin(atlas);
			table = new Table(skin);
			table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			textButtonStyle = new TextButtonStyle();
			textButtonStyle.up = skin.getDrawable("button");
			textButtonStyle.down = skin.getDrawable("button");
			textButtonStyle.pressedOffsetX = 1;
			textButtonStyle.pressedOffsetY = -1;
			textButtonStyle.font = new BitmapFont();
			textButtonStyle.fontColor = Color.BLACK;

			shootButton = new TextButton("",textButtonStyle);
			shootButton.pad(20);
			shootButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if(shootButton.isPressed()) {
						//Gdx.app.log("tag", enemy.getHealth() + "");
						player.shoot();
						return true;
					}
					return false;
				}
			});

			table.add(shootButton);
			table.align(Align.right);
			table.bottom();
			shootButton.align(Align.right);
			shootButton.bottom();
			stage.addActor(table);
			
			inputMultiplexer.addProcessor(stage);
		}
		else if(Gdx.app.getType().equals(ApplicationType.Desktop)) {
			//for 512px -- width - *2.47f, height - *4.2f
			stage = null;

			cam.zoom = 0.3f;
			cam.position.set(Gdx.graphics.getWidth()/5.65f,Gdx.graphics.getHeight()/6.5f,0);
		}


		cam.update();
		
		sr = new ShapeRenderer();
		bullets = new ArrayList<Bullet>();
		
		enemy = new Enemy(enemySprites,blocked,bullets,"e1");
		//for 512px -- x - 0.0f, y - 3584.1846f
		enemy.setPosition(48.0f, 176.0f);
		
		player = new Player(agentSprites,blocked,bullets,"p1",cam);
		//for 512px -- x - 0.0f, y - 0.0f
		player.setPosition(48.0f, 64.0f);

		inputMultiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		bf = new BitmapFont();
		sb = new SpriteBatch();
	}
	
	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		sr.dispose();
		sb.dispose();
		atlas.dispose();
		stage.dispose();
		skin.dispose();
	}
	
	//automatically move the enemy
	public void moveEnemy(Enemy enemy) {
		if(enemy.getTurn()) {		
			//convert to int to get values approx equal (float has extra decimal)
//			if(((int) (enemy.getX()+16.0f) == (int) (player.getX()) && enemy.getY() == player.getY()) || ((int) (enemy.getX()-16.0f) == (int) (player.getX()) && enemy.getY() == player.getY()) || ((int) (enemy.getY()+16.0f) == (int) (player.getY()) && enemy.getX() == player.getX()) || ((int) (enemy.getY()-16.0f) == (int) (player.getY()) && enemy.getX() == player.getX()) && enemy.getDirection() == (player.getDirection()-180.0f)) 
//				enemy.shoot();
//			else
			enemy.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	private void animateAgent(float delta, Player player, Array<Sprite> animArray) {
		if(player.getState().equals(state.WALKING)) {
			renderer.getSpriteBatch().draw(animArray.get((int) delta), player.getX(), player.getY(), player.getOriginX(), player.getOriginY(), player.getWidth(), player.getHeight(), player.getScaleX(), player.getScaleY(), player.getDirection());
			//System.out.println(player.getX() + " " + player.getY());
			}
		else if(player.getState().equals(state.IDLE)) {
			renderer.getSpriteBatch().draw(animArray.get(0), player.getX(), player.getY(), player.getOriginX(), player.getOriginY(), player.getWidth(), player.getHeight(), player.getScaleX(), player.getScaleY(), player.getDirection());
			player.isHit();
		}
		else if(shootButton.isPressed()) {
			player.shoot();
		}
		
	//	System.out.println(player.getId() + " " + player.getHealth());
	}

	private void animateBullets(ShapeRenderer shapeRenderer) {
		if(player.getBullets().size() != 0) {
			
			for(int i = 0; i < player.getBullets().size();i++) {
				if(player.getBullets().get(i).getRemove() == true)
					player.getBullets().remove(i);

				player.getBullets().get(i).draw(shapeRenderer);
			}
			
			
		}	
	}
}
