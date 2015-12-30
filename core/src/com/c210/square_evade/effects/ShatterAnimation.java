package com.c210.square_evade.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.c210.square_evade.BodyFactory;
import com.c210.square_evade.Main;
import com.c210.square_evade.Rectangle;
import com.c210.square_evade.enums.Category;
import com.c210.square_evade.screens.GameManager;

/**
 * Created by MacBook on 2015-12-29.
 */
public class ShatterAnimation {
	GameManager gameManager;
	Piece[] pieces;
	float maxVelocity;
	float offsetX, offsetY;
	float timeToLive = 1.5f;
	float timeAlive = 0;

	public ShatterAnimation(GameManager gameManager, World world, Vector2 size, Vector2 position, int numPieces, float maxVelocity, Color color) {
		pieces = new Piece[numPieces * numPieces];
		this.gameManager = gameManager;
		this.maxVelocity = maxVelocity;

		float width = size.x;
		float height = size.y;
		float pieceWidth = width/numPieces;
		float pieceHeight = height/numPieces;
		offsetX = width/2f;
		offsetY = height/2f;



		int i = 0;
		for (int x = 0; x < numPieces; x++) {
			for (int y = 0; y < numPieces; y++)  {
				float pX = position.x - offsetX  + (x * pieceWidth + pieceWidth/2f);
				float pY = position.y - offsetY  + (y * pieceHeight + pieceHeight/2f);
				Body body = BodyFactory.CreateRectangle(world, pX, pY, pieceWidth, pieceHeight, new Rectangle());
				BodyFactory.setCategoryBits(body, Category.Nothing.bit);
				body.getFixtureList().get(0).setRestitution(0.5f);
				AssignRandomDirection(body);
				pieces[i] = new Piece(body, pX, pY, pieceWidth, pieceHeight);
				pieces[i].color = color;
				i++;
			}
		}
	}

	private void AssignRandomDirection(Body body) {
		double linearVelocity = Math.random() * maxVelocity;
		float rVelo = (float)(linearVelocity - linearVelocity/2f);
		double direction = Math.random() * Math.PI * 2;
		Vector2 velocity = new Vector2((float) (Math.cos(direction) * linearVelocity), (float) (Math.sin(direction) * linearVelocity));
		body.setLinearVelocity(velocity);
		body.setAngularVelocity(rVelo);

	}
	public void update(float delta) {
		timeAlive += delta;
		if (timeAlive >= timeToLive) {
			for (Piece piece : pieces) {
				gameManager.bodiesToDestroy.add(piece.body);
			}
			gameManager.shatterAnimationsToDestroy.add(this);
		}
	}

	public void Draw(ShapeRenderer shapeRenderer) {
		float alpha = 1f - timeAlive / timeToLive;
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for (Piece piece : pieces) {
			piece.update();
			float oX = offsetX* Main.PPM;
			float oY = offsetY* Main.PPM;

			float x = piece.x * Main.PPM;// - oX;
			float y = piece.y * Main.PPM;// - oY;

			float width = piece.width * Main.PPM;
			float height = piece.height * Main.PPM;

			Color c = new Color(piece.color.r, piece.color.g, piece.color.b, alpha);
			shapeRenderer.setColor(c);
			shapeRenderer.rect(x-width/2, y-height/2, width/2, height/2, width, height, 1, 1, (float) Math.toDegrees(piece.r));
		}
		shapeRenderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);


	}
}
