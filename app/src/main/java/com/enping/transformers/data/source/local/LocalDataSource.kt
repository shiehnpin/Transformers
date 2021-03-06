package com.enping.transformers.data.source.local

import com.enping.transformers.data.model.AllSpark
import com.enping.transformers.data.model.Transformer

interface LocalDataSource {
    suspend fun getAllSpark(): String?
    suspend fun setAllSpark(newAllSpark: String)
    suspend fun hasAllSpark(): Boolean
    suspend fun clearAllSpark()
    suspend fun insertTransformer(transformer: Transformer)
    suspend fun updateTransformer(transformer: Transformer)
    suspend fun deleteTransformer(transformerId: String)
    suspend fun getTransformers(): List<Transformer>
    suspend fun getTransformer(transformerId: String): Transformer
}

/**
 * Local data source to persist user data
 *
 * @param db must provide instance to store the data, and can be replace with in-memory db for test.
 */
class LocalDataSourceImpl(private val db: TransformerDatabase) : LocalDataSource {

    override suspend fun getAllSpark(): String? {
        return getDao().getAllSpark()?.allSpark
    }

    override suspend fun setAllSpark(newAllSpark: String) {
        getDao().setAllSpark(AllSpark(allSpark = newAllSpark))
    }

    override suspend fun hasAllSpark(): Boolean {
        return getDao().getAllSpark()!=null
    }

    override suspend fun clearAllSpark() {
        getDao().clearAllSpark()
    }

    override suspend fun insertTransformer(transformer: Transformer) {
        getDao().insertOrUpdateTransformer(transformer)
    }

    override suspend fun updateTransformer(transformer: Transformer) {
        getDao().insertOrUpdateTransformer(transformer)
    }

    override suspend fun deleteTransformer(transformerId: String) {
        getDao().deleteTransformer(transformerId)
    }

    override suspend fun getTransformers(): List<Transformer> {
        return getDao().getTransformers()
    }

    override suspend fun getTransformer(transformerId: String): Transformer {
        return getDao().getTransformer(transformerId)
    }

    private fun getDao(): TransformerDao = db.transformerDao()

}