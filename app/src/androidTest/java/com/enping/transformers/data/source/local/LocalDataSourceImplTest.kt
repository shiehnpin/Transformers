package com.enping.transformers.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
internal class LocalDataSourceImplTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<TransformerDatabase> {
                val context = ApplicationProvider.getApplicationContext<Context>()
                Room.inMemoryDatabaseBuilder(context, TransformerDatabase::class.java)
                    .setTransactionExecutor(Executors.newSingleThreadExecutor())
                    .build()
            }
        })
    }

    @Test
    fun given_no_allspark_when_get_allspark_from_remote_then_save_it_locally() {
        val expected = "allspark"
        val local = LocalDataSourceImpl(get())
        runBlocking {
            Truth.assertThat(local.getAllSpark()).isNull()
            local.setAllSpark(expected)
            Truth.assertThat(local.getAllSpark()).isEqualTo(expected)
        }
    }

    @Test
    fun when_has_no_transformer_when_create_new_one_from_remote_then_store_it() {

        val expected = Transformer(
            id = "1",
            name = "Eric",
            strength = 3,
            intelligence = 3,
            speed = 3,
            endurance = 3,
            rank = 3,
            courage = 3,
            firepower = 3,
            skill = 3,
            team = "D",
            teamIcon = "url"
        )
        val local = LocalDataSourceImpl(get())
        runBlocking {
            Truth.assertThat(local.getTransformers()).isEmpty()
            local.insertTransformer(expected)
            Truth.assertThat(local.getTransformers()).containsExactly(expected)
        }
    }

    @Test
    fun when_has_transformers_when_update_transformer_from_remote_then_sync_it_locally() {
        val previous = Transformer(
            id = "1",
            name = "Eric",
            strength = 3,
            intelligence = 3,
            speed = 3,
            endurance = 3,
            rank = 3,
            courage = 3,
            firepower = 3,
            skill = 3,
            team = "D",
            teamIcon = "url"
        )
        val expected = previous.copy(
            name = "Eric2",
            strength = 4,
            intelligence = 4,
            speed = 4,
            endurance = 4,
            rank = 4,
            courage = 4,
            firepower = 4,
            skill = 4,
            team = "A"
        )
        val local = LocalDataSourceImpl(get())
        runBlocking {
            Truth.assertThat(local.getTransformers()).isEmpty()
            local.insertTransformer(previous)
            local.updateTransformer(expected)
            Truth.assertThat(local.getTransformers()).containsExactly(expected)
        }
    }

    @Test
    fun when_has_transformers_when_delete_transformer_from_remote_then_remove_it_locally() {
        val remain = Transformer(
            id = "1",
            name = "Eric",
            strength = 3,
            intelligence = 3,
            speed = 3,
            endurance = 3,
            rank = 3,
            courage = 3,
            firepower = 3,
            skill = 3,
            team = "D",
            teamIcon = "url"
        )
        val pending = Transformer(
            id = "2",
            name = "Eric2",
            strength = 3,
            intelligence = 3,
            speed = 3,
            endurance = 3,
            rank = 3,
            courage = 3,
            firepower = 3,
            skill = 3,
            team = "D",
            teamIcon = "url"
        )

        val local = LocalDataSourceImpl(get())
        runBlocking {
            Truth.assertThat(local.getTransformers()).isEmpty()
            local.insertTransformer(remain)
            local.insertTransformer(pending)
            local.deleteTransformer(pending.id)
            Truth.assertThat(local.getTransformers()).containsExactly(remain)
        }
    }

}