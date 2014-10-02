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

package com.bel.android.dspmanager.activity;

import android.content.Context;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

public class FxUtils {
    private interface FxParts {
        /* is feature enabled in config */
        public boolean isSupported();

        /* initialize things */
        public void init(Context context);

        /*
         * if not supported or not enabled for route, remove from preference
         * screen
         */
        public void checkAndRemovePreference(PreferenceScreen screen, Route route);

        /* available routes */
        public Route[] getRoutes();
    }

    /* config has been set by boot receiver or app starting */
    private static boolean init = false;

    private enum Route {
        Headset,
        Bluetooth,
        Speaker,
        USB
    }

    private static Route getRouteFromString(String routeString) {
        if (TextUtils.isEmpty(routeString)) {
            return Route.Headset;
        }
        if ("headset".equals(routeString)) {
            return Route.Headset;
        } else if ("bluetooth".equals(routeString)) {
            return Route.Bluetooth;
        } else if ("usb".equals(routeString)) {
            return Route.USB;
        } else if ("speaker".equals(routeString)) {
            return Route.Speaker;
        } else {
            return Route.Headset;
        }
    }

    private static Feature getFeature(FX fx) {
        if (fx == null) {
            return null;
        }
        switch (fx) {
            case Dirac:
                return Feature.Dirac;
            case Compression:
                return Feature.Compression;
            default:
                return null;
        }
    }

    /**
     * Inspired by nuclearmistake from Vanir Nuke, you are a ghastly beast sir
     *
     * @author bigrushdog
     */
    private static enum Feature implements FxParts {
        Dirac(SystemProperties.getBoolean("dsp.dirac.enable", false), "dsp.dirac.category", new Route[] {
                Route.Headset, Route.Bluetooth, Route.Speaker, Route.USB
        }),

        Compression(SystemProperties.getBoolean("dsp.compression.enable", true), "dsp.compress.category", new Route[] {
                Route.Headset, Route.Bluetooth, Route.Speaker, Route.USB
        });

        private final String mPrefKey;
        private final Route[] mRoutes;
        private boolean mSupported;

        private Feature(boolean isSupported, String prefKey, Route[] routes) {
            mSupported = isSupported;
            mPrefKey = prefKey;
            mRoutes = routes;
        }

        @Override
        public boolean isSupported() {
            return mSupported;
        }

        @Override
        public Route[] getRoutes() {
            return mRoutes;
        }

        @Override
        public void checkAndRemovePreference(PreferenceScreen screen, Route route) {
            if (screen == null || route == null) {
                return;
            }
            if (!mSupported) {
                try {
                    Preference pref = screen.findPreference(mPrefKey);
                    if (pref != null) {
                        screen.removePreference(pref);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            // feature is available, but is it enabled for this route
            // if not, remove it
            boolean foundRoute = false;
            for (int i = 0; i < mRoutes.length; i++) {
                if (mRoutes[i] == route) {
                    foundRoute = true;
                }
            }

            // feature available, but not for this route: Remove it
            if (!foundRoute) {
                try {
                    Preference pref = screen.findPreference(mPrefKey);
                    if (pref != null) {
                        screen.removePreference(pref);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void init(Context context) {
            // do something at some point
        }
    }

    private static Feature[] features = new Feature[] {
            Feature.Dirac,
            Feature.Compression
    };

    public enum FX {
        Dirac,
        Compression
    }

    public static void checkAvailablePrefs(PreferenceScreen screen, String route) {
        if (screen == null || route == null) {
            return;
        }
        Route newRoute = getRouteFromString(route);
        for (int i = 0; i < features.length; i++) {
            features[i].checkAndRemovePreference(screen, newRoute);
        }
    }

    public static boolean isFxSupported(FX fx) {
        if (fx == null) {
            return false;
        }
        return getFeature(fx).isSupported();
    }

    public static void init(Context context) {
        if (!init) {
            for (int i = 0; i < features.length; i++) {
                features[i].init(context);
            }
            init = true;
        }
    }
}
