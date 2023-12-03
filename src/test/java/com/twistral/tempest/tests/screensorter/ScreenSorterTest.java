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

package com.twistral.tempest.tests.screensorter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twistral.tempest.screensorter.ScreenSorter;


public class ScreenSorterTest extends Game {

    SpriteBatch batch;
    BitmapFont font;
    ScreenSorter screenSorter;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        screenSorter = new ScreenSorter();
        screenSorter.putScreen(MyScreens.SCREEN_ONE, ScreenOne.class, ScreenSorterTest.class);
        screenSorter.putScreen(MyScreens.SCREEN_TWO, ScreenTwo.class, ScreenSorterTest.class);
        screenSorter.putScreen(MyScreens.SCREEN_THREE, ScreenThree.class, ScreenSorterTest.class, int.class);

        this.setScreen(screenSorter.getScreen(MyScreens.SCREEN_ONE, this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        screenSorter.disposeAll();
        font.dispose();
        batch.dispose();
    }

}
