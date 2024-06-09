package woowacourse.shopping.presentation.ui.cart

import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProduct
import woowacourse.shopping.cartProducts
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class CartViewModelTest {
    @RelaxedMockK
    private lateinit var cartItemRepository: CartItemRepository

    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @RelaxedMockK
    private lateinit var recentProductRepository: RecentProductRepository

    @InjectMockKs
    private lateinit var viewModel: CartViewModel

    @Test
    fun `카트 아이템을 pageCount개씩 불러온다`() {
        coEvery { cartItemRepository.getAllByPaging(any(), any()) } returns
            Result.success(
                cartProducts,
            )
        viewModel.findCartByOffset()
        Thread.sleep(1000)
        assertThat(viewModel.carts.getOrAwaitValue()).isEqualTo(
            UiState.Success(
                cartProducts.map {
                        cartProduct ->
                    CartProductUiModel(cartProduct)
                },
            ),
        )
    }

    @Test
    fun `카트 아이템을 불러오기 실패하면 Error 상태로 변화한다`() {
        coEvery { cartItemRepository.getAllByPaging(any(), any()) } returns Result.failure(Throwable())
        viewModel.findCartByOffset()
        Thread.sleep(1000)
        assertThat(viewModel.errorHandler.getOrAwaitValue().getContentIfNotHandled()).isEqualTo(
            ErrorType.ERROR_CART_LOAD,
        )
    }

    @Test
    fun `데이터 삭제에 실패하면 Error 상태로 변화한다`() {
        coEvery { cartItemRepository.delete(any()) } returns Result.failure(Throwable())
        viewModel.onDelete(
            CartProductUiModel(
                cartProduct = cartProduct,
            ),
        )
        Thread.sleep(1000)
        assertThat(viewModel.errorHandler.getOrAwaitValue().getContentIfNotHandled()).isEqualTo(ErrorType.ERROR_CART_DELETE)
    }
}