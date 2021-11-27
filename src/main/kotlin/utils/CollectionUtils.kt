package utils

object CollectionUtils {

    fun IntRange.toArray() = toList().toTypedArray()

    // TODO
    inline fun <T, R> Collection<T>.forEachLet(block: (T) -> R): String {
        return let {
            ""
        }
    }
}