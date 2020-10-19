package com.enping.transformers.ui.list

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

internal class TransformerViewModelTest : KoinTest {
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
    fun `given has all spark when user create new transformer then call repo create`() {
        val repo: TransformerRepo = get()
        val vm = TransformerViewModel(repo)
        val expected = Transformer.create()
        val transformerObserver = mockk<Observer<Transformer>>(relaxUnitFun = true)
        val transformerSlot = mutableListOf<Transformer>()
        val editObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val editSlot = slot<Boolean>()
        vm.isEdit.observeForever(editObserver)
        vm.transformer.observeForever(transformerObserver)

        vm.load(false, "")
        coVerifyOrder {
            editObserver.onChanged(capture(editSlot))
            transformerObserver.onChanged(capture(transformerSlot))
        }
        Truth.assertThat(editSlot.captured).isEqualTo(false)
        Truth.assertThat(transformerSlot[0]).isEqualTo(expected)

        vm.edit(expected.copy(name = "new"))
        coVerifyOrder {
            transformerObserver.onChanged(capture(transformerSlot))
        }
        Truth.assertThat(transformerSlot[1]).isEqualTo(expected.copy(name = "new"))

        vm.save()
        coVerifyOrder {
            repo.createTransformer(capture(transformerSlot))
        }
        Truth.assertThat(transformerSlot[2]).isEqualTo(expected.copy(name = "new"))

        confirmVerified(editObserver, transformerObserver, repo)

    }

    @Test
    fun `given has all spark when user update exists transformer then call repo edit`() {
        val repo: TransformerRepo = get()
        val vm = TransformerViewModel(repo)
        val expected = Transformer.create(id = "1")
        val transformerObserver = mockk<Observer<Transformer>>(relaxUnitFun = true)
        val transformerSlot = slot<Transformer>()
        val editObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val editSlot = slot<Boolean>()
        coEvery { repo.getTransformer(expected.id) } answers { expected }
        vm.isEdit.observeForever(editObserver)
        vm.transformer.observeForever(transformerObserver)

        vm.load(true, expected.id)
        coVerifyOrder {
            editObserver.onChanged(capture(editSlot))
            repo.getTransformer(expected.id)
            transformerObserver.onChanged(capture(transformerSlot))
        }
        Truth.assertThat(editSlot.captured).isEqualTo(true)
        Truth.assertThat(transformerSlot.captured).isEqualTo(expected)

        vm.edit(expected.copy(name = "new"))
        coVerifyOrder {
            transformerObserver.onChanged(capture(transformerSlot))
        }
        Truth.assertThat(transformerSlot.captured).isEqualTo(expected.copy(name = "new"))

        vm.save()
        coVerifyOrder {
            repo.updateTransformer(capture(transformerSlot))
        }
        Truth.assertThat(transformerSlot.captured).isEqualTo(expected.copy(name = "new"))

        confirmVerified(editObserver, transformerObserver, repo)
    }
}