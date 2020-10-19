package com.enping.transformers.data.source.remote

import com.enping.transformers.data.model.Transformer

interface RemoteDataSource {
    suspend fun createAllSpark(): String
    suspend fun createTransformer(transformer: Transformer): Transformer
    suspend fun getTransformers(): List<Transformer>
    suspend fun updateTransformer(transformer: Transformer): Transformer
    suspend fun deleteTransformer(transformerId: String)
    fun setAllSpark(allSpark: String)
    fun clearAllSpark()
}
}