package com.enping.transformers.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * This data class to manipulate transformers.
 *
 * @property id always 0, DO NOT assign.
 * @property allSpark the token from remote source.
 */
@Entity(tableName = "allspark")
data class AllSpark(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "0",

    @ColumnInfo(name = "allspark")
    val allSpark: String = ""
)