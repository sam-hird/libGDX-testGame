package com.my.game.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.my.game.MyGame;

class GameScreen extends InputAdapter implements Screen  {


    private Stage stage;
    private Game game;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private MouseJoint joint;
    private MouseJointDef jointDef;
    private Fixture fixture;
    private Sound sound;

    GameScreen(Game myGame){
        this.game = myGame;

        stage = new Stage(new ScreenViewport());
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);

        Gdx.input.setInputProcessor(inputMultiplexer);

        Label title = new Label("Game Screen",((MyGame)game).getGameSkin());
        title.setAlignment(Align.center);
        title.setY(Gdx.graphics.getHeight()*2/3);
        title.setWidth(Gdx.graphics.getWidth());
        stage.addActor(title);

        TextButton backButton = new TextButton("Back",((MyGame)game).getGameSkin());
        backButton.setWidth(Gdx.graphics.getWidth());
        backButton.setHeight(Gdx.graphics.getHeight()/30);
        backButton.setPosition(0,20);
        backButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenu(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(backButton);

        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getWidth();
        camera = new OrthographicCamera(screenWidth /100, screenHeight /100);
        //Box2D stuffS
        world = new World(new Vector2(0,-10),true);
        debugRenderer = new Box2DDebugRenderer();

        CircleShape circle = new CircleShape();
        circle.setRadius(0.6f);
        newDynamicBody(world,circle,0.5f,0.4f,0.8f,0,0);
        circle.dispose();

        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pop.mp3"));

        BodyDef bodyDef = new BodyDef();
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

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA() == fixture || contact.getFixtureB() == fixture){
                    sound.play();
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
        });
    }

    private void newDynamicBody(World world, Shape shape, float density, float friction, float restitution, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        final Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixture = body.createFixture(fixtureDef);
    }

    @Override
    public void show() {
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        debugRenderer.render(world,camera.combined);
        world.setGravity(new Vector2(-Gdx.input.getAccelerometerX(),-Gdx.input.getAccelerometerY()));
        world.step(1/30f, 6, 2);
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

    }

}
