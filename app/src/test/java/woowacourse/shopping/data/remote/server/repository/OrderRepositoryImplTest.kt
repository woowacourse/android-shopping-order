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
import woowacourse.shopping.data.remote.server.service.OrderService
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        val orderService = retrofit.create(OrderService::class.java)
        orderRepository = OrderRepositoryImpl(orderService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `주문 생성 시 POST orders로 장바구니 아이템 id 목록을 전송한다`() =
        runBlocking {
            mockWebServer.enqueue(MockResponse.Builder().code(201).build())

            orderRepository.createOrder(listOf(101L, 102L))

            val request = mockWebServer.takeRequest()
            assertEquals("/orders", request.target)
            assertEquals("POST", request.method)
            assertEquals("{\"cartItemIds\":[101,102]}", request.body?.utf8())
        }
}
