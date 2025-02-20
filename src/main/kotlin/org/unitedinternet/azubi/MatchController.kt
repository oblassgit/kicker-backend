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

        updatePlayerStats(matchInDB)
        val stored = matchRepository.save(matchInDB)
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

        updatePlayerStats(match)
        return ResponseEntity.ok(matchRepository.save(match))
    }

    @DeleteMapping("/{id}")
    fun deleteMatch(@PathVariable id: String): ResponseEntity<Void> {
        return if (matchRepository.existsById(id)) {
            revertPlayerStats(matchRepository.findById(id).get())
            matchRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun updatePlayerStats(match: Match) {
        val team1Goals = match.calculateTeamGoals(Team.TEAM_1)
        val team2Goals = match.calculateTeamGoals(Team.TEAM_2)

        val winningTeam = when {
            team1Goals > team2Goals -> Team.TEAM_1
            team2Goals > team1Goals -> Team.TEAM_2
            else -> null // Shouldn't happen, since draws aren't allowed
        }

        match.scores.forEach { score ->
            val player = score.player
            player.goalsScored += score.goalsScored
            player.goalsConceded += match.calculateGoalsConceded(player)

            when (score.team) {
                winningTeam -> {
                    player.wins += 1
                }
                else -> {
                    player.losses += 1
                }
            }

            playerRepository.save(player)
        }
    }

    private fun revertPlayerStats(match: Match?) {
        match?.scores?.forEach { score ->
            val player = score.player
            player.goalsScored -= score.goalsScored
            player.goalsConceded -= match.calculateGoalsConceded(player)

            if (match.calculateTeamGoals(score.team) > match.calculateTeamGoals(if (score.team == Team.TEAM_1) Team.TEAM_2 else Team.TEAM_1)) {
                player.wins -= 1
            } else {
                player.losses -= 1
            }

            playerRepository.save(player)
        }
    }
}


