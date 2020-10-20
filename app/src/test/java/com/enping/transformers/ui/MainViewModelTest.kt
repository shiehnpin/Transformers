package com.enping.transformers.ui

import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.koin.test.get

@ExperimentalCoroutinesApi
internal class MainViewModelTest : BaseViewModelTest() {

    @Test
    fun `given first time use when open app then load transformers`() {
        val repo: TransformerRepo = get()
        val vm = MainViewModel(repo)
        val expected = listOf(
            Transformer.create(name = "A")
        )
        coEvery { repo.getTransformers() } returns expected
        vm.load()

        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }
        confirmVerified(repo)
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expected)
    }

    @Test
    fun `given has transformers when delete the transform then reload transformers`() {
        val repo: TransformerRepo = get()
        val vm = MainViewModel(repo)
        val expected = listOf(
            Transformer.create(id = "A"),
            Transformer.create(id = "B")
        )
        val expectedAfterRemoved = listOf(
            Transformer.create(id = "B")
        )
        coEvery { repo.getTransformers() } returns expected andThen expectedAfterRemoved

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expected)

        vm.delete("A")
        coVerifyOrder {
            repo.deleteTransformer("A")
            repo.getTransformers()
        }
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expectedAfterRemoved)

        confirmVerified(repo)
    }
}