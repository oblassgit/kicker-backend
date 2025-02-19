package org.unitedinternet.azubi

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchRepository: MatchRepository, private val playerRepository: PlayerRepository) {

    @GetMapping
    fun getAllMatches(): List<Match> = matchRepository.findAll()

    @GetMapping("/{id}")
    fun getMatchById(@PathVariable id: String): Match? = matchRepository.findById(id).orElse(null)

    @GetMapping("/{id}/score")
    fun getAllMatchScoresForMatch(@PathVariable id: String): List<MatchScore> {
        val match = matchRepository.findById(id)
        return match.get().scores
    }

    @PostMapping
    fun createMatch(@RequestBody match: Match): ResponseEntity<Any> {

        if (match.scores.size !in 2..4) {
            return ResponseEntity.badRequest().body(null)
        }

        val matchInDB = Match(date = match.date)

        // Convert request scores into entities
        match.scores.forEach { scoreRequest ->
            val player = scoreRequest.player.id?.let { playerRepository.findById(it).orElse(null) }
                ?: return ResponseEntity.badRequest().body("The player with id: ${scoreRequest.player.id} could not be found!")

            val matchScore = MatchScore(
                match = matchInDB,
                player = player,
                team = scoreRequest.team,
                goalsScored = scoreRequest.goalsScored
            )

            matchInDB.scores.add(matchScore)
        }

        val stored = matchRepository.save(matchInDB);
        return ResponseEntity.ok(stored)
    }

    @PutMapping("/score/{id}")
    fun updateMatchScores(@PathVariable id: String, @RequestBody scores: List<MatchScore>): ResponseEntity<Match> {
        val match = matchRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()

        if (scores.size !in 2..4) {
            return ResponseEntity.badRequest().body(null)
        }

        match.scores.clear() // Remove old scores
        scores.forEach { scoreRequest ->
            val player = scoreRequest.player.id?.let { playerRepository.findById(it).orElse(null) }
                ?: return ResponseEntity.badRequest().body(null)

            val matchScore = MatchScore(
                match = match,
                player = player,
                team = scoreRequest.team,
                goalsScored = scoreRequest.goalsScored
            )

            match.scores.add(matchScore)
        }

        return ResponseEntity.ok(matchRepository.save(match))
    }

    @DeleteMapping("/{id}")
    fun deleteMatch(@PathVariable id: String): ResponseEntity<Void> {
        return if (matchRepository.existsById(id)) {
            matchRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}


