package com.my.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Samuel on 06/07/2017.
 */

public class Border extends Actor {
    private Body body;

    public Border(float x, float y, World world, OrthographicCamera camera) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x,y);
        body = world.createBody(bodyDef);
        EdgeShape box = new EdgeShape();
        box.set(camera.viewportWidth,camera.viewportHeight,0,camera.viewportHeight);
        body.createFixture(box,0);
        box.set(0,0,camera.viewportWidth,0);
        body.createFixture(box,0);
        box.set(0,0,0,camera.viewportHeight);
        body.createFixture(box,0);
        box.set(camera.viewportWidth,camera.viewportHeight,camera.viewportWidth,0);
        body.createFixture(box,0);
        box.dispose();
    }

    public Body getBody(){
        return body;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
