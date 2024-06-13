package woowacourse.shopping.presentation.ui.shopping

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.dummyCarts
import woowacourse.shopping.dummyProductList
import woowacourse.shopping.dummyProducts

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class ShoppingViewModelTest {
    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var recentRepository: RecentRepository

    private lateinit var viewModel: ShoppingViewModel

    @BeforeEach
    fun setup() {
        coEvery { cartRepository.loadAll() } returns Result.success(dummyCarts)
        coEvery { productRepository.load(any(), any()) } returns Result.success(dummyProductList)
        coEvery { recentRepository.loadAll() } returns Result.success(dummyProducts)

        viewModel = ShoppingViewModel(productRepository, recentRepository, cartRepository)
    }

    @Test
    fun `viewModel이 초기화되면 최근 본 상품과 장바구니 상품, 상품 리스트 20개가 불러와진다`() = runTest {}
}
