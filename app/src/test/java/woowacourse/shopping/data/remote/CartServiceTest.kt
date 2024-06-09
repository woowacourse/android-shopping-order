package woowacourse.shopping.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.utils.CART_RESPONSE
import woowacourse.shopping.utils.getRetrofit

class CartServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var cartService: CartService
    private lateinit var mockResponse: MockResponse

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        cartService = getRetrofit(mockWebServer).create(CartService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `설정한 양식에 맞는 카트 아이템들을 가져올 수 있다`() =
        runTest {
            // given
            mockResponse =
                MockResponse()
                    .setBody(CART_RESPONSE.trimIndent())
                    .addHeader("Content-Type", "application/json")
            mockWebServer.enqueue(mockResponse)

            // when
            val response = cartService.getCartItems(0, 5, "asc")

            // then
            assertThat(response.cartItems).isEqualTo(
                listOf(
                    CartItem(
                        cartItemId = 16439,
                        quantity = 2,
                        product =
                            Product(
                                id = 2,
                                name = "나이키",
                                price = 1000,
                                imageUrl =
                                    """
                                    https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/a28864e3-de02-48bb-b861-9c592bc9a68b/%EB%B6%81-1-ep-%EB%86%8D%EA%B5%AC%ED%99%94-UOp6QQvg.png
                                    """.trimIndent(),
                                category = "fashion",
                            ),
                    ),
                    CartItem(
                        cartItemId = 16440,
                        quantity = 1,
                        product =
                            Product(
                                id = 3,
                                name = "아디다스",
                                price = 2000,
                                imageUrl = "https://sitem.ssgcdn.com/74/25/04/item/1000373042574_i1_750.jpg",
                                category = "fashion",
                            ),
                    ),
                    CartItem(
                        cartItemId = 16441,
                        quantity = 2,
                        product =
                            Product(
                                id = 10,
                                name = "퓨마",
                                price = 10000,
                                imageUrl = "https://sitem.ssgcdn.com/47/78/22/item/1000031227847_i1_750.jpg",
                                category = "fashion",
                            ),
                    ),
                    CartItem(
                        cartItemId = 16442,
                        quantity = 2,
                        product =
                            Product(
                                id = 11,
                                name = "리복",
                                price = 20000,
                                imageUrl = "https://image.msscdn.net/images/goods_img/20221031/2909092/2909092_6_500.jpg",
                                category = "fashion",
                            ),
                    ),
                    CartItem(
                        cartItemId = 16443,
                        quantity = 3,
                        product =
                            Product(
                                id = 12,
                                name = "컨버스",
                                price = 20000,
                                imageUrl = "https://sitem.ssgcdn.com/65/73/69/item/1000163697365_i1_750.jpg",
                                category = "fashion",
                            ),
                    ),
                ),
            )
        }

    @Test
    fun `장바구니에_담긴_상품의_전체_수를_불러올_수_있다`() =
        runTest {
            // given
            mockResponse =
                MockResponse()
                    .setBody(
                        """
                        {"quantity":5}
                        """.trimIndent(),
                    )
                    .addHeader("Content-Type", "application/json")
            mockWebServer.enqueue(mockResponse)

            // when
            val response = cartService.getCartTotalQuantity()

            // then
            assertThat(response.quantity).isEqualTo(5)
        }
}
