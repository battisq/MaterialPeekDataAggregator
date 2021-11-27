import parser.Parser
import test_data.testTextArray
import translate.TranslateHelper
import utils.JsonUtils.fromJson
import utils.JsonUtils.toJsonString
import java.io.File

const val JSON_FILE_PATH = "/Users/battisq/Data/Projects/Develop/MaterialPeakDataAggregator/src/main/kotlin/FirstPage.json"
const val HTML_FILE_PATH = "/Users/battisq/Data/Projects/Develop/MaterialPeakDataAggregator/src/main/kotlin/html_file.html"

fun saveToFile(fileText: String, path: String) {
    val file = File(path)
    file.writeText(fileText)
}

fun loadFromFile(path: String): String {
    val file = File(path)
    return file.readText()
}

fun main() {
    val translateHelper = TranslateHelper()
    val translatedText = translateHelper.translate(*testTextArray)
    println(translatedText)
//    secondStrategy()
}

fun firstStrategy() {
    val firstPage = Parser.getFirstPage()
    val json = firstPage.toJsonString()

    saveToFile(json, JSON_FILE_PATH)
}

fun secondStrategy() {
    val json = loadFromFile(JSON_FILE_PATH)
    val firstPage = json.fromJson<List<Parser.Page>>()
    println(firstPage)
}