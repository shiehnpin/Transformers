package com.enping.transformers.data.source.remote

import com.enping.transformers.data.model.Transformer
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("allspark")
    suspend fun createAllSpark(): ResponseBody

    @POST("transformers")
    suspend fun getTransformers(): List<Transformer>

    @POST("transformers")
    suspend fun createTransformer(@Body transformer: Transformer): Transformer

    @PUT("transformers")
    suspend fun updateTransformer(@Body transformer: Transformer): Transformer

    @DELETE("transformers/{id}")
    suspend fun deleteTransformer(@Path("id") id: String)
}
