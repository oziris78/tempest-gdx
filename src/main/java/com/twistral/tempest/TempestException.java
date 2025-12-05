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


package com.twistral.tempest;


public class TempestException extends RuntimeException {

    public TempestException(String message) {
        super(message);
    }

    public TempestException(String format, Object... args) {
        super(String.format(format, args));
    }


    public static class UnreachableException extends TempestException {
        public UnreachableException() {
            super("This line should have been UNREACHABLE!");
        }
        public UnreachableException(String... extraLines) {
            super("This line should have been UNREACHABLE!\n"
                    + String.join("\n", extraLines));
        }
    }

    public static class AssetIDDoesntExistException extends TempestException {
        public AssetIDDoesntExistException(String assetID) {
            super("This ID is invalid, it doesnt exist: %s", assetID);
        }
    }


    public static class AssetIDAlreadyExistsException extends TempestException {
        public AssetIDAlreadyExistsException(String assetID) {
            super("This ID already exists for another asset: %s", assetID);
        }
    }



}