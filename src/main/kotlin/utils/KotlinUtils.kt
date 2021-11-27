package utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.*

object KotlinUtils {
    private const val logsTimePattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"

    fun log(text: String) {
        val dateFormat = java.text.SimpleDateFormat(logsTimePattern)
        val time = dateFormat.format(Date())

        println("[$time] -> $text")
    }

    inline fun <T, R> withUnit(receiver: T, block: T.() -> R) {
        with(receiver, block)
    }

    suspend fun <A, B> Iterable<A>.asyncMap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.awaitAll()
    }
}