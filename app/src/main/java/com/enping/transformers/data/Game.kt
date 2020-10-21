package com.enping.transformers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer

class InsufficientFighterException :
    Exception("At least need one Autobot and one Deception to fight.")

class Game(private val fightersList: List<Transformer>) {
    fun battle(): GameResult {
        val autobots = fightersList
            .filter { it.enumTeam == Team.Autobots }
            .sortedWith(compareByDescending(Transformer::rank).thenBy(Transformer::id))
        val deceptions = fightersList
            .filter { it.enumTeam == Team.Decepticons }
            .sortedWith(compareByDescending(Transformer::rank).thenBy(Transformer::id))
        if (autobots.isEmpty() || deceptions.isEmpty()) {
            throw InsufficientFighterException()
        }
        val count = Math.max(autobots.size, deceptions.size)
        val autobotsStatus = mutableListOf<Pair<Transformer, FighterStatus>>()
        val deceptionsStatus = mutableListOf<Pair<Transformer, FighterStatus>>()
        var autobotsWinTime = 0
        var deceptionWinTime = 0
        var battleCount = 0

        try {
            for (i in 0 until count) {
                val a = autobots.getOrNull(i)
                val d = deceptions.getOrNull(i)
                if (a != null && d != null) {
                    battleCount++
                    val result = Fight(a, d).fight()
                    autobotsStatus.add(result.autobotsFighter to result.autobotsFighterStatus)
                    deceptionsStatus.add(result.deceptionsFighter to result.deceptionsFighterStatus)
                    if (result.status == BattleStatus.AUTOBOTS_WIN) autobotsWinTime++
                    if (result.status == BattleStatus.DECEPTICONS_WIN) deceptionWinTime++
                } else if (d != null) {
                    deceptionsStatus.add(d to FighterStatus.SKIP)
                } else if (a != null) {
                    autobotsStatus.add(a to FighterStatus.SKIP)
                } else {
                    error("shouldn't be here")
                }
            }
        } catch (e: BigBangException) {
            return GameResult(
                battle = battleCount,
                result = BattleStatus.TIE,
                autobotsStatus = autobots.map { it to FighterStatus.DESTROYED },
                deceptionsStatus = deceptions.map { it to FighterStatus.DESTROYED }
            )
        }
        val result = when {
            autobotsWinTime == deceptionWinTime -> {
                BattleStatus.TIE
            }
            autobotsWinTime > deceptionWinTime -> {
                BattleStatus.AUTOBOTS_WIN
            }
            else -> {
                BattleStatus.DECEPTICONS_WIN
            }
        }
        return GameResult(
            battle = battleCount,
            result = result,
            autobotsStatus = autobotsStatus,
            deceptionsStatus = deceptionsStatus
        )
    }

}

@Parcelize
data class GameResult(
    val battle: Int,
    val result: BattleStatus,
    val autobotsStatus: List<Pair<Transformer, FighterStatus>> = emptyList(),
    val deceptionsStatus: List<Pair<Transformer, FighterStatus>> = emptyList()
) : Parcelable
