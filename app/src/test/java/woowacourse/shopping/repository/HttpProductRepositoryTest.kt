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
import woowacourse.shopping.repository.http.product.HttpProductRepository
import woowacourse.shopping.repository.http.product.ProductNetworkException
import woowacourse.shopping.repository.http.product.ProductParsingException
import woowacourse.shopping.repository.http.product.ProductResponseException

class HttpProductRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: HttpProductRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        repository =
            HttpProductRepository(
                client = OkHttpClient(),
                baseUrl = mockWebServer.url("/").toString(),
            )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `상품 목록 API 성공 응답을 도메인 객체로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(1L, 2L), last = true)),
            )

            val actual = repository.getProducts(fromIndex = 0, limit = 20).toList()
            val request = mockWebServer.takeRequest()

            assertTrue(request.requestUrl?.encodedPath == "/products")
            assertEquals("0", request.requestUrl?.queryParameter("page"))
            assertEquals("20", request.requestUrl?.queryParameter("size"))
            assertEquals(2, actual.size)
            assertEquals((1L), actual.first().id)
            assertEquals("상품1", actual.first().name)
        }

    @Test
    fun `상품 목록을 한 번 조회한 뒤에는 캐시된 목록으로 슬라이스를 반환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(1L, 2L), last = true)),
            )

            repository.getProducts(fromIndex = 0, limit = 20)
            val actual = repository.getProducts(fromIndex = 1, limit = 1).toList()

            assertEquals(1, mockWebServer.requestCount)
            assertEquals(1, actual.size)
            assertEquals((2L), actual.first().id)
            assertEquals("상품2", actual.first().name)
        }

    @Test
    fun `상품 상세 API 성공 응답을 기준으로 ID 목록을 조회한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(productJson),
            )

            val actual = repository.findAllByIds(setOf((1L)))
            val request = mockWebServer.takeRequest()

            assertTrue(request.path?.endsWith("/products/1") == true)
            assertEquals(setOf((1L)), actual.keys)
            assertEquals("치킨", actual[(1L)]?.name)
        }

    @Test
    fun `카테고리 상품 목록 API 성공 응답을 도메인 객체로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(3L, 6L), last = true)),
            )

            val actual = repository.getProductsByCategory(category = "food", limit = 2).toList()
            val request = mockWebServer.takeRequest()

            assertTrue(request.requestUrl?.encodedPath == "/products")
            assertEquals("0", request.requestUrl?.queryParameter("page"))
            assertEquals("2", request.requestUrl?.queryParameter("size"))
            assertEquals("food", request.requestUrl?.queryParameter("category"))
            assertEquals(listOf(3L, 6L), actual.map { it.id })
        }

    @Test
    fun `상품 목록 API가 서버 오류를 반환하면 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"server error"}"""),
        )

        val actual =
            assertThrows<ProductResponseException> {
                runBlocking { repository.getProducts(fromIndex = 0, limit = 20) }
            }

        assertEquals(500, actual.code)
    }

    @Test
    fun `상품 목록 API 네트워크 호출에 실패하면 예외를 던진다`() {
        val disconnectedServer = MockWebServer()
        disconnectedServer.start()
        val baseUrl = disconnectedServer.url("/").toString()
        disconnectedServer.shutdown()

        val disconnectedRepository =
            HttpProductRepository(
                client = OkHttpClient(),
                baseUrl = baseUrl,
            )

        assertThrows<ProductNetworkException> {
            runBlocking { disconnectedRepository.getProducts(fromIndex = 0, limit = 20) }
        }
    }

    @Test
    fun `상품 상세 API가 서버 오류를 반환하면 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"server error"}"""),
        )

        val actual =
            assertThrows<ProductResponseException> {
                runBlocking { repository.findAllByIds(setOf((1L))) }
            }

        assertEquals(500, actual.code)
    }

    @Test
    fun `상품 목록 API가 빈 응답 본문을 반환하면 파싱 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(""),
        )

        val actual =
            assertThrows<ProductParsingException> {
                runBlocking { repository.getProducts(fromIndex = 0, limit = 20) }
            }

        assertTrue(actual.message?.contains("응답") == true)
    }

    @Test
    fun `상품 상세 API가 빈 응답 본문을 반환하면 파싱 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(""),
        )

        val actual =
            assertThrows<ProductParsingException> {
                runBlocking { repository.findAllByIds(setOf((1L))) }
            }

        assertTrue(actual.message?.contains("응답") == true)
    }

    @Test
    fun `상품 목록이 페이지 크기를 넘기면 다음 페이지를 추가로 조회한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson((1L..20L).toList(), last = false, totalElements = 21L)),
            )
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(21L), last = true, totalElements = 21L)),
            )

            val actual = repository.getProducts(fromIndex = 0, limit = 21).toList()
            val firstRequest = mockWebServer.takeRequest()
            val secondRequest = mockWebServer.takeRequest()

            assertEquals(21, actual.size)
            assertEquals("0", firstRequest.requestUrl?.queryParameter("page"))
            assertEquals("1", secondRequest.requestUrl?.queryParameter("page"))
            assertEquals(2, mockWebServer.requestCount)
        }

    @Test
    fun `현재 인덱스가 마지막 페이지 이전이면 다음 상품이 있음을 반환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson((1L..20L).toList(), last = false, totalElements = 21L)),
            )
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(21L), last = true, totalElements = 21L)),
            )

            val actual = repository.hasNext(19)

            assertTrue(actual)
            assertEquals(2, mockWebServer.requestCount)
        }

    @Test
    fun `상품 상세 API가 Long 범위의 상품 ID를 파싱한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "id": 9007199254740991,
                          "name": "string",
                          "price": 1073741824,
                          "imageUrl": "string",
                          "category": "string"
                        }
                        """.trimIndent(),
                    ),
            )

            val targetId = (9007199254740991L)
            val actual = repository.findAllByIds(setOf(targetId))

            assertEquals(setOf(targetId), actual.keys)
            assertEquals("string", actual[targetId]?.name)
        }

    @Test
    fun `상품 목록 API가 Long 범위의 totalElements를 파싱한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(9007199254740991L), last = true, totalElements = 9007199254740991L)),
            )

            val actual = repository.getProducts(fromIndex = 0, limit = 1).toList()

            assertEquals(1, actual.size)
            assertEquals((9007199254740991L), actual.first().id)
        }

    @Test
    fun `상품 목록 API가 잘못된 JSON을 반환하면 파싱 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"content":[{"id":1,"name":"치킨"}],"totalElements":1,"last":true}"""),
        )

        val actual =
            assertThrows<ProductParsingException> {
                runBlocking { repository.getProducts(fromIndex = 0, limit = 20) }
            }

        assertTrue(actual.message?.contains("파싱") == true || actual.message?.contains("올바르지") == true)
    }

    companion object {
        private val productJson =
            """
            {
              "id": 1,
              "name": "치킨",
              "price": 10000,
              "imageUrl": "http://example.com/chicken.jpg",
              "category": "food"
            }
            """.trimIndent()

        private fun createProductsJson(
            ids: List<Long>,
            last: Boolean,
            totalElements: Long = ids.size.toLong(),
        ): String {
            val content =
                ids.joinToString(",\n") { id ->
                    """
                    {
                      "id": $id,
                      "name": "상품$id",
                      "price": 1000,
                      "imageUrl": "http://example.com/product$id.jpg",
                      "category": "food"
                    }
                    """.trimIndent()
                }

            return """
                {
                  "content": [
                    $content
                  ],
                  "totalElements": $totalElements,
                  "last": $last
                }
                """.trimIndent()
        }
    }
}
