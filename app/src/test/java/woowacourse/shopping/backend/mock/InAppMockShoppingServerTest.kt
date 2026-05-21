package woowacourse.shopping.backend.mock

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.backend.retrofit.dto.ProductResponse

class InAppMockShoppingServerTest {
    private val client = OkHttpClient()
    private val gson = Gson()

    @Test
    fun `products endpoint returns seeded mega coffee menu`() {
        val server = InAppMockShoppingServer()
        server.start()

        try {
            val request =
                Request
                    .Builder()
                    .url("${server.baseUrl}products?page=0&size=100")
                    .build()

            client.newCall(request).execute().use { response ->
                assertTrue(response.isSuccessful)

                val body = response.body.string()
                val productResponse = gson.fromJson(body, ProductResponse::class.java)

                assertEquals(78, productResponse.totalElements.toInt())
                assertEquals(78, productResponse.content.size)
                assertFalse(productResponse.empty)
                assertEquals("꿀수박주스", productResponse.content.first().name)
            }
        } finally {
            server.shutdown()
        }
    }
}
