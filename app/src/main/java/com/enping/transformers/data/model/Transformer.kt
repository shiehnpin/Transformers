package com.enping.transformers.data.model

import com.google.gson.annotations.SerializedName

enum class Team(val key: String) {
    Autobots("A"), Decepticons("D")
}

data class Transformer(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("strength")
    val strength: Int = MIN_RANK,
    @SerializedName("intelligence")
    val intelligence: Int = MIN_RANK,
    @SerializedName("speed")
    val speed: Int = MIN_RANK,
    @SerializedName("endurance")
    val endurance: Int = MIN_RANK,
    @SerializedName("rank")
    val rank: Int = MIN_RANK,
    @SerializedName("courage")
    val courage: Int = MIN_RANK,
    @SerializedName("firepower")
    val firepower: Int = MIN_RANK,
    @SerializedName("skill")
    val skill: Int = MIN_RANK,
    @SerializedName("team")
    val team: String = "",
    @SerializedName("team_icon")
    val teamIcon: String = ""
) {
    val rating: Int
        get() = strength +
                intelligence +
                speed +
                endurance +
                firepower

    companion object {
        const val MAX_RANK = 10
        const val MIN_RANK = 1

        fun create(
            name: String,
            strength: Int = MIN_RANK,
            intelligence: Int = MIN_RANK,
            speed: Int = MIN_RANK,
            endurance: Int = MIN_RANK,
            rank: Int = MIN_RANK,
            courage: Int = MIN_RANK,
            firepower: Int = MIN_RANK,
            skill: Int = MIN_RANK,
            team: Team
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
                "", name,
                strength, intelligence, speed, endurance,
                rank, courage, firepower, skill, team.key
            )
        }
    }
}