// Copyright 2024-2025 Oğuzhan Topaloğlu
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



import com.twistral.tempest.TempestException;



public class AccumulationTimer {

    /** Elapsed time in seconds. */
    protected float time;


    /*//////////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  CONSTRUCTORS  ///////////////////////////*/
    /*//////////////////////////////////////////////////////////////////////*/

    public AccumulationTimer(float initialTime){
        this.time = initialTime;
    }

    public AccumulationTimer() {
        this(0f);
    }


    /*/////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  METHODS  ///////////////////////////*/
    /*/////////////////////////////////////////////////////////////////*/

    public void update(float deltaTime) {
        this.time += deltaTime;
    }


    /*////////////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  STRING METHODS  ///////////////////////////*/
    /*////////////////////////////////////////////////////////////////////////*/


    public String asString(TimerFormatType formatType) {
        if(formatType == null) formatType = TimerFormatType.MIN_SEC_MILLI;

        switch (formatType) {
            case DAY_HOUR_MIN_SEC_MILLI:
                return String.format("%02d:%02d:%02d:%02d:%03d",
                    getDayCount(), getHourCount(), getMinuteCount(), getSecCount(), getMilliCount());
            case HOUR_MIN_SEC_MILLI:
                return String.format("%02d:%02d:%02d:%03d",
                    getHourCount(), getMinuteCount(), getSecCount(), getMilliCount());
            case DAY_HOUR_MIN_SEC:
                return String.format("%02d:%02d:%02d:%02d",
                    getDayCount(), getHourCount(), getMinuteCount(), getSecCount());
            case MIN_SEC_MILLI:
                return String.format("%02d:%02d:%03d", getMinuteCount(), getSecCount(), getMilliCount());
            case HOUR_MIN_SEC:
                return String.format("%02d:%02d:%02d", getHourCount(), getMinuteCount(), getSecCount());
            case SEC_MILLI:
                return String.format("%02d:%03d", getSecCount(), getMilliCount());
            case MIN_SEC:
                return String.format("%02d:%02d", getMinuteCount(), getSecCount());
            case SEC:
                return String.format("%02d", getSecCount());
        }

        throw new TempestException.UnreachableException();
    }


    /*/////////////////////////////////////////////////////////////////*/
    /*///////////////////////////  GETTERS  ///////////////////////////*/
    /*/////////////////////////////////////////////////////////////////*/

    public float getElapsedSecs() { return time; }

    public int getDayCount(){ return (int) (this.time / 86400f); }
    public int getHourCount(){ return (int) (this.time / 3600f) % 24; }
    public int getMinuteCount(){ return (int) ((this.time % 3600f) / 60f); }
    public int getSecCount(){ return (int) ((this.time % 3600f) % 60f); }
    public int getMilliCount(){ return (int) ((this.time % 1f) * 1000f); }

}
