package com.enping.transformers.data

import com.enping.transformers.data.model.Transformer
import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource


interface TransformerRepo {
    /**
     *  Call this to setup allspark
     */
    suspend fun getOrCreateAllSpark(): String

    suspend fun getTransformers(): List<Transformer>

    suspend fun getTransformer(transformerId: String): Transformer

    suspend fun createTransformer(transformer: Transformer): Transformer

    suspend fun updateTransformer(transformer: Transformer): Transformer

    suspend fun deleteTransformer(transformerId: String)

    suspend fun battleTransformers(): GameResult

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

    override suspend fun getTransformers(): List<Transformer> {
        check(local.hasAllSpark())

        return local.getTransformers()
    }

    override suspend fun getTransformer(transformerId: String): Transformer {
        check(local.hasAllSpark())

        return local.getTransformer(transformerId)
    }

    override suspend fun createTransformer(transformer: Transformer): Transformer {
        check(local.hasAllSpark())
        check(transformer.name.isNotBlank()) { "Name must not blank" }

        val createdTransformer = remote.createTransformer(transformer)
        local.insertTransformer(createdTransformer)
        return createdTransformer
    }

    override suspend fun updateTransformer(transformer: Transformer): Transformer {
        check(local.hasAllSpark())
        check(transformer.name.isNotBlank()) { "Name must not blank" }
        
        val updatedTransformer = remote.updateTransformer(transformer)
        local.updateTransformer(updatedTransformer)
        return updatedTransformer
    }

    override suspend fun deleteTransformer(transformerId: String) {
        check(local.hasAllSpark())

        remote.deleteTransformer(transformerId)
        local.deleteTransformer(transformerId)
    }

    override suspend fun battleTransformers(): GameResult {
        return Game(local.getTransformers()).battle()
    }

}