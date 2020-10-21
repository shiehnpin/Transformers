package com.enping.transformers.data.source.remote

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * CustomGlideAppModule
 *
 * This class is for patch the issues including support TLSv1.2 on Android 4.4 and avoid banned by
 * image source site.
 */
@GlideModule
class CustomGlideAppModule() : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val socketFactory = TLSSocketFactory()

        val client = OkHttpClient.Builder()
            .sslSocketFactory(socketFactory, socketFactory.trustManager)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit " +
                                "/ 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36"
                    )
                    .build()
                chain.proceed(request)
            }
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val factory = OkHttpUrlLoader.Factory(client)
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}