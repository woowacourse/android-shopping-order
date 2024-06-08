package woowacourse.shopping.ui.cart

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        productRepository = mockk<ProductRepository>()
        recentProductRepository = mockk<RecentProductRepository>()
        cartRepository = mockk<CartRepository>()
        coEvery { cartRepository.getAllCartItemsWithProduct().getOrThrow() } returns emptyList()
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
