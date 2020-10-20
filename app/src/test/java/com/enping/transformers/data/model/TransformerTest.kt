package com.enping.transformers.data.model

import com.enping.transformers.data.model.Transformer.Companion.MAX_RANK
import com.enping.transformers.data.model.Transformer.Companion.MIN_RANK
import com.google.common.truth.Truth
import org.junit.Test

internal class TransformerTest {

    @Test
    fun `given player when create new Autobots transformer then get new one`() {
        val expected = Transformer(
            name = "Optimus Prime",
            strength = MIN_RANK,
            intelligence = MIN_RANK,
            speed = MIN_RANK,
            endurance = MIN_RANK,
            rank = MIN_RANK,
            courage = MIN_RANK,
            firepower = MIN_RANK,
            skill = MIN_RANK,
            team = "A"
        )

        val actual = Transformer.create(
            name = "Optimus Prime",
            strength = MIN_RANK,
            intelligence = MIN_RANK,
            speed = MIN_RANK,
            endurance = MIN_RANK,
            rank = MIN_RANK,
            courage = MIN_RANK,
            firepower = MIN_RANK,
            skill = MIN_RANK,
            team = Team.Autobots
        )

        Truth.assertThat(actual).isEqualTo(expected)
        Truth.assertThat(actual.overAllRating)
            .isEqualTo(
                expected.strength +
                        expected.intelligence +
                        expected.speed +
                        expected.endurance +
                        expected.firepower
            )
        Truth.assertThat(actual.enumTeam).isEqualTo(Team.Autobots)
    }


    @Test
    fun `given player when create new Decepticons transformer then get new one`() {
        val expected = Transformer(
            name = "Predaking",
            strength = MAX_RANK,
            intelligence = MAX_RANK,
            speed = MAX_RANK,
            endurance = MAX_RANK,
            rank = MAX_RANK,
            courage = MAX_RANK,
            firepower = MAX_RANK,
            skill = MAX_RANK,
            team = "D"
        )

        val actual = Transformer.create(
            name = "Predaking",
            strength = MAX_RANK,
            intelligence = MAX_RANK,
            speed = MAX_RANK,
            endurance = MAX_RANK,
            rank = MAX_RANK,
            courage = MAX_RANK,
            firepower = MAX_RANK,
            skill = MAX_RANK,
            team = Team.Decepticons
        )

        Truth.assertThat(actual).isEqualTo(expected)
        Truth.assertThat(actual.overAllRating)
            .isEqualTo(
                expected.strength +
                        expected.intelligence +
                        expected.speed +
                        expected.endurance +
                        expected.firepower
            )
        Truth.assertThat(actual.enumTeam).isEqualTo(Team.Decepticons)
    }

    @Test(expected = IllegalStateException::class)
    fun `given player when create transformer with over rank then throw exception`() {
        Transformer.create(
            name = "Predaking",
            strength = MAX_RANK + 1,
            intelligence = MAX_RANK,
            speed = MAX_RANK,
            endurance = MAX_RANK,
            rank = MAX_RANK,
            courage = MAX_RANK,
            firepower = MAX_RANK,
            skill = MAX_RANK,
            team = Team.Decepticons
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `given player when create transformer with under rank then throw exception`() {
        Transformer.create(
            name = "Predaking",
            strength = MIN_RANK - 1,
            intelligence = MAX_RANK,
            speed = MAX_RANK,
            endurance = MAX_RANK,
            rank = MAX_RANK,
            courage = MAX_RANK,
            firepower = MAX_RANK,
            skill = MAX_RANK,
            team = Team.Decepticons
        )
    }

}