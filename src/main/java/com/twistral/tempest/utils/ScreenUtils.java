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

package com.twistral.tempest.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;


public class ScreenUtils {

    private ScreenUtils() {}

    /*  COLOR BUFFERS  */

    /**
     * Clears the color buffers with black.
     */
    public static void clear(){
        clear(0f, 0f, 0f, 1f);
    }


    /**
     * Clears the color buffers with the specified color.
     * @param color any color
     */
    public static void clear(Color color){
        clear(color.r, color.g, color.b, color.a);
    }


    /**
     * Clears the color buffers with the specified color.
     * @param r red value in range [0,1]
     * @param g green value in range [0,1]
     * @param b blue value in range [0,1]
     * @param a alpha value in range [0,1]
     */
    public static void clear(float r, float g, float b, float a){
        Gdx.gl20.glClearColor(r, g, b, a);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    /*  WINDOW  */

    public static void resizeWindow(int width, int height){
        Gdx.graphics.setWindowedMode(width, height);
    }


    /**
     * Only resizes the window if the new width is less the minWidth or the new height is less than minHeight
     * @param width the new width of the window after a window resizing
     * @param height the new width of the window after a window resizing
     * @param minWidth min width allowed for this window to have
     * @param minHeight min height allowed for this window to have
     */
    public static void resizeWindow(int width, int height, int minWidth, int minHeight){
        boolean shouldBeResized = false;
        if (width < minWidth) {
            width = minWidth;
            shouldBeResized = true;
        }
        if (height < minHeight) {
            height = minHeight;
            shouldBeResized = true;
        }
        if (shouldBeResized)
            resizeWindow(width, height);
    }


    public static void fullscreenWindow(){
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }





}
