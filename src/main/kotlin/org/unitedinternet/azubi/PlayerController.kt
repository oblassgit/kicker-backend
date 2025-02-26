package org.unitedinternet.azubi

import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.dialect.lock.OptimisticEntityLockException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/players")
@Tag(name = "Player API")
class PlayerController(val playerRepository: PlayerRepository, val matchRepository: MatchRepository) {

    @GetMapping
    fun getAllPlayers(): List<Player> = playerRepository.findAll()

    @GetMapping("/{id}")
    fun getPlayerById(@PathVariable id: String): Player? = playerRepository.findById(id).orElse(null)

    @GetMapping("/{id}/matches")
    fun getMatchesForPlayerById(@PathVariable id: String): List<Match> {
        return matchRepository.findMatchesByPlayerId(id)
    }

    @PostMapping
    @Transactional
    fun createPlayer(@RequestBody player: Player): Player {
        try {
            if (player.id != null) {
                throw IllegalArgumentException("New players should not have an ID")
            }
            return playerRepository.save(player)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw OptimisticEntityLockException(player, "The player was updated by another transaction. Please refresh and try again.")
        }
    }

    @PutMapping("/{id}")
    fun updatePlayer(@PathVariable id: String, @RequestBody updatedPlayer: Player): Player {
        val existingPlayer = playerRepository.findById(id).orElseThrow {
            throw NoSuchElementException("Player with id $id not found")
        }

        existingPlayer.name = updatedPlayer.name
        existingPlayer.wins = updatedPlayer.wins
        existingPlayer.losses = updatedPlayer.losses
        existingPlayer.goalsScored = updatedPlayer.goalsScored
        existingPlayer.goalsConceded = updatedPlayer.goalsConceded

        return playerRepository.save(existingPlayer)
    }

    @DeleteMapping("/{id}")
    fun deletePlayer(@PathVariable id: String) = playerRepository.deleteById(id)
}
