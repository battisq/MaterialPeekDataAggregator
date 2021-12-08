import parser.Parser
import parser.models.Book
import parser.models.ChapterLink
import parser.models.Page
import test_data.testTextArray
import translate.AsyncTranslatorHelper.asyncTranslate
import translate.TranslateHelper
import utils.FileUtils
import utils.FileUtils.BOOK_JSON_PATH
import utils.FileUtils.CHAPTER_LIST_JSON_PATH
import utils.FileUtils.JSON_FILE_PATH
import utils.FileUtils.PAGE_LINK_LIST_JSON_PATH
import utils.FileUtils.PAGE_LIST_JSON_PATH
import utils.FileUtils.PAGE_LIST_PART_OF_DATA_JSON_PATH
import utils.FileUtils.TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH
import utils.FileUtils.LAST_PART_TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH
import utils.FileUtils.LastPart
import utils.FileUtils.loadFromFile
import utils.FileUtils.saveToFile
import utils.JsonUtils.fromJsonList
import utils.JsonUtils.toJsonString
import utils.KotlinUtils.log
import java.io.File

fun main() {
   Parser.getBook()
}

fun translate() {
    runCatching {
        val allPage = File(PAGE_LIST_PART_OF_DATA_JSON_PATH)
            .readText()
            .fromJsonList<Page>()
            .filterIndexed { index, _ -> index < 500 }

        println("Count = ${allPage.size}")
        allPage.forEach {
            println(it.header)
        }

        allPage.asyncTranslate(log = ::log, onComplete = { res ->

            println("Count = ${res.size}")
            res.forEach {
                println(it.header)
            }

            saveToFile(res.toJsonString(), TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH)
            saveToFile(LastPart(res.last().header).toJsonString(), LAST_PART_TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH)
        })
    }.onFailure {
        log(it.stackTraceToString())
    }
}

fun replaceSpaceToEnter() {
    val allPage = File(PAGE_LIST_PART_OF_DATA_JSON_PATH)
        .readText()
        .fromJsonList<Page>()

    val changedListJson = allPage.map { page ->
        Page(
            page.header,
            page.text.replace("　　 ", "\n\n"),
            page.nextLink,
            page.previousLink
        )
    }.toJsonString()

    saveToFile(changedListJson, PAGE_LIST_PART_OF_DATA_JSON_PATH)

    println(allPage.size)
}

fun translatePartOfPartOfData() {
    val allPage = File(PAGE_LIST_PART_OF_DATA_JSON_PATH)
        .readText()
        .fromJsonList<Page>()

    log("allPage")

    val translateHelper = TranslateHelper()

    val translatedPart = allPage.asSequence()
        .filterIndexed { index, _ -> index < 120 }
        .mapIndexed { index, page ->
            val header = translateHelper.translate(page.header)
            val text = translateHelper.translate(
                separator = "\n\n",
                translatableBlocks = (page.text
                    .split("\n\n")
                    .toTypedArray())
            )

            log("translatedPart[$index] header = $header")

            Page(
                header,
                text,
                page.nextLink,
                page.previousLink
            )
        }.toList()

    saveToFile(translatedPart.toJsonString(), TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH)
}

fun isolateNecessary() {
    val allPage = File(PAGE_LIST_JSON_PATH)
        .readText()
        .fromJsonList<Page>()
    log("allPage")

    val partOfPage = allPage.subList(
        allPage.find { page -> page.header == "第2837章 大灾难" }!!.let(allPage::indexOf),
        allPage.lastIndex
    ).toJsonString()
    log("partOfPage")

    saveToFile(partOfPage, PAGE_LIST_PART_OF_DATA_JSON_PATH)
    log("saveToFile(partOfPage")
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

fun loadBookFromSite() {
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
    val translatedText = translateHelper.translate(translatableBlocks = testTextArray)
    println(translatedText)
}