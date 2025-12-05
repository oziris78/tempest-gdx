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

package com.twistral.tempest.timing;



public class LimitTimer extends AccumulationTimer {

    private final float limit; // in seconds


    public LimitTimer(float limitInSeconds) {
        super();
        this.limit = limitInSeconds;
    }


    public boolean hasPassed() {
        if(this.time >= this.limit){
            this.time = 0f;
            return true;
        }
        return false;
    }


    public float getRemainingSecs() {
        return Math.max(0f, limit - time);
    }


}

