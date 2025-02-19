package org.unitedinternet.azubi

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "match_score")
data class MatchScore(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "match_id", nullable = false)
    var match: Match? = null,

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    val player: Player,

    @Enumerated(EnumType.STRING)
    val team: Team,

    val goalsScored: Int = 0
)
