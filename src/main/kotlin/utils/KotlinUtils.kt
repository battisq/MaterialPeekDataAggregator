package utils

object KotlinUtils {
    inline fun <T, R> withUnit(receiver: T, block: T.() -> R): Unit {
        with(receiver, block)
    }
}