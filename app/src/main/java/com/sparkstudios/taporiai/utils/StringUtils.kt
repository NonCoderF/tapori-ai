package com.sparkstudios.taporiai.utils


fun generateRandomString(length: Int = 16): String {
    val chars = ('a'..'z') + ('A'..'Z') // letters only
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}