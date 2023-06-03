package woowacourse.shoppoing.data.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remoteDataSourceImpl.CartRemoteDataSourceImpl
import woowacourse.shopping.utils.RetrofitUtil
import woowacourse.shopping.utils.mockWebServer.CartMockWeb

class CartRemoteDataSourceImplTest {
    private lateinit var mockWebServer: CartMockWeb
    private lateinit var remoteCartDataSource: CartRemoteDataSourceImpl

    @Before
    fun setUp() {
        // given
        mockWebServer = CartMockWeb()
        RetrofitUtil.url = mockWebServer.url
        remoteCartDataSource = CartRemoteDataSourceImpl()
    }

    @Test
    fun `장바구니 목록을 가져온다`() {
        // when
        var lock = true
        remoteCartDataSource.getAll { result ->
            result.onSuccess { cartProducts ->
                // then
                assertThat(cartProducts).hasSize(7)
                for (i in 0 until 7) {
                    assertThat(cartProducts[i].id).isEqualTo(i)
                    assertThat(cartProducts[i].product.name).isEqualTo("치킨$i")
                    assertThat(cartProducts[i].product.price).isEqualTo(10000)
                    assertThat(cartProducts[i].product.imageUrl).isEqualTo(
                        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
                    )
                    assertThat(cartProducts[i].quantity).isEqualTo(i + 1)
                }
            }.onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }

    @Test
    fun `장바구니에 상품을 추가한다`() {
        // when
        var lock = true

        remoteCartDataSource.postItem(1) { result ->
            // then
            result.onSuccess { assertThat(it).isEqualTo(1) }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }

    @Test
    fun `장바구니의 상품 개수를 변경한다`() {
        // when
        var lock = true
        remoteCartDataSource.patchItemQuantity(1, 10) { result ->
            // then
            result.onSuccess { assertThat(it).isEqualTo(10) }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }

    @Test
    fun `장바구니에서 상품을 제거한다`() {
        // when
        var lock = true
        remoteCartDataSource.deleteItem(1) { result ->
            // then
            result.onSuccess { assertThat(it).isEqualTo(0) }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }
}
