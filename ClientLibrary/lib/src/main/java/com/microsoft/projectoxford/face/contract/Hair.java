//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.contract;

import com.google.gson.annotations.SerializedName;

/**
 * Hair class contains hair color information
 */
public class Hair {
    /**
     * Indicating the confidence of a bald head
     */
    public double bald;

    /**
     * Indicating whether hair is occluded or not
     */
    public boolean invisible;

    /**
     * Hair color details
     */
    public static class HairColor{
        /**
         * Hair color type
         */
        public enum HairColorType {
            @SerializedName("unknown")
            Unknown,
            @SerializedName("white")
            White,
            @SerializedName("gray")
            Gray,
            @SerializedName("blond")
            Blond,
            @SerializedName("brown")
            Brown,
            @SerializedName("red")
            Red,
            @SerializedName("black")
            Black,
            @SerializedName("other")
            Other
        }

        /**
         * Indicating the hair color type
         */
        public HairColorType color;

        /**
         * Indicating the confidence for hair color type
         */
        public double confidence;
    };

    /**
     * Indicating all possible hair colors with confidences
     */
    public HairColor[] hairColor;
}
