import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver


class TranslateHelper(private val translator: Translator = Translator.getDefault()) {
    private val driver: WebDriver = ChromeDriver()
    private var isFirstTranslation: Boolean = true
    private lateinit var input: WebElement
    private lateinit var output: WebElement

    init {
        System.setProperty("webdriver.chrome.driver", "chromedriver")
    }

    private fun WebDriver.longOpenYandexTranslator() {
        get("https://www.google.com/")
        Thread.sleep(1000)
        findElement(By.xpath("//input[@class='gLFyf gsfi']"))
            .sendKeys(translator.url, Keys.ENTER)
        Thread.sleep(1000)

        findElement(By.className("yuRUbf"))
            .also { Thread.sleep(1000) }
            .click()
    }

    private fun setupFirstTranslation() = with(driver) {
        get(translator.url)

        findElement(By.xpath("//button[@id='srcLangButton']")).click()
        findElement(By.xpath("//div[@data-value='zh']")).click()

        input = findElement(By.xpath("//div[@id='fakeArea']"))
    }

    fun translate(vararg translatableBlocks: String): String = with(driver) {
        if (isFirstTranslation) setupFirstTranslation()

        var translatedBlocks = ""

        runCatching {
            translatedBlocks = translatableBlocks.fold(StringBuffer()) { acc, s ->
                input.clear()
                input.sendKeys(s, Keys.ENTER)

                if (isFirstTranslation)
                    output = findElement(By.xpath("//pre[@id='translation']"))

                output.waitVisibility()
                val outputCode = output.getAttribute("innerHTML")
                val translationText = getTranslationText(outputCode)

                isFirstTranslation = false

                acc.append(translationText)
            }.toString()
        }.onFailure {
            println(it.stackTrace)
        }

        return translatedBlocks
    }

    private fun WebElement.waitVisibility() {
        while (!isDisplayed) {
            Thread.sleep(10)
        }
    }

    private fun getTranslationText(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.select("span").first()!!.text()
    }

    enum class Translator(val url: String) {
        YANDEX("https://translate.yandex.ru/");
        
        companion object {
            fun getDefault() = YANDEX
        }
    }
}