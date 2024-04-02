// Copyright 2024 Oğuzhan Topaloğlu
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
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Disposable;
import com.twistral.tempest.TempestException.AssetIDDoesntExistException;
import com.twistral.tempest.TempestException.AssetIDAlreadyExistsException;
import java.util.*;



/**
 * {@link AssetSorter} is an improved version of the {@link AssetManager} class. <br><br>
 *
 * For {@link AssetSorter}, there are two types of assets: <br>
 * 1. Existing Assets: assets that are already initialized and loaded properly <br>
 * 2. Queueable Assets: assets that are currently not in the RAM, but can be put into a
 *     loading queue and be loaded into the RAM <br><br>
 *
 * There are two major problems with {@link AssetManager}: <br>
 * 1. It doesn't allow us to store our existing assets in the same place where we store our
 *     queueable assets. Ending up in a messy structure and object all over the place. <br>
 * 2. It has a very badly written design where the method names are misleading. <br><br>
 *
 * This is why {@link AssetSorter} exists. It allows us to store our existing assets inside it
 * while still providing everything that the {@link AssetManager} class provides. Also, it makes
 * a separation between loading, queueing and defining. <br><br>
 *
 * Here's a list of terminology differences ({@link AssetManager} to {@link AssetSorter}): <br>
 * -  functionality doesnt exist  =>  addExistingAsset, removeExistingAsset <br>
 * -  functionality doesnt exist  =>  defineAsset, undefineAsset <br>
 * -  load, unload                =>  queueAsset, dequeueAsset <br>
 * -  update, finishLoading       =>  keepLoading, finishLoading <br>
 * -  get                         =>  getAsset (works both for existing and queueable assets) <br><br>
 *
 * Usage is pretty straightforward: <br>
 * 1. For existing assets: ADD => GET <br>
 * 2. For queueable assets: DEFINE => QUEUE => LOAD => GET <br>
 */
public class AssetSorter implements Disposable {

    private final HashMap<String, Disposable> existingAssets;

    private final HashMap<String, AssetDescriptor> queueableAssets;
    private final AssetManager assetManager;


    public AssetSorter() {
        this.assetManager = new AssetManager();
        this.existingAssets = new HashMap<>(128);
        this.queueableAssets = new HashMap<>(512);
    }


    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  EXISTING ASSETS  /////////////////////////////
    /////////////////////////////////////////////////////////////////////////////


    public AssetSorter addExistingAsset(String assetID, Disposable asset) {
        if(existingAssets.containsKey(assetID))
            throw new AssetIDAlreadyExistsException(assetID);

        existingAssets.put(assetID, asset);
        return this;
    }


    public AssetSorter removeExistingAsset(String assetID) {
        if(!existingAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        existingAssets.remove(assetID);
        return this;
    }


    public void removeAllExistingAssets() {
        existingAssets.clear();
    }


    ////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  DEFINE/UNDEFINE ASSETS  /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////


    public <T> AssetSorter defineAsset(String assetID, String fileName, Class<T> assetType) {
        return this.defineAsset(assetID, fileName, assetType, null);
    }


    public <T> AssetSorter defineAsset(String assetID, String fileName, Class<T> assetType, AssetLoaderParameters<T> params) {
        if(queueableAssets.containsKey(assetID))
            throw new AssetIDAlreadyExistsException(assetID);

        queueableAssets.put(assetID, new AssetDescriptor(fileName, assetType, params));
        return this;
    }


    public AssetSorter undefineAsset(String assetID) {
        if(!queueableAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        queueableAssets.remove(assetID);
        return this;
    }


    public void undefineAllAssets() {
        queueableAssets.clear();
    }


    //////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  QUEUE/DEQUEUE ASSETS  /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////


    public AssetSorter queueAsset(String assetID) {
        if(!this.queueableAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
        this.assetManager.load(assetDesc);
        return this;
    }


    public AssetSorter dequeueAsset(String assetID) {
        if(!this.queueableAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
        this.assetManager.unload(assetDesc.fileName);
        return this;
    }


    public boolean isQueued(String assetID) {
        if(!this.queueableAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
        return this.assetManager.isLoaded(assetDesc);
    }


    ///////////////////////////////////////////////////////////////////////
    ///////////////  QUEUE/DEQUEUE (QUALITY OF LIFE FUNCS)  ///////////////
    ///////////////////////////////////////////////////////////////////////


    public AssetSorter queueAssets(String... assetIDs) {
        for(String assetID : assetIDs) { queueAsset(assetID); }
        return this;
    }


    public AssetSorter dequeueAssets(String... assetIDs) {
        for(String assetID : assetIDs) { dequeueAsset(assetID); }
        return this;
    }


    public AssetSorter queueAssetsWithRegex(final String regex) {
        for(String assetID : this.queueableAssets.keySet()) {
            if(assetID.matches(regex)) queueAsset(assetID);
        }
        return this;
    }


    public AssetSorter dequeueAssetsWithRegex(final String regex) {
        for(String assetID : this.queueableAssets.keySet()) {
            if(assetID.matches(regex)) dequeueAsset(assetID);
        }
        return this;
    }


    public AssetSorter dequeueAll() {
        for(String assetID : this.queueableAssets.keySet()) {
            if(isQueued(assetID)) dequeueAsset(assetID);
        }
        return this;
    }


    public AssetSorter queueAll() {
        for(String assetID : this.queueableAssets.keySet()) {
            if(isQueued(assetID)) queueAsset(assetID);
        }
        return this;
    }


    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  LOADING ASSETS  /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    public boolean keepLoading(int milliseconds) {
        return this.assetManager.update(milliseconds);
    }


    public boolean keepLoading() {
        return keepLoading(17);
    }


    public void finishLoading() {
        this.assetManager.finishLoading();
    }


    public void finishLoadingSpecificAsset(String assetID) {
        if(!this.queueableAssets.containsKey(assetID))
            throw new AssetIDDoesntExistException(assetID);

        final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
        this.assetManager.finishLoadingAsset(assetDesc);
    }


    public boolean isFinishedLoading() {
        return this.assetManager.isFinished();
    }


    /**
     * @return the current loading progress as a float in range [0, 1]
     */
    public float getProgress() {
        return this.assetManager.getProgress();
    }


    ///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  GETTING ANY ASSET  /////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////


    /**
     * This class searches for an asset with the specified ID String and returns it. <br>
     * It doesn't matter if the requested asset is an "Existing Asset" or an "Queueable Asset".
     * @param assetID the id String of the requested asset
     * @param assetClass the class of the requested asset
     * @return the requested asset
     * @param <T> the type of the returned asset
     */
    public <T> T getAsset(String assetID, Class<T> assetClass) {
        if(existingAssets.containsKey(assetID)) {
            final Disposable asset = existingAssets.get(assetID);
            return assetClass.cast(asset);
        }
        if(queueableAssets.containsKey(assetID)) {
            final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
            return this.assetManager.get(assetDesc.fileName, assetClass);
        }

        throw new AssetIDDoesntExistException(assetID);
    }


    public boolean isAvailable(String assetID) {
        if(existingAssets.containsKey(assetID)) return true;
        if(queueableAssets.containsKey(assetID)) {
            final AssetDescriptor assetDesc = this.queueableAssets.get(assetID);
            return this.assetManager.isLoaded(assetDesc);
        }

        throw new AssetIDDoesntExistException(assetID);
    }


    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  CUSTOM LOADERS  /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    public <T, P extends AssetLoaderParameters<T>>
    void setLoader(Class<T> type, String suffix, AssetLoader<T, P> loader) {
        this.assetManager.setLoader(type, suffix, loader);
    }


    public <T, P extends AssetLoaderParameters<T>>
    void setLoader(Class<T> type, AssetLoader<T, P> loader) {
        this.assetManager.setLoader(type, loader);
    }


    /////////////////////////////////////////////////////////////////////
    /////////////////////////////  GETTERS  /////////////////////////////
    /////////////////////////////////////////////////////////////////////


    /**
     * Theoretically speaking, you should never need to use this function to gain access to the
     * underlying {@link AssetManager} instance since {@link AssetSorter} is supposted to be more
     * than enough for all your asset related needs. But just in case, I'm leaving this method in
     * here so that you can use it to implement something on your own or fix an issue that I am
     * currently not aware of.
     * @return the underlying {@link AssetManager} instance
     */
    public AssetManager getAssetManager() { return assetManager; }



    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  OBJECT METHODS  /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    @Override
    public void dispose() {
        this.assetManager.dispose();

        for(Map.Entry<String, Disposable> assetEntry : this.existingAssets.entrySet()) {
            final Disposable asset = assetEntry.getValue();
            asset.dispose();
        }
    }


    @Override
    public String toString() {
        return "AssetOverlord{" + "existingAssets=" + existingAssets + ", queueableAssets=" +
            queueableAssets + ", assetManager=" + assetManager + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        AssetSorter t = (AssetSorter) o;
        return Objects.equals(existingAssets, t.existingAssets) &&
            Objects.equals(queueableAssets, t.queueableAssets) && Objects.equals(assetManager, t.assetManager);
    }


    @Override
    public int hashCode() {
        return Objects.hash(existingAssets, queueableAssets, assetManager);
    }


}
