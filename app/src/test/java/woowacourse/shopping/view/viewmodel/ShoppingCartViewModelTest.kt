package woowacourse.shopping.view.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.MockShoppingCartRepository
import woowacourse.shopping.TestFixture.getOrAwaitValue
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.view.cart.ShoppingCartViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ShoppingCartViewModelTest {
    private lateinit var shoppingCartRepository: ShoppingCartRepository
    private lateinit var viewModel: ShoppingCartViewModel

    @BeforeEach
    fun setUp() {
        shoppingCartRepository = MockShoppingCartRepository()
        viewModel = ShoppingCartViewModel(shoppingCartRepository)
    }

    @Test
    fun `offset을_기준으로_장바구니_리스트를_요청하면_장바구니_정해진_개수만큼_반환해야_한다`() {
        val before = viewModel.shoppingCart.cartItems.getOrAwaitValue()
        Assertions.assertThat(before.size).isEqualTo(0)

        viewModel.loadPagingCartItemList()

        val result = viewModel.shoppingCart.cartItems.getOrAwaitValue()
        Assertions.assertThat(result.size).isEqualTo(3)
    }
}
