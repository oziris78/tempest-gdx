// Copyright 2024 Oğuzhan Topaloğlu
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


package com.twistral.tempest.box2d;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ShortArray;
import com.twistral.tempest.box2d.Box2DUtils.*;

import java.util.LinkedList;




public class ComplexBB {

    private final World world;
    private final Body body;
    private final float PPM;
    private final LinkedList<FixtureDef> fixtureDefs;


    ComplexBB(WorldFacade wf, BodyType bodyType, float centerXInPixels, float centerYInPixels, float angleInDegrees) {
        this.world = wf.getWorld();
        this.PPM = wf.getPPM();
        this.fixtureDefs = new LinkedList<>();

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(centerXInPixels / PPM, centerYInPixels / PPM);
        bodyDef.type = bodyType;
        bodyDef.angle = angleInDegrees * MathUtils.degreesToRadians;
        this.body = world.createBody(bodyDef);
    }


    public Body build() {
        fixtureDefs.forEach(fixtureDef -> {
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        });

        return this.body;
    }


    ////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  FIXTURE ADDING METHODS  /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////


    public ComplexBB addBoxFixture(float wPixels, float hPixels, DFR dfr, Offset offsetPixels) {
        PolygonShape shape = new PolygonShape();

        if(offsetPixels != null) {
            final Vector2 offsetMeters = new Vector2(offsetPixels.xPixels / PPM, offsetPixels.yPixels / PPM);
            shape.setAsBox(wPixels / 2f / PPM, hPixels / 2f / PPM, offsetMeters, 0f);
        }
        else shape.setAsBox(wPixels / 2f / PPM, hPixels / 2f / PPM);

        return saveFixture(shape, dfr);
    }


    public ComplexBB addCircleFixture(float rPixels, DFR dfr, Offset offsetPixels) {
        CircleShape shape = new CircleShape();
        shape.setRadius(rPixels / PPM);
        if(offsetPixels != null) {
            final Vector2 offsetMeters = new Vector2(offsetPixels.xPixels / PPM, offsetPixels.yPixels / PPM);
            shape.setPosition(offsetMeters);
        }
        return saveFixture(shape, dfr);
    }


    public ComplexBB addChainshapeFixture(float[] verticesInPixels, DFR dfr) {
        float[] verticesInMeters = convertToMeters(verticesInPixels);
        ChainShape shape = new ChainShape();
        shape.createChain(verticesInMeters);
        return saveFixture(shape, dfr);
    }


    public ComplexBB addPolygonShapeFixture(float[] verticesInPixels, DFR dfr) {
        float[] verticesInMeters = convertToMeters(verticesInPixels);
        PolygonShape shape = new PolygonShape();
        shape.set(verticesInMeters);
        return saveFixture(shape, dfr);
    }


    public ComplexBB addFixturesFromImgTriangulation(float[] imgPoints, float imgWidth, float imgHeight, DFR dfr) {
        float[] verticesInMeters = getPreparedArray(true, imgPoints, imgWidth, imgHeight);

        // Perform polygon triangulation and add fixtures
        final ShortArray pointsCoords = new EarClippingTriangulator().computeTriangles(verticesInMeters);
        for (int i = 0; i < pointsCoords.size; i += 3) {
            float[] triangles = new float[] {
                    verticesInMeters[pointsCoords.get(i) * 2],         // 0, x
                    verticesInMeters[pointsCoords.get(i) * 2 + 1],     // 1, y
                    verticesInMeters[pointsCoords.get(i+1) * 2],       // 2, x
                    verticesInMeters[pointsCoords.get(i+1) * 2 + 1],   // 3, y
                    verticesInMeters[pointsCoords.get(i+2) * 2],       // 4, x
                    verticesInMeters[pointsCoords.get(i+2) * 2 + 1]    // 5, y
            };

            PolygonShape shape = new PolygonShape();
            shape.set(triangles);
            saveFixture(shape, dfr);
        }

        return this;
    }


    public ComplexBB addFixturesFromImgChainshape(float[] imgPoints, float imgWidth, float imgHeight, DFR dfr) {
        float[] verticesInMeters = getPreparedArray(false, imgPoints, imgWidth, imgHeight);
        ChainShape shape = new ChainShape();
        shape.createChain(verticesInMeters);
        saveFixture(shape, dfr);
        return this;
    }


    // ----------------------  REDIRECTED METHODS ---------------------- //

    public ComplexBB addBoxFixture(float wPixels, float hPixels, DFR dfr) {
        return this.addBoxFixture(wPixels, hPixels, dfr, null);
    }

    public ComplexBB addCircleFixture(float rPixels, DFR dfr) {
        return this.addCircleFixture(rPixels, dfr, null);
    }


    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  HELPER FUNCTIONS  /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////


    private ComplexBB saveFixture(Shape shape, DFR dfr) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = dfr.density;
        fixtureDef.friction = dfr.friction;
        fixtureDef.restitution = dfr.restitution;
        fixtureDefs.add(fixtureDef);
        return this;
    }


    private float[] getPreparedArray(boolean endpointsMustBeDiff, float[] imgPoints, float imgWidth, float imgHeight) {
        final float hWidth = imgWidth / 2f, hHeight = imgHeight / 2f;
        final float p1x = imgPoints[0], p1y = imgPoints[1];
        final float pzx = imgPoints[imgPoints.length - 2];
        final float pzy = imgPoints[imgPoints.length - 1];
        final boolean areSame = MathUtils.isEqual(p1x, pzx) && MathUtils.isEqual(p1y, pzy);

        if(endpointsMustBeDiff) {
            // If they're not different (same), we must remove the last two elements
            // p1, p2, ..., pN, pZ=p1    =>  remove pZ from the end
            // If they are different, we dont do anything
            float[] verticesInMeters = new float[areSame ? imgPoints.length - 2 : imgPoints.length];
            for (int i = 0; i < verticesInMeters.length; i++) {
                verticesInMeters[i] = (i % 2 == 0)
                        ? (imgPoints[i] - hWidth) / PPM    // width
                        : (hHeight - imgPoints[i]) / PPM;  // height
            }
            return verticesInMeters;
        }

        // if(endpointsMustBeSAME):
        float[] verticesInMeters;
        if(areSame) {
            // If they are the same, dont do anything
            verticesInMeters = new float[imgPoints.length];
        }
        else {
            // If they're not the same we must append x0, y0 to the end
            // p1, p2, ..., pZ!=p1    =>  add p1 once again after pN
            verticesInMeters = new float[imgPoints.length + 2];
            verticesInMeters[verticesInMeters.length - 2] = (p1x - hWidth) / PPM;
            verticesInMeters[verticesInMeters.length - 1] = (hHeight - p1y) / PPM;
        }

        for (int i = 0; i < imgPoints.length; i++) {
            verticesInMeters[i] = (i % 2 == 0)
                    ? (imgPoints[i] - hWidth) / PPM    // width
                    : (hHeight - imgPoints[i]) / PPM;  // height
        }

        return verticesInMeters;
    }


    private float[] convertToMeters(float[] verticesInPixels) {
        for (int i = 0; i < verticesInPixels.length; i++)
            verticesInPixels[i] /= PPM;

        return verticesInPixels;
    }


}


