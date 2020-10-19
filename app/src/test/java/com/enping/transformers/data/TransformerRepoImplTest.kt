package com.enping.transformers.data

import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule


internal class TransformerRepoImplTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single { mockk<RemoteDataSource>() }
            single { mockk<LocalDataSource>(relaxed = true) }
        })
    }

    @Test
    fun `given first time when need AllSpark then get one from remote and persist`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val expected = "allSpark"
            coEvery { local.getAllSpark() } returns null
            coEvery { remote.createAllSpark() } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getOrCreateAllSpark()

            coVerifyOrder {
                local.getAllSpark()
                remote.createAllSpark()
                local.setAllSpark(expected)
            }
            confirmVerified(local, remote)
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `given AllSpark exist when need AllSpark then get one from local`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val expected = "allSpark"
            coEvery { local.getAllSpark() } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getOrCreateAllSpark()

            coVerifyOrder {
                local.getAllSpark()
            }
            confirmVerified(local, remote)
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `given AllSpark exist when player drop AllSpark then AllSpark is release`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()

            val repo = TransformerRepoImpl(remote, local)
            repo.releaseAllSpark()

            coVerifyOrder {
                local.clearAllSpark()
            }

        }


}