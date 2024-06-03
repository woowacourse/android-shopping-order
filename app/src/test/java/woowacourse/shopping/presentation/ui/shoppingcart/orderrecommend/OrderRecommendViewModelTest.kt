package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS_PULL

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class OrderRecommendViewModelTest {
    private lateinit var viewModel: OrderRecommendViewModel

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    val recommendCarts = CARTS_PULL.content.take(10)

    @BeforeEach
    fun setUp() {
        every {
            productHistoryRepository.getProductHistoriesByCategory(10)
        } returns Result.success(recommendCarts)

        viewModel =
            OrderRecommendViewModel(
                productHistoryRepository,
                shoppingCartRepository,
                orderRepository,
            )
        Thread.sleep(3000)
    }

    @Test
    fun `추천 상품을 주문 상품에 추가할 수 있다`() {
        // given
        val cartItemId = 0

        every {
            shoppingCartRepository.insertCartProduct(
                recommendCarts.first().product.id,
                1,
            )
        } returns Result.success(cartItemId)

        // when
        viewModel.plusProductQuantity(recommendCarts.first().product.id, 0)
        Thread.sleep(3000)

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.orderCarts.values.first()).isEqualTo(recommendCarts.first().copy(quantity = 1))
    }

    @Test
    fun `주문 상품을 주문할 수 있다`() {
        // when
        val cartItem = CARTS_PULL.content.take(5)

        viewModel.load(cartItem)
        every { orderRepository.insertOrderByIds(cartItem.map { it.id }) } returns Result.success(Unit)

        viewModel.order()
        Thread.sleep(5000)

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        assertThat(actual.value).isEqualTo(OrderRecommendNavigateAction.NavigateToProductList)
    }
}
