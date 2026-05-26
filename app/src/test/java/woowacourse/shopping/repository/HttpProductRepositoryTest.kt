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
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.remote.product.ProductNetworkException
import woowacourse.shopping.data.remote.product.ProductParsingException
import woowacourse.shopping.data.remote.product.ProductResponseException

class ProductRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: ProductRepositoryImpl

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        repository =
            ProductRepositoryImpl(
                client = OkHttpClient(),
                baseUrl = mockWebServer.url("/").toString(),
            )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `상품 목록 API 성공 응답을 페이지 결과로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(1L, 2L), last = true)),
            )

            val actual = repository.getProducts(page = 0, size = 20)
            val request = mockWebServer.takeRequest()

            assertTrue(request.requestUrl?.encodedPath == "/products")
            assertEquals("0", request.requestUrl?.queryParameter("page"))
            assertEquals("20", request.requestUrl?.queryParameter("size"))
            assertEquals(2, actual.items.size)
            assertEquals(1L, actual.items.first().id)
            assertEquals("상품1", actual.items.first().name)
            assertEquals(2, actual.totalElements)
            assertEquals(false, actual.hasNext)
        }

    @Test
    fun `카테고리 조회 이후 일반 목록 조회를 호출해도 결과가 섞이지 않는다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(101L, 102L), last = true)),
            )
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(1L, 2L, 3L), last = true)),
            )

            val categoryResult = repository.getProductsByCategory(category = "fashion", page = 0, size = 2)
            val mainResult = repository.getProducts(page = 0, size = 3)
            val firstRequest = mockWebServer.takeRequest()
            val secondRequest = mockWebServer.takeRequest()

            assertEquals(listOf(101L, 102L), categoryResult.items.map { it.id })
            assertEquals("fashion", firstRequest.requestUrl?.queryParameter("category"))
            assertEquals(listOf(1L, 2L, 3L), mainResult.items.map { it.id })
            assertEquals(null, secondRequest.requestUrl?.queryParameter("category"))
            assertEquals(2, mockWebServer.requestCount)
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

            val actual = repository.findAllByIds(setOf(1L))
            val request = mockWebServer.takeRequest()

            assertTrue(request.path?.endsWith("/products/1") == true)
            assertEquals(setOf(1L), actual.keys)
            assertEquals("치킨", actual[1L]?.name)
        }

    @Test
    fun `카테고리 상품 목록 API 성공 응답을 페이지 결과로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(3L, 6L), last = true)),
            )

            val actual = repository.getProductsByCategory(category = "food", page = 0, size = 2)
            val request = mockWebServer.takeRequest()

            assertTrue(request.requestUrl?.encodedPath == "/products")
            assertEquals("0", request.requestUrl?.queryParameter("page"))
            assertEquals("2", request.requestUrl?.queryParameter("size"))
            assertEquals("food", request.requestUrl?.queryParameter("category"))
            assertEquals(listOf(3L, 6L), actual.items.map { it.id })
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
                runBlocking { repository.getProducts(page = 0, size = 20) }
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
            ProductRepositoryImpl(
                client = OkHttpClient(),
                baseUrl = baseUrl,
            )

        assertThrows<ProductNetworkException> {
            runBlocking { disconnectedRepository.getProducts(page = 0, size = 20) }
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
                runBlocking { repository.findAllByIds(setOf(1L)) }
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
                runBlocking { repository.getProducts(page = 0, size = 20) }
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
                runBlocking { repository.findAllByIds(setOf(1L)) }
            }

        assertTrue(actual.message?.contains("응답") == true)
    }

    @Test
    fun `다음 페이지를 직접 요청하면 해당 페이지 결과와 hasNext 상태를 반환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(createProductsJson(listOf(21L), last = true, totalElements = 21L)),
            )

            val actual = repository.getProducts(page = 1, size = 20)
            val request = mockWebServer.takeRequest()

            assertEquals(listOf(21L), actual.items.map { it.id })
            assertEquals(21, actual.totalElements)
            assertEquals(false, actual.hasNext)
            assertEquals("1", request.requestUrl?.queryParameter("page"))
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

            val targetId = 9007199254740991L
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

            val actual = repository.getProducts(page = 0, size = 1)

            assertEquals(1, actual.items.size)
            assertEquals(9007199254740991L, actual.items.first().id)
            assertEquals(Int.MAX_VALUE, actual.totalElements)
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
                runBlocking { repository.getProducts(page = 0, size = 20) }
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
