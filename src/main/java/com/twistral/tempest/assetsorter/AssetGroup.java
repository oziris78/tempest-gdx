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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import java.util.Collection;
import java.util.HashMap;


final class AssetGroup {

    private HashMap<String, AssetDescriptor> descriptors; // asset keys and assets
    private boolean isQueued;


    public AssetGroup() {
        this.descriptors = new HashMap<>();
        this.isQueued = false;
    }


    public void addAsset(String assetKey, AssetDescriptor assetDescriptor) {
        if (this.descriptors.containsKey(assetKey))
            throw new RuntimeException("Invalid key: " + assetKey);
        if (assetDescriptor == null)
            throw new RuntimeException("The assetDescriptor is null for this key: " + assetKey);
        this.descriptors.put(assetKey, assetDescriptor);
    }


    public AssetDescriptor getAsset(String key) {
        AssetDescriptor temp = this.descriptors.get(key);
        if (temp == null)
            throw new RuntimeException("An asset for this key doesn't exist, key: " + key);
        return temp;
    }


    public void queue(AssetManager assetManager) {
        this.isQueued = true;
        Collection<AssetDescriptor> assets = this.descriptors.values();
        for (AssetDescriptor asset : assets)
            assetManager.load(asset);
    }



    public boolean isQueued() {return this.isQueued;}


}
