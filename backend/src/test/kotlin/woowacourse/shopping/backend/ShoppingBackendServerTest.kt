package woowacourse.shopping.backend

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ShoppingBackendServerTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient.newHttpClient()

    private lateinit var server: ShoppingBackendServer

    @BeforeEach
    fun setUp() {
        server = ShoppingBackendServer(port = 0)
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun `상품 목록과 카테고리 필터를 조회할 수 있다`() {
        val response = get("/products?page=0&size=2&category=dessert")

        assertEquals(200, response.statusCode())

        val body = json.decodeFromString<ProductPagePayload>(response.body())
        assertEquals(2, body.content.size)
        assertTrue(body.content.all { it.category == "dessert" })
        assertEquals(8, body.totalElements)
        assertFalse(body.last)
    }

    @Test
    fun `루트와 헬스 체크 경로를 조회할 수 있다`() {
        val rootResponse = get("/")
        val healthResponse = get("/health")

        assertEquals(200, rootResponse.statusCode())
        assertEquals(200, healthResponse.statusCode())
        assertTrue(rootResponse.body().contains("Shopping backend is running."))
        assertEquals("ok", json.decodeFromString<HealthPayload>(healthResponse.body()).status)
    }

    @Test
    fun `장바구니 추가 수정 조회 삭제와 주문 흐름이 동작한다`() {
        val addResponse =
            post(
                path = "/cart-items",
                body = """{"productId":1,"quantity":2}""",
            )

        assertEquals(201, addResponse.statusCode())

        val cartPage = get("/cart-items?page=0&size=5")
        val cartBody = json.decodeFromString<CartPagePayload>(cartPage.body())
        val cartItemId = cartBody.content.single().id
        assertEquals(2, cartBody.content.single().quantity)

        val patchResponse =
            request(
                method = "PATCH",
                path = "/cart-items/$cartItemId",
                body = """{"quantity":4}""",
            )
        assertEquals(204, patchResponse.statusCode())

        val countResponse = get("/cart-items/counts")
        assertEquals(200, countResponse.statusCode())
        assertEquals(4, json.decodeFromString<CartCountPayload>(countResponse.body()).quantity)

        val orderResponse =
            post(
                path = "/orders",
                body = """{"cartItemIds":[$cartItemId]}""",
            )
        assertEquals(204, orderResponse.statusCode())

        val emptiedCart = json.decodeFromString<CartPagePayload>(get("/cart-items?page=0&size=5").body())
        assertTrue(emptiedCart.content.isEmpty())
        assertEquals(0, emptiedCart.totalElements)
    }

    @Test
    fun `존재하지 않는 상품 상세는 404를 반환한다`() {
        val response = get("/products/999999")

        assertEquals(404, response.statusCode())
    }

    private fun get(path: String): HttpResponse<String> = request(method = "GET", path = path)

    private fun post(
        path: String,
        body: String,
    ): HttpResponse<String> = request(method = "POST", path = path, body = body)

    private fun request(
        method: String,
        path: String,
        body: String? = null,
    ): HttpResponse<String> {
        val builder =
            HttpRequest
                .newBuilder(URI.create(server.baseUrl.removeSuffix("/") + path))
                .method(
                    method,
                    body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody(),
                )
                .header("Content-Type", "application/json")

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    }
}

@Serializable
private data class ProductPagePayload(
    val content: List<ProductPayload>,
    val totalElements: Long,
    val last: Boolean,
)

@Serializable
private data class ProductPayload(
    val id: Long,
    val category: String,
)

@Serializable
private data class CartPagePayload(
    val content: List<CartItemPayload>,
    val totalElements: Long,
)

@Serializable
private data class CartItemPayload(
    val id: Long,
    val quantity: Int,
)

@Serializable
private data class CartCountPayload(
    val quantity: Int,
)

@Serializable
private data class HealthPayload(
    val status: String,
)
