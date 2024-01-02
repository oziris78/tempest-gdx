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

package com.twistral.tempest.tests.box2d;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twistral.tempest.utils.ScreenUtils;

public class EarClippingTest extends ApplicationAdapter {
    private final Viewport viewport = new FitViewport(100, 100);

    private final Array<Polygon> polygons = new Array<>();
    private Polygon polygon;

    private ShapeRenderer sr;

    private World world;
    private Box2DDebugRenderer b2dr;

    @Override
    public void create() {
        sr = new ShapeRenderer();
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        setupPolygons();
    }

    private void setupPolygons() {
        polygon = new Polygon(new float[] {
                8, 17,
                40, 5,
                57, 38,
                31, 26,
                7, 57
        });
        ShortArray pointsCoords = new EarClippingTriangulator().computeTriangles(polygon.getVertices());

        for (int i = 0; i < pointsCoords.size; i += 3) {
            float[] triangles = new float[6];

            for (int j = 0; j < 3; j++) {
                short point = pointsCoords.get(i + j);

                triangles[j * 2] = polygon.getVertices()[point * 2];
                triangles[j * 2 + 1] = polygon.getVertices()[point * 2 + 1];
            }

            polygons.add(new Polygon(triangles));

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bodyDef);

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(triangles);
            body.createFixture(polygonShape, 0.0f);
            polygonShape.dispose();
        }

    }


    private void update(float delta) {
        world.step(delta, 6, 2);
        viewport.apply();
        sr.setProjectionMatrix(viewport.getCamera().combined);
    }


    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());


        ScreenUtils.clear();

        sr.begin(ShapeRenderer.ShapeType.Line);

        // DRAWS THE POLYGON
        sr.setColor(Color.RED);
        sr.polygon(polygon.getTransformedVertices());

        // DRAWS THE TRIANGLES
        sr.setColor(Color.BLUE);
        for(Polygon p : polygons)
            sr.polygon(p.getTransformedVertices());

        sr.end();

        // DRAWS THE TRIANGLES FOR BOX2D
        b2dr.render(world, viewport.getCamera().combined);

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


}