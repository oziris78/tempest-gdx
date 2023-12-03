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



public final class TimingUtils {

    private TimingUtils(){}


    public static final float DAY_IN_SECS = 86_400; // 24 * 60 * 60
    public static final float HOUR_IN_SECS = 3_600; // 60 * 60
    public static final float MIN_IN_SECS = 60; // 60



    ////////////////////////////////////////////
    /////////   CONVERTION METHODS   ///////////
    ////////////////////////////////////////////
    // huge numbers in seconds to days/hours/mins/secs/milisecs

    public static int getDayCount(float timeInSeconds){
        return (int) (timeInSeconds / 86400);
    }

    public static int getHourCount(float timeInSeconds){
        return (int) (timeInSeconds / 3600f) % 24;
    }

    public static int getMinuteCount(float timeInSeconds){
        return (int) ((timeInSeconds % 3600) / 60f);
    }

    public static int getSecondCount(float timeInSeconds){
        return (int) ((timeInSeconds % 3600) % 60);
    }

    public static int getMillisecondCount(float timeInSeconds){
        return (int) ((timeInSeconds % 1f) * 1000);
    }


    ////////////////////////////////////////////
    //////////   TO STRING METHODS   ///////////
    ////////////////////////////////////////////


    public static String toDayString(float timeInSeconds){
        return String.format("%02d:%02d:%02d:%02d:%03d", getDayCount(timeInSeconds), getHourCount(timeInSeconds),
                getMinuteCount(timeInSeconds), getSecondCount(timeInSeconds), getMillisecondCount(timeInSeconds));
    }

    public static String toHourString(float timeInSeconds){
        return String.format("%02d:%02d:%02d:%03d", getHourCount(timeInSeconds), getMinuteCount(timeInSeconds),
                getSecondCount(timeInSeconds), getMillisecondCount(timeInSeconds));
    }

    public static String toMinuteString(float timeInSeconds){
        return String.format("%02d:%02d:%03d", getMinuteCount(timeInSeconds), getSecondCount(timeInSeconds),
                getMillisecondCount(timeInSeconds));
    }

    public static String toSecondString(float timeInSeconds){
        return String.format("%02d:%03d", getSecondCount(timeInSeconds), getMillisecondCount(timeInSeconds));
    }

    public static String toDayStringNoMilli(float timeInSeconds){
        return String.format("%02d:%02d:%02d:%02d", getDayCount(timeInSeconds), getHourCount(timeInSeconds),
                getMinuteCount(timeInSeconds), getSecondCount(timeInSeconds));
    }

    public static String toHourStringNoMilli(float timeInSeconds){
        return String.format("%02d:%02d:%02d", getHourCount(timeInSeconds), getMinuteCount(timeInSeconds), getSecondCount(timeInSeconds));
    }

    public static String toMinuteStringNoMilli(float timeInSeconds){
        return String.format("%02d:%02d", getMinuteCount(timeInSeconds), getSecondCount(timeInSeconds));
    }

    public static String toSecondStringNoMilli(float timeInSeconds){
        return String.format("%02d", getSecondCount(timeInSeconds));
    }





}
