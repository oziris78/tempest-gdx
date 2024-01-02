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

package com.twistral.tempest.box2d;


import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;


/**  A utility class to use Tiled with Box2D.  */
public class TiledParser {


    /**
     * Parses every object that is in the specified tiledMap's layer and adds those objects to the Box2D world.
     * @param world box2d world
     * @param objects objects of some tiledmap's layer
     * @param userData any object for this method to use to set userData for every single object
     */
    public static void parseTiledObjectLayer(World world, MapObjects objects, Object userData) {
        Body body;

        for (MapObject object : objects) {
            MyPair pair = createShapeAndBodyDefinition(object);
            BodyDef bDef = pair.getBodyDef();
            Shape shape = pair.getShape();

            body = world.createBody(bDef);
            body.createFixture(shape, 1.0f);
            body.setUserData(userData);

            shape.dispose();
        }

    }



    /////////////////////////////////////////////////////////
    //////////////////////  HELPERS  ////////////////////////
    /////////////////////////////////////////////////////////


    private static class MyPair {
        private Shape shape;
        private BodyDef bodyDef;

        public MyPair(Shape shape, BodyDef bodyDef) {
            this.shape = shape;
            this.bodyDef = bodyDef;
        }

        public Shape getShape() {
            return shape;
        }

        public BodyDef getBodyDef() {
            return bodyDef;
        }
    }


    private static MyPair createShapeAndBodyDefinition(MapObject object) {
        if (object instanceof PolygonMapObject)
            return createPolyLine((PolygonMapObject) object);
        if (object instanceof RectangleMapObject)
            return createBox((RectangleMapObject) object);
        if (object instanceof EllipseMapObject)
            return createCircle((EllipseMapObject) object);
        else return null;
    }


    private static MyPair createCircle(EllipseMapObject object) {
        final float PPM = Box2DConstants.PPM;
        Ellipse ellipse = object.getEllipse();

        // force it to be a circle if it's not a circle
        if (ellipse.width != ellipse.height){
            if(ellipse.height < ellipse.width){ // if height is smaller
                ellipse.width = ellipse.height; // make the bigger one (width) equal to smaller one (height)
            }
            else{ // if width is smaller
                ellipse.height = ellipse.width; // make the bigger one (height) equal to smaller one (width)
            }
        }

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ellipse.width / 2f / PPM);

        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(ellipse.x / PPM, ellipse.y / PPM);

        return new MyPair(circleShape, bDef);
    }


    private static MyPair createBox(RectangleMapObject object) {
        final float PPM = Box2DConstants.PPM;
        Rectangle rectangle = object.getRectangle();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.getWidth() / 2f / PPM, rectangle.getHeight() / 2f / PPM);

        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(
                (rectangle.getX() + rectangle.getWidth() / 2f) / PPM,
                (rectangle.getY() + rectangle.getHeight() / 2f) / PPM
        );

        return new MyPair(polygonShape, bDef);
    }


    private static MyPair createPolyLine(PolygonMapObject object) {
        final float PPM = Box2DConstants.PPM;
        float[] vertices = object.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < worldVertices.length; i++)
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);

        ChainShape cs = new ChainShape();

        if (firstAndLastAreSame(worldVertices)) {
            cs.createChain(worldVertices);
        } else {  // must add the last one manually
            Vector2[] newWorldVertices = new Vector2[worldVertices.length + 1];

            for (int i = 0; i < worldVertices.length; i++) newWorldVertices[i] = worldVertices[i];
            Vector2 firstVertex = worldVertices[0];
            newWorldVertices[newWorldVertices.length - 1] = new Vector2(firstVertex.x, firstVertex.y);

            cs.createChain(newWorldVertices);
        }

        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        return new MyPair(cs, bDef);
    }


    private static boolean firstAndLastAreSame(Vector2[] worldVertices) {
        return worldVertices[0].equals(worldVertices[worldVertices.length - 1]);
    }


}
