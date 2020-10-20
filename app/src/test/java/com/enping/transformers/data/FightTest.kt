package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import org.junit.Test

internal class FightTest {

    @Test(expected = IllegalStateException::class)
    fun `given 2 fighter when assign wrong side then throw exception`() {
        val fighterA = Transformer.create(
            team = Team.Decepticons
        )
        val fighterB = Transformer.create(
            team = Team.Autobots
        )
        Fight(fighterA, fighterB).fight()
    }

    @Test
    fun `given 2 fighter when autobot courage is up 4 and strength is up 3 than opponent then it wins`() {
        val fighterA = Transformer.create(
            name = "A",
            courage = 5,
            strength = 4,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = "B",
            courage = 1,
            strength = 1,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.AUTOBOTS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.VICTOR,
            deceptionsFighterStatus = FighterStatus.ELIMINATED
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when deception courage is up 4 and strength is up 3 than opponent then it wins`() {
        val fighterA = Transformer.create(
            name = "A",
            courage = 3,
            strength = 3,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = "B",
            courage = 10,
            strength = 10,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.DECEPTICONS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.ELIMINATED,
            deceptionsFighterStatus = FighterStatus.VICTOR
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when autobot skills is up 3 than opponent then it wins`() {
        val fighterA = Transformer.create(
            name = "A",
            skill = 6,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = "B",
            skill = 3,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.AUTOBOTS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.VICTOR,
            deceptionsFighterStatus = FighterStatus.ELIMINATED
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when deception skills is up 3 than opponent then it wins`() {
        val fighterA = Transformer.create(
            name = "A",
            skill = 5,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = "B",
            skill = 8,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.DECEPTICONS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.ELIMINATED,
            deceptionsFighterStatus = FighterStatus.VICTOR
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when autobot overall rating is greater than opponent then it wins`() {
        val winners = listOf(
            Transformer.create(
                name = "A",
                strength = 2,
                team = Team.Autobots
            ),
            Transformer.create(
                name = "A",
                intelligence = 2,
                team = Team.Autobots
            ),
            Transformer.create(
                name = "A",
                speed = 2,
                team = Team.Autobots
            ),
            Transformer.create(
                name = "A",
                endurance = 2,
                team = Team.Autobots
            ),
            Transformer.create(
                name = "A",
                firepower = 2,
                team = Team.Autobots
            )
        )
        val loser = Transformer.create(
            name = "B",
            team = Team.Decepticons
        )

        for (winner in winners){
            val expected = FightResult(
                status = BattleStatus.AUTOBOTS_WIN,
                autobotsFighter = winner,
                deceptionsFighter = loser,
                autobotsFighterStatus = FighterStatus.VICTOR,
                deceptionsFighterStatus = FighterStatus.ELIMINATED
            )
            val actual = Fight(winner, loser).fight()
            Truth.assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `given 2 fighter when deception overall rating is greater than opponent then it wins`() {
        val winners = listOf(
            Transformer.create(
                name = "B",
                strength = 2,
                team = Team.Decepticons
            ),
            Transformer.create(
                name = "B",
                intelligence = 2,
                team = Team.Decepticons
            ),
            Transformer.create(
                name = "B",
                speed = 2,
                team = Team.Decepticons
            ),
            Transformer.create(
                name = "B",
                endurance = 2,
                team = Team.Decepticons
            ),
            Transformer.create(
                name = "B",
                firepower = 2,
                team = Team.Decepticons
            )
        )
        val loser = Transformer.create(
            name = "A",
            team = Team.Autobots
        )

        for (winner in winners){
            val expected = FightResult(
                status = BattleStatus.DECEPTICONS_WIN,
                autobotsFighter = loser,
                deceptionsFighter = winner,
                autobotsFighterStatus = FighterStatus.ELIMINATED,
                deceptionsFighterStatus = FighterStatus.VICTOR
            )
            val actual = Fight(loser,winner).fight()
            Truth.assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `given 2 fighter when fighters overall rating is equal opponent then tie`() {
        val fighterA = Transformer.create(
            name = "A",
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = "B",
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.TIE,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.DESTROYED,
            deceptionsFighterStatus = FighterStatus.DESTROYED
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when autobot is called Optimus Prime then it sudden win`(){
        val fighterA = Transformer.create(
            name = Transformer.OptimusPrime,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.AUTOBOTS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.VICTOR,
            deceptionsFighterStatus = FighterStatus.ELIMINATED
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when autobot is called Predaking then it sudden win`(){
        val fighterA = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.AUTOBOTS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.VICTOR,
            deceptionsFighterStatus = FighterStatus.ELIMINATED
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when deception is called Optimus Prime then it sudden win`(){
        val fighterA = Transformer.create(
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.OptimusPrime,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.DECEPTICONS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.ELIMINATED,
            deceptionsFighterStatus = FighterStatus.VICTOR
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 fighter when deception is called Predaking then it sudden win`(){
        val fighterA = Transformer.create(
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Decepticons
        )
        val expected = FightResult(
            status = BattleStatus.DECEPTICONS_WIN,
            autobotsFighter = fighterA,
            deceptionsFighter = fighterB,
            autobotsFighterStatus = FighterStatus.ELIMINATED,
            deceptionsFighterStatus = FighterStatus.VICTOR
        )
        val actual = Fight(fighterA, fighterB).fight()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test(expected = BigBangException::class)
    fun `given 2 fighter when both side is called Predaking then throw exception`(){
        val fighterA = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Decepticons
        )
        Fight(fighterA, fighterB).fight()
    }

    @Test(expected = BigBangException::class)
    fun `given 2 fighter when both side is called OptimusPrime then throw exception`(){
        val fighterA = Transformer.create(
            name = Transformer.OptimusPrime,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Decepticons
        )
        Fight(fighterA, fighterB).fight()
    }

    @Test(expected = BigBangException::class)
    fun `given 2 fighter when autobot is called OptimusPrime and opponent is called Predaking then throw exception`(){
        val fighterA = Transformer.create(
            name = Transformer.OptimusPrime,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Decepticons
        )
        Fight(fighterA, fighterB).fight()
    }

    @Test(expected = BigBangException::class)
    fun `given 2 fighter when autobot is called Predaking and opponent is called OptimusPrime then throw exception`(){
        val fighterA = Transformer.create(
            name = Transformer.OptimusPrime,
            team = Team.Autobots
        )
        val fighterB = Transformer.create(
            name = Transformer.Predaking,
            team = Team.Decepticons
        )
        Fight(fighterA, fighterB).fight()
    }

}