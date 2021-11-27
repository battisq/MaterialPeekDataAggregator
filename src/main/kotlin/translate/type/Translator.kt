package translate.type

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import utils.KotlinUtils.withUnit

// TODO: Add multi-language + multi-translator
enum class Translator(val url: String): TranslatorContract {
    YANDEX("https://translate.yandex.ru/") {
        override fun getLanguageUrlIdentifier(language: Language): String = when(language) {
            Language.CHINE -> "zh"
        }

        override fun firstSetUpSite(driver: WebDriver) = withUnit(driver) {
            require(currentUrl.contains(url))

            findElement(By.xpath("//button[@id='srcLangButton']")).click()
            findElement(By.xpath("//div[@data-value='zh']")).click()
        }

        override fun getInput(driver: WebDriver): WebElement = with(driver) {
            require(currentUrl.contains(url))
            findElement(By.xpath("//div[@id='fakeArea']"))
        }

        override fun getOutput(driver: WebDriver): WebElement = with(driver) {
            require(currentUrl.contains(url))
            findElement(By.xpath("//pre[@id='translation']"))
        }
    };

    companion object {
        fun getDefault() = YANDEX
    }
}