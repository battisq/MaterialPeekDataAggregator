import parser.Parser
import parser.models.Book
import parser.models.ChapterLink
import test_data.testTextArray
import translate.TranslateHelper
import utils.FileUtils.BOOK_JSON_PATH
import utils.FileUtils.CHAPTER_LIST_JSON_PATH
import utils.FileUtils.JSON_FILE_PATH
import utils.FileUtils.PAGE_LINK_LIST_JSON_PATH
import utils.FileUtils.PAGE_LIST_JSON_PATH
import utils.FileUtils.loadFromFile
import utils.FileUtils.saveToFile
import utils.JsonUtils.fromJsonList
import utils.JsonUtils.toJsonString
import utils.KotlinUtils
import utils.KotlinUtils.log

fun main() {
    loadBook()
}

fun Book.saveBook() {
    val bookJson = toJsonString()
    log("bookJson")
    val pageLinkJson = pageLink.toJsonString()
    log("pageLinkJson")
    val chapterLinkJson = chapterLink.toJsonString()
    log("chapterLinkJson")
    val pageListJson = pageList.toJsonString()
    log("pageListJson")

    saveToFile(bookJson, BOOK_JSON_PATH)
    log("saveToFile(bookJson")

    saveToFile(pageLinkJson, PAGE_LINK_LIST_JSON_PATH)
    log("saveToFile(pageLinkJson")

    saveToFile(chapterLinkJson, CHAPTER_LIST_JSON_PATH)
    log("saveToFile(chapterLinkJson")

    saveToFile(pageListJson, PAGE_LIST_JSON_PATH)
    log("saveToFile(pageListJson")

}

fun loadBook() {
    val book = Parser.getBook()
    log("book")

    book.saveBook()
}

fun printFirstPageLinkList() {
    val json = loadFromFile(JSON_FILE_PATH)
    val firstPage = json.fromJsonList<ChapterLink>()
    println(firstPage[0].toString())
}

fun translateParsedText(vararg textBlock: String) {
    val translateHelper = TranslateHelper()
    val translatedText = translateHelper.translate(*testTextArray)
    println(translatedText)
}