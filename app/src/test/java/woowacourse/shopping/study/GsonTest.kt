package woowacourse.shopping.study

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GsonTest {
    private val gson = Gson()

    data class Profile(
        @SerializedName("name")
        val name: String,
        @SerializedName("age")
        val age: Int,
    )

    @Test
    fun serialize() {
        val expected = """
            {"name":"dino","age":28}
        """.trimIndent()

        val profile = Profile("dino", 28)
        val actual = gson.toJson(profile)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun deserialize() {
        val expected = Profile("dino", 28)

        val jsonString = """
            {"name":"dino","age":28}
        """.trimIndent()
        val actual = gson.fromJson(jsonString, Profile::class.java)

        assertThat(actual).isEqualTo(expected)
    }
}