package com.me.SpecOps.misc;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class CustInputMultiplexer extends InputMultiplexer {
	private Array<InputProcessor> processors = new Array<InputProcessor>(4);

	public CustInputMultiplexer (InputProcessor... processors) {
		for (int i = 0; i < processors.length; i++)
			this.processors.add(processors[i]);
	}
	
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		return false;
	}
}
