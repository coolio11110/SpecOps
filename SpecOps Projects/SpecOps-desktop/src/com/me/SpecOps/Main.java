package com.me.SpecOps;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SpecOps";
		cfg.width = 500;
		cfg.height = 475;
		
		new LwjglApplication(new SpecOps(), cfg);
	}
}
