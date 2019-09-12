/*
 * Copyright (C) KOOMPI Co., LTD.
 * Copyright (C) 2016 The PIONUX OS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.desktop;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for keeping track of displays relevant to PIONUX.
 *
 * Right now, this keeps track of all displays that are eligible for mirroring,
 * i.e. all public presentation displays.
 */
public class PionuxDisplayListener implements DisplayManager.DisplayListener {
    private static final String TAG = PionuxDisplayListener.class.getName();
    private final Context mContext;
    private final DisplayManager mDisplayManager;
    private Set<Integer> mMirrorableDisplays;

    public interface PionuxDisplayCallback {
        void onDisplayAdded();

        void onDisplayRemoved();
    }

    private PionuxDisplayCallback mCallback;

    public PionuxDisplayListener(Context context, DisplayManager displayManager) {
        mContext = context;
        mDisplayManager = displayManager;
        mMirrorableDisplays = new HashSet<Integer>();
    }

    @Override
    public void onDisplayAdded(int displayId) {
        Display display = mDisplayManager.getDisplay(displayId);

        if (display.isPublicPresentation() && mMirrorableDisplays.add(displayId)) {
            if (mCallback != null) {
                mCallback.onDisplayAdded();
            }
        }
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        if (mMirrorableDisplays.remove(displayId)) {
            if (mCallback != null) {
                mCallback.onDisplayRemoved();
            }
        }
    }

    @Override
    public void onDisplayChanged(int displayId) {
        /* no-op */ }

    public void sync() {
        mMirrorableDisplays.clear();
        Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        for (Display display : displays) {
            if (display.isPublicPresentation()) {
                mMirrorableDisplays.add(display.getDisplayId());
            }
        }
    }

    public boolean isPionuxDisplayConnected() {
        return !mMirrorableDisplays.isEmpty();
    }

    public void setDisplayCallback(PionuxDisplayCallback callback) {
        mCallback = callback;
    }

    public void removeDisplayCallback() {
        mCallback = null;
    }
}
