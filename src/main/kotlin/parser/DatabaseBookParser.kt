package parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import parser.entity.ChapterMatch
import parser.models.Book
import parser.models.ChapterLink
import parser.models.Page
import parser.models.PageLink
import utils.JsoupUtils.selectXpathOrNull
import utils.KotlinUtils.log
import java.util.*
import kotlin.collections.ArrayList

object DatabaseBookParser {
    private const val BASE_URL = "https://sj.uukanshu.com/"

    fun getBook(): Book {
        val site = Jsoup.connect("https://sj.uukanshu.com/book.aspx?id=439").get()
        log("connect")
        val bookName = site.getBookName()
        val bookPreviewLink = "https://img.uukanshu.com/fengmian/2012/10/634865275395707500.jpg"
        log("bookName")

        val pageLinkList = site.getPageLinkList()
        log("pageLinkList")
        val chapterMatchList = ArrayList<ChapterMatch>(pageLinkList.size)
        val chapterAllLink = pageLinkList
            .flatMap { pageLink ->
                val page = Jsoup.connect(pageLink.link).get()
                page.getChaptersFromPage()
            }
            .also { log("chapterAllLink.flatMap") }

        log("chapterAllLink")

        val pageList = chapterAllLink.mapIndexed { index, chapter ->
            val page = Jsoup.connect(chapter.link).get()
            page.getPageObject()
                .also { log("chapterAllLink[$index]") }
        }

        log("pageList")

        return Book(
            bookName,
            pageLinkList,
            chapterAllLink,
            pageList
        )
    }

    private fun Document.getBookName(): String =
        selectXpath("//h1[@class='bookname']")
            .text()

    private fun Document.getPageObject(): Page {
        val header = selectXpath("//h3").text()
        val previousPageLink = selectXpathOrNull("//a[@id='read_pre']")?.getLink()
        val nextPageLink = selectXpathOrNull("//a[@id='read_next']")?.getLink()

        val chapterText = selectXpath("//div[@class='rp-article bookContent uu_cont']")
            .eachText()
            .joinToString(separator = "\n\n")
            .replace(
                Regex("\\s\\s\\s"),
                "\n\n"
            )

        return Page(
            header,
            chapterText,
            nextPageLink,
            previousPageLink
        )
    }

    private fun Elements.getLink() = BASE_URL + attr("href")

    @Throws(IllegalArgumentException::class)
    private fun Document.getPageLinkList(): List<PageLink> {
        val parentPageList = selectFirst("div[class='pages clear']")
            ?: throw IllegalArgumentException("Non Material Peak Page")

        val linkingPage = parentPageList.select("a")
            .apply { removeLast() }

        val pageListWithoutFirst = linkingPage.map { el ->
            val link = el.attr("href")
            PageLink(
                el.text().toInt(),
                BASE_URL + link
            )
        }.let(::LinkedList)

        val firstLink = pageListWithoutFirst.first.link.replace(
            "page=2",
            "page=1"
        )

        val firstPage = PageLink(
            1,
            firstLink
        )

        return pageListWithoutFirst.apply { addFirst(firstPage) }
    }

    private fun Document.getChaptersFromPage(): List<ChapterLink> {
        val chapterList = select("div.ml-list")
            .select("ul[id=chapterList]")
            .select("li")

        return chapterList.fold(ArrayList(chapterList.size)) { acc, el ->
            val a = el.select("a")
            if (a.size == 0) return@fold acc

            val linkHref = a.attr("href")
            val linkId = a.attr("name")
            val name = a.text()


            /**
             * linkHref - link of chapter
             * linkId - id of the chapter which is contained in the link
             * name - name of chapter
             */
            val chapterLink = ChapterLink(
                BASE_URL + linkHref,
                linkId,
                name
            )

            acc.apply { add(chapterLink) }
        }
    }
}