package woowacourse.shopping.data.db.datasource

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertNotNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.exception.NetworkError

class RemoteCartDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var cartService: CartService
    private lateinit var remoteCartDataSource: RemoteCartDataSource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        cartService = retrofit.create(CartService::class.java)
        remoteCartDataSource = RemoteCartDataSource(cartService, NetworkResultHandler())
    }

    @Test
    fun `장바구니_상품을_추가_후_헤더에_Location이_비어있으면_MissingLocationHeaderError이_발생한다`() =
        runTest {
            // given
            val mockResponse =
                MockResponse()
                    .setResponseCode(201)
                    .setHeader("Location", "")

            mockWebServer.enqueue(mockResponse)

            // When
            val result = remoteCartDataSource.addCart(CartItemRequest(1, 1))

            // then
            assertTrue(result.isFailure)

            val error = result.exceptionOrNull()
            assertNotNull(result)
            assertTrue(error is NetworkError.MissingLocationHeaderError)
        }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
