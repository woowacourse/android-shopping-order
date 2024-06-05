package woowacourse.shopping.presentation.ui.detail

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class ProductEntityDetailViewModelTest {
    @MockK
    private lateinit var repository: Repository

    @InjectMockKs
    private lateinit var viewModel: ProductDetailViewModel

    @Test
    fun `saveCartItem으로 상품을 장바구니에 저장한다`() {
        coEvery { repository.postCartItem(any()) } returns Result.success(1)
        viewModel.onAddToCart(
            DetailCartProduct(
                isNew = true,
                cartProduct = cartProduct,
            ),
        )
        Thread.sleep(1000)
        coVerify(exactly = 1) { repository.saveRecentProduct(any()) }
    }
}
