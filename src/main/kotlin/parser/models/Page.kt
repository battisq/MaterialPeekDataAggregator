package parser.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Page(
    @JsonProperty("header")
    val header: String,
    @JsonProperty("text")
    val text: String,
    @JsonProperty("nextLink")
    val nextLink: String?,
    @JsonProperty("previousLink")
    val previousLink: String?
)