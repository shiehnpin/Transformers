package com.enping.transformers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer

class InsufficientFighterException :
    Exception("At least need one Autobot and one Decepticon to fight.")

class Game(private val fightersList: List<Transformer>) {
    fun battle(): GameResult {
        val autobots = fightersList
            .filter { it.enumTeam == Team.Autobots }
            .sortedWith(compareByDescending(Transformer::rank).thenBy(Transformer::id))
        val decepticons = fightersList
            .filter { it.enumTeam == Team.Decepticons }
            .sortedWith(compareByDescending(Transformer::rank).thenBy(Transformer::id))
        if (autobots.isEmpty() || decepticons.isEmpty()) {
            throw InsufficientFighterException()
        }
        val count = Math.max(autobots.size, decepticons.size)
        val autobotsStatus = mutableListOf<Pair<Transformer, FighterStatus>>()
        val decepticonsStatus = mutableListOf<Pair<Transformer, FighterStatus>>()
        var autobotsWinTime = 0
        var decepticonsWinTime = 0
        var battleCount = 0

        try {
            for (i in 0 until count) {
                val a = autobots.getOrNull(i)
                val d = decepticons.getOrNull(i)
                if (a != null && d != null) {
                    battleCount++
                    val result = Fight(a, d).fight()
                    autobotsStatus.add(result.autobotsFighter to result.autobotsFighterStatus)
                    decepticonsStatus.add(result.decepticonsFighter to result.decepticonsFighterStatus)
                    if (result.status == BattleStatus.AUTOBOTS_WIN) autobotsWinTime++
                    if (result.status == BattleStatus.DECEPTICONS_WIN) decepticonsWinTime++
                } else if (d != null) {
                    decepticonsStatus.add(d to FighterStatus.SKIP)
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
                decepticonsStatus = decepticons.map { it to FighterStatus.DESTROYED }
            )
        }
        val result = when {
            autobotsWinTime == decepticonsWinTime -> {
                BattleStatus.TIE
            }
            autobotsWinTime > decepticonsWinTime -> {
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
            decepticonsStatus = decepticonsStatus
        )
    }

}

@Parcelize
data class GameResult(
    val battle: Int,
    val result: BattleStatus,
    val autobotsStatus: List<Pair<Transformer, FighterStatus>> = emptyList(),
    val decepticonsStatus: List<Pair<Transformer, FighterStatus>> = emptyList()
) : Parcelable
