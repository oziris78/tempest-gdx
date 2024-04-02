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

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.viewport.*;
import com.twistral.tempest.TempestUtils;
import com.twistral.tempest.box2d.ComplexBB;
import com.twistral.tempest.box2d.WorldFacade;
import static com.twistral.tempest.box2d.Box2DUtils.*;


public class Box2DComplexBodyTest extends ApplicationAdapter {

    private static boolean DEBUG = true;

    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Box2DDebugRenderer b2dr;
    private WorldFacade wf;
    private World world;

    private static final float[][] verts = new float[][] {
            // correct ones
            new float[]{ 8, 17, 40, 5, 57, 38, 38, 41, 31, 26, 7, 57 },
            new float[]{ 8, 46, 35, 56, 52, 34, 76, 59, 89, 35, 107, 59, 114, 12, 37, 7},
            new float[]{ 8, 17, 40, 5, 57, 38, 38, 41, 31, 26, 7, 57},
            new float[]{ 8, 46, 35, 56, 52, 34, 76, 59, 89, 35, 107, 59, 114, 12, 37, 7 },
            // incorrect ones (has extra)
            new float[]{ 8, 17, 40, 5, 57, 38, 38, 41, 31, 26, 7, 57, 8, 17 },
            new float[]{ 8, 46, 35, 56, 52, 34, 76, 59, 89, 35, 107, 59, 114, 12, 37, 7, 8, 46 },
            new float[]{ 8, 17, 40, 5, 57, 38, 38, 41, 31, 26, 7, 57, 8, 17 },
            new float[]{ 8, 46, 35, 56, 52, 34, 76, 59, 89, 35, 107, 59, 114, 12, 37, 7, 8, 46 }
    };
    private static final boolean[] isTriangulation = new boolean[] {
        true, // COR, 1, TRIAN
        true, // COR, 2, TRIAN
        false, // COR, 1, CHAIN
        false, // COR, 2, CHAIN
        true, // INV, 1, TRIAN
        true, // INV, 2, TRIAN
        false, // INV, 1, CHAIN
        false // INV, 2, CHAIN
    };
    private static final int PLAYER_COUNT = verts.length;

    private final Body[] players = new Body[PLAYER_COUNT];
    private final Sprite[] sprites = new Sprite[PLAYER_COUNT];

    private static final int SCR_W = 1000;
    private static final int SCR_H = 600;
    private static final float PPM = 32f;
    private static final float moveAmount = 300f;




    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(SCR_W/2f, SCR_H/2f, 0);
        batch.setProjectionMatrix(camera.combined);
        camera.update();

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0,0), false);
        wf = new WorldFacade(world, PPM);

        // ENCAPSULATE THE AREA
        wf.newSimpleBodyAsBox(BodyType.StaticBody, 0+1, 0, SCR_W - 1, 3, dfr(0.5f, 0.5f, 1f));
        wf.newSimpleBodyAsBox(BodyType.StaticBody, 0+1, SCR_H - 4, SCR_W - 1, 3, dfr(0.5f, 0.5f, 1f));
        wf.newSimpleBodyAsBox(BodyType.StaticBody, 0+1, 0, 3, SCR_H - 1, dfr(0.5f, 0.5f, 1f));
        wf.newSimpleBodyAsBox(BodyType.StaticBody, SCR_W - 3, 0, 3, SCR_H - 1, dfr(0.5f, 0.5f, 1f));

        // Ref objects
        wf.newComplexBody(BodyType.DynamicBody, SCR_W/2f, SCR_H/2f)
                .addBoxFixture(20, 20, dfr(1, 1, 1))
                // circles to the sides
                .addCircleFixture(10, dfr(1, 1, 1), offset(0, 20))
                .addCircleFixture(10, dfr(1, 1, 1), offset(0, -20))
                .addCircleFixture(10, dfr(1, 1, 1), offset(20, 0))
                .addCircleFixture(10, dfr(1, 1, 1), offset(-20, 0))
                // boxes to the corners
                .addBoxFixture(20, 20, dfr(1, 1, 1), offset(20, 20))
                .addBoxFixture(20, 20, dfr(1, 1, 1), offset(-20, 20))
                .addBoxFixture(20, 20, dfr(1, 1, 1), offset(20, -20))
                .addBoxFixture(20, 20, dfr(1, 1, 1), offset(-20, -20))
                .build();



        Texture t1 = new Texture(Gdx.files.internal("test-assets/images/box2dpolygons/polygon.png"));
        Texture t2 = new Texture(Gdx.files.internal("test-assets/images/box2dpolygons/polygon2.png"));

        for (int i = 0; i < PLAYER_COUNT; i++)
            sprites[i] = new Sprite(i % 2 == 0 ? t1 : t2);


        int ch = 1, tr = 1;
        for (int i = 0; i < PLAYER_COUNT; i++) {
            final float spWidth = sprites[i].getWidth();
            final float spHeight = sprites[i].getHeight();
            final float y = isTriangulation[i] ? tr++ * 80 : ch++ * 80;
            final float x = isTriangulation[i] ? 100 + (tr++ * 20) : 500 + (ch++ * 20);

            ComplexBB cbb = wf.newComplexBody(BodyType.DynamicBody, x, y, 0f);

            if(isTriangulation[i]) {
                cbb = cbb.addFixturesFromImgTriangulation(verts[i], spWidth, spHeight, dfr(0.5f, 0.5f, 0.5f));
            }
            else cbb = cbb.addFixturesFromImgChainshape(verts[i], spWidth, spHeight, dfr(0.5f, 0.5f, 0.5f));

            players[i] = cbb.build();
        }

    }




    private void update(float deltaTime) {
        world.step(1f/60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyJustPressed(Input.Keys.B))
            DEBUG = !DEBUG;

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            players[0].applyForceToCenter(-moveAmount / PPM, 0f, true);

        if(Gdx.input.isKeyPressed(Input.Keys.D))
            players[0].applyForceToCenter(moveAmount / PPM, 0f, true);

        if(Gdx.input.isKeyPressed(Input.Keys.W))
            players[0].applyForceToCenter(0f, moveAmount / PPM, true);

        if(Gdx.input.isKeyPressed(Input.Keys.S))
            players[0].applyForceToCenter(0f, -moveAmount / PPM, true);

        for (int j = 0; j < PLAYER_COUNT; j++)
            wf.syncSpriteToBody(sprites[j], players[j]);
    }


    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        TempestUtils.clear();

        if(DEBUG){
            b2dr.render(world, camera.combined.cpy().scl(PPM));
        }
        else {
            batch.begin();
            for (int i = 0; i < PLAYER_COUNT; i++) {
                sprites[i].draw(batch);
            }
            batch.end();
        }

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
        config.width = SCR_W;
        config.height = SCR_H;
        new LwjglApplication(new Box2DComplexBodyTest(), config);
    }

}
