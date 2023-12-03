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

package com.twistral.tempest.tests.box2d;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.viewport.*;
import com.twistral.tempest.utils.ScreenUtils;
import com.twistral.tempest.box2d.BodyFactory;
import com.twistral.tempest.box2d.Box2DUtils;


public class Box2DComplexBodyTest extends ApplicationAdapter {

    static boolean DEBUG = false;

    SpriteBatch batch;
    Sprite spritePlayer;

    Viewport viewport;
    OrthographicCamera camera;

    Box2DDebugRenderer b2dr;
    World world;
    Body player, refObject;

    final float PPM = 32f;
    final float moveAmount = 500f;


    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(300, 300, 0);
        batch.setProjectionMatrix(camera.combined);
        camera.update();

        Box2DUtils.init(PPM);
        Box2D.init();

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0,0), false);

        refObject = BodyFactory.createBodyAsBox(world, BodyType.StaticBody, false, 64, 64, 300, 300, 0.5f, 0.5f, 0.5f,
                0.5f, 0f, false, (short) 0, (short) 0, (short) 0, null, false);

//        createPlayer1AsChainShape();
//        createPlayer2AsChainShape();

//        createPlayer1AsPolygonShape();
        createPlayer2AsPolygonShape();


    }


    private void createPlayer1AsChainShape() {
        spritePlayer = new Sprite(new Texture(Gdx.files.internal("images/box2dpolygons/polygon.png")));

        float imgWidth = 64f;
        float imgHeight = 64f;

        short s = 0;
        Vector2[] playerVertices = new Vector2[]{
                new Vector2(8 , 17),
                new Vector2(40, 5),
                new Vector2(57, 38),
                new Vector2(38, 41),
                new Vector2(31, 26),
                new Vector2(7 , 57),
                new Vector2(8 , 17)
        };

        playerVertices = Box2DUtils.initArrayForChainShapes(playerVertices, imgWidth, imgHeight);


        player = BodyFactory.createBodyAsChainShape(world, BodyType.DynamicBody, false,
                128, 64, 0, 0, playerVertices, 0.5f, 0f, 0.5f,
                1f, 0f, false, s, s, s, null, false
        );

    }

    private void createPlayer2AsChainShape() {
        spritePlayer = new Sprite(new Texture(Gdx.files.internal("images/box2dpolygons/polygon2.png")));

        float imgWidth = 128f;
        float imgHeight = 64f;

        short s = 0;
        Vector2[] playerVertices = new Vector2[]{
                new Vector2( 8   ,46 ),
                new Vector2( 35  ,56 ),
                new Vector2( 52  ,34 ),
                new Vector2( 76  ,59 ),
                new Vector2( 89  ,35 ),
                new Vector2( 107 ,59 ),
                new Vector2( 114 ,12 ),
                new Vector2( 37  ,7),
                new Vector2( 8   ,46 )
        };

        playerVertices = Box2DUtils.initArrayForChainShapes(playerVertices, imgWidth, imgHeight);

        player = BodyFactory.createBodyAsChainShape(world, BodyType.DynamicBody, false,
                128, 64, 0, 0, playerVertices, 0.5f, 0f, 0.5f,
                1f, 0f, false, s, s, s, null, false
        );


    }

    private void createPlayer1AsPolygonShape() {
        spritePlayer = new Sprite(new Texture(Gdx.files.internal("images/box2dpolygons/polygon.png")));

        float imgWidth = 64f;
        float imgHeight = 64f;

        float[] playerVertices = new float[]{
                8f , 17f,
                40f, 5f ,
                57f, 38f,
                38f, 41f,
                31f, 26f,
                7f , 57f
        };

        playerVertices = Box2DUtils.initArrayForTriangulation(playerVertices, imgWidth, imgHeight);


        player = BodyFactory.createBodyWithPolygonTriangulation(world, BodyType.DynamicBody, true,
                (int) imgWidth, (int) imgHeight, 0f, 0f, playerVertices, 0.5f, 0.5f, 0.5f, 1f, 0f,
                false, (short) 0, (short) 0, (short) 0, null, false
        );


    }

    private void createPlayer2AsPolygonShape() {
        spritePlayer = new Sprite(new Texture(Gdx.files.internal("images/box2dpolygons/polygon2.png")));

        float imgWidth = 128f;
        float imgHeight = 64f;

        float[] playerVertices = new float[]{
                 (8f   - imgWidth / 2f) / PPM, ( imgHeight / 2f - 46f) / PPM,
                 (35f  - imgWidth / 2f) / PPM, ( imgHeight / 2f - 56f) / PPM,
                 (52f  - imgWidth / 2f) / PPM, ( imgHeight / 2f - 34f) / PPM,
                 (76f  - imgWidth / 2f) / PPM, ( imgHeight / 2f - 59f) / PPM,
                 (89f  - imgWidth / 2f) / PPM, ( imgHeight / 2f - 35f) / PPM,
                 (107f - imgWidth / 2f) / PPM, ( imgHeight / 2f - 59f) / PPM,
                 (114f - imgWidth / 2f) / PPM, ( imgHeight / 2f - 12f) / PPM,
                 (37f  - imgWidth / 2f) / PPM, ( imgHeight / 2f - 7f ) / PPM
        };

        player = BodyFactory.createBodyWithPolygonTriangulation(world, BodyType.DynamicBody, true,
                (int) imgWidth, (int) imgHeight, 0f, 0f, playerVertices, 0.5f, 0.5f, 0.5f, 1f, 0f,
                false, (short) 0, (short) 0, (short) 0, null, false
        );


    }



    private void update(float deltaTime) {
        world.step(1f/60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyJustPressed(Input.Keys.B))
            DEBUG = !DEBUG;

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            player.applyForceToCenter(-moveAmount / PPM, 0f, true);

        if(Gdx.input.isKeyPressed(Input.Keys.D))
            player.applyForceToCenter(moveAmount / PPM, 0f, true);

        if(Gdx.input.isKeyPressed(Input.Keys.W))
            player.applyForceToCenter(0f, moveAmount / PPM, true);

        if(Gdx.input.isKeyPressed(Input.Keys.S))
            player.applyForceToCenter(0f, -moveAmount / PPM, true);

        Box2DUtils.moveAndRotateSprite(player, spritePlayer);
    }


    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        ScreenUtils.clear();

        if(DEBUG){
            b2dr.render(world, camera.combined.cpy().scl(PPM));
        }
        else {
            batch.begin();
            spritePlayer.draw(batch);
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


}
