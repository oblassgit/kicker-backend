package org.unitedinternet.azubi

import org.springframework.data.jpa.repository.JpaRepository

interface MatchScoreRepository : JpaRepository<MatchScore, String>
