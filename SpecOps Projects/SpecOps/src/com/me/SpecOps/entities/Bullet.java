package com.me.SpecOps.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Bullet  {
	private static float lifeTime, lifeTimer, direction, xVal, yVal, xBeg, yBeg, xEnd, yEnd;
	private boolean remove = false;
	private String id;
	
	public Bullet(float x, float y, float direction, String id) {
		lifeTime = 2;
		lifeTimer = 0;
		Bullet.xVal = x;
		Bullet.yVal = y;
		Bullet.direction = direction;
		this.id = id;
		
	}
	
	public boolean getRemove() {
		return remove;
	}
	
	public float getX() {
		return xEnd;
	}
	
	public float getY() {
		return yEnd;
	}
	
	public String getbulID() {
		return id;
	}
	
	//direction of bullet - add to coordinates to align with gun
	private void bulletDirection(float x, float y, float dir) {
		if(dir == 90.0f) {
			xBeg = x + 400.0f;
			yBeg = y + 400.0f;
			xEnd = xBeg;
			yEnd = yBeg + 200.0f;
		}
		else if(dir == 270.0f) {
			xBeg = x + 112.0f;
			yBeg = y;
			xEnd = xBeg;
			yEnd = yBeg - 200.0f;
		}
		else if(dir == 180.0f) {
			xBeg = x;
			yBeg = y + 400.0f;
			xEnd = xBeg - 200.0f;
			yEnd = yBeg;
		}
		else if(dir == 0.0f) {
			xBeg = x + 400.0f;
			yBeg = y + 112.0f;
			xEnd = xBeg + 200.0f;
			yEnd = yBeg;
		}
	}
	
	public void update(float dir) {
		bulletDirection(xVal,yVal,dir);
		
		if(dir == 90.0f) {
			yBeg += 256.0f;
			yEnd += 256.0f;
		}
		else if(dir == 270.0f) {
			yBeg -= 256.0f;
			yEnd -= 256.0f;
		}
		else if(dir == 180.0f) {
			xBeg -= 256.0f;
			xEnd -= 256.0f;
		}
		else if(dir == 0.0f) {
			xBeg += 256.0f;
			xEnd += 256.0f;
		}
			
		lifeTimer++;
		
		if(lifeTimer > lifeTime)
			remove = true;
	}
	
	public void draw(ShapeRenderer sr) {
		update(direction);

		sr.begin(ShapeType.Line);
		Gdx.gl20.glLineWidth(3.0f);
		sr.setColor(Color.BLACK);
		sr.line(xBeg, yBeg, xEnd, yEnd);
		sr.end();
	}
	
	
}
