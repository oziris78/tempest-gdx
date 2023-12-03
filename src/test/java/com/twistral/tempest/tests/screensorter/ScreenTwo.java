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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.twistral.tempest.utils.ScreenUtils;
import com.twistral.tempest.screensorter.TScreen;

import java.util.Objects;


public class ScreenTwo implements TScreen {

    private final ScreenSorterTest game;

    public ScreenTwo(final ScreenSorterTest game){
        this.game = game;
        System.out.println("constructor of screen two");
    }


    @Override
    public void configure() {
        System.out.println("config of screen two");
    }


    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
            this.game.screenSorter.nullifyScreen(MyScreens.SCREEN_THREE);
            this.game.setScreen(this.game.screenSorter.getScreen(MyScreens.SCREEN_THREE, game, 152));
        }
    }


    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);

        this.game.batch.begin();
        this.game.font.draw(this.game.batch, "hi from screen TWO press K to change screens", 100f, 100f);
        this.game.batch.end();
    }


    // unused methods
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenTwo screenTwo = (ScreenTwo) o;
        return Objects.equals(game, screenTwo.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }
}
