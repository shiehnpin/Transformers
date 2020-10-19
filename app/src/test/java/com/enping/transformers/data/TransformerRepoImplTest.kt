package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource
import com.google.common.truth.Truth
import io.mockk.*
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
            single { mockk<RemoteDataSource>(relaxed = true) }
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
                remote.setAllSpark(expected)
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
                remote.setAllSpark(expected)
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
                remote.clearAllSpark()
            }
        }

    @Test
    fun `given AllSpark exist when create new transformer then call API and persist locally`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val slot = slot<Transformer>()
            coEvery { local.getAllSpark() } returns key
            coEvery {
                remote.createTransformer(capture(slot))
            } answers {
                //Issued by server
                slot.captured.copy(id = "id", teamIcon = "icon")
            }

            val expected = Transformer.create(
                name = "Bot",
                team = Team.Autobots
            )
            val expectedFromServer = expected.copy(
                id = "id",
                teamIcon = "icon"
            )
            val repo = TransformerRepoImpl(remote, local)
            repo.createTransformer(expected)

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                remote.createTransformer(expected)
                local.insertTransformer(expectedFromServer)
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given AllSpark exist but data is not load when get transformers then get from API`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val expected = listOf(
                Transformer.create(
                    name = "Bot1",
                    team = Team.Autobots
                )
            )
            coEvery { local.getAllSpark() } returns key
            coEvery { local.isLoaded() } returns false
            coEvery { local.getTransformers() } returns expected
            coEvery { remote.getTransformers() } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getTransformers()

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.isLoaded()
                remote.getTransformers()
                local.insertTransformers(expected)
                local.getTransformers()
            }
            confirmVerified(local, remote)
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `given AllSpark exist but data load when get transformers then get from local`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val expected = listOf(
                Transformer.create(
                    name = "Bot1",
                    team = Team.Autobots
                )
            )
            coEvery { local.getAllSpark() } returns key
            coEvery { local.isLoaded() } returns true
            coEvery { local.getTransformers() } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getTransformers()

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.isLoaded()
                local.getTransformers()
            }
            confirmVerified(local, remote)
            Truth.assertThat(actual).isEqualTo(expected)
        }


    @Test
    fun `given AllSpark exist when update transformer then call API and persist locally`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val slot = slot<Transformer>()
            coEvery { local.getAllSpark() } returns key
            coEvery {
                remote.updateTransformer(capture(slot))
            } answers {
                slot.captured
            }

            val expected = Transformer.create(
                name = "Bot",
                team = Team.Autobots
            )
            val repo = TransformerRepoImpl(remote, local)
            repo.updateTransformer(expected)

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                remote.updateTransformer(expected)
                local.updateTransformer(expected)
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given AllSpark exist when delete transformer then call API and removed locally`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            coEvery { local.getAllSpark() } returns key

            val repo = TransformerRepoImpl(remote, local)
            repo.deleteTransformer("id")

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                remote.deleteTransformer( "id")
                local.deleteTransformer("id")
            }
            confirmVerified(local, remote)
        }
}