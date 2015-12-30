package com.c210.square_evade.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by MacBook on 2015-12-29.
 */
public class Piece {
	public Body body;
	public float x, y, r;
	public float width, height;
	public Color color = Color.BLUE;

	public Piece(Body body, float x, float y, float width, float height) {
		this.body = body;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public void update() {
		x = body.getPosition().x;
		y = body.getPosition().y;
		r = body.getAngle();
	}

}
