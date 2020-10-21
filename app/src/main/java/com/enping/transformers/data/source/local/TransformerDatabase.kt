package com.enping.transformers.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enping.transformers.data.model.AllSpark
import com.enping.transformers.data.model.Transformer

/**
 * Room database class, DO NOT USE it directly.
 */
@Database(entities = [Transformer::class, AllSpark::class], version = 1)
abstract class TransformerDatabase : RoomDatabase() {
    abstract fun transformerDao(): TransformerDao
}



