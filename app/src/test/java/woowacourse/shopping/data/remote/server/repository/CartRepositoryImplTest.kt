package woowacourse.shopping.data.remote.server.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var cartService: CartService
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        cartService = retrofit.create(CartService::class.java)
        cartRepository = CartRepositoryImpl(cartService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `상품 추가 시 요청 메서드는 POST이고 경로는 cart-items이다`() = runBlocking {
        // given
        mockWebServer.enqueue(MockResponse.Builder().code(201).build())
        val product = Product("카테고리", 1L, "url", "상품", 1000)
        val purchaseProduct = PurchaseProduct(1L, product, 2)

        // when
        cartRepository.insert(purchaseProduct)

        // then
        val request = mockWebServer.takeRequest()
        assertEquals("/cart-items", request.target)
        assertEquals("POST", request.method)
    }

    @Test
    fun `수량 수정 시 요청 메서드는 PATCH이고 경로는 cart-items {id}이다`() = runBlocking {
        // given
        mockWebServer.enqueue(MockResponse.Builder().code(200).build())
        val cartItemId = 1L
        val newQuantity = 5

        // when
        cartRepository.updateCount(cartItemId, newQuantity)

        // then
        val request = mockWebServer.takeRequest()
        assertEquals("/cart-items/$cartItemId", request.target)
        assertEquals("PATCH", request.method)
    }

    @Test
    fun `상품 삭제 시 요청 메서드는 DELETE이고 경로는 cart-items {id}이다`() = runBlocking {
        // given
        mockWebServer.enqueue(MockResponse.Builder().code(204).build())
        val cartItemId = 1L

        // when
        cartRepository.deleteCartItem(cartItemId)

        // then
        val request = mockWebServer.takeRequest()
        assertEquals("/cart-items/$cartItemId", request.target)
        assertEquals("DELETE", request.method)
    }

    @Test
    fun `총 수량 조회 시 응답 body의 quantity 필드 값을 반환한다`() = runBlocking {
        // given
        val responseBody = """{"quantity": 10}"""
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body(responseBody)
                .addHeader("Content-Type", "application/json")
                .build()
        )

        // when
        val count = cartRepository.getProductCount()

        // then
        assertEquals(10, count)
        val request = mockWebServer.takeRequest()
        assertEquals("/cart-items/counts", request.target)
    }
}
