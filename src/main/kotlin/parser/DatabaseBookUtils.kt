package parser

import parser.entity.Book
import utils.FileUtils
import utils.JsonUtils.toJsonString
import utils.KotlinUtils

object DatabaseBookUtils {

    private const val RESULT = "result_database"
    private const val PART_OF_DATA = "part_of_data"

    private const val CHINA = "china"
    private const val RUSSIAN = "russian"

    const val BOOK_JSON_PATH = "${FileUtils.PROJECT_DIR}/$RESULT/$CHINA/BookJson.json"
    const val PAGE_LINK_LIST_JSON_PATH = "${FileUtils.PROJECT_DIR}/$RESULT/$CHINA/PageLinkListJson.json"
    const val PAGE_MATCH_LIST_JSON_PATH = "${FileUtils.PROJECT_DIR}/$RESULT/$CHINA/PageMatchListJson.json"
    const val CHAPTER_LIST_JSON_PATH = "${FileUtils.PROJECT_DIR}/$RESULT/$CHINA/ChapterListJson.json"
    const val CHAPTER_MATCH_LIST_JSON_PATH = "${FileUtils.PROJECT_DIR}/$RESULT/$CHINA/ChapterMatchListJson.json"

    fun t() {
        "".let {
            FileUtils.saveToFile(it, BOOK_JSON_PATH)
        }
    }

    fun Book.saveBook() {
        val bookJson = toJsonString()
        KotlinUtils.log("bookJson")
        val pageLinkJson = pageLinkList.toJsonString()
        KotlinUtils.log("pageLinkJson")
        val pageMatchList = pageMatchList.toJsonString()
        KotlinUtils.log("pageLinkJson")
        val chapterListJson = chapterList.toJsonString()
        KotlinUtils.log("chapterLinkJson")
        val chapterMatchListJson = chapterMatchList.toJsonString()
        KotlinUtils.log("chapterLinkJson")

        FileUtils.saveToFile(bookJson, BOOK_JSON_PATH)
        KotlinUtils.log("saveToFile(bookJson)")

        FileUtils.saveToFile(pageLinkJson, PAGE_LINK_LIST_JSON_PATH)
        KotlinUtils.log("saveToFile(pageLinkJson)")

        FileUtils.saveToFile(pageMatchList, PAGE_MATCH_LIST_JSON_PATH)
        KotlinUtils.log("saveToFile(pageMatchList)")

        FileUtils.saveToFile(chapterListJson, CHAPTER_LIST_JSON_PATH)
        KotlinUtils.log("saveToFile(chapterLinkJson)")

        FileUtils.saveToFile(chapterMatchListJson, CHAPTER_MATCH_LIST_JSON_PATH)
        KotlinUtils.log("saveToFile(chapterMatchListJson)")
    }
}