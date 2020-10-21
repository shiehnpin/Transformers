package com.enping.transformers.data.source.local

import androidx.room.*
import com.enping.transformers.data.model.AllSpark
import com.enping.transformers.data.model.Transformer

/**
 * Room database Dao
 */
@Dao
interface TransformerDao{

    @Query("SELECT * FROM transformer")
    suspend fun getTransformers(): List<Transformer>

    @Query("SELECT * FROM transformer WHERE id = :id LIMIT 1")
    suspend fun getTransformer(id: String): Transformer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTransformer(transformer: Transformer)

    @Query("DELETE FROM transformer WHERE id = :id")
    suspend fun deleteTransformer(id: String)

    @Query("SELECT * FROM allspark LIMIT 1")
    suspend fun getAllSpark(): AllSpark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAllSpark(allSpark: AllSpark)

    @Query("DELETE FROM allspark")
    suspend fun clearAllSpark()
}