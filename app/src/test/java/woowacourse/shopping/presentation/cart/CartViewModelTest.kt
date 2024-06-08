package woowacourse.shopping.presentation.cart

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.cart.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DeleteCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.LoadCartUseCase
import woowacourse.shopping.domain.usecase.cart.LoadPagingCartUseCase
import woowacourse.shopping.presentation.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(
    InstantTaskExecutorExtension::class,
    MockKExtension::class,
    CoroutinesTestExtension::class
)
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
    fun setUp() = runTest {
        coEvery { loadCartUseCase() } returns
                Result.success(
                    Cart(fakeCartProduct()),
                )
        coEvery { cartRepository.canLoadMoreCartProducts(any(), PAGE_SIZE) } returns
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
    fun test0() = runTest {
        coVerify(exactly = 1) { loadCartUseCase() }
        uiState.currentPage shouldBe 1
    }

    @Test
    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다,")
    fun test1() = runTest {
        val nextPage = 2
        // View 에서는 페이지가 1부터 시작, 서버에서는 0부터 시작
        val serverNextPage = 1
        // given
        coEvery { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) } returns
                Result.success(
                    Cart(
                        fakeCartProduct(),
                    ),
                )
        // when
        cartViewModel.moveToNextPage()
        // then
        coVerify(exactly = 1) { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) }
        uiState.currentPage shouldBe nextPage
    }

    @Test
    @DisplayName("현재 페이지가 1일 때, 다음 페이지로 이동하면, 페이지가 2가 된다")
    fun test2() = runTest {
        val nextPage = 2
        val serverNextPage = 1
        // given
        coEvery {
            cartRepository.canLoadMoreCartProducts(
                0,
                PAGE_SIZE
            )
        } returns Result.success(true)
        coEvery {
            cartRepository.canLoadMoreCartProducts(
                2,
                PAGE_SIZE
            )
        } returns Result.success(true)
        coEvery { loadPagingCartUseCase(serverNextPage, PAGE_SIZE) } returns
                Result.success(
                    Cart(
                        fakeCartProduct(),
                    ),
                )
        // when
        cartViewModel.moveToNextPage()
        // then
        coVerify(exactly = 1) { cartRepository.canLoadMoreCartProducts(0, PAGE_SIZE) }
        coVerify(exactly = 1) { cartRepository.canLoadMoreCartProducts(2, PAGE_SIZE) }
        uiState.currentPage shouldBe nextPage
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
