package woowacourse.shopping.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.utils.PRODUCT_RESPONSE
import woowacourse.shopping.utils.SINGLE_PRODUCT_RESPONSE
import woowacourse.shopping.utils.getRetrofit

class ProductServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var productService: ProductService
    private lateinit var mockResponse: MockResponse

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        productService = getRetrofit(mockWebServer).create(ProductService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `요청한_양식대로_상품들을_불러온다`() =
        runTest {
            // given
            mockResponse =
                MockResponse()
                    .setBody(PRODUCT_RESPONSE.trimIndent())
                    .addHeader("Content-Type", "application/json")
            mockWebServer.enqueue(mockResponse)

            // when
            val response = productService.getProducts(null, 0, 5, "asc")

            // then
            assertThat(response.products).hasSize(5)
        }

    @Test
    fun `아이디에_맞는_상품_정보를_불러온다`() =
        runTest {
            // given
            mockResponse =
                MockResponse()
                    .setBody(SINGLE_PRODUCT_RESPONSE.trimIndent())
                    .addHeader("Content-Type", "application/json")
            mockWebServer.enqueue(mockResponse)

            // when
            val response = productService.getProductById(1)

            // then
            assertThat(response).isEqualTo(
                Product(
                    id = 1,
                    category = "vegetable",
                    imageUrl = "string",
                    name = "string",
                    price = 0,
                ),
            )
        }
}
