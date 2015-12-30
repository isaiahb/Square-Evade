package com.c210.square_evade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.c210.square_evade.screens.GameScreen;
import com.c210.square_evade.screens.SplashScreen;

public class Main extends Game {

	public static ShapeRenderer shapeRenderer;
	public static int width = 480;
	public static int height = 800;
	public static float PPM = 16f;
	public static final String name = "Square Evade";
	public static final String GooglePlayLink = "https://play.google.com/store/apps/developer?id=iball";

	public Color backgroundColor;
	public Color backgroundColor2;
	public BitmapFont font16;
	public BitmapFont font50;
	public BitmapFont font;
	public Preferences prefs;
	public ExtendViewport viewPort;
	public ExtendViewport box2DViewPort;
	public OrthographicCamera camera;
	public OrthographicCamera box2DCamera;
	public BodyFactory create;

	Stage stage;

	@Override
	public void create () {
		PPM = Gdx.graphics.getWidth()/50f;
		System.out.println("PPM " + PPM);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		// freetype font
		FreeTypeFontGenerator openSans = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = (int)(16 * Gdx.graphics.getDensity());
		font16 = openSans.generateFont(parameter);
		parameter.size = (int)(50 * Gdx.graphics.getDensity());
		font50 = openSans.generateFont(parameter);
		parameter.size = (int)(24 * Gdx.graphics.getDensity());
		font = openSans.generateFont(parameter);

		prefs = Gdx.app.getPreferences(name + " Preferences");
		camera = new OrthographicCamera(width, height);
		viewPort = new ExtendViewport(width, height, camera);
		camera.position.set(width/2f, height/2f, 1);

		box2DCamera = new OrthographicCamera(width / PPM, height / PPM);
		box2DViewPort = new ExtendViewport(width / PPM, height / PPM, box2DCamera);

		box2DCamera.position.set(width / PPM / 2f, height / PPM / 2f, 0);
		box2DCamera.update();

		create = new BodyFactory(this);
		backgroundColor = new Color(0.95f, 0.95f, 0.95f, 1f);
		backgroundColor2 = new Color(0.85f, 0.85f, 0.85f, 1f);
		shapeRenderer = new ShapeRenderer();
		setScreen(new SplashScreen(this, new GameScreen(this)));
	}

	public void drawBackground() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(backgroundColor);
		shapeRenderer.rect(0,0, width, height);

		shapeRenderer.setColor(backgroundColor2);
		shapeRenderer.rect(0,0, width, height/2);
		shapeRenderer.end();
	}

	public void update() {

		Vector2 p = new Vector2(box2DCamera.position.x * PPM + width/2, box2DCamera.position.y * PPM + height/2);
		//camera.position.set(p, 1);
		// TODO fix Box2DCamera and normal camera moving tracking, disabled for now since camera will not move
		camera.update();
	}
	@Override
	public void render () {
		update();
		Gdx.gl.glClearColor(.90f, .90f, .90f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.setProjectionMatrix(camera.combined);

		drawBackground();
		super.render();

	}
}
