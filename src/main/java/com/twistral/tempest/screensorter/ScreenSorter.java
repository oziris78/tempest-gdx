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

package com.twistral.tempest.screensorter;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;



/**
 * This class helps you organize your screens in an optimized way especially if you're going to
 * have multiple screens of a single class that extends Screen. <br> <br>
 *
 * This class only accepts screens that implement {@link TScreen}. The {@link TScreen} class
 * defines two additional methods: update(dt), configure(). <br> <br>
 *
 * You're supposted to reset every single field, input etc. you have in the screen in the configure()
 * method. This means you need to write more code but it will pay off at the end. <br> <br>
 *
 * You can also use update(dt) as your update method in each screen, this is a very common method
 * to have in every screen so {@link TScreen} automaticly defines this method for you! <br> <br>
 *
 * If you don't want to use the configure() method and just dispose the screen and initialize a new
 * one, you can do that with a collection of {@link #nullifyScreen(String)} and {@link #getScreen(String, Object...)}
 * methods. <br> <br>
 *
 */
public class ScreenSorter {

    private static class ScreenBag<T extends TScreen>{
        T screen;
        Class<T> screenClass;
        Constructor<T> constructor;

        public ScreenBag(Class<T> screenClass, Constructor<T> constructor){
            this.screen = null;
            this.screenClass = screenClass;
            this.constructor = constructor;
        }
    }

    ////////////////////
    /*  CONSTRUCTORS  */
    ////////////////////

    private final HashMap<String, ScreenBag> bags;

    public ScreenSorter(){
        this.bags = new HashMap<>();
    }


    ///////////////
    /*  METHODS  */
    ///////////////


    /**
     * Defines a screen with the specified key. <br>
     * The screen will be initialized as null and it will be automaticly created for you
     * with the constructor you specify later on.
     *
     * @param key a string to use as a key, it's recommended to
     *            define a "constants class" for your screen keys so that you
     *            won't need to write them every single time you need them
     * @param screenClass the class of the screen that implements {@link TScreen}
     * @param constructorParameterTypes the parameter types to determine the constructor
     * @param <T> the screen class
     */
    public <T extends TScreen> void putScreen(String key, Class<T> screenClass, Class<?>... constructorParameterTypes){
        try{
            this.bags.put(key, new ScreenBag<T>(screenClass, screenClass.getConstructor(constructorParameterTypes)));
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Disposes and nullifies the screenBag of the specified key. <br> <br>
     * Use this if you want to dispose a screen and create a new one automaticly
     * instead of using the {@link TScreen#configure()} method
     * @param key a screen key
     */
    public void nullifyScreen(String key){
        ScreenBag<?> bag = this.bags.get(key);
        if(bag.screen != null){
            bag.screen.dispose();
            bag.screen = null;
        }
    }


    /**
     * Gets the screen with the specified key from the screensorter, creates a new screen
     * if the current existing one is null. <br> <br>
     * It also runs the {@link TScreen#configure()} before returning it (allowing you to reset fields,
     * inputs etc. before you get the screen) <br> <br>
     * @param key a screen key
     * @param constructorArgs the arguments that will be used in the constructor if the
     *                        screen with the specified key is null (it will be constructed automaticly)
     * @param <T> class of the screen
     * @return the TScreen object in the correct type (not {@link TScreen} but the actual class of the screen)
     */
    public <T extends TScreen> T getScreen(String key, Object... constructorArgs){
        ScreenBag<T> bag = this.bags.get(key);
        try{
            if(bag.screen == null){
                Object newScreen = bag.constructor.newInstance(constructorArgs);
                bag.screen = bag.screenClass.cast(newScreen);
            }
            bag.screen.configure();
            return bag.screen;
        }
        catch (InvocationTargetException e) {e.printStackTrace();}
        catch (InstantiationException e) {e.printStackTrace();}
        catch (IllegalAccessException e) {e.printStackTrace();}
        throw new RuntimeException();
    }


    /**
     * Disposes the screenSorter (by disposing every single screen inside it)
     */
    public void disposeAll(){
        for(Map.Entry<String, ScreenBag> entry : this.bags.entrySet()){
            if(entry.getValue().screen != null){
                entry.getValue().screen.dispose();
            }
            this.bags.remove(entry.getKey());
        }
    }



}