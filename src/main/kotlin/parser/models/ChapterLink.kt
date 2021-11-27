package parser.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ChapterLink(
    @JsonProperty("link")
    val link: String,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String
)