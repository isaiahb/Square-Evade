package com.c210.square_evade;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.c210.square_evade.enums.Category;
import com.c210.square_evade.enums.Colors;

/**
 * Created by MacBook on 2015-12-17.
 */
public class BodyFactory {
	Main main;
	public BodyFactory(Main main) {
		this.main = main;
	}

	public Vector2 ConvertToBox(float x, float y){
		Vector3 v = new Vector3(new Vector3(x/Main.PPM/Main.PPM, y/Main.PPM/Main.PPM, 0));
		main.box2DCamera.project(v);
		return new Vector2(v.x, v.y);
	}

	public Body EdgeBody(World world, BodyDef.BodyType bodyType,
							   float v1x, // X1 WORLD COORDINATE
							   float v1y, // Y1 WORLD COORDINATE
							   float v2x, // X2 WORLD COORDINATE
							   float v2y  // Y2 WORLD COORDINATE
	){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;

		//CALCULATE CENTER OF LINE SEGMENT
		float posx=(v1x+v2x)/2f;
		float posy=(v1y+v2y)/2f;

		//CALCULATE LENGTH OF LINE SEGMENT
		float len=(float) Math.sqrt((v1x-v2x)*(v1x-v2x)+(v1y-v2y)*(v1y-v2y));
		//SET LENGTH IN BOX COORDINATES
		len/= Main.PPM;

		//CONVERT CENTER TO BOX COORDINATES
		Vector2 posBox = ConvertToBox(posx, posy);
		//float bx=ConvertToBox(posx);
		//float by=ConvertToBox(posy);
		bodyDef.position.set(posBox.x,posBox.y);
		bodyDef.angle=0;

		Body body = world.createBody(bodyDef);

		//ADD EDGE FIXTURE TO BODY
		MakeEdgeShape(body,len,bodyType,1,0,1);

		//CALCULATE ANGLE OF THE LINE SEGMENT
		body.setTransform(posBox.x, posBox.y, MathUtils.atan2(v2y-v1y, v2x-v1x));

		return body;
	}

	void MakeEdgeShape(Body body, float len, BodyDef.BodyType bodyType, float density, float restitution, float friction){
		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=density;
		fixtureDef.restitution=restitution;
		fixtureDef.friction=friction;

		EdgeShape es = new EdgeShape();

		//SETTING THE POINTS AS OFFSET DISTANCE FROM CENTER
		es.set(-len/2f,0,len/2f,0);
		fixtureDef.shape=es;
		fixtureDef.filter.categoryBits = Category.Ground.bit;
		fixtureDef.filter.maskBits = -1;//(short) (Category.TopPlayer.bit | Category.BottomPlayer.bit);//-1;

		Fixture f = body.createFixture(fixtureDef);
		f.setUserData(new Rectangle());
		fixtureDef.shape.dispose();
	}

	public static Body CreateRectangle(World world, float x, float y, float width, float height, Object userData){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Body body = world.createBody(bodyDef);
		body.setTransform(x, y, 0);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2f, height/2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		Fixture fixture = body.createFixture(fixtureDef);
		fixture.setUserData(userData);

		shape.dispose();
		return body;
	}

	public static Body CreateRectangle(World world, float x, float y, float width, float height){
		return CreateRectangle(world, x, y, width, height, new Rectangle());
	}


	public static void setCategoryBits(Body body, short... bits) {
		short bitmask = 0;
		for (short b : bits)
			bitmask |= b;

		// just do it!
		Filter filter = body.getFixtureList().get(0).getFilterData();
		filter.categoryBits = bitmask;
		body.getFixtureList().get(0).setFilterData(filter);
	}

	public static void setMaskBits(Body body, short... bits) {
		short bitmask = 0;
		for (short b : bits)
			bitmask |= b;

		// just do it!
		Filter filter = body.getFixtureList().get(0).getFilterData();
		filter.maskBits = bitmask;
		body.getFixtureList().get(0).setFilterData(filter);
	}

}
