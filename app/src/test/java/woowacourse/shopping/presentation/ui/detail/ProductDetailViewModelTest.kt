package woowacourse.shopping.presentation.ui.detail

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProduct
import woowacourse.shopping.domain.Repository

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class ProductDetailViewModelTest {
    @MockK
    private lateinit var repository: Repository

    @InjectMockKs
    private lateinit var viewModel: ProductDetailViewModel

    @BeforeEach
    fun setup() {
        repository = mockk()
        viewModel = ProductDetailViewModel(repository)
        coEvery { repository.saveRecentProduct(any()) } returns Result.success(1)
    }

    @Test
    fun `saveCartItem으로 상품을 장바구니에 저장한다`() =
        runTest {
            // given
            val detailProduct =
                DetailCartProduct(
                    isNew = true,
                    cartProduct = cartProduct,
                )
            coEvery { repository.postCartItem(any()) } returns Result.success(1)
            // when
            viewModel.onAddToCart(detailProduct)
            // then
            coVerify(exactly = 1) { repository.postCartItem(any()) }
        }
}
