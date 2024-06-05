package woowacourse.shopping.ui.cart

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.ui.FakeRecentProductDao
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel
    private lateinit var productRepository: ProductRepositoryImpl
    private val recentProductRepository = RecentProductRepositoryImpl.get(FakeRecentProductDao)
    private lateinit var cartRepository: CartRepositoryImpl

    @BeforeEach
    fun setUp() {
        productRepository = mockk<ProductRepositoryImpl>()
        cartRepository = mockk<CartRepositoryImpl>()
        every { cartRepository.getAllCartItemsWithProduct() } returns emptyList()
        viewModel = CartViewModel(productRepository, cartRepository, recentProductRepository)
    }

    @Test
    fun `장바구니에 상품을 넣지 않았다면 장바구니가 비어있어야 한다`() {
        // when
        val actual = viewModel.cart.getOrAwaitValue()

        // then
        assertThat(actual.cartItems.size).isEqualTo(0)
    }
}
