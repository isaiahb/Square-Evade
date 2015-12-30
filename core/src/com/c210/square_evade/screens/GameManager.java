package com.c210.square_evade.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.c210.square_evade.ForceGenerator;
import com.c210.square_evade.Main;
import com.c210.square_evade.MyContactListener;
import com.c210.square_evade.Rectangle;
import com.c210.square_evade.effects.ShatterAnimation;
import com.c210.square_evade.enums.Action;
import com.c210.square_evade.enums.Category;
import com.c210.square_evade.enums.Colors;
import com.c210.square_evade.enums.Worlds;

import java.util.ArrayList;

// TODO fix size bug
// TODO recreate logo
// TODO ad advertisements
// TODO put on app store

public class GameManager {
	Main main;

	//--Box2D Variables--//
	public World world = new World(new Vector2(0, 0), true);
	public Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	final float TIME_STEP = 1f/60f;
	final int VELOCITY_ITERATIONS = 6;
	final int POSITION_ITERATIONS = 2;
	private float accumulator = 0;
	private Body topBody, bottomBody;
	public Fixture tFixture, bFixture;
	private ForceGenerator bottomGravity, topGravity;

	//--Game Variables--//
	boolean playing = true;
	Worlds currentWorld = Worlds.Top;
	Action action = Action.Nothing;
	int score = 0;
	int highscore;
	float unit = 1.6f; // Half size of the rectangles in PPM
	float xOffset = 8;
	float topPos, bottomPos;
	float pixel = 1f/Main.PPM;
	float velocity = 14.5f;
	int[] positions = new int[] {1,3,-1,-3};
	float obbyGenTime = 1.25f;
	float time = obbyGenTime;
	float jumpForce = 1050 * 1.255f;
	float gravity = 33.5f * 1.29f;
	float gameOverTime = 1f;
	float gameOverTimer = 0;


	//--Miscellaneous--//
	public ArrayList<ShatterAnimation> shatterAnimations = new ArrayList<>();
	public ArrayList<ShatterAnimation> shatterAnimationsToDestroy = new ArrayList<>();
	public ArrayList<Fixture> fixturesToShatter = new ArrayList<>();
	public ArrayList<Body> bodiesToDestroy = new ArrayList<>();

	//--Scene2D--//
	Stage stage;
	Label scoreLabel;
	Label worldLabel;
	Label highscoreLabel;
	Label playAgainLabel;
	Label rateGameLabel;

	public GameManager(Main main) {
		this.main = main;
		highscore = main.prefs.getInteger("HighScore");
		world.setContactListener(new MyContactListener(this));

		stage = new Stage(main.viewPort);
		bottomGravity = new ForceGenerator(0, gravity);
		topGravity = new ForceGenerator(0, -gravity);

		createLabels();
		reset();

	}

	public void reset() {
		Array<Body> bodies = new Array<>();
		world.getBodies(bodies);

		topGravity.removeBody(topBody);
		bottomGravity.removeBody(bottomBody);

		System.out.println("about to destroy all bodies in world");
		for (Body body : bodies) {
			world.destroyBody(body);
		}
		System.out.println("cleared all bodies in world");

		bodiesToDestroy.clear();
		fixturesToShatter.clear();
		shatterAnimations.clear();
		shatterAnimationsToDestroy.clear();

		createEdge();
		createPlayer();
		rateGameLabel.remove();
		playAgainLabel.remove();

		topGravity.addBody(topBody);
		bottomGravity.addBody(bottomBody);
		currentWorld = Worlds.Top;
		playing = true;
		score = 0;
		gameOverTimer = 0;


	}
	public void endGame() {
		playing = false;
		highscore = main.prefs.getInteger("HighScore");
		if (score > highscore) {
			main.prefs.putInteger("HighScore", score);
			main.prefs.flush();
			highscore = score;
			updateLabels();
		}
		stage.addActor(playAgainLabel);
		stage.addActor(rateGameLabel);
		//TODO code to show score and ask for replay and to end game or w/e

	}

	private void createShatter(Fixture fixture) {
		Body body = fixture.getBody();
		Vector2 size = new Vector2(unit * 2, unit * 2);
		Color color = ((Rectangle)fixture.getUserData()).color;
		shatterAnimations.add(new ShatterAnimation(this, world, size, body.getPosition(), 4, 5, color));
		bodiesToDestroy.add(body);
	}
	public void shatter(Fixture... fixtures) {
		for (Fixture fixture : fixtures) {
			fixturesToShatter.add(fixture);
		}
	}
	private void updateFixturesToShatter() {
		for (Fixture fixture : fixturesToShatter) {
			createShatter(fixture);
		}
		fixturesToShatter.clear();
	}
	private void updateBodiesToRemove() {
		for (Body body : bodiesToDestroy) {
			world.destroyBody(body);
		}
		bodiesToDestroy.clear();
	}
	private void updateShatterAnimationsToRemove() {
		for (ShatterAnimation shatterAnimation : shatterAnimationsToDestroy) {
			shatterAnimations.remove(shatterAnimation);
		}
		shatterAnimationsToDestroy.clear();
	}

	public void createObstacle(int position) {
		float mid = main.height /2f /Main.PPM;
		float y = mid + unit*position +  pixel*position;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Body body = world.createBody(bodyDef);
		body.setTransform(main.width/Main.PPM, y, 0);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(unit, unit);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Category.Obstacle.bit;
		fixtureDef.filter.maskBits = (short)(Category.TopPlayer.bit | Category.BottomPlayer.bit | Category.Ground.bit);
		Fixture fixture = body.createFixture(fixtureDef);

		fixture.setUserData(new Rectangle(body, unit*2, Colors.GetObbyColor(position), "Obby"));

		body.setLinearVelocity(-velocity, 0);
		shape.dispose();

	}
	public void createPlayer() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		topBody = world.createBody(bodyDef);
		bottomBody = world.createBody(bodyDef);
		float mid = main.height /2f /Main.PPM;
		topPos = mid + unit + pixel;
		bottomPos = mid - unit - pixel;
		topBody.setTransform(xOffset, topPos,0);
		bottomBody.setTransform(xOffset, bottomPos,0);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(unit, unit);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		// Create our fixture and attach it to the body
		fixtureDef.filter.categoryBits = Category.TopPlayer.bit;
		fixtureDef.filter.maskBits = (short)(Category.Obstacle.bit | Category.Ground.bit);
		tFixture = topBody.createFixture(fixtureDef);

		fixtureDef.filter.categoryBits = Category.BottomPlayer.bit;
		fixtureDef.filter.maskBits = (short)(Category.Obstacle.bit | Category.Ground.bit);
		bFixture = bottomBody.createFixture(fixtureDef);

		//changing the filter for correct starting
		Filter filter = bFixture.getFilterData();
		filter.categoryBits = Category.Nothing.bit;
		bFixture.setFilterData(filter);

		filter = tFixture.getFilterData();
		filter.categoryBits = Category.TopPlayer.bit;
		tFixture.setFilterData(filter);

		tFixture.setUserData(new Rectangle(topBody, unit*2, Colors.TopBlue.color));
		bFixture.setUserData(new Rectangle(bottomBody, unit*2, Colors.BottomBlue.color));
		shape.dispose();
	}
	public void createLabels() {
		Label.LabelStyle labelStyle = new Label.LabelStyle(main.font, new Color(0.7f, 0.7f, 0.7f, 1));
		scoreLabel = new Label("Score " + score , labelStyle);
		scoreLabel.setPosition(main.width/2 - scoreLabel.getWidth()/2, main.height - scoreLabel.getHeight());
		stage.addActor(scoreLabel);

//		worldLabel = new Label("World " + currentWorld , labelStyle);
//		worldLabel.setPosition(main.width/2 - worldLabel.getWidth()/2, main.height - worldLabel.getHeight() * 2);
//		stage.addActor(worldLabel);

		highscoreLabel = new Label("Highscore " + highscore, labelStyle);
		highscoreLabel.setPosition(main.width/2 - highscoreLabel.getWidth()/2, main.height - highscoreLabel.getHeight() * 3);
		stage.addActor(highscoreLabel); 

		rateGameLabel = new Label("tap up here to rate game", labelStyle);
		rateGameLabel.setPosition(main.width/2 - rateGameLabel.getWidth()/2, main.height/2 + rateGameLabel.getHeight()/2);

		Label.LabelStyle labelStyle2 = new Label.LabelStyle(main.font, new Color(0.95f, 0.95f, 0.95f, 1));
		playAgainLabel = new Label("tap down here to retry", labelStyle2);
		playAgainLabel.setPosition(main.width/2 - playAgainLabel.getWidth()/2, main.height/2 - playAgainLabel.getHeight());
	}
	public void createEdge() {
		float midY = main.height/2f;
		main.create.EdgeBody(world, BodyDef.BodyType.StaticBody, 0, midY, main.width, midY);
	}

	public void updateObbies(float delta) {
		time -= delta;
		if (time <= 0) {
			time = obbyGenTime;
			int p = positions[(int)(Math.random() * 4)];
			for (int i = 0; i < 4; i++) {
				if (positions[i] != p) {
					createObstacle(positions[i]);
				}
			}
		}

	}
	public void update(float delta) {

		updateBox2D(delta);

		if(!playing) {
			gameOverTimer += delta;
			if(gameOverTimer >= gameOverTime) {
				if (Gdx.input.justTouched()) {
					if (Gdx.input.getY() > main.height/2) {
						reset();
					}
					else {
						//TODO open a link to my apps on app store
						Gdx.net.openURI(Main.GooglePlayLink);
					}
				}
			}

			return;
		}
		updateObbies(delta);
		updateControls();
		updateLabels();
	}

	public void updateLabels() {
		scoreLabel.setText("Score " + score);
		scoreLabel.setPosition(main.width/2 - scoreLabel.getGlyphLayout().width/2, main.height - scoreLabel.getHeight()*2);

//		worldLabel.setText("World " + currentWorld);
//		worldLabel.setPosition(main.width/2 - worldLabel.getGlyphLayout().width/2, main.height - worldLabel.getHeight() * 2);

		highscoreLabel.setText("Highscore " + highscore);
		highscoreLabel.setPosition(main.width/2 - highscoreLabel.getGlyphLayout().width/2, main.height - highscoreLabel.getHeight());

	}

	public void updateControls() {
		if (Gdx.input.justTouched()) {
//			Gdx.app.log("MyApp", "1 = " + Gdx.input.isTouched(0));
//			Gdx.app.log("MyApp", "2 = " + Gdx.input.isTouched(1));
			boolean doubleTap = Gdx.input.isTouched(1);

			if(Gdx.input.getY() <= Gdx.graphics.getHeight()/2) {
				//Controls if pressed the top of the screen
				if (currentWorld == Worlds.Top) {
					//Control what happens to topBody for top press
					if (topBody.getPosition().y > topPos) {
						//top is trying to jump but is already jumped, dont do it, dont jump!
						action = Action.Nothing;
						System.out.println(topBody.getPosition().y + " " + topPos);
					}
					if (topBody.getPosition().y <= topPos) {
						//top is trying to jump but are at rest, so jump!
						action = Action.Jump;
						topBody.applyForceToCenter(0,jumpForce, true);

					}
				}
				if (currentWorld == Worlds.Bottom) {
					//Control what happens to bottomBody for top press
					if (bottomBody.getPosition().y < bottomPos) {
						//bottom is trying to switch but is jumped,try to unjump!
						action = Action.Fall;
						bottomBody.applyForceToCenter(0, 8000, true);

					}

					if (bottomBody.getPosition().y > bottomPos) {
						//bottom is trying to switch, and is at rest, so switch!
						action = Action.Switch;
						currentWorld = Worlds.Top;
						if (doubleTap)
							topBody.applyForceToCenter(0, jumpForce, true);
						//
						Filter filter = bFixture.getFilterData();
						filter.categoryBits = Category.Nothing.bit;
						bFixture.setFilterData(filter);

						filter = tFixture.getFilterData();
						filter.categoryBits = Category.TopPlayer.bit;
						tFixture.setFilterData(filter);

					}
				}
			}
			else {
				//Controls if pressed the bottom of the screen
				if (currentWorld == Worlds.Bottom){
					//Control what happens to bottomBody for bottom press
					System.out.println(bottomBody.getPosition().y + " " + bottomPos);

					if (bottomBody.getPosition().y <= bottomPos) {
						//bottom is trying to switch but is jumped,dont jump!
						action = Action.Nothing;

					}
					if (bottomBody.getPosition().y > bottomPos) {
						//bottom is trying to jump, and is at rest, so jump!
						action = Action.Jump;
						bottomBody.applyForceToCenter(0,-jumpForce, true);

					}
				}
				if (currentWorld == Worlds.Top) {
					//Control what happens to topBody for bottom press
					if (topBody.getPosition().y > topPos) {
						//top is trying to switch but is jumped, fall!!
						action = Action.Fall;
						topBody.applyForceToCenter(0, -8000, true);
					}
					if (topBody.getPosition().y <= topPos) {
						//top is trying to switch at rest, so switch!
						action = Action.Switch;
						currentWorld = Worlds.Bottom;
						if (doubleTap)
							bottomBody.applyForceToCenter(0, -jumpForce, true);
						//
						Filter filter = tFixture.getFilterData();
						filter.categoryBits = Category.Nothing.bit;
						tFixture.setFilterData(filter);

						filter = bFixture.getFilterData();
						filter.categoryBits = Category.BottomPlayer.bit;
						bFixture.setFilterData(filter);

					}
				}


			} //Gdx.input.getY()
		} //Gdx.justTouched();

	}//public void updateControls()

	public void updateBox2D(float delta) {
		// fixed time step
		// max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(delta, 0.25f);
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			bottomGravity.update(TIME_STEP);
			topGravity.update(TIME_STEP);
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			accumulator -= TIME_STEP;
			for (ShatterAnimation shatterAnimation: shatterAnimations) {
				shatterAnimation.update(TIME_STEP);
			}
			updateShatterAnimationsToRemove();
			cleanUp();
			updateFixturesToShatter();
			updateBodiesToRemove();

			if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
				reset();
			}
		}
	}
	public void cleanUp() {
		Array<Body> bodies = new Array<>();
		world.getBodies(bodies);
		//System.out.println("body count " + bodies.size);

		for (Body body : bodies) {
			if (body.getPosition().x <= xOffset -unit*4) {
				if (((Rectangle)body.getFixtureList().get(0).getUserData()).draw) {
					fixturesToShatter.add(body.getFixtureList().get(0));
					if(playing) score++;
//					bodiesToDestroy.add(body);
				}
			}
		}
	}

	public void draw(ShapeRenderer shapeRenderer) {
		if (playing) {
			if (currentWorld == Worlds.Top) {
				((Rectangle)topBody.getFixtureList().get(0).getUserData()).draw = true;
				((Rectangle)bottomBody.getFixtureList().get(0).getUserData()).draw = false;
			}
			else {
				((Rectangle)bottomBody.getFixtureList().get(0).getUserData()).draw = true;
				((Rectangle)topBody.getFixtureList().get(0).getUserData()).draw = false;
			}
		}

		Array<Body> bodies = new Array<>();
		world.getBodies(bodies);
		for (Body body : bodies) {
			((Rectangle)body.getFixtureList().get(0).getUserData()).draw(shapeRenderer);
		}
		for (ShatterAnimation shatterAnimation: shatterAnimations) {
			shatterAnimation.Draw(shapeRenderer);
		}
	}
}
