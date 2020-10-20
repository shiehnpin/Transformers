package com.enping.transformers.data.source.remote

import com.enping.transformers.data.model.Transformer
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface RemoteDataSource {
    suspend fun createAllSpark(): String
    suspend fun createTransformer(transformer: Transformer): Transformer
    suspend fun getTransformers(): List<Transformer>
    suspend fun updateTransformer(transformer: Transformer): Transformer
    suspend fun deleteTransformer(transformerId: String)
    fun setAllSpark(allSpark: String)
    fun clearAllSpark()
}

class RemoteDataSourceImpl(baseUrl: HttpUrl) : RemoteDataSource {

    private var service: ApiService
    private var allSpark: String? = null
    private val requestInterceptor: (Interceptor.Chain) -> Response = { chain ->
        val request: Request = chain.request()
        val ongoing = request.newBuilder()
        ongoing.addHeader("Accept", "application/json")
        if (request.url().encodedPath().startsWith("/transformers")) {
            checkNotNull(allSpark)
            ongoing.addHeader("Authorization", "Bearer $allSpark")
        }
        chain.proceed(ongoing.build())
    }

    init {
        val socketFactory = TLSSocketFactory()

        val client = OkHttpClient.Builder()
            .sslSocketFactory(socketFactory, socketFactory.trustManager)
            .addInterceptor(requestInterceptor)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ApiService::class.java)

    }

    override suspend fun createAllSpark(): String {
        return service.createAllSpark().string()
    }

    override suspend fun createTransformer(transformer: Transformer): Transformer {
        return service.createTransformer(transformer)
    }

    override suspend fun getTransformers(): List<Transformer> {
        return service.getTransformers()
    }

    override suspend fun updateTransformer(transformer: Transformer): Transformer {
        return service.updateTransformer(transformer)
    }

    override suspend fun deleteTransformer(transformerId: String) {
        service.deleteTransformer(transformerId)
    }

    override fun setAllSpark(allSpark: String) {
        this.allSpark = allSpark
    }

    override fun clearAllSpark() {
        this.allSpark = null
    }
}