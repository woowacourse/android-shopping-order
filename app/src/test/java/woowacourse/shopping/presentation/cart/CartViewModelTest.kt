package woowacourse.shopping.presentation.cart

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.fakeCartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.DeleteCartProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.LoadCartUseCase
import woowacourse.shopping.domain.usecase.LoadPagingCartUseCase
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class CartViewModelTest {
    @RelaxedMockK
    lateinit var cartRepository: CartRepository

    @RelaxedMockK
    lateinit var loadCartUseCase: LoadCartUseCase

    @RelaxedMockK
    private lateinit var increaseCartProductUseCase: IncreaseCartProductUseCase

    @RelaxedMockK
    private lateinit var decreaseCartProductUseCase: DecreaseCartProductUseCase

    @RelaxedMockK
    private lateinit var deleteCartProductUseCase: DeleteCartProductUseCase

    @RelaxedMockK
    private lateinit var loadPagingCartUseCase: LoadPagingCartUseCase

    private lateinit var cartViewModel: CartViewModel
    private val uiState get() = cartViewModel.uiState.getOrAwaitValue()

    @BeforeEach
    fun setUp() {
        every { loadCartUseCase() } returns
            Result.success(
                Cart(fakeCartProduct()),
            )
        every { cartRepository.canLoadMoreCartProducts(any(), PAGE_SIZE) } returns
            Result.success(
                true,
            )
        cartViewModel =
            CartViewModel(
                cartRepository,
                increaseCartProductUseCase,
                decreaseCartProductUseCase,
                deleteCartProductUseCase,
                loadCartUseCase,
                loadPagingCartUseCase,
            )
    }

    @Test
    @DisplayName("ViewModel 이 초기화될 때, 장바구니 상품을 모두 가져온다")
    fun test0() {
        verify(exactly = 1) { loadCartUseCase() }
        uiState.currentPage shouldBe 1
    }

    @Test
    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다,")
    fun test1() {
        val nextPage = 2
        // View 에서는 페이지가 1부터 시작, 서버에서는 0부터 시작
        val serverNextPage = 1
        // given
        every { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) } returns
            Result.success(
                Cart(
                    fakeCartProduct(),
                ),
            )
        // when
        cartViewModel.moveToNextPage()
        // then
        verify(exactly = 1) { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) }
        uiState.currentPage shouldBe nextPage
    }

    @Test
    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다")
    fun test2() {
        val nextPage = 2
        val serverNextPage = 1
        // given
        every { cartRepository.canLoadMoreCartProducts(0, PAGE_SIZE) } returns Result.success(true)
        every { cartRepository.canLoadMoreCartProducts(2, PAGE_SIZE) } returns Result.success(true)
        every { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) } returns
            Result.success(
                Cart(
                    fakeCartProduct(),
                ),
            )
        // when
        cartViewModel.moveToNextPage()
        // then
        verify(exactly = 1) { cartRepository.canLoadMoreCartProducts(0, PAGE_SIZE) }
        verify(exactly = 1) { cartRepository.canLoadMoreCartProducts(2, PAGE_SIZE) }
        uiState.currentPage shouldBe nextPage
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
