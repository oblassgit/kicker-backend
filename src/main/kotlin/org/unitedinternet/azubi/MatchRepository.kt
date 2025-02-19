package org.unitedinternet.azubi

import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository : JpaRepository<Match, String>

