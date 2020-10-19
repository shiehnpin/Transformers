package com.enping.transformers.data.source.remote

interface RemoteDataSource{
    suspend fun createAllSpark() : String
}