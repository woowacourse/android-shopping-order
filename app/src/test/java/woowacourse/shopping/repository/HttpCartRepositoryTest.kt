@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import woowacourse.shopping.data.remote.cart.CartNetworkException
import woowacourse.shopping.data.remote.cart.CartResponseException
import woowacourse.shopping.data.repository.CartRepositoryImpl

class CartRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: CartRepositoryImpl

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        repository =
            CartRepositoryImpl(
                client = OkHttpClient(),
                baseUrl = mockWebServer.url("/").toString(),
            )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `장바구니 조회 API 성공 응답을 페이지 결과로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "content": [
                            {
                              "id": 10,
                              "product": {
                                "id": 1,
                                "name": "새우깡",
                                "price": 1500,
                                "imageUrl": "https://example.com/shrimp.png",
                                "category": "과자"
                              },
                              "quantity": 2
                            }
                          ],
                          "totalElements": 1,
                          "totalPages": 1,
                          "number": 0,
                          "size": 5
                        }
                        """.trimIndent(),
                    ),
            )

            val actual = repository.getCartPage(page = 0, size = 5)
            val request = mockWebServer.takeRequest()

            assertEquals("/cart-items", request.requestUrl?.encodedPath)
            assertEquals("0", request.requestUrl?.queryParameter("page"))
            assertEquals("5", request.requestUrl?.queryParameter("size"))
            assertEquals(1, actual.totalElements)
            assertEquals(1, actual.totalPages)
            assertEquals(0, actual.page)
            assertEquals(10L, actual.items.first().cartItemId)
            assertEquals(1L, actual.items.first().productId)
            assertEquals(2, actual.items.first().quantity)
        }

    @Test
    fun `장바구니 조회 API가 서버 오류를 반환하면 응답 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"Unauthorized"}"""),
        )

        val actual =
            assertThrows<CartResponseException> {
                runBlocking { repository.getCartPage(page = 0, size = 5) }
            }

        assertEquals(401, actual.code)
        assertTrue(actual.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `장바구니 조회 API 네트워크 호출에 실패하면 네트워크 예외를 던진다`() {
        val disconnectedServer = MockWebServer()
        disconnectedServer.start()
        val baseUrl = disconnectedServer.url("/").toString()
        disconnectedServer.shutdown()

        val disconnectedRepository =
            CartRepositoryImpl(
                client = OkHttpClient(),
                baseUrl = baseUrl,
            )

        assertThrows<CartNetworkException> {
            runBlocking { disconnectedRepository.getCartPage(page = 0, size = 5) }
        }
    }
}
