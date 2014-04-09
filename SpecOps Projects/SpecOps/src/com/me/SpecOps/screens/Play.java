package com.me.SpecOps.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.Array;
import com.me.SpecOps.SpecOps;
import com.me.SpecOps.entities.Bullet;
import com.me.SpecOps.entities.Enemy;
import com.me.SpecOps.entities.Player;
import com.me.SpecOps.entities.Player.state;

public class Play extends AbstractScreen {
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera cam;
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

	public Play(SpecOps game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		renderer.setView(cam);
		renderer.render();
		
		//orient the camera to the center of the screen
		if(Gdx.app.getType().equals(ApplicationType.Android)) {
			cam.position.set(Gdx.graphics.getWidth()/2.0f,Gdx.graphics.getHeight()/5.0f,0);
		}
		else if(Gdx.app.getType().equals(ApplicationType.Desktop)) {
			cam.position.set(Gdx.graphics.getWidth()*2.47f,Gdx.graphics.getHeight()*4.2f,0);
		}
		
		cam.update();
		
		time+=(delta*4);
		if(time >= 3)
			time = 0;
		
		moveEnemy(enemy);
		
		renderer.getSpriteBatch().begin();
		
		if(player.getState() != state.DEAD) {
			if(player.getMoves() <= 0) {
				enemy.getPlayerPos(player.getX(), player.getY());
				player.setState(state.IDLE);
				Gdx.input.setInputProcessor(enemy);
				enemy.setTurn(true);
				player.setMoves(3);
			}
			
			animateAgent(time,player,agentSprites);
		}
		
		if(enemy.getState() != state.DEAD) {
			if(enemy.getMoves() <= 0) {
				enemy.setState(state.IDLE);
				enemy.setTurn(false);
				Gdx.input.setInputProcessor(player);
				enemy.setMoves(3);
			}

			animateAgent(time,enemy,enemySprites);
		}
		
		bf.setColor(Color.RED);
		bf.setScale(15);
		bf.draw(renderer.getSpriteBatch(),player.getsX() + " , " + player.getsY(),0,0);
		//bf.draw(renderer.getSpriteBatch(),player.getX() + " , " + player.getsY(),1,1);
		
		sr.setProjectionMatrix(cam.combined);
		
		try {
		animateBullets(sr);
		} catch(IndexOutOfBoundsException e) {}
		
		renderer.getSpriteBatch().end();
		
		if(enemy.getState() == state.DEAD || player.getState() == state.DEAD)
			game.setScreen(new GameOver(game));
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
		cam.zoom = 8.0f;
		cam.update();
		
		map = new TmxMapLoader().load("data/batMap.tmx");
		blocked = (TiledMapTileLayer) map.getLayers().get(1);
		renderer = new OrthogonalTiledMapRenderer(map);

		atlas = new TextureAtlas("data/specOps.txt");
		enemySprites = atlas.createSprites("enemy");
		agentSprites = atlas.createSprites("agent");
				
		sr = new ShapeRenderer();
		bullets = new ArrayList<Bullet>();
		
		enemy = new Enemy(enemySprites,blocked,bullets,"e1");
		enemy.setPosition(0.0f, 3584.1846f);
		
		player = new Player(agentSprites,blocked,bullets,"p1");
		player.setPosition(0.0f, 0.0f);
		Gdx.input.setInputProcessor(player);
		
		bf = new BitmapFont();
		sb = new SpriteBatch();
	}
	
	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		sr.dispose();
		sb.dispose();
	}
	
	//automatically move the enemy
	public void moveEnemy(Enemy enemy) {
		if(enemy.getTurn()) {		
			//convert to int to get values approx equal (float has extra decimal)
			if(((int) (enemy.getX()+512.02637) == (int) (player.getX()) && enemy.getY() == player.getY()) || ((int) (enemy.getX()-512.02637) == (int) (player.getX()) && enemy.getY() == player.getY()) || ((int) (enemy.getY()+512.02637) == (int) (player.getY()) && enemy.getX() == player.getX()) || ((int) (enemy.getY()-512.02637) == (int) (player.getY()) && enemy.getX() == player.getX()) && enemy.getDirection() == (player.getDirection()-180.0f)) 
				enemy.shoot();
			else
				enemy.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	private void animateAgent(float delta, Player player, Array<Sprite> animArray) {
		if(player.getState().equals(state.WALKING)) {
			renderer.getSpriteBatch().draw(animArray.get((int) delta), player.getX(), player.getY(), player.getOriginX(), player.getOriginY(), player.getWidth(), player.getHeight(), player.getScaleX(), player.getScaleY(), player.getDirection());
			}
		else if(player.getState().equals(state.IDLE)) {
			renderer.getSpriteBatch().draw(animArray.get(0), player.getX(), player.getY(), player.getOriginX(), player.getOriginY(), player.getWidth(), player.getHeight(), player.getScaleX(), player.getScaleY(), player.getDirection());
			player.isHit();
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
