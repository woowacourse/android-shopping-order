package woowacourse.shopping.presentation.cart

import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class CartViewModelTest {
    @RelaxedMockK
    lateinit var cartRepository: CartRepository

    private lateinit var cartViewModel: CartViewModel
    private val uiState get() = cartViewModel.uiState.getOrAwaitValue()

    @BeforeEach
    fun setUp() {
        cartViewModel = CartViewModel(cartRepository)
    }

    @Test
    fun `장바구니의 최초 페이지는 1이어야 한다`() {
        coVerify { cartRepository.totalCartProducts() }
        assertThat(uiState.currentPage).isEqualTo(1)
    }

//    @Test
//    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다,")
//    fun test1() {
//        val nextPage = 2
//        val serverNextPage = 1
//        // given
//        every { cartRepository.cartProducts(serverNextPage, PAGE_SIZE) } returns
//            Result.success(
//                listOf(
//                    cartProduct(),
//                ),
//            )
//        // when
//        cartViewModel.moveToNextPage()
//        // then
//        verify(exactly = 1) { cartRepository.cartProducts(serverNextPage, PAGE_SIZE) }
//        uiState.currentPage shouldBe nextPage
//    }
//
//    @Test
//    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다")
//    fun test2() {
//        val nextPage = 2
//        val serverNextPage = 1
//        // given
//        every { cartRepository.canLoadMoreCartProducts(0, PAGE_SIZE) } returns Result.success(true)
//        every { cartRepository.canLoadMoreCartProducts(2, PAGE_SIZE) } returns Result.success(true)
//        every { cartRepository.cartProducts(serverNextPage, PAGE_SIZE) } returns
//            Result.success(
//                listOf(
//                    cartProduct(),
//                ),
//            )
//        // when
//        cartViewModel.moveToNextPage()
//        // then
//        verify(exactly = 1) { cartRepository.canLoadMoreCartProducts(0, PAGE_SIZE) }
//        verify(exactly = 1) { cartRepository.canLoadMoreCartProducts(2, PAGE_SIZE) }
//        uiState.currentPage shouldBe nextPage
//    }
//
//    companion object {
//        const val PAGE_SIZE = 5
//    }
}
