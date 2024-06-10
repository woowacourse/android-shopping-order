package woowacourse.shopping.presentation.ui.cart

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
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.dummyCarts
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.toUiModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class CartViewModelTest {
    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var recentRepository: RecentRepository

    private lateinit var viewModel: CartViewModel

    private val initialTotalCartItemCount: Int = dummyCarts.size

    private val dummyCartUiWithChecked = dummyCarts.map { it.toUiModel(isChecked = true) }

    @BeforeEach
    fun setUp() {
        coEvery { cartRepository.load(0, initialTotalCartItemCount) } returns
            Result.success(
                dummyCarts,
            )
        viewModel = CartViewModel(cartRepository, productRepository, recentRepository, initialTotalCartItemCount)
    }

    @Test
    fun `모든 장바구니 아이템들을 불러와 주문 목록에 포함한다`() =
        runTest {
            val actual = viewModel.cartItems.getOrAwaitValue()
            val expected = UiState.Success(dummyCarts.map { it.toUiModel(isChecked = true) })

            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `선택한 장바구니 아이템을 삭제한다`() =
        runTest {
            val deletedItem = dummyCarts[0].toUiModel()
            coEvery { cartRepository.deleteCartItem(deletedItem.cartId) } returns Result.success(Unit)

            viewModel.onDeleteClick(deletedItem.cartId)

            val actual = viewModel.cartItems.getOrAwaitValue()
            val expected = dummyCartUiWithChecked.toMutableList().apply { removeAt(0) }
            assertThat(actual).isEqualTo(UiState.Success(expected))
        }

    @Test
    fun `장바구니 아이템들 중 선택한 아이템들의 총 개수를 알 수 있다`() =
        runTest {
            val actual = viewModel.totalCount.getOrAwaitValue()
            val expected = dummyCartUiWithChecked.sumOf { it.quantity }
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `장바구니 아이템들 중 선택한 아이템들의 총 가격을 알 수 있다`() =
        runTest {
            val actual = viewModel.totalPrice.getOrAwaitValue()
            val expected = dummyCartUiWithChecked.sumOf { it.quantity * it.price }
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `한 번에 모든 장바구니 아이템을 주문 목록 포함을 해제시킬 수 있다`() =
        runTest {
            viewModel.onTotalCheckBoxClicked(isChecked = false)

            val actual = viewModel.isAllCartItemsSelected.getOrAwaitValue()
            val expected = false
            assertThat(actual).isEqualTo(expected)
        }
}
