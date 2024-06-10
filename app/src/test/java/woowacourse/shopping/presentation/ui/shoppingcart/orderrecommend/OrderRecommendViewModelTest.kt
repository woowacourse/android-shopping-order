package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS_PULL

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderRecommendViewModelTest {
    private lateinit var viewModel: OrderRecommendViewModel

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    val recommendCarts = CARTS_PULL.content.take(10)

    @BeforeEach
    fun setUp() {
        coEvery {
            productHistoryRepository.getProductHistoriesByCategory(10)
        } returns Result.success(recommendCarts)

        viewModel =
            OrderRecommendViewModel(
                productHistoryRepository,
                shoppingCartRepository,
            )
    }

    @Test
    fun `추천 상품을 주문 상품에 추가할 수 있다`() {
        // given
        val cartItemId = 0

        coEvery {
            shoppingCartRepository.insertCartProduct(
                recommendCarts.first().product.id,
                1,
            )
        } returns Result.success(cartItemId)

        // when
        viewModel.plusProductQuantity(recommendCarts.first().product.id, 0)

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.orderCarts.values.first()).isEqualTo(recommendCarts.first().copy(quantity = 1))
    }
}
