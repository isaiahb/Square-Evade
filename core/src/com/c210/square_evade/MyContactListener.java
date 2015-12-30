package com.c210.square_evade;

import com.badlogic.gdx.physics.box2d.*;
import com.c210.square_evade.screens.GameManager;

/**
 * Created by MacBook on 2015-12-28.
 */
public class MyContactListener implements ContactListener {
	private Fixture fA;
	private Fixture fB;
	GameManager gameManager;
	public MyContactListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	@Override
	public void beginContact(Contact contact) {
		fA = contact.getFixtureA();
		fB = contact.getFixtureB();
		boolean aIsPlayer = fA.equals(gameManager.tFixture) || fA.equals(gameManager.bFixture);
		boolean bIsPlayer = fB.equals(gameManager.tFixture) || fB.equals(gameManager.bFixture);
		boolean aIsObby = ((Rectangle)(fA.getUserData())).userData.equals("Obby");
		boolean bIsObby = ((Rectangle)(fB.getUserData())).userData.equals("Obby");
		if (aIsPlayer && bIsObby){
			System.out.println("game over");
			gameManager.endGame();
			gameManager.shatter(fA, fB);
		}

		if (bIsPlayer && aIsObby){
			System.out.println("game over");
			gameManager.endGame();
			gameManager.shatter(fA, fB);
		}

	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
