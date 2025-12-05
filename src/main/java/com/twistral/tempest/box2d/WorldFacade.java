// Copyright 2024-2025 Oğuzhan Topaloğlu
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


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.twistral.tempest.box2d.Box2DUtils.*;


public class WorldFacade {

    private final World world;
    private final float PPM;

    public WorldFacade(World world, float PPM) {
        this.world = world;
        this.PPM = PPM;
    }


    /*/////////////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  GENERAL METHODS  ///////////////////////////*/
    /*/////////////////////////////////////////////////////////////////////////*/


    public void moveSpriteToBody(Sprite sprite, Body body) {
        sprite.setPosition(
            body.getPosition().x * PPM - sprite.getWidth() / 2,
            body.getPosition().y * PPM - sprite.getHeight() / 2
        );
    }


    public void rotateSpriteToBody(Sprite sprite, Body body) {
        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }


    public void syncSpriteToBody(Sprite sprite, Body body) {
        moveSpriteToBody(sprite, body);
        rotateSpriteToBody(sprite, body);
    }


    /*///////////////////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  TILED RELATED METHODS  ///////////////////////////*/
    /*///////////////////////////////////////////////////////////////////////////////*/


    private static final String POLY_UDATA_STR = "PolygonMapObject";
    private static final String RECT_UDATA_STR = "RectangleMapObject";
    private static final String ELLP_UDATA_STR = "EllipseMapObject";


    /**
     * Parses every object thats in the given {@link MapObjects} instance and adds them to the world. <br>
     * This function also sets the userData field of each parsed object to one of the following strings
     * according to their shape: {@link #POLY_UDATA_STR}, {@link #RECT_UDATA_STR}, {@link #ELLP_UDATA_STR}.
     * @param objects objects of some tiledmap's layer
     */
    public void parseTiledObjectLayer(MapObjects objects) {
        for (MapObject mapObject : objects) {
            BodyDef bDef = null;
            Shape shape = null;
            String userDataString = null;

            // Fill bDef and shape according to the object type
            if (mapObject instanceof PolygonMapObject) {
                userDataString = POLY_UDATA_STR;
                PolygonMapObject castedObj = (PolygonMapObject) mapObject;
                float[] vertices = castedObj.getPolygon().getTransformedVertices();
                Vector2[] worldVertices = new Vector2[vertices.length / 2];

                for (int i = 0; i < worldVertices.length; i++)
                    worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);

                ChainShape cs = new ChainShape();

                // if first and last are the same
                if (worldVertices[0].equals(worldVertices[worldVertices.length - 1])) {
                    cs.createChain(worldVertices);
                }
                else {  // must add the last one manually
                    Vector2[] newWorldVertices = new Vector2[worldVertices.length + 1];

                    for (int i = 0; i < worldVertices.length; i++) newWorldVertices[i] = worldVertices[i];
                    Vector2 firstVertex = worldVertices[0];
                    newWorldVertices[newWorldVertices.length - 1] = new Vector2(firstVertex.x, firstVertex.y);

                    cs.createChain(newWorldVertices);
                }

                bDef = new BodyDef();
                bDef.type = BodyDef.BodyType.StaticBody;
                shape = cs;
            }
            else if (mapObject instanceof RectangleMapObject) {
                userDataString = RECT_UDATA_STR;
                RectangleMapObject castedObj = (RectangleMapObject) mapObject;

                Rectangle rectangle = castedObj.getRectangle();
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth() / 2f / PPM, rectangle.getHeight() / 2f / PPM);

                bDef = new BodyDef();
                bDef.type = BodyDef.BodyType.StaticBody;
                bDef.position.set(
                        (rectangle.getX() + rectangle.getWidth() / 2f) / PPM,
                        (rectangle.getY() + rectangle.getHeight() / 2f) / PPM
                );

                shape = polygonShape;
            }
            else if (mapObject instanceof EllipseMapObject) {
                userDataString = ELLP_UDATA_STR;
                EllipseMapObject castedObj = (EllipseMapObject) mapObject;
                Ellipse ellipse = castedObj.getEllipse();

                // force it to be a circle if it's not a circle
                if (ellipse.width != ellipse.height){
                    if(ellipse.height < ellipse.width) { // if height is smaller
                        ellipse.width = ellipse.height; // make the bigger one (width) equal to smaller one (height)
                    }
                    else { // if width is smaller
                        ellipse.height = ellipse.width; // make the bigger one (height) equal to smaller one (width)
                    }
                }

                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(ellipse.width / 2f / PPM);

                bDef = new BodyDef();
                bDef.type = BodyDef.BodyType.StaticBody;
                bDef.position.set(ellipse.x / PPM, ellipse.y / PPM);

                shape = circleShape;
            }

            Body body = world.createBody(bDef);
            body.createFixture(shape, 1.0f);
            body.setUserData(userDataString);

            shape.dispose();
        }

    }


    /*///////////////////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  BODY CREATION METHODS  ///////////////////////////*/
    /*///////////////////////////////////////////////////////////////////////////////*/


    public Body newSimpleBodyAsBox(BodyType type, float xPixels, float yPixels,
                                   float wPixels, float hPixels, DFR dfr, float angleDegrees)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((xPixels + wPixels / 2f) / PPM, (yPixels + hPixels / 2f) / PPM);
        bodyDef.type = type;
        bodyDef.angle = angleDegrees * MathUtils.degreesToRadians;
        Body body = world.createBody(bodyDef);

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(wPixels / 2f / PPM, hPixels / 2f / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = dfr.density;
        fixtureDef.friction = dfr.friction;
        fixtureDef.restitution = dfr.restitution;
        body.createFixture(fixtureDef);

        boxShape.dispose();
        return body;
    }


    public Body newSimpleBodyAsCircle(BodyType type, float xPixels, float yPixels,
                                      float rPixels, DFR dfr, float angleDegrees)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((xPixels + rPixels) / PPM, (yPixels + rPixels) / PPM);
        bodyDef.type = type;
        bodyDef.angle = angleDegrees * MathUtils.degreesToRadians;
        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(rPixels / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = dfr.density;
        fixtureDef.friction = dfr.friction;
        fixtureDef.restitution = dfr.restitution;
        body.createFixture(fixtureDef);
        circleShape.dispose();

        return body;
    }


    public ComplexBB newComplexBody(BodyType type, float centerXPixels, float centerYPixels, float angleDegrees) {
        return new ComplexBB(this, type, centerXPixels, centerYPixels, angleDegrees);
    }


    // --------------  redirections  ------------- //

    public Body newSimpleBodyAsBox(BodyType type, float xPixels, float yPixels, float wPixels, float hPixels, DFR dfr) {
        return this.newSimpleBodyAsBox(type, xPixels, yPixels, wPixels, hPixels, dfr, 0f);
    }

    public Body newSimpleBodyAsCircle(BodyType type, float xPixels, float yPixels, float rPixels, DFR dfr) {
        return this.newSimpleBodyAsCircle(type, xPixels, yPixels, rPixels, dfr, 0f);
    }

    public ComplexBB newComplexBody(BodyType type, float centerXPixels, float centerYPixels) {
        return this.newComplexBody(type, centerXPixels, centerYPixels, 0f);
    }


    /*/////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  GETTERS  ///////////////////////////*/
    /*/////////////////////////////////////////////////////////////////*/

    public float getPPM() { return PPM; }
    public World getWorld() { return world; }


}

