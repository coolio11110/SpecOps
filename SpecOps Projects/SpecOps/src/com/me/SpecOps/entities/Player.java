package com.me.SpecOps.entities;


import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite implements InputProcessor  {
	public enum state {
		IDLE, WALKING, DEAD
	}
	
	private static Vector2 velocity = new Vector2(); //movement velocity
	private static float speed = 4.0f; //how fast move; 22.628f equates to ~512px(512.02637)
	private static int sX = 0,sY = 0;
	private int moves;
	private float opponentPosX, opponentPosY, opponentDir, health, direction = 0.0f;
	private TiledMapTileLayer collisionLayer;
	private Array<Sprite> animMove;
	private state curState = state.IDLE;
	private ArrayList<Bullet> bullets;
	private String id;
	private OrthographicCamera cam;
	
	
	public Player(Array<Sprite> animMove, TiledMapTileLayer collisionLayer, ArrayList<Bullet> bullets, String id, OrthographicCamera cam) {
		super(animMove.get(0));
		this.animMove = animMove;
		this.collisionLayer = collisionLayer;
		this.bullets = bullets;
		this.id = id;
		this.cam = cam;
		moves = 3;
		health = 3;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		update(Gdx.graphics.getDeltaTime());
		super.draw(spriteBatch);
	}
	
	public void update(float delta) {	
//		find x,y coordinates for collisionLayer
//		for(int i = 0; i < collisionLayer.getWidth(); i++) {
//			for(int j = 0; j < collisionLayer.getHeight(); j++) {
//				if(collisionLayer.getCell(i, j) != null)
//					System.out.println(i + " " + j + " " + collisionLayer.getCell(i,j).getTile().getId());
//			}
//		}
		
		if(velocity.x == speed && !collidesRight()) {
			setX(getX() + velocity.x * delta);
		}
		else if (velocity.x == -speed && !collidesLeft()) {
			setX(getX() - velocity.x * delta);
		}
		else if(velocity.y == speed && !collidesTop()) {
			setY(getY() + velocity.y * delta);
		}
		else if(velocity.y == -speed && !collidesBottom()) {
			setY(getY() - velocity.y * delta);
		}
		moves--;
	}
	
	public void isHit() {
		if(bullets.size() > 0 
				&& id.charAt(0) != bullets.get(0).getbulID().charAt(0) 
				//check if bullet in next grid -- add one tile
				&& (bullets.get(0).getX() > getX() 
				&& bullets.get(0).getX() < getX()+16.0f) 
				&& (bullets.get(0).getY() > getY() 
				&& bullets.get(0).getY() < getY()+16.0f ))
			setHealth(getHealth()-0.5f);
		
		if(health <= 0) 
			setState(state.DEAD);
	}
	
	public void shoot() {
		if(moves > 0) {
			bullets.add(new Bullet(getX(),getY(),getDirection(),getId()));
			setMoves(getMoves()-1);
		}
	}
	
	//check if cell id blocked
	public boolean blockedCell(float x, float y) {
		int xVal = (int) (x / collisionLayer.getTileWidth()),
			yVal =  (int) (y / collisionLayer.getTileHeight());
				
		//if coordinates are out of the map - blocked
		//for 512px -- xVal < 0 || xVal > collisionLayer.getWidth()-1 || yVal < 0 || yVal > collisionLayer.getHeight()-1 

		Cell cell = collisionLayer.getCell(xVal,yVal);
		//&& cell.getTile().getProperties().containsKey("blocked")
		return (cell != null && cell.getTile() != null) || xVal < 3 || xVal > 7 || yVal < 4 || yVal > 11 || (xVal == (opponentPosX/collisionLayer.getTileWidth()) && yVal == (opponentPosY/collisionLayer.getTileHeight()));
	}
	
	//getWidth() - get width of sprite
	public boolean collidesRight() {
		return blockedCell(getX() + getWidth(),getY());
	}
	
	public boolean collidesLeft() {
		return blockedCell(getX() - getWidth(),getY());
	}
	
	//getHeight() - get height of sprite
	public boolean collidesTop() {
		return blockedCell(getX(),getY() + getHeight());
	}
	
	public boolean collidesBottom() {
		return blockedCell(getX(),getY() - getHeight());
	}
	
	public void getOpponentPos(float x, float y, float dir) {
		opponentPosX = x;
		opponentPosY = y;
		opponentDir = dir;
	}
	
	public float getOpponentX() {
		return opponentPosX;
	}
	
	public float getOpponentY() {
		return opponentPosY;
	}
	
	public float getOpponentDirection() {
		return opponentDir;
	}
	
	public void setMoves(int moves) {
		this.moves = moves;
	}
	
	public int getMoves() {
		return moves;
	}
	
	public Array<Sprite> getAnimMove() {
		return animMove;
	}
	
	public float getDirection() {
		return direction;
	}

	public state getState() {
		return curState;
	}
	
	public void setState(state s) {
		this.curState = s;
	}
	
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	
	public String getId() {
		return id;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth(float h) {
		this.health = h;
	}
	
	public TiledMapTileLayer getCollisionLayer() {
		return collisionLayer;
	}
	
	public int getsX() {
		return sX;
	}
	
	public int getsY() {
		return sY;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.W) {
			curState = state.WALKING;
			direction = 90.0f;
			velocity.y = speed;
			update(speed);
		}
		if(keycode == Keys.A) {
			curState = state.WALKING;
			direction = 180.0f;
			velocity.x = -speed;
			update(-speed);
		}
		if(keycode == Keys.S) {
			curState = state.WALKING;
			direction = 270.0f;
			velocity.y = -speed;
			update(-speed);
		}
		if(keycode == Keys.D) {
			curState = state.WALKING;
			direction = 0.0f;
			velocity.x = speed;
			update(speed);
		}
		if(keycode == Keys.SPACE) {
			shoot();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.W) {
			curState = state.IDLE;
			velocity.y = 0;
		}
		if(keycode == Keys.A) {
			curState = state.IDLE;
			velocity.x = 0;
		}
		if(keycode == Keys.S) {
			curState = state.IDLE;
			velocity.y = 0;
		}
		if(keycode == Keys.D) {
			curState = state.IDLE;
			velocity.x = 0;
		}
		if(keycode == Keys.SPACE) {
			curState = state.IDLE;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		else if (screenX > 600 && screenY > 1200)
			return false;
		
		Vector3 touch = new Vector3(screenX,screenY,0);
		cam.unproject(touch);
		
		sX = (int) touch.x;
		sY = (int) touch.y;

		//left
		if(touch.x < getX() && (touch.y >= getY()-1 && touch.y <= getY()+16)) {
			curState = state.WALKING;
			direction = 180.0f;
			velocity.x = -speed;
			update(-speed);
		}
		//right
		else if(touch.x > getX() && (touch.y >= getY()-1 && touch.y <= getY()+16)) {
			curState = state.WALKING;
			direction = 0.0f;
			velocity.x = speed;
			update(speed);
		}
		//down
		else if(touch.y < getY() && (touch.x >= getX()-2 && touch.x <= getX()+16)) {
			curState = state.WALKING;
			direction = 270.0f;
			velocity.y = -speed;
			update(-speed);
		}
		//up
		else if(touch.y > getY() && (touch.x >= getX()-2 && touch.x <= getX()+16)) {
			curState = state.WALKING;
			direction = 90.0f;
			velocity.y = speed;
			update(speed);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
	
		if(screenX < getWidth()/2) {
			curState = state.IDLE;
			velocity.x = 0;
		}
		else if(screenX > getWidth()/2) {
			curState = state.IDLE;
			velocity.x = 0;
		}
		else if(screenY < getHeight()/2) {
			curState = state.IDLE;
			velocity.y = 0;
		}
		else if(screenY > getHeight()/2) {
			curState = state.IDLE;
			velocity.y = 0;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}