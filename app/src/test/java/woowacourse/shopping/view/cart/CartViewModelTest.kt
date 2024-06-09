package woowacourse.shopping.view.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.FakeCartRepository
import woowacourse.shopping.data.repository.FakeProductRepository
import woowacourse.shopping.data.repository.FakeRecentProductRepository
import woowacourse.shopping.utils.CoroutinesTestExtension
import woowacourse.shopping.utils.InstantTaskExecutorExtension
import woowacourse.shopping.utils.getOrAwaitValue
import woowacourse.shopping.view.cart.list.CartListUiEvent
import woowacourse.shopping.view.cart.recommend.RecommendListUiEvent

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var cartViewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        val cartRepository = FakeCartRepository(count = 10)
        val recentProductRepository = FakeRecentProductRepository()
        val productRepository = FakeProductRepository(count = 100, cartCount = 10)
        cartViewModel = CartViewModel(cartRepository, recentProductRepository, productRepository)
    }

    @Test
    fun `장바구니 선택 화면에서 추천 화면로 이동할 수 있다`() {
        // when
        cartViewModel.navigate()

        // then
        val actual = cartViewModel.currentScreen.getOrAwaitValue()
        assertThat(actual).isEqualTo(CurrentScreen.RECOMMEND)
    }

    @Test
    fun `추천 화면에서 주문 화면로 이동할 수 있다`() {
        // when
        cartViewModel.navigate()
        cartViewModel.navigate()

        // then
        val uiState = cartViewModel.cartUiState.getOrAwaitValue()
        val actual = cartViewModel.recommendListUiEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(actual).isEqualTo(RecommendListUiEvent.NavigateToOrder(uiState.selectedCartItems))
    }

    @Test
    fun `장바구니 선택 화면에서 전체 아이템을 구매 선택할 수 있다`() {
        // when
        cartViewModel.updateEntireCheck(true)

        // then
        val cartUiState = cartViewModel.cartUiState.getOrAwaitValue()
        val cartListUiState = cartViewModel.cartListUiState.getOrAwaitValue()
        val actual = cartListUiState.cartViewItems.all { it.isSelected }
        assertAll(
            { assertThat(actual).isTrue() },
            { assertThat(cartUiState.totalPrice).isEqualTo(385000) },
        )
    }

    @Test
    fun `장바구니 선택 화면에서 전체 아이템을 구매 선택 해제할 수 있다`() {
        // when
        cartViewModel.onSelectChanged(1, true)
        cartViewModel.updateEntireCheck(false)

        // then
        val cartUiState = cartViewModel.cartUiState.getOrAwaitValue()
        val cartListUiState = cartViewModel.cartListUiState.getOrAwaitValue()
        val actual = cartListUiState.cartViewItems.all { !it.isSelected }
        assertAll(
            { assertThat(actual).isTrue() },
            { assertThat(cartUiState.totalPrice).isEqualTo(0) },
        )
    }

    @Test
    fun `장바구니 상품을 클릭하면 상세 화면으로 이동한다`() {
        // when
        cartViewModel.onCartItemClick(1)

        // then
        val cartListUiEvent =
            cartViewModel.cartListUiEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(cartListUiEvent).isEqualTo(CartListUiEvent.NavigateToProductDetail(1, false))
    }

    @Test
    fun `장바구니 상품을 삭제할 수 있다`() {
        // when
        cartViewModel.onDeleteButtonClick(1)

        // then
        val cartListUiState = cartViewModel.cartListUiState.getOrAwaitValue()
        assertThat(cartListUiState.cartViewItems).hasSize(9)
    }

    @Test
    fun `장바구니 상품을 구매하도록 선택할 수 있다`() {
        // when
        cartViewModel.onSelectChanged(1, true)

        // then
        val cartUiState = cartViewModel.cartUiState.getOrAwaitValue()
        val cartListUiState = cartViewModel.cartListUiState.getOrAwaitValue()
        val actual = cartListUiState.cartViewItems.count { it.isSelected }
        assertAll(
            { assertThat(actual).isEqualTo(1) },
            { assertThat(cartUiState.totalPrice).isEqualTo(1000) },
        )
    }

    @Test
    fun `장바구니 상품 구매 선택을 해제할 수 있다`() {
        // when
        cartViewModel.onSelectChanged(1, true)
        cartViewModel.onSelectChanged(2, true)
        cartViewModel.onSelectChanged(2, false)

        // then
        val cartUiState = cartViewModel.cartUiState.getOrAwaitValue()
        val cartListUiState = cartViewModel.cartListUiState.getOrAwaitValue()
        val actual = cartListUiState.cartViewItems.count { it.isSelected }
        assertAll(
            { assertThat(actual).isEqualTo(1) },
            { assertThat(cartUiState.totalPrice).isEqualTo(1000) },
        )
    }
}
