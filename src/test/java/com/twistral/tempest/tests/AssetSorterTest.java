// Copyright 2020-2025 Oğuzhan Topaloğlu
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


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twistral.tempest.TempestUtils;
import com.twistral.tempest.assetsorter.AssetSorter;


public class AssetSorterTest extends ApplicationAdapter {

    SpriteBatch batch;

    Viewport viewport;
    OrthographicCamera camera;

    AssetSorter assetSorter;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.position.set(300, 300, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        assetSorter = new AssetSorter();
        assetSorter.addExistingAsset("notCoolFont", new BitmapFont());

        assetSorter.defineAsset("coolImage", "test-assets/badlogic.jpg", Texture.class);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetSorter.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetSorter.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreeTypeFontLoaderParameter mySmallFont = new FreeTypeFontLoaderParameter();
        mySmallFont.fontFileName = "test-assets/font/Audiowide-Regular.ttf";
        mySmallFont.fontParameters.size = 30;
        assetSorter.defineAsset("coolFont", "test-assets/font/Audiowide-Regular.ttf", BitmapFont.class, mySmallFont);
    }




    @Override
    public void render() {
        // Updating
        final float deltaTime = Gdx.graphics.getDeltaTime();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            assetSorter.queueAsset("coolImage");
            assetSorter.queueAsset("coolFont");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            assetSorter.keepLoading();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            assetSorter.finishLoading();
        }

        // Rendering
        TempestUtils.clear();
        batch.begin();

        if(assetSorter.isAvailable("coolImage")) {
            Texture badlogic = assetSorter.getAsset("coolImage", Texture.class);
            batch.draw(badlogic, 0, 0);
        }

        BitmapFont font = assetSorter.getAsset("notCoolFont", BitmapFont.class);
        font.draw(batch, "Press A to queue image and cool font!", 20, 500);
        font.draw(batch, "Press S to load for 17 ms", 20, 450);
        font.draw(batch, "Press D to load till the end", 20, 400);
        font.draw(batch, String.format("Current progress: %f", assetSorter.getProgress()), 500, 500);

        if(assetSorter.isAvailable("coolFont")) {
            BitmapFont font2 = assetSorter.getAsset("coolFont", BitmapFont.class);
            font2.draw(batch, "OMG you have a cool font!", 20, 575);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }



    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        new LwjglApplication(new AssetSorterTest(), config);
    }

}
