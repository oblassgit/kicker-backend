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
        get() = wins * 3
}
