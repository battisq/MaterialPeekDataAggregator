package parser

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import parser.entity.Chapter
import parser.entity.ChapterMatch
import parser.entity.PageMatch
import parser.entity.Book
import parser.models.ChapterLink
import parser.models.Page
import parser.entity.PageLink
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
        log("After bookName & bookPreviewLink")

        val pageLinkList = site.getPageLinkList()
        log("load pageLinkList; size = ${pageLinkList.size}")

        val chapterList = ArrayList<Chapter>(7000)
        val pageMatchList = ArrayList<PageMatch>(7000)

        Flowable.fromIterable(pageLinkList)
            .parallel(pageLinkList.size)
            .runOn(Schedulers.computation())
            .concatMap { Flowable.just(it) }
            .map { pageLink ->
                (pageLink.ordinal to pageLink.parsePage()).also {
                    log("Parsed ${pageLink.ordinal}")
                }
            }
            .sequential()
            .blockingIterable().let { pageLink ->
                pageLink.sortedBy { it.first }
                    .map { it.second }
                    .forEach { pageData ->
                        chapterList.addAll(pageData.first)
                        pageMatchList.addAll(pageData.second)
                    }
            }

//        pageLinkList.forEach { pageLink ->
//            val pageData = pageLink.parsePage()
//            chapterList.addAll(pageData.first)
//            pageMatchList.addAll(pageData.second)
//        }

        val chapterMatchList = chapterList.getChapterMatchList()

        log("pageList")

        return Book(
            bookName,
            bookPreviewLink,
            pageLinkList,
            pageMatchList,
            chapterList,
            chapterMatchList
        )
    }

    private fun PageLink.parsePage(): Pair<List<Chapter>, List<PageMatch>> {
        val pageSite = Jsoup.connect(link).get()
        val chapterLinkList = pageSite.getChapterLinkListFromPage()
        log("Parsed chapterLinkList in page[$ordinal]; count = ${chapterLinkList.size}")

        val chapterList = ArrayList<Chapter>(1000)
        val pageMatchList = ArrayList<PageMatch>(1000)

        chapterLinkList.forEach { chapterLink ->
            val chapterSite = Jsoup.connect(chapterLink.link).get()
            val page = chapterSite.getPageObject()
            log("Parsed page ${page.header}")

            Chapter(
                chapterLink.id.toInt(),
                chapterLink.link,
                page.header,
                page.text
            ).let(chapterList::add)

            PageMatch(
                chapterLink.id.toInt(),
                link
            ).let(pageMatchList::add)
        }

        log("Parsed all chapters in page[$ordinal]")

        return chapterList to pageMatchList
    }

    private fun List<Chapter>.getChapterMatchList() = mapIndexed { index, chapter ->
        ChapterMatch(
            chapter.id,
            takeIf { index > 0 }?.get(index - 1)?.link,
            takeIf { index < lastIndex }?.get(index + 1)?.link
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

    private fun Document.getChapterLinkListFromPage(): List<ChapterLink> {
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