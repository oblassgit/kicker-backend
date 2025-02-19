package org.unitedinternet.azubi

import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository : JpaRepository<Player, String>
