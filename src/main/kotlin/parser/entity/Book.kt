package parser.entity

class Book(
    val name: String,
    val previewLink: String,
    val pageLinkList: List<PageLink>,
    val pageMatchList: List<PageMatch>,
    val chapterList: List<Chapter>,
    val chapterMatchList: List<ChapterMatch>
)