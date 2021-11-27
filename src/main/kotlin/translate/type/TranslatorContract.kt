package translate.type

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

interface TranslatorContract {
    fun getLanguageUrlIdentifier(language: Language): String
    fun firstSetUpSite(driver: WebDriver)
    fun getInput(driver: WebDriver): WebElement
    fun getOutput(driver: WebDriver): WebElement
}