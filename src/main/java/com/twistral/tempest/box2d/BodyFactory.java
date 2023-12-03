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

package com.twistral.tempest.box2d;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import static com.twistral.tempest.box2d.Box2DConstants.PPM;


/**
 * Use this class to create physics (box2d) bodies.
 */
public class BodyFactory {


    /*  --------------------------------------  */
    /*  --------------- BODIES ---------------  */
    /*  --------------------------------------  */



    public static Body createBodyAsBox(World world, BodyType bodyType, boolean isRotationFixed,
                                      int bodyWidthInPixels, int bodyHeightInPixels,
                                      float bodyPosXInPixels, float bodyPosYInPixels,
                                      float density, float friction, float restitution,
                                      float linearDampingInPixels, float angleInDegrees,
                                      boolean useBitsAndGroupIndexes, short categoryBits, short maskBits, short groupIndex,
                                      Object userDataForBody, boolean isSensor)
    {
        // BODY DEFINITION => SHAPE => FIXTURE DEFINITION => BODY
        BodyDef bdef = initBdefAsBox(bodyType, bodyWidthInPixels, bodyHeightInPixels, bodyPosXInPixels, bodyPosYInPixels, angleInDegrees);
        PolygonShape shape = getBoxShape(bodyWidthInPixels, bodyHeightInPixels);
        FixtureDef fdef = initFdef(shape, density, friction, restitution, isSensor, useBitsAndGroupIndexes, categoryBits, maskBits, groupIndex);
        Body body = initBody(world, bdef, fdef, linearDampingInPixels, isRotationFixed, userDataForBody);

        // DISPOSE AND RETURN
        shape.dispose();
        return body;
    }



    public static Body createBodyAsCircle(World world, BodyType bodyType, boolean isRotationFixed,
                                         float leftDownPointXInPixels, float leftDownPointYInPixels, float radiusInPixels,
                                         float density, float friction, float restitution,
                                         float linearDampingInPixels, float angleInDegrees,
                                         boolean useBitsAndGroupIndexes, short categoryBits, short maskBits, short groupIndex,
                                         Object userDataForBody, boolean isSensor)
    {
        // BODY DEFINITION => SHAPE => FIXTURE DEFINITION => BODY
        BodyDef bdef = initBdefAsCircle(bodyType, leftDownPointXInPixels, leftDownPointYInPixels, radiusInPixels, angleInDegrees);
        CircleShape shape = getCircleShape(radiusInPixels);
        FixtureDef fdef = initFdef(shape, density, friction, restitution, isSensor, useBitsAndGroupIndexes, categoryBits, maskBits, groupIndex);
        Body body = initBody(world, bdef, fdef, linearDampingInPixels, isRotationFixed, userDataForBody);

        // DISPOSE AND RETURN
        shape.dispose();
        return body;
    }



    public static Body createBodyAsChainShape(World world, BodyType bodyType, boolean isRotationFixed,
                                              int bodyWidthInPixels, int bodyHeightInPixels,
                                              float bodyPosXInPixels, float bodyPosYInPixels,
                                              Vector2[] vertices, float density, float friction, float restitution,
                                              float linearDampingInPixels, float angleInDegrees,
                                              boolean useBitsAndGroupIndexes, short categoryBits, short maskBits, short groupIndex,
                                              Object userDataForBody, boolean isSensor)
    {
        // BODY DEFINITION => SHAPE => FIXTURE DEFINITION => BODY
        BodyDef bdef = initBdefAsBox(bodyType, bodyWidthInPixels, bodyHeightInPixels, bodyPosXInPixels, bodyPosYInPixels, angleInDegrees);
        ChainShape shape = getChainShape(vertices);
        FixtureDef fdef = initFdef(shape, density, friction, restitution, isSensor, useBitsAndGroupIndexes, categoryBits, maskBits, groupIndex);
        Body body = initBody(world, bdef, fdef, linearDampingInPixels, isRotationFixed, userDataForBody);

        // DISPOSE AND RETURN
        shape.dispose();
        return body;
    }


    public static Body createBodyWithPolygonTriangulation(World world, BodyType bodyType, boolean isRotationFixed,
                                                          int bodyWidthInPixels, int bodyHeightInPixels,
                                                          float bodyPosXInPixels, float bodyPosYInPixels,
                                                          float[] vertices, float density, float friction, float restitution,
                                                          float linearDampingInPixels, float angleInDegrees,
                                                          boolean useBitsAndGroupIndexes, short categoryBits, short maskBits, short groupIndex,
                                                          Object userDataForBody, boolean isSensor)
    {
        // BODY DEFINITION => BODY => ADD FIXTURES
        BodyDef bdef = initBdefAsBox(bodyType, bodyWidthInPixels, bodyHeightInPixels, bodyPosXInPixels, bodyPosYInPixels, angleInDegrees);

        // CHECK FOR NULLS
        Body body = initBody(world, bdef, null, linearDampingInPixels, isRotationFixed, userDataForBody);

        applyPolygonTriangulation(body, vertices, density, friction, restitution,
                useBitsAndGroupIndexes, categoryBits, maskBits, groupIndex, isSensor);

        return body;
    }





    /*  --------------------------------------  */
    /*  ----------- ACTUAL METHODS -----------  */
    /*  --------------------------------------  */


    private static Body initBody(World world, BodyDef bdef, FixtureDef fdef,
                                 float linearDampingInPixels, boolean isRotationFixed, Object userDataForBody)
    {
        Body body = world.createBody(bdef);
        if(fdef != null) body.createFixture(fdef);
        body.setLinearDamping(linearDampingInPixels);
        body.setFixedRotation(isRotationFixed);
        body.setUserData(userDataForBody);

        return body;
    }


    /////////////////
    /*  BODY DEFS  */
    /////////////////

    private static BodyDef initBdefAsBox(BodyType bodyType, int bodyWidthInPixels, int bodyHeightInPixels,
                                        float bodyPosXInPixels, float bodyPosYInPixels, float angleInDegrees)
    {
        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        bdef.position.set( // box2d bodies only use their centers as their position so add half of the sizes
                (bodyPosXInPixels + bodyWidthInPixels / 2) / PPM,
                (bodyPosYInPixels + bodyHeightInPixels / 2) / PPM
        );
        bdef.angle = angleInDegrees * MathUtils.degreesToRadians; // box2d uses radians

        return bdef;
    }


    private static BodyDef initBdefAsCircle(BodyType bodyType, float leftDownPointXInPixels, float leftDownPointYInPixels,
                                           float radiusInPixels, float angleInDegrees)
    {
        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        bdef.position.set(
                (leftDownPointXInPixels + radiusInPixels) / PPM,
                (leftDownPointYInPixels + radiusInPixels) / PPM
        );
        bdef.angle = angleInDegrees * MathUtils.degreesToRadians; // box2d uses radians

        return bdef;
    }


    ////////////////////
    /*  FIXTURE DEFS  */
    ////////////////////

    private static FixtureDef initFdef(Shape shape, float density, float friction, float restitution,
                                      boolean isSensor, boolean useBitsAndGroupIndexes,
                                      short categoryBits, short maskBits, short groupIndex)
    {
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.friction = friction;
        fdef.restitution = restitution;
        fdef.isSensor = isSensor;
        if (useBitsAndGroupIndexes) {
            fdef.filter.categoryBits = categoryBits;
            fdef.filter.maskBits = maskBits;
            fdef.filter.groupIndex = groupIndex;
        }

        return fdef;
    }


    //////////////
    /*  SHAPES  */
    //////////////

    private static PolygonShape getBoxShape(int bodyWidthInPixels, int bodyHeightInPixels){
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bodyWidthInPixels / 2f / PPM, bodyHeightInPixels / 2f / PPM);

        return shape;
    }


    private static CircleShape getCircleShape(float radiusInPixels) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radiusInPixels / PPM);

        return shape;
    }


    private static ChainShape getChainShape(Vector2[] vertices){
        ChainShape shape = new ChainShape();
        shape.createChain(vertices);

        return shape;
    }


    private static void applyPolygonTriangulation(Body body, float[] vertices, float density, float friction, float restitution,
                                                  boolean useBitsAndGroupIndexes, short categoryBits, short maskBits, short groupIndex,
                                                  boolean isSensor)
    {
        final Array<Polygon> polygons = new Array<>();
        Polygon polygon = new Polygon(vertices);
        ShortArray pointsCoords = new EarClippingTriangulator().computeTriangles(polygon.getVertices());

        for (int i = 0; i < pointsCoords.size; i += 3) {
            float[] triangles = new float[6];

            for (int j = 0; j < 3; j++) {
                short point = pointsCoords.get(i + j);

                triangles[j * 2] = polygon.getVertices()[point * 2];
                triangles[j * 2 + 1] = polygon.getVertices()[point * 2 + 1];
            }

            polygons.add(new Polygon(triangles));

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(triangles);

            FixtureDef fdef = initFdef(polygonShape, density, friction, restitution, isSensor,
                    useBitsAndGroupIndexes, categoryBits, maskBits, groupIndex);

            body.createFixture(fdef);
            polygonShape.dispose();
        }

    }




}
