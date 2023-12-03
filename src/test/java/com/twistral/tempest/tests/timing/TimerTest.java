// Copyright 2020-2023 Oğuzhan Topaloğlu
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

package com.twistral.tempest.tests.timing;


import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.viewport.*;
import com.twistral.tempest.utils.ScreenUtils;
import com.twistral.tempest.timing.AccumulationTimer;
import com.twistral.tempest.timing.LimitTimer;
import com.twistral.tempest.box2d.BodyFactory;
import com.twistral.tempest.box2d.Box2DUtils;


public class TimerTest extends ApplicationAdapter {

    SpriteBatch batch;
    BitmapFont font;

    Viewport viewport;
    OrthographicCamera camera;

    Box2DDebugRenderer b2dr;
    World world;
    Body player, refObject;

    private static final float MAX_VELOCITY = 500f;
    final float PPM = 32f;

    AccumulationTimer accTimer = new AccumulationTimer();
    LimitTimer limitTimer = new LimitTimer(2f);


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


        Box2DUtils.init(PPM);
        Box2D.init();

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0,-12f), false);

        refObject = BodyFactory.createBodyAsBox(world, BodyType.StaticBody, false, 600, 64, 0f, 0f, 1f, 0f, 0f,
                0.5f, 0f, false, (short) 0, (short) 0, (short) 0, null, false);

        // player
        player = BodyFactory.createBodyAsBox(world, BodyType.DynamicBody, true,
                64, 64, 0f, 50f, 0.65f, 0f, 0f, 1.75f, 0f,
                false, (short) 0, (short) 0, (short) 0, null, false
        );

    }






    private void update(float deltaTime) {
        accTimer.update(deltaTime);
        limitTimer.update(deltaTime);

        world.step(1f/60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Vector2 vel = this.player.getLinearVelocity();
        Vector2 pos = this.player.getPosition();

        if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
            this.player.applyLinearImpulse(-0.80f, 0, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
            this.player.applyLinearImpulse(0.80f, 0, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyJustPressed(Keys.SPACE) && limitTimer.hasPassed()) {
            this.player.applyLinearImpulse(0, 25f, pos.x, pos.y, true);
        }


    }


    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        ScreenUtils.clear();

        b2dr.render(world, camera.combined.cpy().scl(PPM));

        batch.begin();
        font.draw(batch, "left to jump (secs): " + limitTimer.howManySecsLeft(), 25, 50);
        font.draw(batch, "play time (mins): " + accTimer.toMinuteString(), 25, 25);
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


}
