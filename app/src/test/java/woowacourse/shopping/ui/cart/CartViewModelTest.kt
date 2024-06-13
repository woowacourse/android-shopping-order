package woowacourse.shopping.ui.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.fixture.CoroutinesTestExtension
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.fake.FakeCartRepository
import woowacourse.shopping.fixture.fake.FakeProductRepository
import woowacourse.shopping.fixture.fake.FakeRecentRepository
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel
    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentProductRepository: FakeRecentRepository
    private lateinit var cartRepository: FakeCartRepository

    @BeforeEach
    fun setUp() {
        recentProductRepository = FakeRecentRepository()
        cartRepository = FakeCartRepository(CART_STUB)
        productRepository = FakeProductRepository(cartRepository)
        viewModel = CartViewModel(productRepository, cartRepository, recentProductRepository)
    }

    @Test
    fun `장바구니에 아이템이 4개 있을 때, 삭제 버튼을 누르면 아이템이 3개가 된다`() = runTest {
        // when
        val before = viewModel.cart.getOrAwaitValue()
        assertThat(before.cartItems).hasSize(4)

        // given
        viewModel.removeCartItem(DELETED_CART_ID)

        // then
        advanceUntilIdle()
        val actual = viewModel.cart.getOrAwaitValue()
        assertThat(actual.cartItems.size).isEqualTo(3)
    }

    @Test
    fun `장바구니의 상품 개수가 2개일 때, 개수를 증가시키면 3개가 된다`() = runTest {
        // when
        val before =
            viewModel.cart.getOrAwaitValue().cartItems.first { it.id == UPDATE_COUNT_CART_ID }
        assertThat(before.quantity).isEqualTo(2)

        // given
        viewModel.plusCount(UPDATE_COUNT_CART_ID)

        // then
        advanceUntilIdle()
        val actual =
            viewModel.cart.getOrAwaitValue().cartItems.first { it.id == UPDATE_COUNT_CART_ID }
        assertThat(actual.quantity).isEqualTo(3)
    }


    companion object {
        val CART_STUB =
            (1..4).toList().map { id ->
                CartWithProduct(
                    id.toLong(), FakeProductRepository.productStubs.first { it.id == id.toLong() },
                    Quantity(id)
                )
            }
        const val DELETED_CART_ID = 3L
        const val UPDATE_COUNT_CART_ID = 2L

    }
}
