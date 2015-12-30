package com.c210.square_evade.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.c210.square_evade.Main;

/**
 * Created by MacBook on 2015-12-27.
 */
public class SplashScreen implements Screen {
	private Main main;
	private Screen screen;
	private Stage stage;

	private float timer = 1;
	private float rectWidth;
	private float rectHeight;
	private float rpx = 30; //Rectangle Padding
	private float rpy = 3; //Rectangle Padding
	private Label label;

	public SplashScreen(Main main, Screen screen) {
		this.main = main;
		this.screen = screen;
		stage = new Stage(main.viewPort);
		Label.LabelStyle labelStyle = new Label.LabelStyle(main.font50, new Color(0.7f, 0.7f, 0.7f, 1));
		label  = new Label("C210", labelStyle);
		label.setPosition(main.width/2 - label.getWidth()/2, main.height - label.getHeight()*2);
		stage.addActor(label);
	}

	public void createGraphics() {
		Label.LabelStyle labelStyle = new Label.LabelStyle(main.font16, new Color(1, 1, 1, 1));
		label  = new Label("C210", labelStyle);
		label.setPosition(main.width/2 - label.getWidth()/2, main.height - label.getHeight()*2);
		stage.addActor(label);

		rectWidth = label.getWidth() + rpx*2;
		rectHeight = label.getHeight() + rpy*2;

		Image logo = new Image(new Texture(Gdx.files.internal("RedDoor.png")));
		logo.setPosition(main.width/2 - logo.getWidth()/2, 0);
		stage.addActor(logo);
	}

	@Override
	public void show() {
		System.out.println("@ Splash Screen");
//		createGraphics();
	}
	public void update(float delta) {
		timer -= delta;
		if (timer <= 0) {
//			main.setScreen(new SplashScreen(main));
			main.setScreen(screen);
			dispose();
		}
	}

	public void drawShapes() {
		main.shapeRenderer.setColor(Color.WHITE);
		main.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		main.shapeRenderer.rect(label.getX() - rpx ,label.getY() - rpy, rectWidth, rectHeight);
		main.shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		main.shapeRenderer.end();
	}

	@Override
	public void render(float delta) {
		update(delta);
		drawShapes();
		stage.draw();
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
		stage.dispose();
	}
}
