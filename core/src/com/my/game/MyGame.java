package com.my.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.my.game.screens.MainMenu;

public class MyGame extends Game {

	private Skin gameSkin;

	@Override
	public void create () {
		gameSkin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
		setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}

	@Override
	public void dispose () {

	}


	public Skin getGameSkin() {
		return gameSkin;
	}
}
