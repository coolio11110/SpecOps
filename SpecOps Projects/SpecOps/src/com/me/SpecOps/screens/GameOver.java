package com.me.SpecOps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.me.SpecOps.SpecOps;

public class GameOver extends AbstractScreen {
	private BitmapFont bf;
	private SpriteBatch sb;
	private String winner;
	private TextureAtlas atlas;
	private Stage stage;
	private Skin skin;
	private Table table;
	private TextButtonStyle textButtonStyle;
	private TextButton replayButton;

	public GameOver(SpecOps game, String winner) {
		super(game);
		this.winner = winner;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		
		sb.begin();
		bf.setColor(Color.BLACK);
		bf.drawMultiLine(sb,"Game\nOver\n\n" + winner + " wins",(Gdx.graphics.getWidth()/4),(Gdx.graphics.getHeight()/1.1f));
		sb.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		bf = new BitmapFont();
		bf.setScale(5);
		
		sb = new SpriteBatch();
		atlas = new TextureAtlas("data/16pxdata/specOps16px.txt");
		stage = new Stage();
		skin = new Skin(atlas);
		table = new Table(skin);
		table.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("replayUp");
		textButtonStyle.over = skin.getDrawable("replayDown");
		textButtonStyle.down = skin.getDrawable("replayDown");
		textButtonStyle.downFontColor = Color.LIGHT_GRAY;
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = 2;
		textButtonStyle.font = bf;
		textButtonStyle.fontColor = Color.RED;
		
		replayButton = new TextButton("Replay", textButtonStyle);
		replayButton.pad(20);
		replayButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new Play(game));
				return true;
			}
		});
		
		table.add(replayButton);
		table.align(Align.bottom);
		table.padBottom(10);
		
		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
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
		atlas.dispose();
		skin.dispose();
		stage.dispose();
	}

}
