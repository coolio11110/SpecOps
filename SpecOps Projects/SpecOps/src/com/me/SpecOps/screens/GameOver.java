package com.me.SpecOps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.SpecOps.SpecOps;

public class GameOver extends AbstractScreen {
	private BitmapFont bf;
	private SpriteBatch sb;

	public GameOver(SpecOps game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		sb.begin();
		bf.setColor(Color.WHITE);
		bf.setScale(5);
		bf.drawMultiLine(sb,"Game\nOver",(Gdx.graphics.getHeight()/2)-75.0f,(Gdx.graphics.getWidth()/2)+75.0f);
		sb.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		bf = new BitmapFont();
		sb = new SpriteBatch();
		
	}

	@Override
	public void hide() {
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
	public void dispose() {
		sb.dispose();
		bf.dispose();
	}

}
