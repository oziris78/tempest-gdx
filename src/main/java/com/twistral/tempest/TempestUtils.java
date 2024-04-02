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

package com.twistral.tempest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;



public class TempestUtils {


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
    public static void resizeWindow(int width, int height, int minWidth, int minHeight) {
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
            Gdx.graphics.setWindowedMode(width, height);
    }


    public static void fullscreenWindow() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }


    /////////////////////////////////////////////////////////////////////////////////////


    public static void clear(float r, float g, float b, float a){
        Gdx.gl20.glClearColor(r, g, b, a);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void clear() {
        clear(0f, 0f, 0f, 1f);
    }

    public static void clear(Color color) {
        clear(color.r, color.g, color.b, color.a);
    }


    /////////////////////////////////////////////////////////////////////////////////////


    public static TextureRegion repeatedTexture(Texture texture, int row, int col) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.setRegion(0, 0, texture.getWidth() * row, texture.getHeight() * col);
        return textureRegion;
    }


    /////////////////////////////////////////////////////////////////////////////////////


    /**
     * You need to run this function once in a method like create() or show(). <b>Also make sure
     * to call {@link AnimatedTiledMapTile#updateAnimationBaseTime()} in your update() method before
     * any drawing happens so that animation will not be stuck on the first frame all the time.</b>
     * <br> Example: TiledUtils.addAnimatedTileToMap(map, 0.5f, "myCoolTileset", "tile-layer", "blueFlowerAnimation", "heyyo");
     * @param map Any tiled map
     * @param durationOfEachFrame How much time is one frame going to take in the animation
     * @param tilesetNameWithoutExtension What is the name of the tileset that these animation frames are in? (without extension)
     * @param layerNameForAnimation The tile layer in your map that will have this animation
     * @param animationPropertyName You need to set a property in Tiled editor to all the animation tiles (name and value of that property
     *                     should be a string) this is the name of that property
     * @param animationPropertyValue This is the value of that property (the property you set on Tiled)
     */
    public static void addAnimatedTileToMap(TiledMap map, float durationOfEachFrame,
                            String tilesetNameWithoutExtension, String layerNameForAnimation,
                            String animationPropertyName, String animationPropertyValue)
    {
        Array<StaticTiledMapTile> frameTiles = new Array<>();

        Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet(tilesetNameWithoutExtension).iterator();
        while (tiles.hasNext()) {
            TiledMapTile tile = tiles.next();
            MapProperties tileProp = tile.getProperties();
            if(tileProp.containsKey(animationPropertyName)) {
                if(tileProp.get(animationPropertyName, String.class).equals(animationPropertyValue)) {
                    frameTiles.add((StaticTiledMapTile) tile);
                }
            }
        }

        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(durationOfEachFrame, frameTiles);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerNameForAnimation);
        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();

        for (int x = 0; x < layerWidth; x++) {
            for (int y = 0; y < layerHeight; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                MapProperties cellProp = cell.getTile().getProperties();
                if (cellProp.containsKey(animationPropertyName)) {
                    if(cellProp.get(animationPropertyName, String.class).equals(animationPropertyValue)) {
                        cell.setTile(animatedTile);
                    }
                }
            }
        }

    }


}
