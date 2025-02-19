package org.unitedinternet.azubi

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "matches")
data class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",

    var date: LocalDate? = null,

    @OneToMany(mappedBy = "match", cascade = [CascadeType.ALL], orphanRemoval = true)
    val scores: MutableList<MatchScore> = mutableListOf()
) {
    fun addScore(score: MatchScore) {
        require(scores.size < 4) { "A match can have at most 4 scores." }
        scores.add(score)
    }

    fun calculateTeamGoals(team: Team): Int {
        return scores.filter { it.team == team }.sumOf { it.goalsScored }
    }

    fun calculateGoalsConceded(player: Player): Int {
        val playerTeam = scores.find { it.player == player }?.team ?: return 0
        val opponentTeam = if (playerTeam == Team.TEAM_1) Team.TEAM_2 else Team.TEAM_1
        return calculateTeamGoals(opponentTeam)
    }
}
