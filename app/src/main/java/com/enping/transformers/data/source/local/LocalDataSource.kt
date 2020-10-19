package com.enping.transformers.data.source.local

import com.enping.transformers.data.model.Transformer

interface LocalDataSource {
    suspend fun getAllSpark(): String?
    suspend fun setAllSpark(newAllSpark: String)
    suspend fun clearAllSpark()
    suspend fun insertTransformer(transformer: Transformer)
    suspend fun insertTransformers(transformers:List<Transformer>)
    suspend fun updateTransformer(transformer: Transformer)
    suspend fun deleteTransformer(transformerId: String)
    suspend fun getTransformers():List<Transformer>
    fun isLoaded(): Boolean
}
