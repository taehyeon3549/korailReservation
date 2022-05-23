package com.taehyeon.korail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KorailReservationApplication

fun main(args: Array<String>) {
    runApplication<KorailReservationApplication>(*args)
}
