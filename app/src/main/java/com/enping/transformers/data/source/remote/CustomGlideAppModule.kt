package com.enping.transformers.data.source.remote

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class CustomGlideAppModule() : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val socketFactory = TLSSocketFactory()

        val client = OkHttpClient.Builder()
            .sslSocketFactory(socketFactory, socketFactory.trustManager)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val factory = OkHttpUrlLoader.Factory(client)
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}