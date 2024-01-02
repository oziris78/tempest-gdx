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

package com.twistral.tempest.assetsorter;


import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;


public final class AssetSorter {


    /*  FIELDS  */

    private AssetManager assetManager;
    private HashMap<String, AssetGroup> groups;
    private HashMap<String, Disposable> immediatelyNeededAssets;


    /*  CONSTRUCTORS  */

    public AssetSorter(){
        this.assetManager = new AssetManager();
        this.immediatelyNeededAssets = new HashMap<>();
        this.groups = new HashMap<>();
    }



    /*  MAIN METHODS  */


    /**
     * Adds the specified immediately-needed-asset with the specified assetKey to it's inside.
     * @param assetKey key for your immediately-needed-assets asset
     * @param asset any immediately-needed-assets asset
     * @param <T> type of your asset
     */
    public <T extends Disposable> void addImmediatelyNeededAsset(String assetKey, T asset){
        if( this.immediatelyNeededAssets.containsKey(assetKey) )
            throw new RuntimeException("This key already exists: " + assetKey);
        this.immediatelyNeededAssets.put(assetKey, asset);
    }



    /**
     * Adds the specified to-be-loaded asset with the specified assetKey to the specified group.
     * If this group doesn't exist, it will create it for you!
     * @param groupKey key for your group
     * @param assetKey key for your to-be-loaded asset
     * @param asset any to-be-loaded asset
     */
    public void addAsset(String groupKey, String assetKey, AssetDescriptor asset){
        if(!this.groups.containsKey(groupKey)){ // group doesn't exist so create it
            this.groups.put( groupKey, new AssetGroup() );
        }
        // find the group and add the asset to that group
        this.groups.get(groupKey).addAsset(assetKey, asset);
    }



    /**
     * Use this method to get your to-be-loaded assets. You should call
     * {@link #update(String)} or {@link #update(String, int)} to load some
     * group before you get any assets from that group.
     * Example:  assetSorter.getResource("myFont", BitmapFont.class);
     * @param groupKey key for your group.
     * @param assetKey key for your to-be-loaded asset.
     * @param clazz the class of your to-be-loaded asset.
     * @param <T> type of the asset.
     * @return Your to-be-loaded asset.
     */
    public <T> T getResource(String groupKey, String assetKey, Class<T> clazz) {
        return this.assetManager.get(getGroup(groupKey).getAsset(assetKey).fileName, clazz);
    }



    /**
     * Use this method to get your immediately-needed assets.
     * Example:  assetSorter.getResource("myFont", BitmapFont.class);
     * @param assetKey key for your immediately-needed asset.
     * @param clazz the class of your immediately-needed asset.
     * @param <T> any type that extends/implements {@link Disposable}.
     * @return Your immediately-needed asset after it's casted into the specified class.
     */
    public <T extends Disposable> T getResource(String assetKey, Class<T> clazz){
        if( !this.immediatelyNeededAssets.containsKey(assetKey) )
            throw new RuntimeException("This key doesn't exists: " + assetKey);
        return clazz.cast( immediatelyNeededAssets.get(assetKey) );
    }



    /**  Starts/continues loading the specified group with milliseconds.  */
    public boolean update(String groupKey, int milliseconds) {
        queueGroup( getGroup(groupKey) );
        return this.assetManager.update(milliseconds);
    }



    /**
     * Starts/continues loading the specified group with milliseconds set to 17 which is the recommended value.
     * Example:  while(!game.assetSorter.update("GAME_SCREEN")){ float curProgress = game.assetSorter.getPercentage(); ... }
     */
    public boolean update(String groupKey) {
        return update(groupKey, 17);
    }


    /** Disposes all assets. (both to-be-loaded assets and immediately-needed assets) */
    public void disposeAll() {
        // dispose all to-be-loaded assets
        this.assetManager.dispose();
        // dispose all immediately needed assets
        for(Disposable asset : this.immediatelyNeededAssets.values()) asset.dispose();
    }


    /*  UTILITY METHODS  */

    /**  @return The current progress of the assetSorter (of all queued assets) as a float in range [0,1]  */
    public float getPercentage() {
        return this.assetManager.getProgress();
    }



    /*  GETTERS AND SETTERS  */


    /**
     * If you're only going to use AsserSorter for your asset management needs, then you will never ever
     * need this method because AssetSorter does everything that you need to do with assetManager for you!
     * <br> However, I'm leaving this method in just in case you ever want to implement something on your own.
     * @return the assetManager wrapped inside this assetSorter
     */
    public AssetManager getAssetManager() {return assetManager;}



    /*  HELPERS  */

    private void queueGroup(AssetGroup group) {
        if (!group.isQueued())
            group.queue(this.assetManager);
    }


    private AssetGroup getGroup(String groupKey) {
        if(!this.groups.containsKey(groupKey))
            throw new RuntimeException("Group not found for this groupID: " + groupKey);
        return this.groups.get(groupKey);
    }


}
