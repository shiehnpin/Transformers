package com.enping.transformers.ui.list

import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.ui.BaseViewModelTest
import com.enping.transformers.ui.getOrAwaitValue
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.koin.test.get

@ExperimentalCoroutinesApi
internal class TransformerEditViewModelTest : BaseViewModelTest() {

    @Test
    fun `given has all spark when user create new transformer then call repo create`() {
        val repo: TransformerRepo = get()
        val vm = TransformerEditViewModel(repo)
        val expected = Transformer.create()
        val expectedAfterEdit = expected.copy(name = "new")
        vm.load(false, "")
        Truth.assertThat(vm.isEdit.getOrAwaitValue()).isEqualTo(false)
        Truth.assertThat(vm.transformer.getOrAwaitValue()).isEqualTo(expected)

        vm.edit(expectedAfterEdit)
        Truth.assertThat(vm.transformer.getOrAwaitValue()).isEqualTo(expectedAfterEdit)

        vm.save()
        coVerify { repo.createTransformer(expectedAfterEdit) }
        Truth.assertThat(vm.isSubmit.getOrAwaitValue()).isEqualTo(true)
        confirmVerified(repo)
    }

    @Test
    fun `given has all spark when user update exists transformer then call repo edit`() {
        val repo: TransformerRepo = get()
        val vm = TransformerEditViewModel(repo)
        val expected = Transformer.create(id = "1")
        val expectedAfterEdit = Transformer.create(id = "1", name = "new")
        coEvery { repo.getTransformer(expected.id) } returns expected

        vm.load(true, expected.id)
        coVerify { repo.getTransformer(expected.id) }
        Truth.assertThat(vm.isEdit.getOrAwaitValue()).isEqualTo(true)
        Truth.assertThat(vm.transformer.getOrAwaitValue()).isEqualTo(expected)

        vm.edit(expectedAfterEdit)
        Truth.assertThat(vm.transformer.getOrAwaitValue()).isEqualTo(expectedAfterEdit)

        vm.save()
        coVerify { repo.updateTransformer(expectedAfterEdit) }
        Truth.assertThat(vm.isSubmit.getOrAwaitValue()).isEqualTo(true)
        confirmVerified(repo)
    }
}