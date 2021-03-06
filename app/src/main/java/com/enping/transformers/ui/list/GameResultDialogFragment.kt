package com.enping.transformers.ui.list

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.enping.transformers.data.BattleStatus
import com.enping.transformers.data.FighterStatus
import com.enping.transformers.data.GameResult
import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import kotlinx.android.parcel.Parcelize

class GameResultDialogFragment : DialogFragment() {

    @Parcelize
    data class ParcelGameResult(
        val battle: Int,
        val result: BattleStatus,
        val autobotsStatus: List<Pair<Transformer, FighterStatus>> = emptyList(),
        val decepticonsStatus: List<Pair<Transformer, FighterStatus>> = emptyList()
    ): Parcelable

    companion object {
        const val RESULT = "result"
        fun show(fragmentManager: FragmentManager, gameResult: GameResult) {
            val dialogFragment = GameResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, ParcelGameResult(
                        gameResult.battle,
                        gameResult.result,
                        gameResult.autobotsStatus,
                        gameResult.decepticonsStatus
                    ))
                }
            }
            dialogFragment.show(fragmentManager, "war_result")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val gameResult = requireArguments().getParcelable<ParcelGameResult>(RESULT)!!

        return AlertDialog.Builder(requireContext())
            .setTitle("War Result")
            .setMessage(createReadableGameResult(gameResult))
            .setPositiveButton("OK") { _, _ -> }
            .create()
    }

    private fun createReadableGameResult(gameResult: ParcelGameResult): String {
        val battlesMessage = "${gameResult.battle} battle(s)"
        val detailMessage = when (gameResult.result) {
            BattleStatus.AUTOBOTS_WIN,
            BattleStatus.DECEPTICONS_WIN -> {
                val (winner, winnerTeam) =
                    if (gameResult.result == BattleStatus.AUTOBOTS_WIN) {
                        Team.Autobots to gameResult.autobotsStatus
                    } else {
                        Team.Decepticons to gameResult.decepticonsStatus
                    }
                val (loser, loserTeam) =
                    if (gameResult.result == BattleStatus.AUTOBOTS_WIN) {
                        Team.Decepticons to gameResult.decepticonsStatus
                    } else {
                        Team.Autobots to gameResult.autobotsStatus
                    }
                createWinnerLoserReadableMessage(winner, winnerTeam, loser, loserTeam)
            }
            else -> {
                createTieReadableMessage(gameResult.autobotsStatus, gameResult.decepticonsStatus)
            }
        }
        return listOf(battlesMessage, detailMessage).joinToString("\n")
    }

    private fun createTieReadableMessage(
        autobotsTeam: List<Pair<Transformer, FighterStatus>>,
        decepticonsTeam: List<Pair<Transformer, FighterStatus>>
    ): String {
        val autobotsSurvivors =
            autobotsTeam
                .filter { status ->
                    status.second in setOf(FighterStatus.VICTOR, FighterStatus.SKIP)
                }
                .joinToString(",") { p -> p.first.name }

        val decepticonsSurvivors =
            decepticonsTeam
                .filter { status ->
                    status.second in setOf(FighterStatus.VICTOR, FighterStatus.SKIP)
                }
                .joinToString(",") { p -> p.first.name }

        val summaryMessage = "The game is a tie."

        val autobotsMessage = if (autobotsSurvivors.isBlank()) {
            "No survivors from the (${Team.Autobots.name}) team."
        } else {
            "Survivors from the the (${Team.Autobots.name}) team: $autobotsSurvivors."
        }

        val decepticonsMessage = if (decepticonsSurvivors.isBlank()) {
            "No survivors from the (${Team.Decepticons.name}) team."
        } else {
            "Survivors from the the (${Team.Decepticons.name}) team: $decepticonsSurvivors."
        }

        return listOf(summaryMessage, autobotsMessage, decepticonsMessage).joinToString("\n")
    }

    private fun createWinnerLoserReadableMessage(
        winner: Team,
        winnerTeam: List<Pair<Transformer, FighterStatus>>,
        loser: Team,
        loserTeam: List<Pair<Transformer, FighterStatus>>
    ): String {
        val winnerSurvivors =
            winnerTeam
                .filter { status ->
                    status.second in setOf(FighterStatus.VICTOR, FighterStatus.SKIP)
                }
                .joinToString(",") { p -> p.first.name }

        val loserSurvivors =
            loserTeam
                .filter { status ->
                    status.second in setOf(FighterStatus.VICTOR, FighterStatus.SKIP)
                }
                .joinToString(",") { p -> p.first.name }

        val summaryMessage = "The winner goes to ${winner.name}."

        val winnerMessage = "Survivors from the winning team (${winner.name}): $winnerSurvivors."

        val loserMessage = if (loserSurvivors.isBlank()) {
            "No survivors from the losing team (${loser.name})."
        } else {
            "Survivors from the losing team (${loser.name}): $loserSurvivors."
        }

        return listOf(summaryMessage, winnerMessage, loserMessage).joinToString("\n")
    }

}