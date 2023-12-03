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

package com.twistral.tempest.timing;


public class AccumulationTimer {

    //////////////
    /*  FIELDS  */
    //////////////


    /** Elapsed time in seconds. */
    protected float time;


    ////////////////////
    /*  CONSTRUCTORS  */
    ////////////////////

    public AccumulationTimer(float initialTime){
        this.time = initialTime;
    }

    public AccumulationTimer(){
        this(0f);
    }


    ///////////////
    /*  METHODS  */
    ///////////////

    public void update(float deltaTime){
        this.time += deltaTime;
    }

    public float getTime() {
        return time;
    }



    //////////////////////////////////////////////////////
    ////////////////   STRING METHODS   //////////////////
    //////////////////////////////////////////////////////


    public String toDayString(){
        return TimingUtils.toDayString(time);
    }

    public String toHourString(){
        return TimingUtils.toHourString(time);
    }

    public String toMinuteString(){
        return TimingUtils.toMinuteString(time);
    }

    public String toSecondString(){
        return TimingUtils.toSecondString(time);
    }

    public String toDayStringNoMilli(){
        return TimingUtils.toDayStringNoMilli(time);
    }

    public String toHourStringNoMilli(){
        return TimingUtils.toHourStringNoMilli(time);
    }

    public String toMinuteStringNoMilli(){
        return TimingUtils.toMinuteStringNoMilli(time);
    }

    public String toSecondStringNoMilli(){
        return TimingUtils.toSecondStringNoMilli(time);
    }





}
