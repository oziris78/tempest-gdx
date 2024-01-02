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

package com.twistral.tempest.timing;


public class LimitTimer extends AccumulationTimer {


    //////////////
    /*  FIELDS  */
    //////////////

    /** A limit in seconds. */
    private final float limit;


    ////////////////////
    /*  CONSTRUCTORS  */
    ////////////////////

    public LimitTimer(boolean hasAlreadyExceeded, final float limit){
        this.limit = limit;
        this.time = hasAlreadyExceeded ? limit : 0f;
    }

    public LimitTimer(final float limit){
        this(true, limit);
    }

    ///////////////
    /*  METHODS  */
    ///////////////

    public boolean hasPassed(){
        if(this.time >= limit){
            this.time = 0f;
            return true;
        }
        return false;
    }

    public float howManySecsLeft(){
        return (limit >= time) ? limit - time : 0f;
    }

    public float howManyMinsLeft(){
        return howManySecsLeft() / 60f;
    }

    public int howManyMillisecsLeft(){
        return (int) (howManySecsLeft() * 1000f);
    }


}

