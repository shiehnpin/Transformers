package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.remote.RemoteDataSource
import com.google.common.truth.Truth
import io.mockk.*
import junit.framework.Assert.fail
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
            coEvery { local.hasAllSpark() } returns true

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
    fun `given AllSpark exist when create new transformer then call API and persist locally`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val slot = slot<Transformer>()
            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
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
            repo.getOrCreateAllSpark()
            repo.createTransformer(expected)

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.hasAllSpark()
                remote.createTransformer(expected)
                local.insertTransformer(expectedFromServer)
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given AllSpark exist when get transformers then get from local`() =
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
            coEvery { local.hasAllSpark() } returns true
            coEvery { local.getTransformers() } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getTransformers()

            coVerifyOrder {
                local.hasAllSpark()
                local.getTransformers()
            }
            confirmVerified(local, remote)
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `given AllSpark exist when get transformer with id then get from local`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val id = "1"
            val expected = Transformer(
                id = id,
                name = "Bot1",
                team = "A"
            )
            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
            coEvery { local.getTransformer(id) } returns expected

            val repo = TransformerRepoImpl(remote, local)
            val actual = repo.getTransformer(id)

            coVerifyOrder {
                local.hasAllSpark()
                local.getTransformer(id)
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
            coEvery { local.hasAllSpark() } returns true
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
            repo.getOrCreateAllSpark()
            repo.updateTransformer(expected)

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.hasAllSpark()
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
            coEvery { local.hasAllSpark() } returns true

            val repo = TransformerRepoImpl(remote, local)
            repo.getOrCreateAllSpark()
            repo.deleteTransformer("id")

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.hasAllSpark()
                remote.deleteTransformer("id")
                local.deleteTransformer("id")
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given Allspark exist when create blank name transformer then throw exception`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
            val repo = TransformerRepoImpl(remote, local)
            repo.getOrCreateAllSpark()
            try {
                repo.createTransformer(Transformer.create())
                fail()
            } catch (e: Throwable) {
                Truth.assertThat(e).isInstanceOf(IllegalStateException::class.java)
            }
            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.hasAllSpark()
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given Allspark exist when update blank name transformer then throw exception`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
            val repo = TransformerRepoImpl(remote, local)
            repo.getOrCreateAllSpark()

            try {
                repo.updateTransformer(Transformer.create())
                fail()
            } catch (e: Throwable) {
                Truth.assertThat(e).isInstanceOf(IllegalStateException::class.java)
            }
            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.hasAllSpark()
            }
            confirmVerified(local, remote)
        }

    @Test
    fun `given enough transformers when start war then get the result`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"
            val Bluestreak = Transformer.create("Bluestreak", 6, 6, 7, 9, 5, 2, 9, 7, Team.Autobots)
            val Hubcap = Transformer.create("Hubcap", 4, 4, 4, 4, 4, 4, 4, 4, Team.Autobots)
            val Soundwave =
                Transformer.create("Soundwave", 8, 9, 2, 6, 7, 5, 6, 10, Team.Decepticons)
            val transformers = listOf(Bluestreak, Hubcap, Soundwave)

            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
            coEvery { local.getTransformers() } returns transformers

            val repo = TransformerRepoImpl(remote, local)
            repo.getOrCreateAllSpark()
            val actual = repo.battleTransformers()

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.getTransformers()
            }
            val expected = GameResult(
                battle = 1,
                result = BattleStatus.DECEPTICONS_WIN,
                autobotsStatus = listOf(
                    Bluestreak to FighterStatus.ELIMINATED,
                    Hubcap to FighterStatus.SKIP
                ),
                decepticonsStatus = listOf(
                    Soundwave to FighterStatus.VICTOR
                )
            )
            Truth.assertThat(actual).isEqualTo(expected)
            confirmVerified(local, remote)
        }

    @Test
    fun `given not enough transformers when start war then get the exception`() =
        runBlocking {
            val remote: RemoteDataSource = get()
            val local: LocalDataSource = get()
            val key = "allspark"

            coEvery { local.getAllSpark() } returns key
            coEvery { local.hasAllSpark() } returns true
            coEvery { local.getTransformers() } returns emptyList()

            val repo = TransformerRepoImpl(remote, local)
            repo.getOrCreateAllSpark()

            try {
                repo.battleTransformers()
                fail()
            } catch (e: Throwable) {
                Truth.assertThat(e).isInstanceOf(InsufficientFighterException::class.java)
            }

            coVerifyOrder {
                local.getAllSpark()
                remote.setAllSpark(key)
                local.getTransformers()
            }
            confirmVerified(local, remote)
        }

}