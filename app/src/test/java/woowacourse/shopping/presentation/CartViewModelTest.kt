package woowacourse.shopping.presentation

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.fixture.ProductsFixture
import woowacourse.shopping.presentation.cart.CartViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = mockk<CartRepository>(relaxed = true)
        productRepository = mockk<ProductRepository>(relaxed = true)
        viewModel = CartViewModel(cartRepository)
    }

    @Test
    fun `장바구니_상품을_불러온다`() {
        coEvery { cartRepository.fetchPagedCartItems(0) } returns Result.success(ProductsFixture.dummyCartItems)

        viewModel.loadItems()

        val result = viewModel.cartItems.getOrAwaitValue()
        assertThat(result).hasSize(10)
    }
}
