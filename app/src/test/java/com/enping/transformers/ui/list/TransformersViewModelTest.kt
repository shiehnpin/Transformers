package com.enping.transformers.ui.list

import com.enping.transformers.data.*
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.enping.transformers.ui.BaseViewModelTest
import com.enping.transformers.ui.getOrAwaitValue
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.koin.test.get

@ExperimentalCoroutinesApi
internal class TransformersViewModelTest : BaseViewModelTest() {

    @Test
    fun `given first time use when open app then load transformers`() {
        val repo: TransformerRepo = get()
        val vm = TransformersViewModel(repo)
        val expected = listOf(
            Transformer.create(name = "A")
        )
        coEvery { repo.getTransformers() } returns expected
        Truth.assertThat(vm.isLoaded.getOrAwaitValue()).isEqualTo(false)

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }
        confirmVerified(repo)
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expected)
        Truth.assertThat(vm.isLoaded.getOrAwaitValue()).isEqualTo(true)
    }

    @Test
    fun `given first time use when open app without network then got error`() {
        val repo: TransformerRepo = get()
        val vm = TransformersViewModel(repo)
        coEvery { repo.getOrCreateAllSpark() } throws MissingAllSparkException()

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
        }
        confirmVerified(repo)
        Truth.assertThat(vm.errorEvent.getOrAwaitValue().peekContent())
            .isInstanceOf(MissingAllSparkException::class.java)
        Truth.assertThat(vm.isLoaded.getOrAwaitValue()).isEqualTo(false)
    }

    @Test
    fun `given has transformers when delete the transform then reload transformers`() {
        val repo: TransformerRepo = get()
        val vm = TransformersViewModel(repo)
        val expected = listOf(
            Transformer.create(id = "A"),
            Transformer.create(id = "B")
        )
        val expectedAfterRemoved = listOf(
            Transformer.create(id = "B")
        )
        coEvery { repo.getTransformers() } returns expected andThen expectedAfterRemoved
        Truth.assertThat(vm.isLoaded.getOrAwaitValue()).isEqualTo(false)

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expected)
        Truth.assertThat(vm.isLoaded.getOrAwaitValue()).isEqualTo(true)

        vm.delete("A")
        coVerifyOrder {
            repo.deleteTransformer("A")
            repo.getTransformers()
        }
        Truth.assertThat(vm.transformers.getOrAwaitValue()).isEqualTo(expectedAfterRemoved)

        confirmVerified(repo)
    }

    @Test
    fun `given has transformers when the transform start war then get battle result`() {
        val repo: TransformerRepo = get()
        val vm = TransformersViewModel(repo)
        val transformers = listOf(
            Transformer.create(id = "A", team = Team.Autobots),
            Transformer.create(id = "B", team = Team.Decepticons)
        )
        val gameResult = GameResult(
            1, BattleStatus.TIE,
            listOf(
                Transformer.create(id = "A", team = Team.Autobots) to FighterStatus.DESTROYED
            ),
            listOf(
                Transformer.create(id = "B", team = Team.Decepticons) to FighterStatus.DESTROYED
            )
        )
        coEvery { repo.getTransformers() } returns transformers
        coEvery { repo.battleTransformers() } returns gameResult

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }

        vm.startWar()
        coVerifyOrder {
            repo.getTransformers()
            repo.battleTransformers()
        }

        Truth.assertThat(vm.warEvent.getOrAwaitValue().peekContent()).isEqualTo(gameResult)
        confirmVerified(repo)
    }

    @Test
    fun `given not enough transformers when start war then get error`() {
        val repo: TransformerRepo = get()
        val vm = TransformersViewModel(repo)
        val transformers = listOf(
            Transformer.create(id = "A", team = Team.Autobots)
        )
        coEvery { repo.getTransformers() } returns transformers
        coEvery { repo.battleTransformers() } throws InsufficientFighterException()

        vm.load()
        coVerifyOrder {
            repo.getOrCreateAllSpark()
            repo.getTransformers()
        }

        vm.startWar()
        coVerifyOrder {
            repo.getTransformers()
            repo.battleTransformers()
        }

        Truth.assertThat(vm.errorEvent.getOrAwaitValue().peekContent())
            .isInstanceOf(InsufficientFighterException::class.java)
        confirmVerified(repo)
    }
}