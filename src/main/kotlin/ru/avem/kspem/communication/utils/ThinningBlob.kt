package ru.avem.stand.utils

data class ThinningBlob(
    var counter: Long = 0L,
    val thinning: Long = 10L
) {
    fun itsTime() = (counter++ % thinning) == 0L
}
