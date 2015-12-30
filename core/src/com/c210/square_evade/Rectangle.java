package com.c210.square_evade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by MacBook on 2015-12-29.
 */
public class Rectangle extends Actor {
	public Vector2 size;
	public Vector2 position;
	public Color color;
	public Body body;
	public boolean draw = true;
	public Object userData = "";

	public Rectangle(float x, float y, float width, float height, Color color) {
		size = new Vector2(width, height);
		position = new Vector2(x, y);
		this.color = color;
	}
	public Rectangle() {
		draw = false;
		size = new Vector2();
		position = new Vector2();
		color = new Color(Color.BLUE);
	}

	public Rectangle(Body body, float unit, Color color) {
		this();
		draw = true;
		set(body, unit, unit);
		this.color = color;
	}
	public Rectangle(Body body, float unit, Color color, Object userData) {
		this(body, unit, color);
		this.userData = userData;
	}

	public void set(Body body, float width, float height) {
		size.set(width, height);
		size.scl(Main.PPM);

		set(body);
	}
	public void set(Body body) {
		this.body = body;
		float x = body.getPosition().x * Main.PPM - size.x/2;
		float y = body.getPosition().y * Main.PPM - size.y/2;
		position.set(x, y);
	}
	public void update() {
		set(body);
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		//draw(Main.shapeRenderer); //TODO fix this rectangle draw so it works for later
		batch.begin();
	}

	public void draw(ShapeRenderer shapeRenderer) {
		if (!draw) return;

		update();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(position.x, position.y, size.x, size.y);
		shapeRenderer.end();

	}

}
