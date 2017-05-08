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
 * Noise class contains noise information
 */
public class Noise {
    /**
     * Definition of noise level
     */
    public enum NoiseLevel {
        /**
         * Low noise level indicating a clear face image
         */
        @SerializedName("low")
        Low,
        /**
         * Medium noise level indicating a slightly noisy face image
         */
        @SerializedName("medium")
        Medium,
        /**
         * High noise level indicating a extremely noisy face image
         */
        @SerializedName("high")
        High
    }

    /**
     * Indicating noise level of face image
     */
    public NoiseLevel noiseLevel;

    /**
     * Noise value is in range [0, 1]. Larger value means the face image is more noisy.
     * [0, 0.3) is low noise level.
     * [0.3, 0.7) is medium noise level.
     * [0.7, 1] is high noise level.
     */
    public double value;
}
