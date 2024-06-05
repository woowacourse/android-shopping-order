package woowacourse.shopping.view.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.MockProductRepository
import woowacourse.shopping.MockRecentlyProductRepository
import woowacourse.shopping.MockShoppingCartRepository
import woowacourse.shopping.TestFixture.getOrAwaitValue
import woowacourse.shopping.view.products.ProductListViewModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProductListViewModel

    @BeforeEach
    fun setUp() {
        val productRepository = MockProductRepository()
        val shoppingCartRepository = MockShoppingCartRepository()
        val recentlyProductRepository = MockRecentlyProductRepository()
        viewModel =
            ProductListViewModel(
                productRepository = productRepository,
                shoppingCartRepository = shoppingCartRepository,
                recentlyProductRepository = recentlyProductRepository,
            )
    }

    @Test
    fun `offset을_기준으로_상품_리스트를_요청하면_상품_목록을_정해진_개수만큼_반환해야_한다`() {
        // given
        val before = viewModel.products.getOrAwaitValue()
        assertThat(before.size).isEqualTo(0)

        // when
        val latch = CountDownLatch(1)
        Thread {
            viewModel.loadPagingProduct()
            latch.countDown()
        }.start()
        latch.await(2, TimeUnit.SECONDS)

        // then
        val result = viewModel.products.getOrAwaitValue()
        assertThat(result.size).isEqualTo(3)
    }
}
