package woowacourse.shopping.presentation

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.presentation.cart.CartViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = FakeCartRepository()
        productRepository = mockk<ProductRepository>(relaxed = true)
        viewModel = CartViewModel(cartRepository)
    }

    @Test
    fun `장바구니_상품을_불러온다`() {
        viewModel.loadItems(0)

        val result = viewModel.cartItems.getOrAwaitValue()
        assertThat(result).hasSize(10)
    }
}
