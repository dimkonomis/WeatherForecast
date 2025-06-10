package com.app.weatherforecast.core.net.flipper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FlipperHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FlipperHelper {

    private val networkFlipperPlugin = NetworkFlipperPlugin()

    override fun init() {
        SoLoader.init(context, false)
        AndroidFlipperClient.getInstance(context).apply {
            addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            addPlugin(networkFlipperPlugin)
            addPlugin(SharedPreferencesFlipperPlugin(context, context.packageName))
            start()
        }
    }

    override fun addInterceptor(builder: OkHttpClient.Builder) {
        builder.addInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
    }

}