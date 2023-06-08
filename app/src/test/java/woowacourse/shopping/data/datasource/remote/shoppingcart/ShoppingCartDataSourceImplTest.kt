package woowacourse.shopping.data.datasource.remote.shoppingcart

import io.mockk.clearAllMocks
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CartProductDTO
import woowacourse.shopping.mockwebserver.CartMockWebserver

internal class ShoppingCartDataSourceImplTest {
    private lateinit var mockWebserver: CartMockWebserver
    private lateinit var cartRemoteDataSourceImpl: ShoppingCartDataSourceImpl

    @Before
    fun setup() {
        mockWebserver = CartMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        cartRemoteDataSourceImpl = ShoppingCartDataSourceImpl()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `장바구니에 담긴 상품들을 모두 가져온다`() {
        // when
        var lock = true
        var products: List<CartProductDTO> = emptyList()
        cartRemoteDataSourceImpl.getAllProductInCart { result ->
            result.onSuccess { products = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(products.size, 10)
        products.forEachIndexed { i, it ->
            assertEquals(it.id, i.toLong())
        }
    }

    @Test
    fun `장바구니에 상품을 넣는다`() {
        // given
        val productId = 1L
        val quantity = 1
        // when
        var lock = true
        var count: Int = 0
        cartRemoteDataSourceImpl.postProductToCart(productId, quantity) { result ->
            result.onSuccess { count = 1 }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }

    @Test
    fun `장바구니 상품을 수정한다`() {
        // given
        val productId = 1L
        val quantity = 1
        // when
        var lock = true
        var count: Int = 0
        cartRemoteDataSourceImpl.patchProductCount(productId, quantity) { result ->
            result.onSuccess { count = 1 }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }

    @Test
    fun `장바구니 상품을 삭제한다`() {
        // given
        val productId = 1L
        // when
        var lock = true
        var count: Int = 0
        cartRemoteDataSourceImpl.deleteProductInCart(productId) { result ->
            result.onSuccess { count = 1 }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }
}
