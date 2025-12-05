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


package com.twistral.tempest.box2d;



public class Box2DUtils {

    static class DFR {
        public final float density, friction, restitution;

        DFR(float density, float friction, float restitution) {
            this.density = density;
            this.friction = friction;
            this.restitution = restitution;
        }
    }

    public static DFR dfr(float density, float friction, float restitution) {
        return new DFR(density, friction, restitution);
    }

    // -------------------------------------- //

    static class Offset {
        public final float xPixels, yPixels;

        Offset(float xPixels, float yPixels) {
            this.xPixels = xPixels;
            this.yPixels = yPixels;
        }
    }

    public static Offset offset(float xPixels, float yPixels) {
        return new Offset(xPixels, yPixels);
    }

}
