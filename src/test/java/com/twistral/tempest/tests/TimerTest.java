// Copyright 2020-2024 Oğuzhan Topaloğlu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.twistral.tempest.tests;


import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.viewport.*;
import com.twistral.tempest.*;
import com.twistral.tempest.timing.*;
import com.twistral.tempest.box2d.*;

import static com.twistral.tempest.box2d.Box2DUtils.dfr;


public class TimerTest extends ApplicationAdapter {

    SpriteBatch batch;
    BitmapFont font;

    Viewport viewport;
    OrthographicCamera camera;

    Box2DDebugRenderer b2dr;
    World world;
    WorldFacade wf;
    Body player, refObject;

    private static final float MAX_VELOCITY = 500f;
    final float PPM = 32f;

    AccumulationTimer accTimer = new AccumulationTimer();
    LimitTimer limitTimer = new LimitTimer(3f);


    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(300, 300, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);


        Box2D.init();

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0,-12f), false);
        wf = new WorldFacade(world, PPM);


        refObject = wf.newSimpleBodyAsBox(BodyType.StaticBody, 0+1, 0f, 600-1, 64, dfr(1f, 0f, 0f));
        refObject.setLinearDamping(0.5f);

        player = wf.newSimpleBodyAsBox(BodyType.DynamicBody, 50f, 250f, 64, 64, dfr(0.65f, 0f, 0f), 44f);
        player.setFixedRotation(false);
        player.setLinearDamping(1.75f);
    }


    private void update(float deltaTime) {
        accTimer.update(deltaTime);
        limitTimer.update(deltaTime);

        world.step(1f/60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Vector2 vel = this.player.getLinearVelocity();
        Vector2 pos = this.player.getPosition();

        if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY)
            this.player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);

        if (Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY)
            this.player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);

        if (Gdx.input.isKeyPressed(Keys.SPACE) && limitTimer.hasPassed()) {
            float randXForce = (float) ((Math.random()-0.5f) * 5);
            this.player.applyLinearImpulse(randXForce, 3, 0, pos.y, true);
        }
    }


    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        TempestUtils.clear();

        b2dr.render(world, camera.combined.cpy().scl(PPM));

        batch.begin();
        font.draw(batch, "left to jump (secs): " + limitTimer.getRemainingSecs(), 25, 50);
        font.draw(batch, "play time (mins): " + accTimer.asString(TimerFormatType.MIN_SEC_MILLI), 25, 25);
        font.draw(batch, "Press SPACE to jump, A/D to move horizontally", 250, 25);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }



    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 600;
        config.height = 600;
        new LwjglApplication(new TimerTest(), config);
    }

}
