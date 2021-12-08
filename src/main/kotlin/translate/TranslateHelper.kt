package translate

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import translate.type.Translator
import utils.KotlinUtils.log

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
        translator.forceFirstSetUpSite(driver)
        input = translator.getInput(driver)
    }

    private fun Translator.forceFirstSetUpSite(driver: WebDriver, count: Int = 1) {
        try {
            firstSetUpSite(driver)
        } catch (ex: Exception) {
            Thread.sleep(10000)
            forceFirstSetUpSite(driver, count + 1)
        }
    }

    fun translate(vararg translatableBlocks: String, separator: String = ""): String = with(driver) {
        if (isFirstTranslation) setupFirstTranslation()

        val filteredBlocks = translatableBlocks.filter { block -> block.isNotBlank() }
        var translatedBlocks = ""

        runCatching {
            translatedBlocks = filteredBlocks.fold(StringBuffer(translatableBlocks.size)) { acc, block ->
                input.clear()

                input.sendKeys(block, Keys.ENTER)
                output = translator.getOutput(driver)
                output.takeIf { it.isDisplayed }?.clear()
                output.waitValidState(block)

                val outputCode = output.getAttribute("innerHTML")
                val translationText = getTranslationText(outputCode)

                isFirstTranslation = false
                output.clear()

                acc.append(translationText)
                    .append(separator)
            }.toString()
        }.onFailure {
            println(it.stackTrace)
        }

        return translatedBlocks
    }

    private fun WebElement.waitValidState(block: String) {
        while (!isDisplayed || block.isNotBlank() && text.isBlank()) {
            Thread.sleep(100)
//            log("After sleep(100)")
        }
    }

    private fun getTranslationText(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.select("span").first()!!.text()
    }
}