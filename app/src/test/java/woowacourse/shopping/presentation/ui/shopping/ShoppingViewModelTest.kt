package woowacourse.shopping.presentation.ui.shopping

import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProducts
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.ui.UiState

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class ShoppingViewModelTest {

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var repository: Repository

    @BeforeEach
    fun setup() {
        repository = mockk()
        viewModel = ShoppingViewModel(repository)
        coEvery { repository.getCartItemsCounts() } returns Result.success(0)
    }

    @Test
    fun `장바구니에 담긴 상품을 불러올 수 있다`() = runTest {
        // given
        coEvery { repository.getCartItems(any(), any()) } returns Result.success(cartProducts)
        // when
        viewModel.loadCartByOffset()
        // then
        assertEquals(UiState.Success(cartProducts), viewModel.carts.getOrAwaitValue())
    }

    @Test
    fun `상품 목록을 불러올 수 있다`() = runTest {
        // given
        coEvery { repository.getProductsByPaging() } returns Result.success(cartProducts)
        // when
        viewModel.loadProductByOffset()
        // then
        assertEquals(UiState.Success(cartProducts), viewModel.products.getOrAwaitValue())
    }
}
