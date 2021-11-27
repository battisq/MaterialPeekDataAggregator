package translate

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import translate.type.Translator

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

    private fun setupFirstTranslation() {
        driver.get(translator.url)
        translator.firstSetUpSite(driver)
        input = translator.getInput(driver)
    }

    fun translate(vararg translatableBlocks: String): String = with(driver) {
        if (isFirstTranslation) setupFirstTranslation()

        var translatedBlocks = ""

        runCatching {
            translatedBlocks = translatableBlocks.fold(StringBuffer()) { acc, s ->
                input.clear()
                input.sendKeys(s, Keys.ENTER)

                if (isFirstTranslation)
                    output = translator.getOutput(driver)

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
}