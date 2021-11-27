package parser.models

class Book(
    val name: String,
    val pageLink: List<PageLink>,
    val chapterLink: List<ChapterLink>,
    val pageList: List<Page>
)