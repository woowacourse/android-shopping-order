package woowacourse.shopping.presentation.ui.detail

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class ProductDetailViewModelTest {
    @RelaxedMockK
    private lateinit var cartItemRepository: CartItemRepository

    @RelaxedMockK
    private lateinit var recentProductRepository: RecentProductRepository

    @InjectMockKs
    private lateinit var viewModel: ProductDetailViewModel

    @Test
    fun `상품을 장바구니에 저장한다`() {
        coEvery { cartItemRepository.post(any()) } returns Result.success(1)
        viewModel.onSaveCart(
            DetailCartProduct(
                isNew = true,
                cartProduct = cartProduct,
            ),
        )
        Thread.sleep(1000)
        coVerify(exactly = 1) { cartItemRepository.post(any()) }
        coVerify(exactly = 1) { recentProductRepository.save(any()) }
    }
}
