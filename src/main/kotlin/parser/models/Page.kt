package parser.models

class Page(
    val header: String,
    val text: String,
    val nextLink: String?,
    val previousLink: String?
)