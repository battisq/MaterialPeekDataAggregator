package utils

import org.jsoup.nodes.Document

object JsoupUtils {
    fun Document.selectXpathOrNull(xpath: String) = selectXpath(xpath).takeIf { el -> el.isNotEmpty() }
}