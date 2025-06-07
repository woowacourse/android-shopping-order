package woowacourse.shopping.data

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.exception.NetworkResult

class CartDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var cartService: CartService
    private lateinit var cartDataSource: CartDataSource

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        cartService = retrofit.create(CartService::class.java)
        cartDataSource = CartDataSource(cartService, NetworkResultHandler())
    }

    @Test
    fun `장바구니 상품을 추가 후 헤더에 Location이 비어있으면 MissingLocationHeaderError이 발생한다`() =
        runTest {
            // given
            val mockResponse =
                MockResponse()
                    .setResponseCode(201)
                    .setHeader("Location", "")

            mockWebServer.enqueue(mockResponse)

            // When
            val result = cartDataSource.addCart(CartItemRequest(1, 1))

            // then
            assertTrue(result is NetworkResult.Error)
            val error = (result as NetworkResult.Error).exception
            assertTrue(error is NetworkError.MissingLocationHeaderError)
        }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
