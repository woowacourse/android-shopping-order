package woowacourse.shoppoing.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mockWebServer.CartMockWeb
import woowacourse.shopping.service.RemoteCartService

class RemoteCartServiceTest {
    private lateinit var mockWebServer: CartMockWeb
    private lateinit var remoteCartDataSource: RemoteCartService

    @Before
    fun setUp() {
        // given
        mockWebServer = CartMockWeb()
        remoteCartDataSource = RemoteCartService(
            mockWebServer.url,
        )
    }

    @Test
    fun `장바구니 목록을 가져온다`() {
        // when
        val cartProducts = remoteCartDataSource.getAll()

        // then
        assertThat(cartProducts).hasSize(7)
        for (i in 0 until 7) {
            assertThat(cartProducts[i].id).isEqualTo(i)
            assertThat(cartProducts[i].name).isEqualTo("치킨$i")
            assertThat(cartProducts[i].price).isEqualTo(10000)
            assertThat(cartProducts[i].imageUrl).isEqualTo(
                "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
            )
            assertThat(cartProducts[i].count).isEqualTo(i + 1)
            assertThat(cartProducts[i].checked).isEqualTo(true)
        }
    }

    @Test
    fun `장바구니에 상품을 추가한다`() {
        // when
        val response = remoteCartDataSource.postItem(1)

        // then
        assertThat(response.code).isEqualTo(201)
        assertThat(response.headers["Location"]).isEqualTo("/cart-items/1")
    }

    @Test
    fun `장바구니의 상품 개수를 변경한다`() {
        // when
        val response = remoteCartDataSource.patchItemQuantity(1, 10)

        // then
        assertThat(response.code).isEqualTo(200)
    }

    @Test
    fun `장바구니에서 상품을 제거한다`() {
        // when
        val response = remoteCartDataSource.deleteItem(1)

        // then
        assertThat(response.code).isEqualTo(200)
    }
}
