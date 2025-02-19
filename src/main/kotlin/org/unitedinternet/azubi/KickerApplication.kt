package org.unitedinternet.azubi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KickerApplication

fun main(args: Array<String>) {
    runApplication<KickerApplication>(*args)
}
