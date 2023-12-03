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


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static com.twistral.tempest.box2d.Box2DConstants.PPM;

public final class Box2DUtils {


    /*  METHODS  */

    /**  Initializes all Box2D classes with PPM, this method must be executed before any Box2D class is used  */
    public static void init(final float PPM) {
        Box2DConstants.PPM = PPM;
    }


    public static void moveSprite(Body body, Sprite sprite) {
        sprite.setPosition(body.getPosition().x * PPM - sprite.getWidth() / 2, body.getPosition().y * PPM - sprite.getHeight() / 2);
    }

    public static void rotateSprite(Body body, Sprite sprite) {
        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }

    public static void moveAndRotateSprite(Body body, Sprite sprite) {
        moveSprite(body, sprite);
        rotateSprite(body, sprite);
    }


    /**
     * Note: first and the last vector must be the same for Box2D to close the shape.
     * @param arr any array including vertices as vectors
     * @param imgWidth width of image
     * @param imgHeight height of image
     * @return changed and ready vector2 array for {@link BodyFactory}
     */
    public static Vector2[] initArrayForChainShapes(Vector2[] arr, float imgWidth, float imgHeight){
        float hWidth = imgWidth / 2f, hHeight = imgHeight / 2f;

        for(Vector2 vec : arr){
            vec.x = (vec.x - hWidth) / PPM;
            vec.y = (hHeight - vec.y) / PPM;
        }

        return arr;
    }


    /**
     * Note: first and the last two floats (points) must be DIFFERENT for Box2D to work properly.
     * @param arr any array including vertices as floats, x1,y1,x2,y2,...
     * @param imgWidth width of image
     * @param imgHeight height of image
     * @return changed and ready float array for {@link BodyFactory}
     */
    public static float[] initArrayForTriangulation(float[] arr, float imgWidth, float imgHeight) {
        float hWidth = imgWidth / 2f, hHeight = imgHeight / 2f;
        int elemCount = arr.length;

        for (int i = 0; i < elemCount; i++) {
            /* it's even <=> it's a width */
            if(i % 2 == 0) arr[i] = (arr[i] - hWidth) / PPM;
            else arr[i] = (hHeight - arr[i]) / PPM;
        }

        return arr;
    }



}
