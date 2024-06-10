package woowacourse.shopping.presentation.ui.shoppingcart.ordderrecommend

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderRecommendViewModel
import woowacourse.shopping.remote.api.DummyData.PRODUCTS

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class OrderRecommendViewModelTest {
    private lateinit var viewModel: OrderRecommendViewModel

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    private val recommendCarts = PRODUCTS.content.take(10)

    @BeforeEach
    fun setUp() =
        runTest {
            coEvery {
                productHistoryRepository.getRecommendedProducts(10)
            } returns Result.success(recommendCarts)

            viewModel =
                OrderRecommendViewModel(
                    productHistoryRepository,
                    shoppingCartRepository,
                )
        }

    @Test
    fun `추천 상품을 주문 상품에 추가할 수 있다`() =
        runTest {
            // given
            val cartItemId = CartItemId(0)

            coEvery {
                shoppingCartRepository.postCartItem(
                    recommendCarts.first().id,
                    1,
                )
            } returns Result.success(cartItemId)

            // when
            viewModel.plusProductQuantity(recommendCarts.first().id, 0)

            // then
            val actual = viewModel.uiState.getOrAwaitValue()

            assertThat(actual.orderCarts).isEqualTo(
                listOf(Cart(id = cartItemId.id, product = recommendCarts.first(), quantity = 1)),
            )
        }
}
