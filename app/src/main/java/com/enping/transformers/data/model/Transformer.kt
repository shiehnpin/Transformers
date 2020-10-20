package com.enping.transformers.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

enum class Team(val key: String) {
    Autobots("A"), Decepticons("D")
}

@Entity(tableName = "transformer")
data class Transformer(

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String = "",

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String = "",

    @ColumnInfo(name = "strength")
    @SerializedName("strength")
    val strength: Int = MIN_RANK,

    @ColumnInfo(name = "intelligence")
    @SerializedName("intelligence")
    val intelligence: Int = MIN_RANK,

    @ColumnInfo(name = "speed")
    @SerializedName("speed")
    val speed: Int = MIN_RANK,

    @ColumnInfo(name = "endurance")
    @SerializedName("endurance")
    val endurance: Int = MIN_RANK,

    @ColumnInfo(name = "rank")
    @SerializedName("rank")
    val rank: Int = MIN_RANK,

    @ColumnInfo(name = "courage")
    @SerializedName("courage")
    val courage: Int = MIN_RANK,

    @ColumnInfo(name = "firepower")
    @SerializedName("firepower")
    val firepower: Int = MIN_RANK,

    @ColumnInfo(name = "skill")
    @SerializedName("skill")
    val skill: Int = MIN_RANK,

    @ColumnInfo(name = "team")
    @SerializedName("team")
    val team: String = "",

    @ColumnInfo(name = "team_icon")
    @SerializedName("team_icon")
    val teamIcon: String = ""
) {
    val overAllRating: Int
        get() = strength +
                intelligence +
                speed +
                endurance +
                firepower

    val enumTeam: Team
        get() = when (team) {
            Team.Autobots.key -> Team.Autobots
            Team.Decepticons.key -> Team.Decepticons
            else -> error("team must be either Autobots or Decepticons")
        }

    companion object {
        const val MAX_RANK = 10
        const val MIN_RANK = 1

        const val OptimusPrime = "Optimus Prime"
        const val Predaking = "Predaking"

        fun create(
            name: String = "",
            strength: Int = MIN_RANK,
            intelligence: Int = MIN_RANK,
            speed: Int = MIN_RANK,
            endurance: Int = MIN_RANK,
            rank: Int = MIN_RANK,
            courage: Int = MIN_RANK,
            firepower: Int = MIN_RANK,
            skill: Int = MIN_RANK,
            team: Team = Team.Autobots,
            id: String = ""
        ): Transformer {

            listOf(
                strength,
                intelligence,
                speed,
                endurance,
                rank,
                courage,
                firepower,
                skill
            ).forEach { rating ->
                check(rating in MIN_RANK..MAX_RANK) {
                    "rating is out of range[1..10]"
                }
            }

            return Transformer(
                id, name,
                strength, intelligence, speed, endurance,
                rank, courage, firepower, skill, team.key
            )
        }
    }
}