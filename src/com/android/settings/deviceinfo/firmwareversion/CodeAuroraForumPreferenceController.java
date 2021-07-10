/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.DeviceInfoUtils;

public class CodeAuroraForumPreferenceController extends BasePreferenceController {

    private static final String CAF_TAG = "ro.caf.revision.tag";
    private static final String TAG = "SecurityPatchCtrl";
    private static final Uri INTENT_URI_DATA = Uri.parse(
            "https://www.codeaurora.org/");

    private final PackageManager mPackageManager;
    private final String mCurrentPatch;

    public CodeAuroraForumPreferenceController(Context context, String key) {
        super(context, key);
        mPackageManager = mContext.getPackageManager();
        mCurrentPatch = DeviceInfoUtils.getSecurityPatch();
    }

    @Override
    public int getAvailabilityStatus() {
        return !TextUtils.isEmpty(mCurrentPatch)
                ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String CAFTag = SystemProperties.get(CAF_TAG);
        return CAFTag;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(INTENT_URI_DATA);
        if (mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            // Don't send out the intent to stop crash
            Log.w(TAG, "queryIntentActivities() returns empty");
            return true;
        }

        mContext.startActivity(intent);
        return true;
    }
}