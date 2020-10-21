package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer


class Fight(
    val autobots: Transformer,
    val decepticons: Transformer
) {
    private val specialNameList = listOf(Transformer.OptimusPrime, Transformer.Predaking)

    @Throws(BigBangException::class)
    fun fight(): FightResult {
        check(autobots.enumTeam == Team.Autobots)
        check(decepticons.enumTeam == Team.Decepticons)
        if (autobots.name in specialNameList && decepticons.name in specialNameList) {
            throw BigBangException()
        }
        val winner =
            if (autobots.name in specialNameList) {
                autobots
            } else if (decepticons.name in specialNameList) {
                decepticons
            } else if (autobots.courage - decepticons.courage >= 4 &&
                autobots.strength - decepticons.strength >= 3
            ) {
                autobots
            } else if (decepticons.courage - autobots.courage >= 4 &&
                decepticons.strength - autobots.strength >= 3
            ) {
                decepticons
            } else if (autobots.skill - decepticons.skill >= 3) {
                autobots
            } else if (decepticons.skill - autobots.skill >= 3) {
                decepticons
            } else if (autobots.overAllRating > decepticons.overAllRating) {
                autobots
            } else if (decepticons.overAllRating > autobots.overAllRating) {
                decepticons
            } else {
                return FightResult.from(
                    this,
                    BattleStatus.TIE,
                    FighterStatus.DESTROYED,
                    FighterStatus.DESTROYED
                )
            }

        return if (winner.enumTeam == Team.Autobots) {
            FightResult.from(
                this,
                BattleStatus.AUTOBOTS_WIN,
                FighterStatus.VICTOR,
                FighterStatus.ELIMINATED
            )
        } else {
            FightResult.from(
                this,
                BattleStatus.DECEPTICONS_WIN,
                FighterStatus.ELIMINATED,
                FighterStatus.VICTOR
            )
        }
    }
}

class BigBangException : Exception()

enum class BattleStatus {
    AUTOBOTS_WIN, DECEPTICONS_WIN, TIE
}

enum class FighterStatus {
    DESTROYED, VICTOR, ELIMINATED, SKIP
}

data class FightResult(
    val status: BattleStatus,
    val autobotsFighter: Transformer,
    val autobotsFighterStatus: FighterStatus,
    val decepticonsFighter: Transformer,
    val decepticonsFighterStatus: FighterStatus
) {
    companion object {
        fun from(
            fight: Fight,
            status: BattleStatus,
            autobotsFighterStatus: FighterStatus,
            decepticonsFighterStatus: FighterStatus
        ): FightResult {
            return FightResult(
                status = status,
                autobotsFighter = fight.autobots,
                decepticonsFighter = fight.decepticons,
                autobotsFighterStatus = autobotsFighterStatus,
                decepticonsFighterStatus = decepticonsFighterStatus
            )
        }
    }
}
