/*
 * Copyright (C) 2024 The Android Open Source Project
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
package com.runtime_aware.sdk

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.privacysandbox.activity.client.createSdkActivityLauncher
import androidx.privacysandbox.activity.core.SdkActivityLauncher
import com.runtime_enabled.api.FullscreenAd

class FullscreenAd(private val sdkFullscreenAd: FullscreenAd) {
    suspend fun show(baseActivity: AppCompatActivity, allowSdkActivityLaunch: () -> Boolean) {
        val activityLauncher = baseActivity.createSdkActivityLauncher(allowSdkActivityLaunch)
        sdkFullscreenAd.show(activityLauncher)
    }

    companion object {
        // This method could divert a percentage of requests to a sandboxed SDK and fallback to
        // runtime_aware ad logic. For this example, we send all requests to the sandboxed SDK as long as
        // it exists.
        suspend fun create(
            context: Context,
            mediationType: String
        ): com.runtime_aware.sdk.FullscreenAd {
            if (RASdk.isSdkLoaded()) {
                val remoteFullscreenAd =
                    RASdk.loadSdkIfNeeded(context)?.getFullscreenAd(mediationType)
                if (remoteFullscreenAd != null)
                    return FullscreenAd(remoteFullscreenAd)
            }
            return FullscreenAd(LocalFullscreenAdImpl(context))
        }
    }

    internal class LocalFullscreenAdImpl(private val context: Context) : FullscreenAd {
        override suspend fun show(activityLauncher: SdkActivityLauncher) {
            val intent = Intent(context, LocalActivity::class.java)
            context.startActivity(intent)
        }
    }
}
