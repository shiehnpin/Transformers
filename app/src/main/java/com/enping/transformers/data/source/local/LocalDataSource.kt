package com.enping.transformers.data.source.local

interface LocalDataSource {
    suspend fun getAllSpark(): String?
    suspend fun setAllSpark(newAllSpark: String)
    suspend fun clearAllSpark()
}
