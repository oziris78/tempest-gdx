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

package com.twistral.tempest.box2d;


public final class Box2DConstants {

    /** PPM value used by all Box2D classes */
    static float PPM;

    /*  CONSTANTS  */
    public static final short DEFAULT_CATEGORY_BITS = 0x0001;
    public static final short DEFAULT_MASK_BITS = -1;
    public static final short DEFAULT_GROUP_INDEX = 0;

    /*  MATERIAL CONSTANTS  */
    public static final float RUBBER_DENSITY = 1f;
    public static final float RUBBER_FRICTION = 0f;
    public static final float RUBBER_RESTITUTION = 1f;

    public static final float STEEL_DENSITY = 1f;
    public static final float STEEL_FRICTION = 0.3f;
    public static final float STEEL_RESTITUTION = 0.1f;

    public static final float WOOD_DENSITY = 0.5f;
    public static final float WOOD_FRICTION = 0.7f;
    public static final float WOOD_RESTITUTION = 0.3f;

    public static final float STONE_DENSITY = 1f;
    public static final float STONE_FRICTION = 0.9f;
    public static final float STONE_RESTITUTION = 0.01f;

}
