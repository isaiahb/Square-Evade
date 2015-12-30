package com.c210.square_evade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

/**
 * Created by MacBook on 2015-12-27.
 */
public class ForceGenerator {
	ArrayList<Body> bodies = new ArrayList<Body>();
	Vector2 force;

	public ForceGenerator(float x, float y) {
		force = new Vector2(x, y);
	}
	public void addBody(Body body) {
		bodies.add(body);
	}
	public void removeBody(Body body) {
		bodies.remove(body);
	}

	public void update(float delta) {
		for (Body b : bodies) {
			b.applyForceToCenter(force.x, force.y, false);
		}
	}

}
