package org.unitedinternet.azubi


import jakarta.persistence.*
import kotlin.math.roundToInt


@Entity
class Player (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,
    var name: String? = null,
    var wins: Int = 0,
    var losses: Int = 0,
    var goalsScored: Int = 0,
    var goalsConceded: Int = 0,
) {

    val totalGames: Int
        get() = wins + losses

    val points: Int
        get() = calculatePoints(wins, losses, goalsConceded, goalsScored)
}

private fun calculatePoints(wins: Int, losses: Int, goalsConceded: Int, goalsScored: Int): Int {
    if (goalsConceded == 0) {
        return (wins - losses) * goalsScored // Avoid division by zero
    }

    val points: Double = (wins.toDouble() - losses.toDouble()) * (goalsScored.toDouble() / goalsConceded.toDouble())
    return if (points >= 0) points.roundToInt() else 0
}

