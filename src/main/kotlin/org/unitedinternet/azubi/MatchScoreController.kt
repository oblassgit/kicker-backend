package org.unitedinternet.azubi

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/match-scores")
class MatchScoreController(private val matchScoreRepository: MatchScoreRepository) {

    @GetMapping
    fun getAllMatchScores(): List<MatchScore> = matchScoreRepository.findAll()

    @GetMapping("/{id}")
    fun getMatchScoreById(@PathVariable id: String): MatchScore? = matchScoreRepository.findById(id).orElse(null)

    @PostMapping
    fun createMatchScore(@RequestBody matchScore: MatchScore): MatchScore = matchScoreRepository.save(matchScore)
}
