package utils

object StringUtils {
    fun String.containsAny(vararg els: Any) = els.any { el -> contains(el.toString()) }

}