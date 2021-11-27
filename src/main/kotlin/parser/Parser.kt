package parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Parser {

    data class Page(
        val link: String,
        val id: String,
        val name: String
    )

    fun getFirstPage(): List<Page> {
        val doc = Jsoup.connect("https://sj.uukanshu.com/book.aspx?id=439").get()
        return doc.getOnePage()
    }

    private fun Document.getOnePage(): List<Page> {
        val mlList = select("div.ml-list")
        val chapterList = mlList.select("ul").select("li")

        val list = ArrayList<Page>(chapterList.size)

        for (chapter in chapterList) {
            val a = chapter.select("a")
            if (a.size == 0) continue

            val linkHref = a.attr("href")
            val linkId = a.attr("name")
            val name = a.text()

            val page = Page(
                linkHref,
                linkId,
                name
            )

            list.add(page)
        }

        return list
    }
}