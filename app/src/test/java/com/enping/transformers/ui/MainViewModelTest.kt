package com.enping.transformers.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import java.util.concurrent.Executors

internal class MainViewModelTest : KoinTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single {
                mockk<TransformerRepo>(relaxed = true)
            }
        })
    }

    @Test
    fun `given first time use when open app then load transformers`() {
        val repo: TransformerRepo = get()
        val vm = MainViewModel(repo)
        val expected = Transformer.create(name = "A")
        val mockObserver = mockk<Observer<List<Transformer>>>(relaxUnitFun = true)
        val slot = slot<List<Transformer>>()
        coEvery { repo.getTransformers() } answers { listOf(expected) }
        vm.transformers.observeForever(mockObserver)
        vm.load()

        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
            mockObserver.onChanged(capture(slot))
        }
        confirmVerified(repo)
        Truth.assertThat(slot.captured).isEqualTo(listOf(expected))
    }

    @Test
    fun `given has transformers when delete the transform then reload transformers`() {
        val repo: TransformerRepo = get()
        val vm = MainViewModel(repo)
        val mockObserver = mockk<Observer<List<Transformer>>>(relaxUnitFun = true)
        val slot = slot<List<Transformer>>()

        coEvery {
            repo.getTransformers()
        } answers {
            listOf(
                Transformer.create(id = "A"),
                Transformer.create(id = "B")
            )
        } andThen {
            listOf(
                Transformer.create(id = "B")
            )
        }
        vm.transformers.observeForever(mockObserver)

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
            mockObserver.onChanged(capture(slot))
        }
        Truth.assertThat(slot.captured).isEqualTo(listOf(
            Transformer.create(id = "A"),
            Transformer.create(id = "B")
        ))

        vm.delete("A")
        coVerifyOrder {
            repo.deleteTransformer("A")
            repo.getTransformers()
            mockObserver.onChanged(capture(slot))
        }
        Truth.assertThat(slot.captured).isEqualTo(listOf(
            Transformer.create(id = "B")
        ))

        confirmVerified(repo)
    }
}