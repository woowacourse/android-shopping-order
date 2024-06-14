package woowacourse.shopping.presentation.cart

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.cartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var cartViewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = mockk()
        cartViewModel = CartViewModel(cartRepository)
    }

    @Test
    fun `장바구니의 최초 페이지는 1이어야 한다`() {
        coVerify { cartRepository.totalCartProducts() }
        assertThat(cartViewModel.uiState.getOrAwaitValue().currentPage).isEqualTo(1)
    }

    @Test
    fun `현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다`() =
        runTest {
            // given
            every { cartRepository.canLoadMoreCartProducts(0, 5) } returns Result.success(true)
            every { cartRepository.canLoadMoreCartProducts(2, 5) } returns Result.success(true)
            coEvery { cartRepository.cartProducts(1, 5) } returns
                Result.success(
                    listOf(
                        cartProduct(),
                    ),
                )
            // when
            cartViewModel.moveToNextPage()
            // then
            assertThat(cartViewModel.uiState.getOrAwaitValue().currentPage).isEqualTo(2)
        }
}
