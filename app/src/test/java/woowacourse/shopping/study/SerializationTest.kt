package woowacourse.shopping.study

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SerializationTest {
    @Serializable
    data class Profile(
        val name: String,
        val age: Int,
    )

    @Test
    fun serialize() {
        val expected = """
            {"name":"dino","age":28}
        """.trimIndent()

        val profile = Profile("dino", 28)
        val actual = Json.encodeToString(profile)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun deserialize() {
        val expected = Profile("dino", 28)

        val jsonString = """
            {"name":"dino","age":28}
        """.trimIndent()
        val actual = Json.decodeFromString<Profile>(jsonString)

        assertThat(actual).isEqualTo(expected)
    }
}
