package com.my.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import java.util.HashMap;
import java.util.Map;

public class MyGame extends ApplicationAdapter implements InputProcessor {
	private int screenWidth, screenHeight;
	private SpriteBatch batch;
	World world;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;
	MouseJoint joint;
	MouseJointDef jointDef;

	private class TouchInfo {
		int x, y;
		TouchInfo(int x, int y){
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public void create () {
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();

		screenHeight = Gdx.graphics.getHeight();
		screenWidth  = Gdx.graphics.getWidth();
		camera = new OrthographicCamera(screenWidth/100,screenHeight/100);
		//Box2D stuffS
		world = new World(new Vector2(0,-10),true);
		debugRenderer = new Box2DDebugRenderer();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(0,0);
		Body body = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(0.6f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		Fixture fixture = body.createFixture(fixtureDef);
		circle.dispose();

		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(0,-camera.viewportHeight/2);
		Body groundBody = world.createBody(bodyDef);
		bodyDef.position.set(0,camera.viewportHeight/2);
		Body roofBody = world.createBody(bodyDef);
		bodyDef.position.set(camera.viewportWidth/2, 1f);
		Body rightBody = world.createBody(bodyDef);
		bodyDef.position.set(-camera.viewportWidth/2, 1f);
		Body leftBody = world.createBody(bodyDef);

		PolygonShape groundBox = new PolygonShape(),wallBox = new PolygonShape();
		groundBox.setAsBox(camera.viewportWidth/2,1f);
		wallBox.setAsBox(1f,camera.viewportHeight/2);

		groundBody.createFixture(groundBox, 0);
		roofBody.createFixture(groundBox, 0);
		rightBody.createFixture(wallBox, 0);
		leftBody.createFixture(wallBox, 0);

		groundBox.dispose();

		jointDef = new MouseJointDef();
		jointDef.bodyA = groundBody;
		jointDef.collideConnected = true;
		jointDef.maxForce = 500;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f,0.5f,0.5f,0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.end();

		debugRenderer.render(world,camera.combined);
		world.step(1/30f, 6, 2);

	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private Vector3 tmp = new Vector3();
	private Vector2 tmp2 = new Vector2();

	private QueryCallback queryCallback = new QueryCallback() {

		@Override
		public boolean reportFixture(Fixture fixture) {
			if(!fixture.testPoint(tmp.x, tmp.y))
				return true;

			jointDef.bodyB = fixture.getBody();
			jointDef.target.set(tmp.x, tmp.y);
			joint = (MouseJoint) world.createJoint(jointDef);
			return false;
		}

	};

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		camera.unproject(tmp.set(screenX, screenY, 0));
		world.QueryAABB(queryCallback, tmp.x, tmp.y, tmp.x, tmp.y);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(joint == null)
			return false;

		camera.unproject(tmp.set(screenX, screenY, 0));
		joint.setTarget(tmp2.set(tmp.x, tmp.y));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(joint == null)
			return false;

		world.destroyJoint(joint);
		joint = null;
		return true;
	}


}
