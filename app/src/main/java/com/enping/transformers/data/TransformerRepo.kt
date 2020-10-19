package com.enping.transformers.data

import com.enping.transformers.data.model.Transformer
import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource


interface TransformerRepo {

    suspend fun getOrCreateAllSpark(): String

    suspend fun releaseAllSpark()

    suspend fun getTransformers(): List<Transformer>

    suspend fun createTransformer(transformer: Transformer): Transformer

    suspend fun updateTransformer(transformer: Transformer): Transformer

    suspend fun deleteTransformer(transformerId: String)

}

class TransformerRepoImpl(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) : TransformerRepo {
    override suspend fun getOrCreateAllSpark(): String {
        val allSpark = local.getAllSpark()
        if (allSpark != null) {
            return allSpark
        }

        val newAllSpark = remote.createAllSpark()
        local.setAllSpark(newAllSpark)

        return newAllSpark
    }

    override suspend fun releaseAllSpark() {
        local.clearAllSpark()
    }

    override suspend fun getTransformers(): List<Transformer> {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)

        if(local.isLoaded()){
            return local.getTransformers()
        }

        val transformers = remote.getTransformers(allSpark)
        local.insertTransformers(transformers)

        return local.getTransformers()
    }

    override suspend fun createTransformer(transformer: Transformer): Transformer {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)

        val createdTransformer = remote.createTransformer(allSpark, transformer)
        local.insertTransformer(createdTransformer)
        return createdTransformer
    }

    override suspend fun updateTransformer(transformer: Transformer): Transformer {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)

        val updatedTransformer = remote.updateTransformer(allSpark, transformer)
        local.updateTransformer(updatedTransformer)
        return updatedTransformer
    }

    override suspend fun deleteTransformer(transformerId: String) {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)

        remote.deleteTransformer(allSpark, transformerId)
        local.deleteTransformer(transformerId)
    }

}