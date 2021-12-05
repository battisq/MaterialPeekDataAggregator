package utils

import kotlinx.coroutines.*
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

    suspend fun <A, B> Iterable<A>.mapAsync(f: suspend (A, CoroutineScope) -> B): List<B> = asyncMapTo(f).awaitAll()

    suspend fun <A, B> Iterable<A>.mapIndexedAsync(f: suspend (Int, A, CoroutineScope) -> B): List<B> = coroutineScope {
        mapIndexed { index, item ->
            async { f(index, item, this) }
        }.awaitAll()
    }

    private suspend fun <A, B> Iterable<A>.asyncMapTo(f: suspend (A, CoroutineScope) -> B) = coroutineScope {
        map { async { f(it, this) } }
    }

//    suspend fun <A, B> Iterable<A>.flatMapAsync(
//        f: suspend (A) -> Iterable<B>
//    ): List<B> = asyncMapTo(f).flatMapIndexed { index: Int, deferred: Deferred<Iterable<B>> ->
//        log("flatMapAsync index = $index")
//        deferred.await()
//    }
}