package woowacourse.shopping.presentation.ui.detail

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class ProductEntityDetailViewModelTest {
    @MockK
    private lateinit var repository: Repository

    @InjectMockKs
    private lateinit var viewModel: ProductDetailViewModel

    @Test
    fun `saveCartItem으로 상품을 장바구니에 저장한다`() {
        every { repository.saveCart(any()) } returns Result.success(1)
        viewModel.onAddToCart(
            DetailCartProduct(
                isNew = false,
                cartProduct = cartProduct,
            ),
        )
        Thread.sleep(1000)
        verify(exactly = 1) { repository.saveCart(any()) }
    }
}
