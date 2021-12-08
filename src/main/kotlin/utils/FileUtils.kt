package utils

import java.io.File

object FileUtils {

    const val CHINA_DIVISION = "　　 "

    const val PROJECT_DIR = "/Users/battisq/Data/Projects/Develop/MaterialPeakDataAggregator/src/main/kotlin"

    const val JSON_FILE_PATH = "${PROJECT_DIR}/test_data/FirstPage.json"
    const val HTML_CHAPTER_FILE_PATH = "${PROJECT_DIR}/test_data/test_chapter_3.html"
    const val HTML_FILE_PATH = "${PROJECT_DIR}/html_file.html"

    private const val RESULT = "result"
    private const val PART_OF_DATA = "part_of_data"

    private const val CHINA = "china"
    private const val RUSSIAN = "russian"

    const val BOOK_JSON_PATH = "$PROJECT_DIR/$RESULT/$CHINA/BookJson.json"
    const val PAGE_LINK_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$CHINA/PageLinkListJson.json"
    const val PAGE_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$CHINA/PageListJson.json"
    const val CHAPTER_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$CHINA/ChapterListJson.json"

    const val RESULT_BOOK_JSON_PATH = "$PROJECT_DIR/$RESULT/$RUSSIAN/RussianBookJson.json"
    const val RESULT_PAGE_LINK_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$RUSSIAN/RussianPageLinkListJson.json"
    const val RESULT_PAGE_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$RUSSIAN/RussianPageListJson.json"
    const val RESULT_CHAPTER_LIST_JSON_PATH = "$PROJECT_DIR/$RESULT/$RUSSIAN/RussianChapterListJson.json"

    const val PAGE_LIST_PART_OF_DATA_JSON_PATH = "$PROJECT_DIR/$RESULT/$PART_OF_DATA/PageListJson.json"
    const val TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH = "$PROJECT_DIR/$RESULT/$PART_OF_DATA/TranslatedPageListJson.json"
    const val LAST_PART_TRANSLATED_PAGE_LIST_PART_OF_DATA_JSON_PATH = "$PROJECT_DIR/$RESULT/$PART_OF_DATA/VersionTranslatedPageListJson.json"

    class LastPart(val lastChapter: String)

    fun saveToFile(fileText: String, path: String) {
        val file = File(path)
        file.writeText(fileText)
    }

    fun loadFromFile(path: String): String {
        val file = File(path)
        return file.readText()
    }
}