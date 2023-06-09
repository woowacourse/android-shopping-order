package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import io.mockk.clearAllMocks
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSourceImpl
import woowacourse.shopping.mockwebserver.CartMockWebserver

internal class CartRepositoryImplTest {
    private lateinit var cartRepositoryImpl: CartRepositoryImpl
    private lateinit var mockWebserver: CartMockWebserver

    @Before
    fun setup() {
        mockWebserver = CartMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        cartRepositoryImpl = CartRepositoryImpl(ShoppingCartDataSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `장바구니에 담긴 상품들을 모두 가져온다`() {
        // when
        var lock = true
        var products: List<CartProduct> = emptyList()
        cartRepositoryImpl.getAllProductInCart { carts ->
            products = carts
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
        cartRepositoryImpl.insert(productId, quantity) { result ->
            count = 1
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }

    @Test
    fun `장바구니에 상품을 삭제한다`() {
        // given
        val productId = 1L
        // when
        var lock = true
        var count: Int = 0
        cartRepositoryImpl.remove(productId) { result ->
            count = 1
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }

    @Test
    fun `장바구니에 상품의 수량을 변경한다`() {
        // given
        val productId = 1L
        val quantity = 1
        // when
        var lock = true
        var count: Int = 0
        cartRepositoryImpl.updateCount(productId, quantity) { result ->
            count = 1
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(count, 1)
    }

    @Test
    fun `장바구니 상품을 가져온다`() {
        // given
        val productId = 1L
        // when
        var lock = true
        var cartProduct: CartProduct? = null
        cartRepositoryImpl.findById(productId) { result ->
            cartProduct = result
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(cartProduct?.id, productId)
    }

    @Test
    fun `장바구니 상품을 상품 아이디와 비교해서 가져온다`() {
        // given
        val productId = 1L
        // when
        var lock = true
        var cartProduct: CartProduct? = null
        cartRepositoryImpl.findById(productId) { result ->
            cartProduct = result
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(cartProduct?.product?.id, productId)
    }

    @Test
    fun `장바구니 상품을 페이지네이션한다`() {
        // given
        val page = 0
        val size = 10
        // when
        var lock = true
        var cartProducts: List<CartProduct> = emptyList()
        cartRepositoryImpl.getSubList(page, size) { result ->
            cartProducts = result
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(cartProducts.size, 10)
        cartProducts.forEachIndexed { i, it ->
            assertEquals(it.id, i.toLong())
        }
    }
}
