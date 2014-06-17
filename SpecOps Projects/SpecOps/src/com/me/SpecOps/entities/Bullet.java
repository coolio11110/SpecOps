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
		//for 512px -- xbeg/ybeg +400.0f, yend -200.0f
		if(dir == 90.0f) {
			xBeg = x + 12.8f;
			yBeg = y + 12.8f;
			xEnd = xBeg;
			yEnd = yBeg + 6.4f;
		}
		//for 512px -- xbeg +112.0f, yend -200.0f
		else if(dir == 270.0f) {
			xBeg = x + 3.5f;
			yBeg = y;
			xEnd = xBeg;
			yEnd = yBeg - 6.4f;
		}
		//for 512px -- ybeg +400.0f, xend -200.0f
		else if(dir == 180.0f) {
			xBeg = x;
			yBeg = y + 12.8f;
			xEnd = xBeg - 6.4f;
			yEnd = yBeg;
		}
		//for 512px -- xbeg +400.0f, ybeg +112.0f, xend +200.0f
		else if(dir == 0.0f) {
			xBeg = x + 12.8f;
			yBeg = y + 3.5f;
			xEnd = xBeg + 6.4f;
			yEnd = yBeg;
		}
	}
	
	public void update(float dir) {
		bulletDirection(xVal,yVal,dir);
		
		//for 512px -- 256.0f
		if(dir == 90.0f) {
			yBeg += 8.0;
			yEnd += 8.0f;
		}
		else if(dir == 270.0f) {
			yBeg -= 8.0f;
			yEnd -= 8.0f;
		}
		else if(dir == 180.0f) {
			xBeg -= 8.0f;
			xEnd -= 8.0f;
		}
		else if(dir == 0.0f) {
			xBeg += 8.0f;
			xEnd += 8.0f;
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
