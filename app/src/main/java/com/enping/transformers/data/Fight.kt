package com.enping.transformers.data

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer


class Fight(
    val autobots: Transformer,
    val deceptions: Transformer
) {
    private val specialNameList = listOf(Transformer.OptimusPrime, Transformer.Predaking)

    @Throws(BigBangException::class)
    fun fight(): FightResult {
        check(autobots.enumTeam == Team.Autobots)
        check(deceptions.enumTeam == Team.Decepticons)
        if (autobots.name in specialNameList && deceptions.name in specialNameList) {
            throw BigBangException()
        }
        val winner =
            if (autobots.name in specialNameList) {
                autobots
            } else if (deceptions.name in specialNameList) {
                deceptions
            } else if (autobots.courage - deceptions.courage >= 4 &&
                autobots.strength - deceptions.strength >= 3
            ) {
                autobots
            } else if (deceptions.courage - autobots.courage >= 4 &&
                deceptions.strength - autobots.strength >= 3
            ) {
                deceptions
            } else if (autobots.skill - deceptions.skill >= 3) {
                autobots
            } else if (deceptions.skill - autobots.skill >= 3) {
                deceptions
            } else if (autobots.overAllRating > deceptions.overAllRating) {
                autobots
            } else if (deceptions.overAllRating > autobots.overAllRating) {
                deceptions
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
    DESTROYED, VICTOR, ELIMINATED
}

data class FightResult(
    val status: BattleStatus,
    val autobotsFighter: Transformer,
    val autobotsFighterStatus: FighterStatus,
    val deceptionsFighter: Transformer,
    val deceptionsFighterStatus: FighterStatus
) {
    companion object {
        fun from(
            fight: Fight,
            status: BattleStatus,
            autobotsFighterStatus: FighterStatus,
            deceptionsFighterStatus: FighterStatus
        ): FightResult {
            return FightResult(
                status = status,
                autobotsFighter = fight.autobots,
                deceptionsFighter = fight.deceptions,
                autobotsFighterStatus = autobotsFighterStatus,
                deceptionsFighterStatus = deceptionsFighterStatus
            )
        }
    }
}
