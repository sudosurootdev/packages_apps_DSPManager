/*
 * Copyright (C) 2014 TeamEOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bel.android.dspmanager.service;

import java.util.UUID;

import android.media.audiofx.AudioEffect;

public class DiracFX extends AudioEffect {
    /* just call it a hunch ;D */
    private static final UUID EFFECT_TYPE_DIRAC =
            UUID.fromString("4c6383e0-ff7d-11e0-b6d8-0002a5d5c51b");

    /* effect factor param index */
    public static final int PARAMS_EFFECT_FACTOR = 2;

    /* min effect level */
    public static final int EFFECT_FACTOR_MIN_VAL = 4096;

    /* max effect level */
    public static final int EFFECT_FACTOR_MAX_VALUE = 6177;

    public DiracFX(int priority, int audioSession) {
        super(EFFECT_TYPE_DIRAC, EFFECT_TYPE_NULL, priority, audioSession);
    }

    /**
     * set level from a seekbar or other widget that uses a 0 base
     *
     * @param level assumes a range of 0 to EFFECT_FACTOR_MAX_VALUE -
     *            EFFECT_FACTOR_MIN_VAL
     */
    public void setFactorLevel(int level) {
        int newLevel = level;

        // sanity check min
        if (newLevel < 0) {
            newLevel = 0;
        }

        // adjust for seekbar
        newLevel += EFFECT_FACTOR_MIN_VAL;

        // sanity check max
        if (newLevel > EFFECT_FACTOR_MAX_VALUE) {
            newLevel = EFFECT_FACTOR_MAX_VALUE;
        }
        setParameter(PARAMS_EFFECT_FACTOR, newLevel);
    }
}
