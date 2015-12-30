package com.c210.square_evade.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.c210.square_evade.Main;

/**
 * Created by MacBook on 2015-12-27.
 */
public class GameScreen implements Screen {
	Main main;
	GameManager gameManager;

	public GameScreen(Main main) {
		this.main = main;
		gameManager = new GameManager(main);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		gameManager.stage.draw();
		gameManager.draw(main.shapeRenderer);
		//gameManager.debugRenderer.render(gameManager.world, main.box2DCamera.combined);

		gameManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
