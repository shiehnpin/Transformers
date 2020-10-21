package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import org.junit.Test


/**
 * Game assumption:
 * 1. Fights order by rank (DESC) then by id alphabetic
 * 2. A fight at least need 1 autobot and 1 decepticon
 */
internal class GameTest {

    @Test(expected = InsufficientFighterException::class)
    fun `given 0 autobot an 1 decepticon when fight then throw exception`() {
        Game(listOf(Transformer.create(team = Team.Autobots))).battle()
    }

    @Test
    fun `given 1 autobot and 1 decepticon when autobot is faster then autobot win the game`() {
        val autobot = Transformer.create(name = "winner", team = Team.Autobots, speed = 3)
        val decepticon = Transformer.create(name = "loser", team = Team.Decepticons)
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.AUTOBOTS_WIN,
            autobotsStatus = listOf(autobot to FighterStatus.VICTOR),
            decepticonsStatus = listOf(decepticon to FighterStatus.ELIMINATED)
        )
        val actual = Game(listOf(autobot, decepticon)).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 1 autobot and 1 decepticon when decepticon is faster then decepticon win the game`() {
        val autobot = Transformer.create(name = "loser", team = Team.Autobots)
        val decepticon = Transformer.create(name = "winner", team = Team.Decepticons, speed = 3)
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.DECEPTICONS_WIN,
            autobotsStatus = listOf(autobot to FighterStatus.ELIMINATED),
            decepticonsStatus = listOf(decepticon to FighterStatus.VICTOR)
        )
        val actual = Game(listOf(autobot, decepticon)).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 1 autobot and 1 decepticon when their rating is equal then it is a tie`() {
        val autobot = Transformer.create(name = "loser", team = Team.Autobots, strength = 3)
        val decepticon = Transformer.create(name = "winner", team = Team.Decepticons, speed = 3)
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.TIE,
            autobotsStatus = listOf(autobot to FighterStatus.DESTROYED),
            decepticonsStatus = listOf(decepticon to FighterStatus.DESTROYED)
        )
        val actual = Game(listOf(autobot, decepticon)).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 1 autobot and 3 decepticon when autobot is stronger then opponent then autobot wins`() {
        val autobot = Transformer.create(name = "winner", team = Team.Autobots, strength = 3)
        val decepticons = listOf(
            Transformer.create(name = "1", team = Team.Decepticons),
            Transformer.create(name = "2", team = Team.Decepticons),
            Transformer.create(name = "3", team = Team.Decepticons)
        )
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.AUTOBOTS_WIN,
            autobotsStatus = listOf(autobot to FighterStatus.VICTOR),
            decepticonsStatus = listOf(
                Transformer.create(name = "1", team = Team.Decepticons) to FighterStatus.ELIMINATED,
                Transformer.create(name = "2", team = Team.Decepticons) to FighterStatus.SKIP,
                Transformer.create(name = "3", team = Team.Decepticons) to FighterStatus.SKIP
            )
        )
        val actual = Game(decepticons + autobot).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 3 autobot and 1 decepticon when decepticon is stronger then opponent then decepticon wins`() {
        val autobots = listOf(
            Transformer.create(name = "1", team = Team.Autobots),
            Transformer.create(name = "2", team = Team.Autobots),
            Transformer.create(name = "3", team = Team.Autobots)
        )
        val decepticon = Transformer.create(name = "winner", team = Team.Decepticons, strength = 3)
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.DECEPTICONS_WIN,
            autobotsStatus = listOf(
                Transformer.create(name = "1", team = Team.Autobots) to FighterStatus.ELIMINATED,
                Transformer.create(name = "2", team = Team.Autobots) to FighterStatus.SKIP,
                Transformer.create(name = "3", team = Team.Autobots) to FighterStatus.SKIP
            ),
            decepticonsStatus = listOf(
                decepticon to FighterStatus.VICTOR
            )
        )
        val actual = Game(autobots + decepticon).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 2 ranked autobots and 2 ranked decepticons when they neck-neck then it is tie`() {
        val autobots = listOf(
            Transformer.create(name = "1", team = Team.Autobots, rank = 2, firepower = 3),
            Transformer.create(name = "2", team = Team.Autobots, rank = 1)
        )
        val decepticons = listOf(
            Transformer.create(name = "A", team = Team.Decepticons, rank = 1, intelligence = 3),
            Transformer.create(name = "B", team = Team.Decepticons, rank = 2)
        )
        val expected = GameResult(
            battle = 2,
            result = BattleStatus.TIE,
            autobotsStatus = listOf(
                Transformer.create(
                    name = "1",
                    team = Team.Autobots,
                    rank = 2,
                    firepower = 3
                ) to FighterStatus.VICTOR,
                Transformer.create(
                    name = "2",
                    team = Team.Autobots,
                    rank = 1
                ) to FighterStatus.ELIMINATED
            ),
            decepticonsStatus = listOf(
                Transformer.create(
                    name = "B",
                    team = Team.Decepticons,
                    rank = 2
                ) to FighterStatus.ELIMINATED,
                Transformer.create(
                    name = "A",
                    team = Team.Decepticons,
                    rank = 1,
                    intelligence = 3
                ) to FighterStatus.VICTOR
            )
        )
        val actual = Game(autobots + decepticons).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given 5 ranked autobots and 1 decepticons when input reversed rank then sorted it by rank(DESC) then id(alphabet)`() {
        val autobots = listOf(
            Transformer.create(id = "a", team = Team.Autobots, rank = 4),
            Transformer.create(id = "b", team = Team.Autobots, rank = 4),
            Transformer.create(id = "c", team = Team.Autobots, rank = 3),
            Transformer.create(id = "d", team = Team.Autobots, rank = 2),
            Transformer.create(id = "e", team = Team.Autobots, rank = 1)
        )
        val decepticon = Transformer.create(name = "A", team = Team.Decepticons)
        val expected = GameResult(
            battle = 1,
            result = BattleStatus.TIE,
            autobotsStatus = listOf(
                Transformer.create(
                    id = "a",
                    team = Team.Autobots,
                    rank = 4
                ) to FighterStatus.DESTROYED,
                Transformer.create(id = "b", team = Team.Autobots, rank = 4) to FighterStatus.SKIP,
                Transformer.create(id = "c", team = Team.Autobots, rank = 3) to FighterStatus.SKIP,
                Transformer.create(id = "d", team = Team.Autobots, rank = 2) to FighterStatus.SKIP,
                Transformer.create(id = "e", team = Team.Autobots, rank = 1) to FighterStatus.SKIP
            ),
            decepticonsStatus = listOf(decepticon to FighterStatus.DESTROYED)
        )
        val actual = Game(autobots.reversed() + decepticon).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `given autobots and decepticons when OptimusPrime fight to Predaking then all destroyed`() {
        val autobots = listOf(
            Transformer.create(team = Team.Autobots, rank = 4),
            Transformer.create(team = Team.Autobots, rank = 3),
            Transformer.create(team = Team.Autobots, rank = 2),
            Transformer.create(team = Team.Autobots, rank = 1, name = Transformer.OptimusPrime)
        )
        val decepticons = listOf(
            Transformer.create(team = Team.Decepticons, rank = 1, name = Transformer.Predaking),
            Transformer.create(team = Team.Decepticons, rank = 2),
            Transformer.create(team = Team.Decepticons, rank = 3),
            Transformer.create(team = Team.Decepticons, rank = 4)
        )
        val expected = GameResult(
            battle = 4,
            result = BattleStatus.TIE,
            autobotsStatus = listOf(
                Transformer.create(team = Team.Autobots, rank = 4) to FighterStatus.DESTROYED,
                Transformer.create(team = Team.Autobots, rank = 3) to FighterStatus.DESTROYED,
                Transformer.create(team = Team.Autobots, rank = 2) to FighterStatus.DESTROYED,
                Transformer.create(
                    team = Team.Autobots,
                    rank = 1,
                    name = Transformer.OptimusPrime
                ) to FighterStatus.DESTROYED
            ),
            decepticonsStatus = listOf(
                Transformer.create(team = Team.Decepticons, rank = 4) to FighterStatus.DESTROYED,
                Transformer.create(team = Team.Decepticons, rank = 3) to FighterStatus.DESTROYED,
                Transformer.create(team = Team.Decepticons, rank = 2) to FighterStatus.DESTROYED,
                Transformer.create(
                    team = Team.Decepticons,
                    rank = 1,
                    name = Transformer.Predaking
                ) to FighterStatus.DESTROYED
            )
        )
        val actual = Game(autobots + decepticons).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `assements sample test`() {
        //Bluestreak, A, 6,6,7,9,5,2,9,7
        //Hubcap: A, 4,4,4,4,4,4,4,4
        val Bluestreak = Transformer.create("Bluestreak", 6, 6, 7, 9, 5, 2, 9, 7, Team.Autobots)
        val Hubcap = Transformer.create("Hubcap", 4, 4, 4, 4, 4, 4, 4, 4, Team.Autobots)

        //Soundwave, D, 8,9,2,6,7,5,6,10
        val Soundwave = Transformer.create("Soundwave", 8, 9, 2, 6, 7, 5, 6, 10, Team.Decepticons)

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
        val actual = Game(listOf(Bluestreak, Soundwave, Hubcap)).battle()
        Truth.assertThat(actual).isEqualTo(expected)
    }

}