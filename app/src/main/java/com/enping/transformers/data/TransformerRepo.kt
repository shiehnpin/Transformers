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
            remote.setAllSpark(allSpark)
            return allSpark
        }

        val newAllSpark = remote.createAllSpark()
        local.setAllSpark(newAllSpark)
        remote.setAllSpark(newAllSpark)

        return newAllSpark
    }

    override suspend fun releaseAllSpark() {
        local.clearAllSpark()
        remote.clearAllSpark()
    }

    override suspend fun getTransformers(): List<Transformer> {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)
        remote.setAllSpark(allSpark)

        if(local.isLoaded()){
            return local.getTransformers()
        }

        val transformers = remote.getTransformers()
        local.insertTransformers(transformers)

        return local.getTransformers()
    }

    override suspend fun createTransformer(transformer: Transformer): Transformer {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)
        remote.setAllSpark(allSpark)

        val createdTransformer = remote.createTransformer(transformer)
        local.insertTransformer(createdTransformer)
        return createdTransformer
    }

    override suspend fun updateTransformer(transformer: Transformer): Transformer {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)
        remote.setAllSpark(allSpark)

        val updatedTransformer = remote.updateTransformer(transformer)
        local.updateTransformer(updatedTransformer)
        return updatedTransformer
    }

    override suspend fun deleteTransformer(transformerId: String) {
        val allSpark = local.getAllSpark()
        checkNotNull(allSpark)
        remote.setAllSpark(allSpark)

        remote.deleteTransformer(transformerId)
        local.deleteTransformer(transformerId)
    }

}