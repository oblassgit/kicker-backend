package org.unitedinternet.azubi


import jakarta.persistence.*

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
    val basePoints = wins - losses // Standard points for wins
    val goalDifferenceBonus = (goalsScored - goalsConceded).coerceAtLeast(0) / 5  // Bonus for goal difference every 5 goals
    val totalPoints = basePoints + goalDifferenceBonus

    return if (totalPoints >= 0) {
        totalPoints
    } else {
        0
    }
}

