import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    fun <T : Any> T.toJsonString(): String =
        ObjectMapper().writeValueAsString(this)

    @Throws(JsonProcessingException::class, JsonMappingException::class)
    inline fun <reified T : Any> String.fromJson(): T =
        ObjectMapper().readValue(this, T::class.java)
}