package com.enping.transformers.data

import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource


interface TransformerRepo {

    suspend fun getOrCreateAllSpark(): String

    suspend fun releaseAllSpark()

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

}