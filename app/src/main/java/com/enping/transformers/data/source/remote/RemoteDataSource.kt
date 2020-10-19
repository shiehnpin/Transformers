package com.enping.transformers.data.source.remote

import com.enping.transformers.data.model.Transformer

interface RemoteDataSource{
    suspend fun createAllSpark() : String
    suspend fun createTransformer(allSpark: String, transformer: Transformer): Transformer
    suspend fun updateTransformer(allSpark: String, transformer: Transformer): Transformer
    suspend fun deleteTransformer(allSpark: String, transformerId: String)
    suspend fun getTransformers(allSpark: String): List<Transformer>
}