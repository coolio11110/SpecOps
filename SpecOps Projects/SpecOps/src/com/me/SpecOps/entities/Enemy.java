package com.me.SpecOps.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy extends Player {
	private static Vector2 velocity = new Vector2(); //movement velocity
	private static float speed = 22.628f; //how fast move; equates to ~512px(512.02637)
	private float playerPosX, playerPosY, direction = 0.0f;
	private state curState = state.IDLE;
	private static boolean turn = false;

	public Enemy(Array<Sprite> animMove, TiledMapTileLayer collisionLayer,
			ArrayList<Bullet> bullets, String id) {
		super(animMove, collisionLayer, bullets, id);
	}
	
	public void getPlayerPos(float x, float y) {
		playerPosX = x;
		playerPosY = y;
	}
	
	public float pathSearch(TiledMapTileLayer collision) {
		float dir = getDirection();
		int enemyX = (int) (getX() / collision.getTileWidth()), enemyY = (int) (getY() / collision.getTileHeight()), 
				playerX = (int) (playerPosX / collision.getTileWidth()), playerY = (int)(playerPosY / collision.getTileHeight()), 
				tilecnt = Math.abs(enemyX - playerX) + Math.abs(enemyY - playerY);
		
		if(enemyX == playerX && enemyY == playerY)
			return dir;
			
		int newEnemyX = (int) ((getX()-512.02637)/collision.getTileWidth()), newEnemyY = enemyY;

		if(!blockedCell((float) (getX()-512.02637), getY())) {
			tilecnt = Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY);
			dir = 180.0f;
		}
		
		newEnemyX = (int) ((getX()+512.02637)/collision.getTileWidth());
		
		if(!blockedCell((float) (getX()+512.02637), getY()) && (Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY)) < tilecnt) {
			tilecnt = Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY);
			dir = 0.0f;
		}
		
		newEnemyX = enemyX;
		newEnemyY = (int) ((getY()-512.02637)/collision.getTileHeight());

		if(!blockedCell(getX(), (float) (getY()-512.02637)) && (Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY)) < tilecnt) {
			tilecnt = Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY);
			dir = 270.0f;
		}
		
		newEnemyY = (int) ((getY()+512.02637)/collision.getTileHeight());
		
		if(!blockedCell(getX(), (float) (getY()+512.02637)) && (Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY)) < tilecnt) {
			tilecnt = Math.abs(newEnemyX - playerX) + Math.abs(newEnemyY - playerY);
			dir = 90.0f;
		}
				
		return dir;
	}
	
	public void update(float delta) {
		System.out.println(getX() + " " + playerPosX + " " + getY() + " " + playerPosY + " " + pathSearch(getCollisionLayer()));
		
		if(pathSearch(getCollisionLayer()) == 180.0f) {
			setCurState(state.WALKING);
			setDirection(180.0f);
			velocity.x = -speed;
			super.update(-speed);
		}
		else if(pathSearch(getCollisionLayer()) == 0.0f) {
			setCurState(state.WALKING);
			setDirection(0.0f);
			velocity.x = speed;
			super.update(speed);
		}
		else if(pathSearch(getCollisionLayer()) == 270.0f) {
			setCurState(state.WALKING);
			setDirection(270.0f);
			velocity.y = -speed;
			super.update(-speed);
		}
		else if(pathSearch(getCollisionLayer()) == 90.0f) {
			setCurState(state.WALKING);
			setDirection(90.0f);
			velocity.y = speed;
			super.update(speed);
		}
				
		velocity.x = 0;
		velocity.y = 0;
	}
	
	public boolean getTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		Enemy.turn = turn;
	}
	
	public boolean keyDown(int keycode) {
		return false;
	}
	
	public boolean keyUp(int keycode) {
		return false;
	}
	
	public boolean touchDown(int keycode) {
		return false;
	}
	
	public boolean touchUp(int keycode) {
		return false;
	}

	public state getCurState() {
		return curState;
	}

	public void setCurState(state curState) {
		this.curState = curState;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

}
