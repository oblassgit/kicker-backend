package org.unitedinternet.azubi

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MatchRepository : JpaRepository<Match, String> {
    @Query("SELECT m FROM Match m JOIN m.scores s WHERE s.player.id = :playerId")
    fun findMatchesByPlayerId(@Param("playerId") playerId: String): List<Match>
}

